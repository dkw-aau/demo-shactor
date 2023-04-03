package shactor;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import cs.Main;
import cs.qse.common.encoders.StringEncoder;
import cs.qse.filebased.Parser;
import cs.qse.querybased.nonsampling.QbParser;
import org.apache.commons.io.FileUtils;
import shactor.utils.Type;
import shactor.utils.Utils;

import java.io.File;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Consumer;


@Tag("selection-view")
@JsModule("./selection-view.ts")
@Route("/selection-view")
public class SelectionView extends LitTemplate {
    @Id("completeShapesExtractionButton")
    private static Button completeShapesExtractionButton;
    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;
    @Id("graphInfo")
    private static H5 graphInfo;
    @Id("vaadinGrid")
    private static Grid<Type> vaadinGrid;
    @Id("searchField")
    private static TextField searchField;
    @Id("footerLeftImage")
    private Image footerLeftImage;
    @Id("footerRightImage")
    private Image footerRightImage;
    @Id("graphStatsCheckBox")
    private static Checkbox graphStatsCheckBox;
    static Parser parser;
    static QbParser qbParser;
    public static List<String> chosenClasses;
    public static Set<Integer> chosenClassesEncoded;
    public static Boolean isFilteredClasses = false;
    public static HashMap<String, String> defaultShapesModelStats;

    public static String defaultShapesOutputFileAddress = "";

    public SelectionView() {
        Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        graphInfo.setVisible(false);
        completeShapesExtractionButton.setEnabled(false);
        searchField.setVisible(false);
        vaadinGrid.setVisible(false);

        beginParsing();
    }

    private static void beginParsing() {
        setPaths();
        switch (IndexView.category) {
            case EXISTING_FILE_BASED -> {
                parser = new Parser(IndexView.graphURL, 50, 5000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
                parser.entityExtraction();
                setGraphInfo(parser.entityDataHashMap.size(), parser.classEntityCount.size());
                setupGridInMultiSelectionMode(getClasses(parser.classEntityCount, parser.getStringEncoder()), parser.getStringEncoder(), parser.classEntityCount.size());

                completeShapesExtractionButton.addClickListener(buttonClickEvent -> {
                    completeFileBasedShapesExtraction();
                });
            }
            case CONNECT_END_POINT -> {
                qbParser = new QbParser(50, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", IndexView.graphURL, IndexView.endPointRepo);
                qbParser.getNumberOfInstancesOfEachClass();
                setGraphInfo(qbParser.getClassEntityCount().size());
                setupGridInMultiSelectionMode(getClasses(qbParser.getClassEntityCount(), qbParser.getStringEncoder()), qbParser.getStringEncoder(), qbParser.getClassEntityCount().size());

                completeShapesExtractionButton.addClickListener(buttonClickEvent -> {
                    completeQueryBasedShapesExtraction();
                });
            }
        }
        Utils.notify("Graph Parsed Successfully!", NotificationVariant.LUMO_SUCCESS, Notification.Position.TOP_CENTER);
    }

    private static void setPaths() {
        try {
            CodeSource codeSource = Parser.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            Main.setDataSetNameForJar(buildDatasetName(IndexView.category));
            Main.setOutputFilePathForJar(jarDir + "/Output/");
            Main.setConfigDirPathForJar(jarDir + "/config/");
            Main.setResourcesPathForJar(jarDir + "/resources/");
            Main.qseFromSpecificClasses = false;
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

    private static void setupGridInMultiSelectionMode(List<Type> classes, StringEncoder encoder, Integer classEntityCountSize) {
        vaadinGrid.setVisible(true);
        vaadinGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        vaadinGrid.addColumn(Type::getName).setHeader(Utils.boldHeader("Class IRI")).setSortable(true);
        vaadinGrid.addColumn(Type::getInstanceCount).setHeader(Utils.boldHeader("Class Instance Count")).setSortable(true);

        vaadinGrid.setItems(classes);
        GridListDataView<Type> dataView = vaadinGrid.setItems(classes);
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
            System.out.printf("Number of selected classes: %s%n", selection.getAllSelectedItems().size());
            if (selection.getAllSelectedItems().size() == classEntityCountSize) {
                System.out.println("Extract Shapes for All Classes");
                chosenClasses = new ArrayList<>();
                chosenClassesEncoded = new HashSet<>();
            } else {
                System.out.println("Extract Shapes for Chosen Classes");
                chosenClasses = new ArrayList<>();
                chosenClassesEncoded = new HashSet<>();
                selection.getAllSelectedItems().forEach(item -> {
                    chosenClasses.add(item.getName());
                    chosenClassesEncoded.add(encoder.encode(item.getName()));
                });
            }
            completeShapesExtractionButton.setEnabled(selection.getAllSelectedItems().size() > 0);
        });
        searchField.setVisible(true);
    }

    // Transform extracted classes to Type
    private static List<Type> getClasses(Map<Integer, Integer> classEntityCountMap, StringEncoder stringEncoder) {
        List<Type> types = new ArrayList<>();
        classEntityCountMap.forEach((k, v) -> {
            Type t = new Type();
            t.setName(stringEncoder.decode(k));
            t.setEncodedKey(v);
            t.setInstanceCount(v);
            types.add(t);
        });
        types.sort((d1, d2) -> d2.getInstanceCount() - d1.getInstanceCount());
        return types;
    }

    private static void setGraphInfo(int entityCount, int classCount) {
        graphInfo.setVisible(true);
        String info = "No. of entities: " + entityCount + " ; " + "No. of classes: " + classCount + ". Please select the classes from the table below for which you want to extract shapes.";
        graphInfo.setText(info);
    }

    private static void setGraphInfo(int classCount) {
        graphInfo.setVisible(true);
        String info = "No. of classes: " + classCount + ". Please select the classes from the table below for which you want to extract shapes.";
        graphInfo.setText(info);
    }


    private static void completeFileBasedShapesExtraction() {
        parser.entityConstraintsExtraction();
        parser.computeSupportConfidence();

        if (chosenClasses.size() > 0) {
            isFilteredClasses = true;
            System.out.println(chosenClasses);
            defaultShapesOutputFileAddress = parser.extractSHACLShapes(true, chosenClasses);
        } else {
            isFilteredClasses = false;
            defaultShapesOutputFileAddress = parser.extractSHACLShapes(false, chosenClasses);
        }
        defaultShapesModelStats = parser.shapesExtractor.getCurrentShapesModelStats();
        Utils.notifyMessage(graphStatsCheckBox.getValue().toString());
        completeShapesExtractionButton.getUI().ifPresent(ui -> ui.navigate("extraction-view"));
    }

    private static void completeQueryBasedShapesExtraction() {
        if (chosenClassesEncoded.size() > 0) {
            isFilteredClasses = true;
            qbParser.setClasses(chosenClassesEncoded);
        } else {
            isFilteredClasses = false;
            qbParser.getDistinctClasses();
        }
        qbParser.getShapesInfoAndComputeSupport();
        defaultShapesOutputFileAddress = qbParser.extractSHACLShapes();
        qbParser.writeSupportToFile();
        defaultShapesModelStats = qbParser.shapesExtractor.getCurrentShapesModelStats();
        //Utils.notifyMessage(graphStatsCheckBox.getValue().toString());
        completeShapesExtractionButton.getUI().ifPresent(ui -> ui.navigate("extraction-view"));
    }


    public static Parser getParser() {
        return parser;
    }

    public static QbParser getQbParser() {
        return qbParser;
    }

    public static String getDefaultShapesOutputFileAddress() {
        return defaultShapesOutputFileAddress;
    }

    private static String buildDatasetName(IndexView.Category category) {
        String name = "file";
        switch (category) {
            case EXISTING_FILE_BASED -> {
                String[] parts = IndexView.graphURL.split("/");
                name = parts[parts.length - 1];
            }
            case CONNECT_END_POINT -> name = IndexView.endPointRepo;
            case ANALYZE_SHAPES -> System.out.println("High level");
        }
        return name;
    }

    private static boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
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


