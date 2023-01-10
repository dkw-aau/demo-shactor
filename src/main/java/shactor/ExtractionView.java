package shactor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
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

import java.util.Collections;
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
    
    
    static Parser parser;
    String currNodeShape;
    
    
    public ExtractionView() {
        parser = SelectionView.getParser();
        
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
                List<NS> nodeShapes = Collections.unmodifiableList(parser.shapesExtractor.getNodeShapes());
                applyPruningThresholds(nodeShapes, support, confidence);
                setupNodeShapesGrid(nodeShapes, support, confidence);
                downloadPrunedShapesButton.setText("Download SHACL Shapes Pruned with Support: " + support + " and Confidence: " + Math.round(confidence * 100) + "%");
                downloadPrunedShapesButton.setVisible(true);
            }
        });
    }
    
    private void setupNodeShapesGrid(List<NS> nodeShapes, Integer support, Double confidence) {
        shapesGrid.setVisible(true);
        shapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        shapesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        //shapesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        shapesGrid.addColumn(NS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>NS</div>"));
        shapesGrid.addColumn(NS::getTargetClass).setHeader("Target Class").setResizable(true);
        shapesGrid.addColumn(NS::getCountPropertyShapes).setHeader("Count PS");
        //shapesGrid.addColumn(NS::getPruneFlag).setHeader("To Prune");
        shapesGrid.addColumn(NS::getCountPsWithPruneFlag).setHeader("PS > (Support: " + support + ", Confidence: " + confidence + ")");
        shapesGrid.addColumn(NS::getCountPscWithPruneFlag).setHeader("PSc > (Support: " + support + ", Confidence: " + confidence + ")");
        
        /*shapesGrid.addComponentColumn(ns -> createStatusIcon(String.valueOf(ns.getPruneFlag()))).setTooltipGenerator(ns -> {
            String val = "NS Support > (Support, Confidence) thresholds. Should not be removed";
            if (ns.getPruneFlag()) {
                val = "NS Support < (Support, Confidence) thresholds. Should be removed";
            }
            return val;
        }).setHeader("Prune NS");*/
        
        
        shapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ns) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.setupPropertyShapesGrid(ns));
            button.setIcon(new Icon(VaadinIcon.OPEN_BOOK));
        })).setHeader(setHeaderWithInfoLogo("Show PS", "See PS of current NS"));
        
        shapesGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ns) -> {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.setId("quality-indicator-progress-bar");
            double value = ns.getCountPropertyShapes() - ns.getCountPsWithPruneFlag();
            System.out.println(value);
            progressBar.setValue(value / 100);
        })).setHeader(setHeaderWithInfoLogo("Quality Indicator", "This shows quality of NS in terms of PS left after pruning (green) and removed by pruning (red) provided user's support and confidence thresholds."));
        
        shapesGrid.setClassNameGenerator(ns -> {
            if (ns.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
        
        shapesGrid.setItems(nodeShapes);
    }
    
    private void setupPropertyShapesGrid(NS ns) {
        if (ns == null) return;
        
        currNodeShape = ns.getLocalNameFromIri();
        propertyShapesGridInfo.setVisible(true);
        propertyShapesGridInfo.setText("Showing Property Shapes of " + currNodeShape);
        
        propertyShapesGrid.removeAllColumns();
        propertyShapesGrid.setVisible(true);
        
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        propertyShapesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        
        propertyShapesGrid.addColumn(PS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>PS</div>")).setResizable(true);
        //propertyShapesGrid.addColumn(PS::getNodeKind).setHeader("NodeKind");
        //propertyShapesGrid.addColumn(PS::getDataTypeOrClass).setHeader("Data Type or Class");
        //propertyShapesGrid.addColumn(PS::getPruneFlag).setHeader("Prune Flag");
        propertyShapesGrid.addColumn(PS::getPath).setHeader("Path");
        propertyShapesGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ps) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryForPropertyShape(ns, ps));
            button.setIcon(new Icon(VaadinIcon.BULLSEYE));
        })).setHeader(setHeaderWithInfoLogo("Generate SPARQL Query", "The generated SPARQL query will fetch the triples responsible for having chosen PS as part of NS"));
        
        
        propertyShapesGrid.setClassNameGenerator(ps -> {
            if (ps.getPruneFlag()) {return "prune";} else {return "no-prune";}
        });
        
        propertyShapesGrid.setItems(ns.getPropertyShapes());
    }
    
    private void generateQueryForPropertyShape(NS ns, PS ps) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Query");
        
        dialog.getHeader().add(createHeaderLayout());
        createFooter(dialog);
        String sparqlQuery = extractSparqlQuery(ns, ps);
        VerticalLayout dialogLayout = createDialogLayout(ps.getLocalNameFromIri(), sparqlQuery);
        dialog.add(dialogLayout);
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.open();
    }
    
    
    private void applyPruningThresholds(List<NS> nodeShapes, Integer support, Double confidence) {
        for (NS currNS : nodeShapes) {
            List<PS> propertyShapes = currNS.getPropertyShapes();
            if (currNS.getSupport() < support) {
                currNS.setPruneFlag(true);
            }
            for (PS currPS : propertyShapes) {
                if (currPS.getSupport() != null && currPS.getConfidence() != null) {
                    currPS.getSupport();
                    currPS.getConfidence();
                    if (currPS.getSupport() < support && currPS.getConfidence() < confidence) {
                        //nodeShapesCopy.get(nsIndex).getPropertyShapes().remove(currPS);
                        currPS.setPruneFlag(true);
                    }
                    
                }
                
                if (currPS.getShaclOrListItems() != null) {
                    List<ShaclOrListItem> orItems = currPS.getShaclOrListItems();
                    orItems.forEach(item -> {
                        if (item.getSupport() != null && item.getConfidence() != null) {
                            if (item.getSupport() < support && item.getConfidence() < confidence) {
                                item.setPruneFlag(true);
                            }
                        }
                    });
                }
            }
        }
    }
    
    
    private String extractSparqlQuery(NS ns, PS ps) {
        String query = "SELECT * WHERE { \n" +
                "\t ?s a <CLASS> .\n" +
                "\t ?s  <PROPERTY> ?o .\n" +
                "} \n";
        query = query.replace("CLASS", ns.getTargetClass().toString());
        query = query.replace("PROPERTY", ps.getPath());
        return query;
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
    
    private H2 createHeaderLayout() {
        H2 headline = new H2("Query");
        headline.getStyle().set("padding-bottom", "0px");
        headline.addClassName("draggable");
        headline.getStyle().set("margin", "0").set("font-size", "1.5em")
                .set("font-weight", "bold").set("cursor", "move")
                .set("padding", "var(--lumo-space-m) 0").set("flex", "1");
        
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
        fieldLayout.getStyle().set("width", "600px").set("max-width", "100%");
        
        return fieldLayout;
    }
    
    private static void createFooter(Dialog dialog) {
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Execute Query", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
    }
}
