package shactor.utils;

import com.storedobject.chart.*;
import com.storedobject.chart.Position;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Legend;
import com.vaadin.flow.component.charts.model.Title;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.charts.model.style.FontWeight;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.charts.model.style.Style;
import cs.mg.MembershipGraph;
import cs.qse.common.encoders.StringEncoder;
import cs.qse.filebased.Parser;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * https://vaadin.com/directory/component/so-charts
 */
public class ChartsUtil {
    public static void setupKnowledgeGraphStatsChart(Chart knowledgeGraphStatsPieChart) {
        Configuration conf = knowledgeGraphStatsPieChart.getConfiguration();
        conf.setTitle(new Title("Knowledge Graph Statistics"));
        conf.getChart().setType(ChartType.COLUMN);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Triples", "Objects", "Literals", "Subjects", "Entities", "Properties", "Classes");

        Labels labels = new Labels();
        //labels.setRotation(-45);
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

        ListSeries series = new ListSeries("Data", 52281114, 19357319, 15269876, 15141546, 5823566, 1323, 427);
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

    public static void setupPieChartsDataWithDefaultStats(Chart chart, HashMap<String, String> statsMap, PruningUtil pruningUtil) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Default Shapes Analysis");
        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("NS : " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR)));
        series.add(new DataSeriesItem("PS : " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")), new SolidColor(Colors.PS_COLOR)));
        series.add(new DataSeriesItem("Literal PSc : " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")), new SolidColor(Colors.LC_COLOR)));
        series.add(new DataSeriesItem("Non-Literal PSc : " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")), new SolidColor(Colors.CC_COLOR)));
        conf.setSeries(series);

        conf.getChart().setStyledMode(true);
        Style styleFont = new Style();
        styleFont.setFontWeight(FontWeight.BOLD);
        conf.getChart().setStyle(styleFont);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        configurePieChart(plotOptionsPie);
        series.setPlotOptions(plotOptionsPie);
    }

    public static HashMap<String, Integer> preparePieChartsDataWithDefaultStats(HashMap<String, String> statsMap, PruningUtil pruningUtil) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("NS : " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")));
        map.put("PS : " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")));
        map.put("Literal PSc : " + statsMap.get("COUNT_LC"), Integer.parseInt(statsMap.get("COUNT_LC")));
        map.put("Non-Literal PSc : " + statsMap.get("COUNT_CC"), Integer.parseInt(statsMap.get("COUNT_CC")));
        return map;
    }

    public static void setupPieChart(Chart chart, HashMap<String, String> statsMap, Integer support, PruningUtil pruningUtil) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Shapes Analysis by Support");
        DataSeries series = new DataSeries();
        conf.getChart().setType(ChartType.PIE);

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
        Style styleFont = new Style();
        styleFont.setFontWeight(FontWeight.BOLD);
        conf.getChart().setStyle(styleFont);

        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        configurePieChart(plotOptionsPie);
        conf.setPlotOptions(plotOptionsPie);
        series.setPlotOptions(plotOptionsPie);
    }
    public static HashMap<String, Integer> preparePieChartDataForSupportAnalysis( HashMap<String, String> statsMap, Integer support, PruningUtil pruningUtil) {
        HashMap<String, Integer> map = new HashMap<>();
        int ns_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_NS")) - Integer.parseInt(statsMap.get("COUNT_NS"));
        map.put("NS > " + support + " = " + ns_green, ns_green);
        map.put("NS < " + support + " = " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")));

        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        map.put("PS > " + support + " = " + ps_green, ps_green);
        map.put("PS < " + support + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")));
        return map;
    }
    public static void setupPieChart(Chart chart, HashMap<String, String> statsMap, Double confidence, PruningUtil pruningUtil) {
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
        Style styleFont = new Style();
        styleFont.setFontWeight(FontWeight.BOLD);
        conf.getChart().setStyle(styleFont);

        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        configurePieChart(plotOptionsPie);
        series.setPlotOptions(plotOptionsPie);
    }
    public static HashMap<String, Integer> preparePieChartDataForConfidenceAnalysis(HashMap<String, String> statsMap, Double confidence, PruningUtil pruningUtil) {
        HashMap<String, Integer> map = new HashMap<>();
        int c = (int) (confidence * 100);
        String confPercent = c + "%";
        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        map.put("PS > " + confPercent + " = " + ps_green, ps_green);
        map.put("PS < " + confPercent + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")));
        return map;
    }
    public static void setupPieChart(Chart chart, HashMap<String, String> statsMap, Integer support, Double confidence, PruningUtil pruningUtil) {
        Configuration conf = chart.getConfiguration();
        conf.setTitle("By Support and Confidence");
        DataSeries series = new DataSeries();
        int c = (int) (confidence * 100);
        String confPercent = c + "%";
    /*
        int ns_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_NS")) - Integer.parseInt(statsMap.get("COUNT_NS"));
        series.add(new DataSeriesItem("NS > " + "(" + support + ", " + confPercent + ") " + " = " + ns_green, ns_green, new SolidColor(Colors.NS_COLOR)));
        series.add(new DataSeriesItem("NS < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_NS"), Integer.parseInt(statsMap.get("COUNT_NS")), new SolidColor(Colors.NS_COLOR_RED)));
    */
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
        Style styleFont = new Style();
        styleFont.setFontWeight(FontWeight.BOLD);
        conf.getChart().setStyle(styleFont);
        conf.setSeries(series);
        conf.getChart().setStyledMode(true);
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        configurePieChart(plotOptionsPie);
        series.setPlotOptions(plotOptionsPie);
    }
    public static HashMap<String, Integer> preparePieChartDataForSupportAndConfidenceAnalysis(HashMap<String, String> statsMap, Integer support, Double confidence, PruningUtil pruningUtil) {
        HashMap<String, Integer> map = new HashMap<>();
        int c = (int) (confidence * 100);
        String confPercent = c + "%";
        int ps_green = Integer.parseInt(pruningUtil.getStatsDefault().get("COUNT_PS")) - Integer.parseInt(statsMap.get("COUNT_PS"));
        map.put("PS > " + "(" + support + ", " + confPercent + ") " + " = " + ps_green, ps_green);
        map.put("PS < " + "(" + support + ", " + confPercent + ") " + " = " + statsMap.get("COUNT_PS"), Integer.parseInt(statsMap.get("COUNT_PS")));
        return map;
    }

    public static void configurePieChart(PlotOptionsPie plotOptionsPie) {
        Style style = new Style();
        style.setFontSize("16px");
        plotOptionsPie.getDataLabels().setStyle(style);
        plotOptionsPie.setAllowPointSelect(true);
        plotOptionsPie.setCursor(Cursor.POINTER);
        plotOptionsPie.setShowInLegend(true);
        plotOptionsPie.setSize("60%");
    }

    /*
    https://vaadin.com/directory/component/so-charts
     */
    public static SOChart buildBarChartUsingDbpediaStats() {
        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("95%", "100%");
        CategoryData labels = new CategoryData("Triples", "Objects", "Literals", "Subjects", "Entities", "Properties", "Classes");
        Data data = new Data(52281114, 19357319, 15269876, 15141546, 5823566, 1323, 427);
        NightingaleRoseChart rc = new NightingaleRoseChart(labels, data);
        //Position p = new Position();
        //p.setTop(Size.percentage(25));
        //rc.setPosition(p);
        soChart.disableDefaultLegend();
        soChart.add(rc);
        return soChart;
    }

    public static SOChart buildPieChart(HashMap<String, Integer> map) {
        SOChart soChart = new SOChart();

        soChart.setSize("350px", "200px");
        CategoryData cd = new CategoryData();
        Data values = new Data();
        map.forEach((k, v) -> {
            cd.add(k);
            values.add(v);
        });
        PieChart pc = new PieChart();
        pc.setItemNames(cd);

        Position p = new Position();
        p.setTop(Size.percentage(30));
        pc.setPosition(p);
        pc.setData(values);
        soChart.add(pc);
        return soChart;
    }


    public static SOChart buildTaxonomyTree(Parser parser) {
        SOChart soChart = new SOChart();
        soChart.setSize("100%", "100%");
        StringEncoder encoder = parser.getStringEncoder();
        TreeChart tc = new TreeChart();
        //ItemStyle itemStyle = new ItemStyle();
        Orientation orientation = new Orientation();
        orientation.topToBottom();
        tc.setOrientation(orientation);

        parser.createMembershipGraph();
        MembershipGraph mg = parser.mg;
        DefaultDirectedGraph<Integer, DefaultEdge> directedGraph = mg.getMembershipGraph();


        TreeData td = new TreeData(encoder.decode(mg.getMembershipGraphRootNode()));
        tc.setTreeData(td);
        directedGraph.removeVertex(mg.getMembershipGraphRootNode());
        Set<Integer> vertices = directedGraph.vertexSet();
        vertices.forEach(vertex -> {
            td.add(new TreeData(getLastWord(encoder.decode(vertex))));
        });

        directedGraph.iterables().edges().forEach(edge -> {
            int source = directedGraph.getEdgeTarget(edge);
            int target = directedGraph.getEdgeSource(edge);
            td.get(source).add(td.get(target));
        });
        soChart.add(tc, new com.storedobject.chart.Title("Taxonomy"));
        return soChart;
    }

    public static String getLastWord(String iri) {
        String[] parts = iri.split("/");
        return parts[parts.length - 1];
    }


}
