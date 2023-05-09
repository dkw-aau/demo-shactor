package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.qse.filebased.Parser;
import cs.qse.querybased.nonsampling.QbParser;
import org.apache.commons.io.FileUtils;
import org.vaadin.olli.FileDownloadWrapper;
import shactor.utils.ChartsUtil;
import shactor.utils.DialogUtil;
import shactor.utils.PruningUtil;
import shactor.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static shactor.utils.ChartsUtil.*;
import static shactor.utils.Utils.*;


@Tag("extraction-view")
@JsModule("./extraction-view.ts")
@CssImport(value = "./grid.css", themeFor = "vaadin-grid")
@Route("/extraction-view")
public class ExtractionView extends LitTemplate {

    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;

    @Id("supportTextField")
    private TextField supportTextField;
    @Id("confidenceTextField")
    private TextField confidenceTextField;
    @Id("startPruningButton")
    private Button startPruningButton;
    @Id("shapesGrid")
    private Grid<NS> shapesGrid;
    @Id("propertyShapesGrid")
    private Grid<PS> propertyShapesGrid;
    @Id("propertyShapesGridInfo")
    private H5 propertyShapesGridInfo;

    @Id("downloadSelectedShapesButton")
    private Button downloadSelectedShapesButton;

    private final PruningUtil pruningUtil = new PruningUtil();

    String currNodeShape;
    String prunedFileAddress = "";

    @Id("headingPieCharts")
    private H2 headingPieCharts;
    @Id("headingNodeShapesAnalysis")
    private H2 headingNodeShapesAnalysis;
    @Id("vaadinRadioGroup")
    private RadioButtonGroup<String> vaadinRadioGroup;
    @Id("psVaadinRadioGroup")
    private RadioButtonGroup<String> psVaadinRadioGroup;
    @Id("psGridRadioButtonInfo")
    private Paragraph psGridRadioButtonInfo;
    @Id("nsGridRadioButtonInfo")
    private Paragraph nsGridRadioButtonInfo;
    static PS currPS;
    static NS currNS;
    static Integer support;
    static Double confidence;
    @Id("actionButtonsHorizontalLayout")
    private HorizontalLayout actionButtonsHorizontalLayout;
    @Id("nsSearchField")
    private TextField nsSearchField;
    @Id("psSearchField")
    private TextField psSearchField;
    @Id("chartsContainerHorizontalLayout")
    private HorizontalLayout chartsContainerHorizontalLayout;
    @Id("soChartsContainerHorizontalLayout")
    private HorizontalLayout soChartsContainerHorizontalLayout;
    @Id("vl1")
    private VerticalLayout vl1;
    @Id("vl2")
    private VerticalLayout vl2;
    @Id("vl3")
    private VerticalLayout vl3;
    @Id("vl4")
    private VerticalLayout vl4;
    @Id("graphStatsVerticalLayout")
    private VerticalLayout graphStatsVerticalLayout;
    @Id("splitLayout")
    private SplitLayout splitLayout;
    @Id("graphStatsHeading")
    private Paragraph graphStatsHeading;
    @Id("pruningParamsHorizontalLayout")
    private HorizontalLayout pruningParamsHorizontalLayout;

    public ExtractionView() {
        chartsContainerHorizontalLayout.removeAll();
        soChartsContainerHorizontalLayout.setVisible(false);

        if (SelectionView.computeStats) {
            if (IndexView.category.equals(IndexView.Category.CONNECT_END_POINT)) {
                //Utils.notifyMessage("Computer Stats over Endpoint (TODO)");
                graphStatsVerticalLayout.setVisible(false);
                splitLayout.setSplitterPosition(100);
            } else {
                splitLayout.setSplitterPosition(60);
                graphStatsVerticalLayout.add(buildBarChartUsingDatasetsStats(IndexView.selectedDataset));
            }
        } else {
            graphStatsVerticalLayout.setVisible(false);
            splitLayout.setSplitterPosition(100);
        }

        //Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        nsSearchField.setVisible(false);
        psSearchField.setVisible(false);
        psGridRadioButtonInfo.setVisible(false);
        nsGridRadioButtonInfo.setVisible(false);
        downloadSelectedShapesButton.setVisible(false);
        vaadinRadioGroup.setVisible(false);
        psVaadinRadioGroup.setVisible(false);
        headingPieCharts.setVisible(false);
        headingNodeShapesAnalysis.setVisible(false);
        shapesGrid.setVisible(false);
        propertyShapesGrid.setVisible(false);
        propertyShapesGridInfo.setVisible(false);


        configureButtonWithFileWrapper(VaadinIcon.BAR_CHART, "Download Shapes Statistics", SelectionView.outputDirectory + SelectionView.buildDatasetName(IndexView.category) + ".csv");
        configureButtonWithFileWrapper(VaadinIcon.TIMER, "Download SHACTOR extraction logs", SelectionView.outputDirectory + SelectionView.buildDatasetName(IndexView.category) + "_RUNTIME_LOGS.csv");
        configureButtonWithFileWrapper(VaadinIcon.DOWNLOAD, "Download Shapes", SelectionView.getDefaultShapesOutputFileAddress());
        //Utils.setIconForButtonWithToolTip(readShapesStatsButton, VaadinIcon.BAR_CHART, "Download Shapes Statistics");
        //Utils.setIconForButtonWithToolTip(readShactorLogsButton, VaadinIcon.TIMER, "Download SHACTOR extraction logs");
        //Utils.setIconForButtonWithToolTip(taxonomyVisualizationButton, VaadinIcon.FILE_TREE, "Visualize Shapes Taxonomy");

        /*
        taxonomyVisualizationButton.setVisible(false);
        taxonomyVisualizationButton.addClickListener(buttonClickEvent -> {
            RouterLink link = new RouterLink("taxonomy-view", TaxonomyView.class);
            taxonomyVisualizationButton.getUI().ifPresent(ui -> ui.getPage().open(link.getHref()));
        });*/

        startPruningButton.addClickListener(buttonClickEvent -> {
            if (supportTextField.getValue().isEmpty() || confidenceTextField.getValue().isEmpty()) {
                Utils.notify("Please enter valid values!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            } else {
                beginPruning();
                configurePrunedShapesDownloadButton();
            }
        });
    }

    private void configureButtonWithFileWrapper(VaadinIcon vaadinIcon, String label, String fileAddress) {
        System.out.println(fileAddress);
        Button button = new Button();
        Utils.setIconForButtonWithToolTip(button, vaadinIcon, label);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper buttonWrapper;
        try {
            File file = new File(fileAddress);
            ByteArrayInputStream stream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            buttonWrapper = new FileDownloadWrapper(new StreamResource(file.getName(), () -> stream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buttonWrapper.wrapComponent(button);
        actionButtonsHorizontalLayout.add(buttonWrapper);
    }

    private void configurePrunedShapesDownloadButton() {
        Button button = new Button();
        button.setText("Download Reliable Shapes");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper buttonWrapper;
        try {
            File file = new File(this.prunedFileAddress);
            ByteArrayInputStream stream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            buttonWrapper = new FileDownloadWrapper(new StreamResource(file.getName(), () -> stream));
            buttonWrapper.getStyle().set("align-self", "end");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buttonWrapper.wrapComponent(button);
        pruningParamsHorizontalLayout.add(buttonWrapper);
    }

    Parser parser;

    private void beginPruning() {
        soChartsContainerHorizontalLayout.setVisible(true);
        shapesGrid.removeAllColumns();
        vl1.removeAll();
        vl2.removeAll();
        vl3.removeAll();
        vl4.removeAll();

        support = Integer.parseInt(supportTextField.getValue());
        confidence = (Double.parseDouble(confidenceTextField.getValue())) / 100;
        List<NS> nodeShapes = null;

        switch (IndexView.category) {
            case EXISTING_FILE_BASED -> {
                parser = SelectionView.getParser();
                this.prunedFileAddress = parser.extractSHACLShapesWithPruning(SelectionView.isFilteredClasses, confidence, support, SelectionView.chosenClasses); // extract shapes with pruning
                nodeShapes = parser.shapesExtractor.getNodeShapes();
            }
            case CONNECT_END_POINT -> {
                QbParser qbParser = SelectionView.getQbParser();
                this.prunedFileAddress = qbParser.extractSHACLShapesWithPruning(confidence, support); // extract shapes with pruning
                nodeShapes = qbParser.shapesExtractor.getNodeShapes();
            }
        }
        assert nodeShapes != null;
        pruningUtil.applyPruningFlags(nodeShapes, support, confidence);
        pruningUtil.getDefaultStats(nodeShapes);
        pruningUtil.getStatsBySupport(nodeShapes);
        pruningUtil.getStatsByConfidence(nodeShapes);
        pruningUtil.getStatsByBoth(nodeShapes);

        headingPieCharts.setVisible(true);

        headingNodeShapesAnalysis.setVisible(true);
        vl1.add(getParagraph("Default Shapes Analysis"));
        vl2.add(getParagraph("Shapes Analysis by Support"));
        vl3.add(getParagraph("Shapes Analysis by Confidence"));
        vl4.add(getParagraph("By Support and Confidence"));

        vl1.add(ChartsUtil.buildPieChart(preparePieChartsDataWithDefaultStats(pruningUtil.getStatsDefault(), pruningUtil)));
        vl2.add(ChartsUtil.buildPieChart(preparePieChartDataForSupportAnalysis(pruningUtil.getStatsBySupport(), support, pruningUtil)));
        vl3.add(ChartsUtil.buildPieChart(preparePieChartDataForConfidenceAnalysis(pruningUtil.getStatsByConfidence(), confidence, pruningUtil)));
        vl4.add(ChartsUtil.buildPieChart(preparePieChartDataForSupportAndConfidenceAnalysis(pruningUtil.getStatsByBoth(), support, confidence, pruningUtil)));

        setupNodeShapesGrid(nodeShapes, support, confidence);
        setupFilterRadioGroup(vaadinRadioGroup);
        vaadinRadioGroup.setVisible(true);
        List<NS> finalNodeShapes = nodeShapes;
        vaadinRadioGroup.addValueChangeListener(listener -> {
            if (listener.getValue() != null) {
                if (vaadinRadioGroup.getValue().equals("Above")) {
                    shapesGrid.setItems(positive(finalNodeShapes));
                    shapesGrid.getDataProvider().refreshAll();
                }

                if (vaadinRadioGroup.getValue().equals("Below")) {
                    shapesGrid.setItems(negative(finalNodeShapes));
                    shapesGrid.getDataProvider().refreshAll();
                }

                if (vaadinRadioGroup.getValue().equals("All")) {
                    System.out.println("Default");
                    shapesGrid.setItems(finalNodeShapes);
                    shapesGrid.getDataProvider().refreshAll();
                }
            }
        });
    }


    // -------------------------   Setup Grids   -----------------------------
    private void setupNodeShapesGrid(List<NS> nodeShapes, Integer support, Double confidence) {
        shapesGrid.setVisible(true);
        nsGridRadioButtonInfo.setVisible(true);
        shapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        shapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        shapesGrid.addColumn(NS::getLocalNameFromIri).setHeader(Utils.boldHeader("Node Shape")).setResizable(true).setAutoWidth(true).setComparator(NS::getPruneFlag);
        shapesGrid.addColumn(NS::getTargetClass).setHeader(Utils.boldHeader("Target Class")).setResizable(true).setResizable(true).setAutoWidth(true);
        shapesGrid.addColumn(NS::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(true).setSortable(true);
        shapesGrid.addColumn(NS::getCountPropertyShapes).setHeader(Utils.boldHeader("Count PS")).setResizable(true).setAutoWidth(true);
        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithSupportPruneFlag();
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader((setHeaderWithInfoLogo(
                "PS Quality (by Support)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds."))).setResizable(true).setAutoWidth(true);

        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithConfidencePruneFlag();
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader(setHeaderWithInfoLogo(
                "PS Quality (by Confidence)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds.")).setResizable(true).setAutoWidth(true);

        shapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ns) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
                currNS = ns;
                propertyShapesGrid.removeAllColumns();
                this.setupPropertyShapesGrid(ns);
            });
            button.setIcon(new Icon(VaadinIcon.LIST_UL));
            button.setText("PS List");
        })).setHeader(setHeaderWithInfoLogo("Show PS", "See PS of current NS"));

        //setClassNameToHighlightNodeShapesInRed(shapesGrid);
        nodeShapes.sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        GridListDataView<NS> dataView = shapesGrid.setItems(nodeShapes);
        shapesGrid.addSelectionListener(selection -> {
            System.out.printf("Number of selected classes: %s%n", selection.getAllSelectedItems().size());
            downloadSelectedShapesButton.setVisible(true);

            downloadSelectedShapesButton.addClickListener(listener -> {
                String shapes = Utils.constructModelForGivenNodeShapesAndTheirPropertyShapes(selection.getAllSelectedItems());
                DialogUtil.getDialogWithHeaderAndFooterForShowingShapeSyntax(shapes);
                //System.out.println(shapes);
            });
        });
        nsSearchField.setVisible(true);
        nsSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        nsSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        nsSearchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(type -> {
            psSearchField.clear();
            String searchTerm = nsSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            return matchesTerm(type.getLocalNameFromIri(), searchTerm);
        });
    }

    private void setupPropertyShapesGrid(NS ns) {
        if (ns == null) return;
        psGridRadioButtonInfo.setVisible(true);
        currNodeShape = ns.getLocalNameFromIri();
        propertyShapesGridInfo.setVisible(true);
        propertyShapesGridInfo.setText("Property Shapes Analysis for " + currNodeShape);

        //propertyShapesGrid.removeAllColumns();
        propertyShapesGrid.setVisible(true);
        propertyShapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        propertyShapesGrid.addColumn(PS::getLocalNameFromIri).setHeader(Utils.boldHeader("Property Shape")).setResizable(true).setAutoWidth(true).setComparator(PS::getPruneFlag);
        propertyShapesGrid.addColumn(PS::getPath).setHeader(Utils.boldHeader("Property Path")).setResizable(true).setAutoWidth(true);
        propertyShapesGrid.addColumn(PS::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(true).setSortable(true);
        propertyShapesGrid.addColumn(PS::getConfidenceInPercentage).setHeader(Utils.boldHeader("Confidence")).setResizable(true).setAutoWidth(true).setComparator(PS::getConfidence);
        propertyShapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ps) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            if (ps.getConfidence() != null) {
                progressBar.setValue(ps.getConfidence());
            } else {
                ShaclOrListItem item = null;
                for (ShaclOrListItem currItem : ps.getShaclOrListItems()) {
                    if (item == null) {
                        item = currItem;
                    }
                    if (currItem.getConfidence() > item.getConfidence()) {
                        item = currItem;
                    }
                }
                assert item != null;
                progressBar.setValue(item.getConfidence());
            }
        })).setHeader(setHeaderWithInfoLogo("PSc Quality (by Confidence)", " This shows")).setResizable(true).setAutoWidth(true);
        propertyShapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ps) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            //button.addClickListener(e -> this.generateQueryForPropertyShape(ns, ps));
            RouterLink link = new RouterLink("ps-view", PsView.class);
            button.addClickListener(e -> {
                currPS = ps;
                button.getUI().ifPresent(ui -> ui.getPage().open(link.getHref()));
            });

            button.setIcon(new Icon(VaadinIcon.EXTERNAL_LINK));
            button.setText("Analyze");
        })).setHeader(setHeaderWithInfoLogo("Action", "The generated SPARQL query will fetch the triples responsible for having chosen PS as part of NS"));

        setClassNameToHighlightPropertyShapesInRed(propertyShapesGrid);

        for (PS ps : ns.getPropertyShapes()) {
            if (ps.getConfidence() == null) {
                ShaclOrListItem item = null;
                for (ShaclOrListItem currItem : ps.getShaclOrListItems()) {
                    if (item == null) {
                        item = currItem;
                    }
                    if (currItem.getConfidence() > item.getConfidence()) {
                        item = currItem;
                    }
                }
                assert item != null;
                ps.setConfidence(item.getConfidence());
                ps.setSupport(item.getSupport());
            }
        }
        ns.getPropertyShapes().sort((d1, d2) -> d2.getSupport() - d1.getSupport());

        psVaadinRadioGroup.setVisible(true);
        setupFilterRadioGroup(psVaadinRadioGroup);
        psVaadinRadioGroup.addValueChangeListener(listener -> {
            if (listener.getValue() != null) {
                if (listener.getValue().equals("Above")) {
                    propertyShapesGrid.setItems(positivePs(ns.getPropertyShapes()));
                    propertyShapesGrid.getDataProvider().refreshAll();
                }

                if (listener.getValue().equals("Below")) {
                    propertyShapesGrid.setItems(negativePs(ns.getPropertyShapes()));
                    propertyShapesGrid.getDataProvider().refreshAll();
                }

                if (listener.getValue().equals("All")) {
                    System.out.println("Default");
                    propertyShapesGrid.setItems(ns.getPropertyShapes());
                    propertyShapesGrid.getDataProvider().refreshAll();
                }
            }
        });

        GridListDataView<PS> dataView = propertyShapesGrid.setItems(ns.getPropertyShapes());

        psSearchField.setVisible(true);
        psSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        psSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        psSearchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(type -> {
            String searchTerm = psSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            return matchesTerm(type.getLocalNameFromIri(), searchTerm);
        });
    }

    private void setupFilterRadioGroup(RadioButtonGroup<String> vaadinRadioGroup) {
        vaadinRadioGroup.setItems("All", "Above", "Below");
        vaadinRadioGroup.setValue("All");
    }
    // -------------------------   Grids Helper Methods   -----------------------------


    private List<NS> negative(List<NS> ns) {
        List<NS> list = new ArrayList<>();
        for (NS nodeShape : ns) {
            if (nodeShape.getPruneFlag()) {
                list.add(nodeShape);
            }
        }
        ns = list;
        ns.sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        return ns;
    }

    private List<NS> positive(List<NS> ns) {
        List<NS> list = new ArrayList<>();
        for (NS nodeShape : ns) {
            if (!nodeShape.getPruneFlag()) {
                list.add(nodeShape);
            }
        }
        ns = list;
        ns.sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        return ns;
    }

    private List<PS> negativePs(List<PS> ps) {
        List<PS> list = new ArrayList<>();
        for (PS nodeShape : ps) {
            if (nodeShape.getPruneFlag()) {
                list.add(nodeShape);
            }
        }
        ps = list;
        ps.sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        return ps;
    }

    private List<PS> positivePs(List<PS> ps) {
        List<PS> list = new ArrayList<>();
        for (PS nodeShape : ps) {
            if (!nodeShape.getPruneFlag()) {
                list.add(nodeShape);
            }
        }
        ps = list;
        ps.sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        return ps;
    }


    private static void setClassNameToHighlightNodeShapesInRed(Grid<NS> shapesGrid) {
        shapesGrid.setClassNameGenerator(ns -> {
            if (ns.getPruneFlag()) {
                return "prune";
            } else {
                return "no-prune";
            }
        });
    }

    private void setClassNameToHighlightPropertyShapesInRed(Grid<PS> propertyShapesGrid) {
        propertyShapesGrid.setClassNameGenerator(ps -> {
            if (ps.getPruneFlag()) {
                return "prune";
            } else {
                return "no-prune";
            }
        });
    }


    // -------------------------   Getter Methods   -----------------------------
    public static PS getCurrPS() {
        return currPS;
    }

    public static NS getCurrNS() {
        return currNS;
    }

    public static Integer getSupport() {
        return support;
    }

    public static Double getConfidence() {
        return confidence;
    }

}
