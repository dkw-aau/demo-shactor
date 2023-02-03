package shactor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.qse.filebased.Parser;
import shactor.utils.*;

import static shactor.utils.ChartsUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Tag("extraction-view")
@JsModule("./extraction-view.ts")
@CssImport(value = "./grid.css", themeFor = "vaadin-grid")
@Route("/extraction-view")
public class ExtractionView extends LitTemplate {
    
    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;
    @Id("downloadShapesButton")
    private Button downloadShapesButton;
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
    GraphExplorer graphExplorer;
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
    
    
    public ExtractionView() {
        setupKnowledgeGraphStatsChart(knowledgeGraphStatsPieChart);
        //Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        parser = SelectionView.getParser();
        graphExplorer = new GraphExplorer("http://10.92.0.34:7200/", "DBPEDIA_ML");
        downloadSelectedShapesButton.setVisible(false);
        vaadinRadioGroup.setVisible(false);
        psVaadinRadioGroup.setVisible(false);
        headingPieCharts.setVisible(false);
        headingNodeShapesAnalysis.setVisible(false);
        shapesGrid.setVisible(false);
        propertyShapesGrid.setVisible(false);
        propertyShapesGridInfo.setVisible(false);
        downloadPrunedShapesButton.setVisible(false);
        
        Utils.setIconForButtonWithToolTip(downloadShapesButton, VaadinIcon.DOWNLOAD, "Download Shapes");
        Utils.setIconForButtonWithToolTip(readShapesStatsButton, VaadinIcon.BAR_CHART, "Read Shapes Statistics");
        Utils.setIconForButtonWithToolTip(readShactorLogsButton, VaadinIcon.TIMER, "Read SHACTOR extraction logs");
        Utils.setIconForButtonWithToolTip(taxonomyVisualizationButton, VaadinIcon.FILE_TREE, "Visualize Shapes Taxonomy");
        
        downloadShapesButton.addClickListener(buttonClickEvent -> {});
        readShapesStatsButton.addClickListener(buttonClickEvent -> {});
        readShactorLogsButton.addClickListener(buttonClickEvent -> {});
        taxonomyVisualizationButton.addClickListener(buttonClickEvent -> {});
        
        startPruningButton.addClickListener(buttonClickEvent -> {
            if (supportTextField.getValue().isEmpty() || confidenceTextField.getValue().isEmpty()) {
                Utils.notify("Please enter valid values!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            } else {
                beginPruning();
            }
        });
    }
    
    private void beginPruning() {
        Integer support = Integer.parseInt(supportTextField.getValue());
        Double confidence = (Double.parseDouble(confidenceTextField.getValue())) / 100;
        System.out.println(support + " - " + confidence);
        System.out.println(parser.shapesExtractor.getNodeShapes().size());
        shapesGrid.removeAllColumns();
        
        AtomicReference<List<NS>> nodeShapes = new AtomicReference<>(parser.shapesExtractor.getNodeShapes());
        
        pruningUtil.applyPruningFlags(nodeShapes.get(), support, confidence);
        pruningUtil.getDefaultStats(nodeShapes.get());
        pruningUtil.getStatsBySupport(nodeShapes.get());
        pruningUtil.getStatsByConfidence(nodeShapes.get());
        pruningUtil.getStatsByBoth(nodeShapes.get());
        
        parser.extractSHACLShapesWithPruning(SelectionView.isFilteredClasses, confidence, support, SelectionView.chosenClasses); // extract shapes with pruning
        headingPieCharts.setVisible(true);
        headingNodeShapesAnalysis.setVisible(true);
        setupPieChartsDataWithDefaultStats(defaultShapesStatsPieChart, pruningUtil.getStatsDefault(), pruningUtil);
        setupPieChart(shapesStatsBySupportPieChart, pruningUtil.getStatsBySupport(), support, pruningUtil);
        setupPieChart(shapesStatsByConfidencePieChart, pruningUtil.getStatsByConfidence(), confidence, pruningUtil);
        ;
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
            System.out.println(listener.getValue());
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
        });
    }
    
    private void setupFilterRadioGroup(RadioButtonGroup<String> vaadinRadioGroup) {
        vaadinRadioGroup.setItems("All", "Above", "Below");
        vaadinRadioGroup.setValue("All");
    }
    
    // -------------------------   Setup Grids   -----------------------------
    private void setupNodeShapesGrid(List<NS> nodeShapes, Integer support, Double confidence) {
        shapesGrid.setVisible(true);
        shapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        shapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        shapesGrid.addColumn(NS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>Node Shape</div>")).setResizable(true).setAutoWidth(true).setComparator(NS::getPruneFlag);
        shapesGrid.addColumn(NS::getTargetClass).setHeader("Target Class").setResizable(true).setResizable(true).setAutoWidth(true);
        shapesGrid.addColumn(NS::getSupport).setHeader("Support").setResizable(true).setAutoWidth(true).setSortable(true);
        shapesGrid.addColumn(NS::getCountPropertyShapes).setHeader("Count PS").setResizable(true).setAutoWidth(true);
        //shapesGrid.addColumn(NS::getCountPsWithPruneFlag).setHeader("PS > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true);
        //shapesGrid.addColumn(NS::getCountPscWithPruneFlag).setHeader("PSc > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true);
        
        /*shapesGrid.addComponentColumn(ns -> createStatusIcon(String.valueOf(ns.getPruneFlag()))).setTooltipGenerator(ns -> {
            String val = "NS Support > (Support, Confidence) thresholds. Should not be removed";
            if (ns.getPruneFlag()) {
                val = "NS Support < (Support, Confidence) thresholds. Should be removed";
            }
            return val;
        }).setHeader("Prune NS");*/
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            //double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithPruneFlag();
            //progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithSupportPruneFlag();
            System.out.println(ns.getLocalNameFromIri());
            System.out.println(ns.getCountPropertyShapes() + " - " + ns.getCountPsWithSupportPruneFlag() + " = " + psCountGreen);
            System.out.println("division = " + psCountGreen / ns.getCountPropertyShapes());
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader(setHeaderWithInfoLogo(
                "PS Quality (by Support)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds.")).setResizable(true).setAutoWidth(true);
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithConfidencePruneFlag();
            
            System.out.println(ns.getLocalNameFromIri());
            System.out.println(ns.getCountPropertyShapes() + " - " + ns.getCountPsWithConfidencePruneFlag() + " = " + psCountGreen);
            System.out.println("division = " + psCountGreen / ns.getCountPropertyShapes());
            
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader(setHeaderWithInfoLogo(
                "PS Quality (by Confidence)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds.")).setResizable(true).setAutoWidth(true);
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ns) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.setupPropertyShapesGrid(ns));
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
        
        currNodeShape = ns.getLocalNameFromIri();
        propertyShapesGridInfo.setVisible(true);
        propertyShapesGridInfo.setText("Property Shapes Analysis for " + currNodeShape);
        
        propertyShapesGrid.removeAllColumns();
        propertyShapesGrid.setVisible(true);
        
        propertyShapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES); //LUMO_COMPACT
        propertyShapesGrid.addColumn(PS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>Property Shape</div>")).setResizable(true).setAutoWidth(true).setComparator(PS::getPruneFlag);
        propertyShapesGrid.addColumn(PS::getPath).setHeader("Property Path").setResizable(true).setAutoWidth(true);
        ;
        propertyShapesGrid.addColumn(PS::getSupport).setHeader("Support").setResizable(true).setAutoWidth(true).setSortable(true);
        propertyShapesGrid.addColumn(PS::getConfidenceInPercentage).setHeader("Confidence").setResizable(true).setAutoWidth(true).setComparator(PS::getConfidence);
        
        //propertyShapesGrid.addColumn(PS::getNodeKind).setHeader("NodeKind");
        //propertyShapesGrid.addColumn(PS::getDataTypeOrClass).setHeader("Data Type or Class");
        
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
            button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.getPage().open(link.getHref())));
            
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
        
        setupFilterRadioGroup(psVaadinRadioGroup);
        psVaadinRadioGroup.setVisible(true);
        psVaadinRadioGroup.addValueChangeListener(listener -> {
            System.out.println(listener.getValue());
            if (psVaadinRadioGroup.getValue().equals("Above")) {
                propertyShapesGrid.setItems(positivePs(ns.getPropertyShapes()));
                propertyShapesGrid.getDataProvider().refreshAll();
            }
            
            if (psVaadinRadioGroup.getValue().equals("Below")) {
                propertyShapesGrid.setItems(negativePs(ns.getPropertyShapes()));
                propertyShapesGrid.getDataProvider().refreshAll();
            }
            
            if (psVaadinRadioGroup.getValue().equals("All")) {
                System.out.println("Default");
                propertyShapesGrid.setItems(ns.getPropertyShapes());
                propertyShapesGrid.getDataProvider().refreshAll();
            }
        });
        
        ns.getPropertyShapes().sort((d1, d2) -> d2.getSupport() - d1.getSupport());
        propertyShapesGrid.setItems(ns.getPropertyShapes());
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
    
    private static Component setHeaderWithInfoLogo(String headerTitle, String headerDetails) {
        Span span = new Span(headerTitle);
        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.getElement().setAttribute("title", headerDetails);
        icon.getStyle().set("height", "var(--lumo-font-size-m)").set("color", "var(--lumo-contrast-70pct)").set("margin-right", "10px");
        
        HorizontalLayout layout = new HorizontalLayout(span, icon);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        
        return layout;
    }
    
    private static void setClassNameToHighlightNodeShapesInRed(Grid<NS> shapesGrid) {
        shapesGrid.setClassNameGenerator(ns -> {
            if (ns.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
    }
    
    private void setClassNameToHighlightPropertyShapesInRed(Grid<PS> propertyShapesGrid) {
        propertyShapesGrid.setClassNameGenerator(ps -> {
            if (ps.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
    }
    
    
    private void generateQueryForPropertyShape(NS ns, PS ps) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Query");
        String sparqlQuery = extractSparqlQuery(ns, ps);
        
        dialog.getHeader().add(createHeaderLayout(ps.getLocalNameFromIri()));
        createFooter(dialog, sparqlQuery);
        VerticalLayout dialogLayout = createDialogLayout(ps.getLocalNameFromIri(), sparqlQuery);
        dialog.add(dialogLayout);
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.open();
    }
    
    private String extractSparqlQuery(NS ns, PS ps) {
        String selectQuery = """
                SELECT ?s ?p ?o WHERE {\s
                \t BIND( <PROPERTY> AS ?p) .\s
                \t ?s a <CLASS> .
                \t ?s  ?p ?o .
                }\s
                """;
        String constructQuery = """
                CONSTRUCT WHERE { \s
                \t ?s a <CLASS> . \s
                \t  ?s a ?types. \s
                \t ?s <PROPERTY> ?o . \s
                } \s
                """;
        
        constructQuery = constructQuery.replace("CLASS", ns.getTargetClass().toString());
        constructQuery = constructQuery.replace("PROPERTY", ps.getPath());
        
        return constructQuery;
    }
    
    
    // -------------------------   Helper Methods   -----------------------------
    
    
    // -------------------------   Dialogue Methods   -----------------------------
    
    
    private H2 createHeaderLayout(String title) {
        H2 headline = new H2("SPARQL Query to Extract Triples for " + title);
        headline.getStyle().set("padding-bottom", "0px");
        headline.addClassName("draggable");
        headline.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold").set("cursor", "move").set("padding", "var(--lumo-space-m) 0").set("flex", "1");
        return headline;
    }
    
    private VerticalLayout createDialogLayout(String psName, String sparqlQuery) {
        Paragraph paragraph = new Paragraph("SHACTOR has generated the following SPARQL query to be executed on the provided Knowledge Graph. This query will fetch the triples responsible for extracting the following SHACL constraint:");
        H6 nsTitle = new H6("NS: " + currNodeShape);
        H6 psTitle = new H6("PS: " + psName);
        
        nsTitle.getStyle().set("margin-top", "0px");
        TextArea descriptionArea = new TextArea("SPARQL Query");
        descriptionArea.setValue(sparqlQuery);
        
        VerticalLayout fieldLayout = new VerticalLayout(paragraph, nsTitle, psTitle, descriptionArea);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "1000px").set("max-width", "100%");
        
        return fieldLayout;
    }
    
    private void createFooter(Dialog dialog, String sparqlQuery) {
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button executeQueryButton = new Button("Execute Query", e -> dialog.close());
        executeQueryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(executeQueryButton);
        
        executeQueryButton.addClickListener(buttonClickEvent -> {
            queryGraph(sparqlQuery);
        });
    }
    
    private void createDialogueToShowTriples(List<Triple> tripleList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Triples");
        
        //dialog.getFooter().add(createFilterButton(dialog));
        VerticalLayout dialogLayout = createDialogContentForShowingTriples(dialog, tripleList);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }
    
    private VerticalLayout createDialogContentForShowingTriples(Dialog dialog, List<Triple> tripleList) {
        Grid<Triple> grid = new Grid<>(Triple.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Triple::getSubject).setHeader("Subject").setResizable(true).setSortable(true);
        grid.addColumn(Triple::getPredicate).setHeader("Predicate").setResizable(true).setSortable(true);
        grid.addColumn(Triple::getObject).setHeader("Object").setResizable(true).setSortable(true);
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, triple) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryToRemoveTriple(triple));
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader(setHeaderWithInfoLogo("Delete", "A SPARQL Query to remove this triple will be executed."));
        
        grid.getStyle().set("width", "2000px").set("max-width", "100%");
        grid.setItems(tripleList);
        
        VerticalLayout dialogLayout = new VerticalLayout(grid);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("min-width", "1500px").set("max-width", "100%").set("height", "100%");
        
        return dialogLayout;
    }
    
    private void generateQueryToRemoveTriple(Triple triple) {
        System.out.println("Hi, I will remove this triple: " + triple.toString());
    }
    
    private Icon createStatusIcon(String status) {
        boolean isAvailable = "true".equals(status);
        Icon icon;
        if (isAvailable) {
            icon = VaadinIcon.CHECK.create();
            icon.getElement().getThemeList().add("badge success");
        } else {
            icon = VaadinIcon.CLOSE_SMALL.create();
            icon.getElement().getThemeList().add("badge error");
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }
    
    
    private void queryGraph(String query) {
        List<Triple> tripleList = graphExplorer.runQuery(query);
        for (Triple triple : tripleList) {
            if (triple.getPredicate().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                triple.setPredicate("rdf:type");
            }
            if (triple.getPredicate().contains("http://dbpedia.org/ontology/")) {
                triple.setPredicate(triple.getPredicate().replace("http://dbpedia.org/ontology/", "dbo:"));
            }
            
            if (triple.getObject().contains("http://dbpedia.org/ontology/")) {
                triple.setObject(triple.getObject().replace("http://dbpedia.org/ontology/", "dbo:"));
            }
            
            if (triple.getSubject().contains("http://dbpedia.org/resource/")) {
                triple.setSubject(triple.getSubject().replace("http://dbpedia.org/resource/", "dbr:"));
            }
            if (triple.getObject().contains("http://dbpedia.org/resource/")) {
                triple.setObject(triple.getObject().replace("http://dbpedia.org/resource/", "dbr:"));
            }
            
            if (triple.getObject().contains("http://www.w3.org/2002/07/owl#")) {
                triple.setObject(triple.getObject().replace("http://www.w3.org/2002/07/owl#", "owl:"));
            }
        }
        createDialogueToShowTriples(tripleList);
    }
    
}
