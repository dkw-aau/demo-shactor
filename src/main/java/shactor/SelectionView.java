package shactor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import cs.Main;
import cs.qse.filebased.Parser;
import org.apache.commons.io.FileUtils;
import shactor.utils.Type;
import shactor.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


@Tag("selection-view")
@JsModule("./selection-view.ts")
@Route("/selection-view")
public class SelectionView extends LitTemplate {
    static Parser parser;
    
    @Id("completeShapesExtractionButton")
    private Button completeShapesExtractionButton;
    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;
    @Id("graphInfo")
    private H5 graphInfo;
    @Id("vaadinGrid")
    private Grid<Type> vaadinGrid;
    @Id("searchField")
    private TextField searchField;
    @Id("footerLeftImage")
    private Image footerLeftImage;
    @Id("footerRightImage")
    private Image footerRightImage;
    @Id("graphStatsCheckBox")
    private Checkbox graphStatsCheckBox;
    
    public static List<String> chosenClasses;
    public static Boolean isFilteredClasses = false;
    public static HashMap<String, String> defaultShapesModelStats;

    
    public SelectionView() {
        Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        graphInfo.setVisible(false);
        completeShapesExtractionButton.setEnabled(false);
        searchField.setVisible(false);
        vaadinGrid.setVisible(false);
        
        //startShapesExtractionButton.addClickListener(buttonClickEvent -> {
        beginParsing();
        setGraphInfo();
        Utils.notify("Graph Parsed Successfully!", NotificationVariant.LUMO_SUCCESS, Notification.Position.TOP_CENTER);
        setupGridInMultiSelectionMode();
        searchField.setVisible(true);
        //});
        
        completeShapesExtractionButton.addClickListener(buttonClickEvent -> {
            completeShapesExtraction();
        });
    }
    
    private static void beginParsing() {
        setPaths();
        parser = new Parser(IndexView.graphURL, 50, 5000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        parser.entityExtraction();
    }
    
    private static void setPaths() {
        try {
            CodeSource codeSource = Parser.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            String[] parts = IndexView.graphURL.split("/");
            Main.setDataSetNameForJar(parts[parts.length - 1]);
            Main.setOutputFilePathForJar(jarDir + "/Output/");
            Main.setConfigDirPathForJar(jarDir + "/config/");
            Main.setResourcesPathForJar(jarDir + "/resources/");
            
            //Clean output directory
            File[] filesInOutputDir = new File(jarDir + "/Output/").listFiles();
            assert filesInOutputDir != null;
            for (File file : filesInOutputDir) {
                if (!file.getName().equals(".keep")) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("Deleted already existing file: " + file.getPath());
                    }
                }
                if (file.isDirectory()) {
                    FileUtils.forceDelete(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupGridInMultiSelectionMode() {
        vaadinGrid.setVisible(true);
        //vaadinGrid = new Grid<>(Type.class, false); do not initialize again if you have added it from the designer.
        vaadinGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        //vaadinGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        vaadinGrid.addColumn(Type::getName).setHeader(new Html("<div style='font-weight: bold;'>Class IRI</div>")).setSortable(true);
        vaadinGrid.addColumn(Type::getInstanceCount).setHeader(new Html("<div style='font-weight: bold;'>Clas Instance Count</div>")).setSortable(true);
        
        List<Type> classes = getClasses();
        vaadinGrid.setItems(classes);
        
        GridListDataView<Type> dataView = vaadinGrid.setItems(classes);
        //TypeFilter typeFilter = new TypeFilter(dataView);
        //vaadinGrid.getHeaderRows().clear();
        //HeaderRow headerRow = vaadinGrid.appendHeaderRow();
        //headerRow.getCell(nameColumn).setComponent(createFilterHeader("Filter", typeFilter::setName));
        
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());
        
        dataView.addFilter(type -> {
            String searchTerm = searchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            return matchesTerm(type.getName(), searchTerm);
        });
        
        vaadinGrid.addSelectionListener(selection -> {
            //System.out.printf("Number of selected classes: %s%n", selection.getAllSelectedItems().size());
            if (selection.getAllSelectedItems().size() == parser.classEntityCount.size()) {
                System.out.println("Extract Shapes for All Classes");
                chosenClasses = new ArrayList<>();
            } else {
                System.out.println("Extract Shapes for Chosen Classes");
                chosenClasses = new ArrayList<>();
                selection.getAllSelectedItems().forEach(item -> {
                    chosenClasses.add(item.getName());
                });
            }
            completeShapesExtractionButton.setEnabled(selection.getAllSelectedItems().size() > 0);
        });
        
    }
    
    
    private List<Type> getClasses() {
        List<Type> types = new ArrayList<>();
        parser.classEntityCount.forEach((k, v) -> {
            Type t = new Type();
            t.setName(parser.getStringEncoder().decode(k));
            t.setEncodedKey(v);
            t.setInstanceCount(v);
            types.add(t);
        });
        types.sort((d1, d2) -> d2.getInstanceCount() - d1.getInstanceCount());
        return types;
    }
    
    private void setGraphInfo() {
        graphInfo.setVisible(true);
        String info = "No. of entities: " + parser.entityDataHashMap.size() + " ; " + "No. of classes: " + parser.classEntityCount.size() + ". Please select the classes from the table below for which you want to extract shapes.";
        graphInfo.setText(info);
    }
    
    
    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
    
    private void completeShapesExtraction() {
        parser.entityConstraintsExtraction();
        parser.computeSupportConfidence();
        
        if (chosenClasses.size() > 0) {
            isFilteredClasses = true;
            System.out.println(chosenClasses);
            parser.extractSHACLShapes(true, chosenClasses);
            defaultShapesModelStats = parser.shapesExtractor.getCurrentShapesModelStats();
        } else {
            isFilteredClasses = false;
            parser.extractSHACLShapes(false, chosenClasses);
            defaultShapesModelStats = parser.shapesExtractor.getCurrentShapesModelStats();
        }
        Utils.notifyMessage(graphStatsCheckBox.getValue().toString());
        completeShapesExtractionButton.getUI().ifPresent(ui -> ui.navigate("extraction-view"));
    }
    
    // Not used for now, but will be useful if you have to create filter over columns of grid
    private static class TypeFilter {
        private final GridListDataView<Type> dataView;
        
        private String name;
        private Integer instanceCount;
        
        public TypeFilter(GridListDataView<Type> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }
        
        public void setName(String fullName) {
            this.name = fullName;
            this.dataView.refreshAll();
        }
        
        
        public boolean test(Type type) {
            return matches(type.getName(), name);
        }
        
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
        
    }
    
    private static Component createFilterHeader(String labelText, Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        
        return layout;
    }
    
    public static Parser getParser() {
        return parser;
    }
}


