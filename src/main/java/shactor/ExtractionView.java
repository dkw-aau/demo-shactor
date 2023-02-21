package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.qse.filebased.Parser;
import org.apache.commons.io.FileUtils;
import org.vaadin.olli.FileDownloadWrapper;
import shactor.utils.ChartsUtil;
import shactor.utils.PruningUtil;
import shactor.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static shactor.utils.ChartsUtil.*;
import static shactor.utils.Utils.setHeaderWithInfoLogo;


@Tag("extraction-view")
@JsModule("./extraction-view.ts")
@CssImport(value = "./grid.css", themeFor = "vaadin-grid")
@Route("/extraction-view")
public class ExtractionView extends LitTemplate {

    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;

    @Id("readShapesStatsButton")
    private Button readShapesStatsButton;
    @Id("readShactorLogsButton")
    private Button readShactorLogsButton;
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
    @Id("downloadPrunedShapesButton")
    private Button downloadPrunedShapesButton;
    @Id("downloadSelectedShapesButton")
    private Button downloadSelectedShapesButton;


    //Charts
    @Id("knowledgeGraphStatsPieChart")
    private Chart knowledgeGraphStatsPieChart;
    @Id("defaultShapesStatsPieChart")
    private Chart defaultShapesStatsPieChart;
    @Id("shapesStatsBySupportPieChart")
    private Chart shapesStatsBySupportPieChart;
    @Id("shapesStatsByConfidencePieChart")
    private Chart shapesStatsByConfidencePieChart;
    @Id("shapesStatsByBothPieChart")
    private Chart shapesStatsByBothPieChart;

    private final PruningUtil pruningUtil = new PruningUtil();
    static Parser parser;
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
    @Id("taxonomyVisualizationButton")
    private Button taxonomyVisualizationButton;
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


    public ExtractionView() {
        setupKnowledgeGraphStatsChart(knowledgeGraphStatsPieChart);
        //Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        parser = SelectionView.getParser();
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
        downloadPrunedShapesButton.setVisible(false);

        configureDefaultShapesDownloadButton();
        Utils.setIconForButtonWithToolTip(readShapesStatsButton, VaadinIcon.BAR_CHART, "Read Shapes Statistics");
        Utils.setIconForButtonWithToolTip(readShactorLogsButton, VaadinIcon.TIMER, "Read SHACTOR extraction logs");
        Utils.setIconForButtonWithToolTip(taxonomyVisualizationButton, VaadinIcon.FILE_TREE, "Visualize Shapes Taxonomy");


        readShapesStatsButton.addClickListener(buttonClickEvent -> {
        });
        readShactorLogsButton.addClickListener(buttonClickEvent -> {
        });

        taxonomyVisualizationButton.addClickListener(buttonClickEvent -> {
            RouterLink link = new RouterLink("taxonomy-view", TaxonomyView.class);
            taxonomyVisualizationButton.getUI().ifPresent(ui -> ui.getPage().open(link.getHref()));
        });

        startPruningButton.addClickListener(buttonClickEvent -> {
            if (supportTextField.getValue().isEmpty() || confidenceTextField.getValue().isEmpty()) {
                Utils.notify("Please enter valid values!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            } else {
                beginPruning();
            }
        });
    }

    private void configureDefaultShapesDownloadButton() {
        Button button = new Button();
        Utils.setIconForButtonWithToolTip(button, VaadinIcon.DOWNLOAD, "Download Shapes");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper buttonWrapper;
        try {
            File file = new File(SelectionView.getDefaultShapesOutputFileAddress());
            ByteArrayInputStream stream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            buttonWrapper = new FileDownloadWrapper(new StreamResource(file.getName(), () -> stream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buttonWrapper.wrapComponent(button);
        actionButtonsHorizontalLayout.add(buttonWrapper);
    }

    private void beginPruning() {
        support = Integer.parseInt(supportTextField.getValue());
        confidence = (Double.parseDouble(confidenceTextField.getValue())) / 100;
        System.out.println(support + " - " + confidence);
        System.out.println(parser.shapesExtractor.getNodeShapes().size());
        shapesGrid.removeAllColumns();

        AtomicReference<List<NS>> nodeShapes = new AtomicReference<>(parser.shapesExtractor.getNodeShapes());

        pruningUtil.applyPruningFlags(nodeShapes.get(), support, confidence);
        pruningUtil.getDefaultStats(nodeShapes.get());
        pruningUtil.getStatsBySupport(nodeShapes.get());
        pruningUtil.getStatsByConfidence(nodeShapes.get());
        pruningUtil.getStatsByBoth(nodeShapes.get());

        this.prunedFileAddress = parser.extractSHACLShapesWithPruning(SelectionView.isFilteredClasses, confidence, support, SelectionView.chosenClasses); // extract shapes with pruning
        headingPieCharts.setVisible(true);
        headingNodeShapesAnalysis.setVisible(true);
        setupPieChartsDataWithDefaultStats(defaultShapesStatsPieChart, pruningUtil.getStatsDefault(), pruningUtil);
        setupPieChart(shapesStatsBySupportPieChart, pruningUtil.getStatsBySupport(), support, pruningUtil);
        setupPieChart(shapesStatsByConfidencePieChart, pruningUtil.getStatsByConfidence(), confidence, pruningUtil);
        setupPieChart(shapesStatsByBothPieChart, pruningUtil.getStatsByBoth(), support, confidence, pruningUtil);

        defaultShapesStatsPieChart.drawChart();
        shapesStatsBySupportPieChart.drawChart();
        shapesStatsByConfidencePieChart.drawChart();
        shapesStatsByBothPieChart.drawChart();

        setupNodeShapesGrid(nodeShapes.get(), support, confidence);
        //downloadPrunedShapesButton.setText("Download Reliable Shapes Pruned with Support: " + support + " and Confidence: " + Math.round(confidence * 100) + "%");

        downloadPrunedShapesButton.setVisible(true);
        setupFilterRadioGroup(vaadinRadioGroup);
        vaadinRadioGroup.setVisible(true);
        vaadinRadioGroup.addValueChangeListener(listener -> {
            if (listener.getValue() != null) {
                if (vaadinRadioGroup.getValue().equals("Above")) {
                    shapesGrid.setItems(positive(nodeShapes.get()));
                    shapesGrid.getDataProvider().refreshAll();
                }

                if (vaadinRadioGroup.getValue().equals("Below")) {
                    shapesGrid.setItems(negative(nodeShapes.get()));
                    shapesGrid.getDataProvider().refreshAll();
                }

                if (vaadinRadioGroup.getValue().equals("All")) {
                    System.out.println("Default");
                    shapesGrid.setItems(nodeShapes.get());
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
        //shapesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        shapesGrid.addColumn(NS::getLocalNameFromIri).setHeader(Utils.boldHeader("Node Shape")).setResizable(true).setAutoWidth(true).setComparator(NS::getPruneFlag);
        shapesGrid.addColumn(NS::getTargetClass).setHeader(Utils.boldHeader("Target Class")).setResizable(true).setResizable(true).setAutoWidth(true);
        shapesGrid.addColumn(NS::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(true).setSortable(true);
        shapesGrid.addColumn(NS::getCountPropertyShapes).setHeader(Utils.boldHeader("Count PS")).setResizable(true).setAutoWidth(true);
        //shapesGrid.addColumn(NS::getCountPsWithPruneFlag).setHeader(Utils.boldHeader("PS > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true);
        //shapesGrid.addColumn(NS::getCountPscWithPruneFlag).setHeader(Utils.boldHeader("PSc > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true);
        
        /*shapesGrid.addComponentColumn(ns -> createStatusIcon(String.valueOf(ns.getPruneFlag()))).setTooltipGenerator(ns -> {
            String val = "NS Support > (Support, Confidence) thresholds. Should not be removed";
            if (ns.getPruneFlag()) {
                val = "NS Support < (Support, Confidence) thresholds. Should be removed";
            }
            return val;
        }).setHeader(Utils.boldHeader("Prune NS");*/


        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            //double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithPruneFlag();
            //progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithSupportPruneFlag();
            //System.out.println(ns.getLocalNameFromIri());
            //System.out.println(ns.getCountPropertyShapes() + " - " + ns.getCountPsWithSupportPruneFlag() + " = " + psCountGreen);
            //System.out.println("division = " + psCountGreen / ns.getCountPropertyShapes());
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader((setHeaderWithInfoLogo(
                "PS Quality (by Support)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds."))).setResizable(true).setAutoWidth(true);


        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithConfidencePruneFlag();

            //System.out.println(ns.getLocalNameFromIri());
            //System.out.println(ns.getCountPropertyShapes() + " - " + ns.getCountPsWithConfidencePruneFlag() + " = " + psCountGreen);
            //System.out.println("division = " + psCountGreen / ns.getCountPropertyShapes());

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
        shapesGrid.setItems(nodeShapes);
        shapesGrid.addSelectionListener(selection -> {
            System.out.printf("Number of selected classes: %s%n", selection.getAllSelectedItems().size());
            downloadSelectedShapesButton.setVisible(true);
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
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES); //LUMO_COMPACT

        propertyShapesGrid.addColumn(PS::getLocalNameFromIri).setHeader(Utils.boldHeader("Property Shape")).setResizable(true).setAutoWidth(true).setComparator(PS::getPruneFlag);
        propertyShapesGrid.addColumn(PS::getPath).setHeader(Utils.boldHeader("Property Path")).setResizable(true).setAutoWidth(true);

        propertyShapesGrid.addColumn(PS::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(true).setSortable(true);
        propertyShapesGrid.addColumn(PS::getConfidenceInPercentage).setHeader(Utils.boldHeader("Confidence")).setResizable(true).setAutoWidth(true).setComparator(PS::getConfidence);

        //propertyShapesGrid.addColumn(PS::getNodeKind).setHeader(Utils.boldHeader("NodeKind");
        //propertyShapesGrid.addColumn(PS::getDataTypeOrClass).setHeader(Utils.boldHeader("Data Type or Class");

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

        ns.getPropertyShapes().sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        propertyShapesGrid.setItems(ns.getPropertyShapes());
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
