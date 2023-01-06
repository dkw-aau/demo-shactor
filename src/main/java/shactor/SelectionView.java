package shactor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H5;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import cs.Main;
import cs.qse.filebased.Parser;
import shactor.utils.Type;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
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
    @Id("startShapesExtractionButton")
    private Button startShapesExtractionButton;
    
    public SelectionView() {
        graphInfo.setVisible(false);
        completeShapesExtractionButton.setVisible(false);
        searchField.setVisible(false);
        vaadinGrid.setVisible(false);
        
        startShapesExtractionButton.addClickListener(buttonClickEvent -> {
            beginParsing();
            setGraphInfo();
            notify("Graph Parsed Successfully!", NotificationVariant.LUMO_SUCCESS, Notification.Position.TOP_CENTER);
            setupGridInMultiSelectionMode();
        });
        
        completeShapesExtractionButton.addClickListener(buttonClickEvent -> {
            completeShapesExtraction();
        });
    }
    
    private static void notify(String message, NotificationVariant notificationVariant, Notification.Position position) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(position);
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
            Main.setDataSetNameForJar("Graph");
            Main.setOutputFilePathForJar(jarDir + "/Output/");
            Main.setConfigDirPathForJar(jarDir + "/config/");
            Main.setResourcesPathForJar(jarDir + "/resources/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setupGridInMultiSelectionMode() {
        vaadinGrid.setVisible(true);
        //vaadinGrid = new Grid<>(Type.class, false); do not initialize again if you have added it from the designer.
        vaadinGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        //vaadinGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        vaadinGrid.addColumn(Type::getName).setHeader("Class").setSortable(true);
        vaadinGrid.addColumn(Type::getInstanceCount).setHeader("Class Instance Count").setSortable(true);
        
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
            completeShapesExtractionButton.setVisible(selection.getAllSelectedItems().size() > 0);
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
        notify("Extracting Constraints Information. Please Wait!", NotificationVariant.LUMO_PRIMARY, Notification.Position.MIDDLE);
        parser.entityConstraintsExtraction();
        
        notify("Computing Support and Confidence. Please Wait!", NotificationVariant.LUMO_PRIMARY, Notification.Position.MIDDLE);
        parser.computeSupportConfidence();
        
        notify("Designing SHACL Shapes. Please be patient!", NotificationVariant.LUMO_PRIMARY, Notification.Position.MIDDLE);
        parser.extractSHACLShapes(false, false);
        
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
}


