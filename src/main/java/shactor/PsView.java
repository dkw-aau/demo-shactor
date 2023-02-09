package shactor;

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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
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
import com.vaadin.flow.router.RouteAlias;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import shactor.utils.GraphExplorer;
import shactor.utils.Triple;
import shactor.utils.Utils;

import java.util.List;

import static shactor.utils.Utils.setHeaderWithInfoLogo;

/**
 * A Designer generated component for the ps-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("ps-view")
@RouteAlias("propertyShapeDashboard")
@CssImport(value = "./grid.css", themeFor = "vaadin-grid")
@CssImport(value = "./text-field.css", themeFor = "vaadin-text-field")
@JsModule("./ps-view.ts")
@Route("/ps-view")
public class PsView extends LitTemplate {
    @Id("psConstraintsGrid")
    private Grid<PS> psConstraintsGrid;
    @Id("psSyntaxTextArea")
    private TextArea psSyntaxTextArea;
    @Id("infoHorizontalLayout")
    private HorizontalLayout infoHorizontalLayout;
    @Id("infoHorizontalLayoutTwo")
    private HorizontalLayout infoHorizontalLayoutTwo;
    @Id("buttonA")
    private Button buttonA;
    @Id("buttonB")
    private Button buttonB;
    @Id("buttonC")
    private Button buttonC;

    NS nodeShape;
    PS propertyShape;
    GraphExplorer graphExplorer;

    public PsView() {
        graphExplorer = new GraphExplorer("http://10.92.0.34:7200/", "DBPEDIA_ML");
        nodeShape = ExtractionView.getCurrNS();
        propertyShape = ExtractionView.getCurrPS();

        setupNodeShapeInfo(infoHorizontalLayout);
        setupPropertyShapeInfo(infoHorizontalLayoutTwo);
        setupGrid();
        setupTextArea();
        setupActionButtons();
    }

    private void setupNodeShapeInfo(HorizontalLayout vl) {
        //vl.add(new H4("Node Shape (NS) Info:"));
        vl.add(Utils.getReadOnlyTextField("Node Shape: ", nodeShape.getLocalNameFromIri()));
        vl.add(Utils.getReadOnlyTextField("No. of Property Shapes (PS) of :" + nodeShape.getLocalNameFromIri() + ": ", nodeShape.getCountPropertyShapes().toString()));
        vl.add(Utils.getReadOnlyTextField("Target Class: ", nodeShape.getTargetClass().toString()));
    }

    private void setupPropertyShapeInfo(HorizontalLayout vl) {
        //vl.add(new H4("Property Shape (PS) Info:"));
        vl.add(Utils.getReadOnlyTextField("PS: ", propertyShape.getLocalNameFromIri()));
        vl.add(Utils.getReadOnlyTextField("PS IRI: ", nodeShape.getIri().toString()));
        vl.add(Utils.getReadOnlyTextField("Support : ", ExtractionView.getSupport().toString()));
        vl.add(Utils.getReadOnlyTextField("Confidence : ", ExtractionView.getConfidence() * 100 + "%"));
    }

    private void setupGrid() {
        //psConstraintsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        psConstraintsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER); //LUMO_COMPACT

        //psConstraintsGrid.addColumn(PS::getLocalNameFromIri).setHeader(new Html("<div style='font-weight: bold;'>Property Shape</div>")).setResizable(true).setAutoWidth(true).setComparator(PS::getPruneFlag);
        psConstraintsGrid.addColumn(PS::getPath).setHeader("Property Path").setResizable(true).setAutoWidth(true);
        psConstraintsGrid.addColumn(PS::getSupport).setHeader("Support").setResizable(true).setAutoWidth(true).setSortable(true);
        psConstraintsGrid.addColumn(PS::getConfidenceInPercentage).setHeader("Confidence").setResizable(true).setAutoWidth(true).setComparator(PS::getConfidence);

        psConstraintsGrid.addColumn(PS::getNodeKind).setHeader("Node Kind").setResizable(true).setAutoWidth(true);
        psConstraintsGrid.addColumn(PS::getDataTypeOrClass).setHeader("Node Type").setResizable(true).setAutoWidth(true);
        psConstraintsGrid.addColumn(new ComponentRenderer<>(ProgressBar::new, (progressBar, ps) -> {
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

        // set items (we have only one selected property shape
        psConstraintsGrid.setItems(propertyShape);
    }

    //TODO:
    private void setupTextArea() {
    }

    private void setupActionButtons() {
        //Button A
        buttonA.addClickListener(e -> this.generateQueryForPropertyShape(nodeShape, propertyShape));
    }

    // -------------------------    Methods   -----------------------------

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
        H6 nsTitle = new H6("NS: " + nodeShape.getLocalNameFromIri());
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
