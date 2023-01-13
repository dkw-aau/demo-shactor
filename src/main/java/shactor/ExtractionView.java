package shactor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.charts.model.style.Style;
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
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.qse.filebased.Parser;
import shactor.utils.Colors;
import shactor.utils.GraphExplorer;
import shactor.utils.PruningUtil;
import shactor.utils.Triple;

import java.util.HashMap;
import java.util.List;


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
    
    public ExtractionView() {
        setupKnowledgeGraphStatsChart();
        
        parser = SelectionView.getParser();
        graphExplorer = new GraphExplorer("http://130.226.98.152:7200", "DBPEDIA_ML");
        downloadSelectedShapesButton.setVisible(false);
        shapesGrid.setVisible(false);
        propertyShapesGrid.setVisible(false);
        propertyShapesGridInfo.setVisible(false);
        downloadPrunedShapesButton.setVisible(false);
        
        downloadShapesButton.addClickListener(buttonClickEvent -> {});
        
        readShapesStatsButton.addClickListener(buttonClickEvent -> {});
        
        readShactorLogsButton.addClickListener(buttonClickEvent -> {});
        
        startPruningButton.addClickListener(buttonClickEvent -> {
            if (supportTextField.getValue().isEmpty() || confidenceTextField.getValue().isEmpty()) {
                notify("Please enter valid values!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            } else {
                Integer support = Integer.parseInt(supportTextField.getValue());
                Double confidence = (Double.parseDouble(confidenceTextField.getValue())) / 100;
                System.out.println(support + " - " + confidence);
                System.out.println(parser.shapesExtractor.getNodeShapes().size());
                shapesGrid.removeAllColumns();
                
                List<NS> nodeShapes = parser.shapesExtractor.getNodeShapes();
                
                pruningUtil.applyPruningFlags(nodeShapes, support, confidence);
                pruningUtil.getDefaultStats(nodeShapes);
                pruningUtil.getStatsBySupport(nodeShapes);
                pruningUtil.getStatsByConfidence(nodeShapes);
                pruningUtil.getStatsByBoth(nodeShapes);
                
                parser.extractSHACLShapesWithPruning(SelectionView.isFilteredClasses, confidence, support, SelectionView.chosenClasses); // extract shapes with pruning
                
                setupPieChartsDataWithDefaultStats(defaultShapesStatsPieChart, pruningUtil.getStatsDefault());
                setupPieChart(shapesStatsBySupportPieChart, pruningUtil.getStatsBySupport(), support);
                setupPieChart(shapesStatsByConfidencePieChart, pruningUtil.getStatsByConfidence(), confidence);
                setupPieChart(shapesStatsByBothPieChart, pruningUtil.getStatsByBoth(), support, confidence);
                
                defaultShapesStatsPieChart.drawChart();
                shapesStatsBySupportPieChart.drawChart();
                shapesStatsByConfidencePieChart.drawChart();
                shapesStatsByBothPieChart.drawChart();
                
                setupNodeShapesGrid(nodeShapes, support, confidence);
                //downloadPrunedShapesButton.setText("Download Reliable Shapes Pruned with Support: " + support + " and Confidence: " + Math.round(confidence * 100) + "%");
                downloadPrunedShapesButton.setVisible(true);
            }
        });
    }
    
    
    private void setupKnowledgeGraphStatsChart() {
        Configuration conf = knowledgeGraphStatsPieChart.getConfiguration();
        conf.setTitle(new Title("Knowledge Graph Statistics"));
        conf.getChart().setType(ChartType.COLUMN);
        
        XAxis xAxis = new XAxis();
        xAxis.setCategories("Triples", "Literals", "Objects", "Subjects", "Entities", "Properties", "Classes");
        
        Labels labels = new Labels();
        labels.setRotation(-45);
        labels.setAlign(HorizontalAlign.CENTER);
        Style style = new Style();
        style.setFontSize("16px");
        style.setColor(new SolidColor("#041E42"));
        labels.setStyle(style);
        xAxis.setLabels(labels);
        conf.addxAxis(xAxis);
        
        YAxis yAxis = new YAxis();
        Labels labelsYaxis = new Labels();
        labelsYaxis.setStyle(style);
        yAxis.setLabels(labelsYaxis);
        yAxis.setTitle("Count");
        yAxis.getTitle().setStyle(style);
        conf.addyAxis(yAxis);
        
        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);
        
        //Tooltip tooltip = new Tooltip();
        //tooltip.setFormatter("'<b>'+ this.x +'</b><br/>'+'Population in 2008: '" + "+ Highcharts.numberFormat(this.y, 1) +' millions'");
        //conf.setTooltip(tooltip);
        
        ListSeries series = new ListSeries("Data", 52000000, 28000000, 19000000, 15000000, 5000000, 1323, 427);
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColor(new SolidColor(Colors.LC_COLOR));
        Style styleFont = new Style();
        styleFont.setFontSize("16px");
        styleFont.setColor(new SolidColor("#041E42"));
        plotOptionsColumn.getDataLabels().setStyle(styleFont);
        plotOptionsColumn.setDataLabels(dataLabels);
        series.setPlotOptions(plotOptionsColumn);
        conf.addSeries(series);
        
        
        knowledgeGraphStatsPieChart.drawChart();
    }
    
    private void setupPieChartsDataWithDefaultStats(Chart chart, HashMap<String, String> statsMap) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Default Shapes Analysis");
        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("NS : " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR)));
        series.add(new DataSeriesItem("PS : " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")), new SolidColor(Colors.PS_COLOR)));
        series.add(new DataSeriesItem("Literal PSc : " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")), new SolidColor(Colors.LC_COLOR)));
        series.add(new DataSeriesItem("Non-Literal PSc : " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")), new SolidColor(Colors.CC_COLOR)));
        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        Style style = new Style();
        style.setFontSize("16px");
        plotOptionsPie.getDataLabels().setStyle(style);
        series.setPlotOptions(plotOptionsPie);
    }
    
    private void setupPieChart(Chart chart, HashMap<String, String> statsMap, Integer support) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Shapes Analysis by Support");
        DataSeries series = new DataSeries();
        
        int ns_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_NS")) - Integer.parseInt(statsMap.get("COUNT_NS"));
        series.add(new DataSeriesItem("NS > " + support + " = " + ns_green, ns_green, new SolidColor(Colors.NS_COLOR)));
        series.add(new DataSeriesItem("NS < " + support + " = " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR_RED)));
        
        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        series.add(new DataSeriesItem("PS > " + support + " = " + ps_green, ps_green, new SolidColor(Colors.PS_COLOR)));
        series.add(new DataSeriesItem("PS < " + support + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")), new SolidColor(Colors.PS_COLOR_RED)));
        
        /*int literal_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_LC")) - Integer.parseInt(statsMap.get("COUNT_LC"));
        series.add(new DataSeriesItem("Literal PSc > " + support + " = " + literal_green, literal_green, new SolidColor(Colors.LC_COLOR)));
        series.add(new DataSeriesItem("Literal PSc < " + support + " = " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")), new SolidColor(Colors.LC_COLOR_RED)));
        
        int nonLiteral_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_CC")) - Integer.parseInt(statsMap.get("COUNT_CC"));
        series.add(new DataSeriesItem("Non-Literal PSc > " + support + " = " + nonLiteral_green, nonLiteral_green, new SolidColor(Colors.CC_COLOR)));
        series.add(new DataSeriesItem("Non-Literal PSc < " + support + " = " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")), new SolidColor(Colors.CC_COLOR_RED)));
        */
        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        Style style = new Style();
        style.setFontSize("16px");
        plotOptionsPie.getDataLabels().setStyle(style);
        series.setPlotOptions(plotOptionsPie);
    }
    
    private void setupPieChart(Chart chart, HashMap<String, String> statsMap, Double confidence) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Shapes Analysis by Confidence");
        DataSeries series = new DataSeries();
        int c = (int) (confidence * 100);
        String confPercent = c + "%";
        //int ns_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_NS")) - Integer.parseInt(statsMap.get("COUNT_NS"));
        //series.add(new DataSeriesItem("NS > " + confPercent + " = " + ns_green, ns_green, new SolidColor(Colors.NS_COLOR)));
        //series.add(new DataSeriesItem("NS < " + confPercent + " = " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR_RED)));
        
        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        series.add(new DataSeriesItem("PS > " + confPercent + " = " + ps_green, ps_green, new SolidColor(Colors.PS_COLOR)));
        series.add(new DataSeriesItem("PS < " + confPercent + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")), new SolidColor(Colors.PS_COLOR_RED)));
        
        /*int literal_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_LC")) - Integer.parseInt(statsMap.get("COUNT_LC"));
        series.add(new DataSeriesItem("Literal PSc > " + confPercent + " = " + literal_green, literal_green, new SolidColor(Colors.LC_COLOR)));
        series.add(new DataSeriesItem("Literal PSc < " + confPercent + " = " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")), new SolidColor(Colors.LC_COLOR_RED)));
        
        int nonLiteral_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_CC")) - Integer.parseInt(statsMap.get("COUNT_CC"));
        series.add(new DataSeriesItem("Non-Literal PSc > " + confPercent + " = " + nonLiteral_green, nonLiteral_green, new SolidColor(Colors.CC_COLOR)));
        series.add(new DataSeriesItem("Non-Literal PSc < " + confPercent + " = " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")), new SolidColor(Colors.CC_COLOR_RED)));
        */
        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        Style style = new Style();
        style.setFontSize("16px");
        plotOptionsPie.getDataLabels().setStyle(style);
        series.setPlotOptions(plotOptionsPie);
    }
    
    private void setupPieChart(Chart chart, HashMap<String, String> statsMap, Integer support, Double confidence) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Shapes Analysis by Support and Confidence");
        DataSeries series = new DataSeries();
        int c = (int) (confidence * 100);
        String confPercent = c + "%";
    
        int ns_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_NS")) - Integer.parseInt(statsMap.get("COUNT_NS"));
        series.add(new DataSeriesItem("NS > " + "(" + support + ", " + confPercent + ") " + " = " + ns_green, ns_green, new SolidColor(Colors.NS_COLOR)));
        series.add(new DataSeriesItem("NS < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR_RED)));
    
        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        series.add(new DataSeriesItem("PS > " + "(" + support + ", " + confPercent + ") " + " = " + ps_green, ps_green, new SolidColor(Colors.PS_COLOR)));
        series.add(new DataSeriesItem("PS < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")), new SolidColor(Colors.PS_COLOR_RED)));
    
       /* int literal_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_LC")) - Integer.parseInt(statsMap.get("COUNT_LC"));
        series.add(new DataSeriesItem("Literal PSc > " + "(" + support + ", " + confPercent + ") " + " = " + literal_green, literal_green, new SolidColor(Colors.LC_COLOR)));
        series.add(new DataSeriesItem("Literal PSc < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")), new SolidColor(Colors.LC_COLOR_RED)));
    
        int nonLiteral_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_CC")) - Integer.parseInt(statsMap.get("COUNT_CC"));
        series.add(new DataSeriesItem("Non-Literal PSc > " + "(" + support + ", " + confPercent + ") " + " = " + nonLiteral_green, nonLiteral_green, new SolidColor(Colors.CC_COLOR)));
        series.add(new DataSeriesItem("Non-Literal PSc < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")), new SolidColor(Colors.CC_COLOR_RED)));
    */
        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        Style style = new Style();
        style.setFontSize("16px");
        plotOptionsPie.getDataLabels().setStyle(style);
        series.setPlotOptions(plotOptionsPie);
    }
    
    
    private void setupNodeShapesGrid(List<NS> nodeShapes, Integer support, Double confidence) {
        shapesGrid.setVisible(true);
        shapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        shapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        shapesGrid.addColumn(NS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>Node Shape</div>")).setResizable(true).setAutoWidth(true).setFlexGrow(0).setComparator(NS::getPruneFlag);
        shapesGrid.addColumn(NS::getTargetClass).setHeader("Target Class").setResizable(true).setResizable(true).setAutoWidth(true).setFlexGrow(0);
        shapesGrid.addColumn(NS::getSupport).setHeader("Support").setResizable(true).setAutoWidth(true).setFlexGrow(0).setSortable(true);
        shapesGrid.addColumn(NS::getCountPropertyShapes).setHeader("Count PS").setResizable(true).setAutoWidth(true).setFlexGrow(0);
        //shapesGrid.addColumn(NS::getCountPsWithPruneFlag).setHeader("PS > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true).setFlexGrow(0);
        //shapesGrid.addColumn(NS::getCountPscWithPruneFlag).setHeader("PSc > (Supp: " + support + ", Conf: " + confidence + ")").setResizable(true).setFlexGrow(0);
        
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
                "Quality Indicator for PS (by Support)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds.")).setResizable(true).setAutoWidth(true).setFlexGrow(0);
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double psCountGreen = ns.getCountPropertyShapes() - ns.getCountPsWithConfidencePruneFlag();
            
            System.out.println(ns.getLocalNameFromIri());
            System.out.println(ns.getCountPropertyShapes() + " - " + ns.getCountPsWithConfidencePruneFlag() + " = " + psCountGreen);
            System.out.println("division = " + psCountGreen / ns.getCountPropertyShapes());
            
            progressBar.setValue(psCountGreen / ns.getCountPropertyShapes());
        })).setHeader(setHeaderWithInfoLogo(
                "Quality Indicator for PS (by Confidence)",
                "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds.")).setResizable(true).setAutoWidth(true).setFlexGrow(0);
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ns) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.setupPropertyShapesGrid(ns));
            button.setIcon(new Icon(VaadinIcon.OPEN_BOOK));
            button.setText("Open List of PS");
        })).setHeader(setHeaderWithInfoLogo("Show PS", "See PS of current NS"));
        
        
        shapesGrid.setClassNameGenerator(ns -> {
            if (ns.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
        
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
        propertyShapesGridInfo.setText("Showing Property Shapes of " + currNodeShape);
        
        propertyShapesGrid.removeAllColumns();
        propertyShapesGrid.setVisible(true);
        
        propertyShapesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES); //LUMO_COMPACT
        propertyShapesGrid.addColumn(PS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>Property Shape</div>")).setResizable(true).setAutoWidth(true).setFlexGrow(0).setComparator(PS::getPruneFlag);
        propertyShapesGrid.addColumn(PS::getPath).setHeader("Property Path");
        propertyShapesGrid.addColumn(PS::getSupport).setHeader("Support").setResizable(true).setAutoWidth(true).setFlexGrow(0).setSortable(true);
        propertyShapesGrid.addColumn(PS::getConfidenceInPercentage).setHeader("Confidence").setResizable(true).setAutoWidth(true).setFlexGrow(0).setComparator(PS::getConfidence);
        
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
        })).setHeader(setHeaderWithInfoLogo("Quality Indicator (by Confidence)", " This shows")).setResizable(true).setAutoWidth(true).setFlexGrow(0);
        
        
        propertyShapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ps) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryForPropertyShape(ns, ps));
            button.setIcon(new Icon(VaadinIcon.EYE));
            button.setText("Generate SPARQL Query and Fetch Triples");
        })).setHeader(setHeaderWithInfoLogo("Action", "The generated SPARQL query will fetch the triples responsible for having chosen PS as part of NS"));
        
        
        propertyShapesGrid.setClassNameGenerator(ps -> {
            if (ps.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
        
        
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
        
        propertyShapesGrid.setItems(ns.getPropertyShapes());
        
        //propertyShapesGrid.addColumn(PS::getNodeKind).setHeader("NodeKind");
        //propertyShapesGrid.addColumn(PS::getDataTypeOrClass).setHeader("Data Type or Class");
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
                \t ?s <PROPERTY> ?o . \s
                } \s
                """;
        
        constructQuery = constructQuery.replace("CLASS", ns.getTargetClass().toString());
        constructQuery = constructQuery.replace("PROPERTY", ps.getPath());
        
        return constructQuery;
    }
    
    
    private void queryGraph(String query) {
        List<Triple> tripleList = graphExplorer.runQuery(query);
        createDialogueToShowTriples(tripleList);
    }
    
    // -------------------------   Helper Methods   -----------------------------
    
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
    
    private static void notify(String message, NotificationVariant notificationVariant, Notification.Position position) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(position);
    }
    
    
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
}
