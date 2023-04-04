package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.utils.Constants;
import de.atextor.turtle.formatter.FormattingStyle;
import de.atextor.turtle.formatter.TurtleFormatter;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.query.BindingSet;
import shactor.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static shactor.utils.Utils.*;

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
@JsModule("./copytoclipboard.js")
@Route("/ps-view")

public class PsView extends LitTemplate {
    @Id("psConstraintsGrid")
    private Grid<PS> psConstraintsGrid;
    @Id("psOrItemsConstraintsGrid")
    private Grid<ShaclOrListItem> psOrItemsConstraintsGrid;
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
    @Id("copySyntaxButton")
    private Button copySyntaxButton;
    @Id("statusHorizontalLayout")
    private HorizontalLayout statusHorizontalLayout;

    NS nodeShape;
    PS propertyShape;
    GraphExplorer graphExplorer;
    TextArea descriptionArea;
    Model currNsPsModel;
    @Id("propCoverageInfoParagraph")
    private Paragraph propCoverageInfoParagraph;
    @Id("propCoverageQueryButton")
    private Button propCoverageQueryButton;

    public PsView() {
        graphExplorer = new GraphExplorer("http://10.92.0.34:7200/", "DBPEDIA_ML");
        nodeShape = ExtractionView.getCurrNS();
        propertyShape = ExtractionView.getCurrPS();

        setupNodeShapeInfo(infoHorizontalLayout);
        setupPropertyShapeInfo(infoHorizontalLayoutTwo);
        //setupTypesScopeOfPs(infoHorizontalLayoutTwo);
        setupStatus(statusHorizontalLayout);
        setupTextArea();
        setupGrid();
        extractEntitiesNotHavingFocusProperty();
        setupActionButtons();
    }

    private void setupNodeShapeInfo(HorizontalLayout hl) {
        //vl.add(new H4("Node Shape (NS) Info:"));
        hl.add(Utils.getReadOnlyTextField("Node Shape: ", nodeShape.getLocalNameFromIri()));
        hl.add(Utils.getReadOnlyTextField("No. of PS of :" + nodeShape.getLocalNameFromIri() + ": ", nodeShape.getCountPropertyShapes().toString()));
        hl.add(Utils.getReadOnlyTextField("Target Class: ", nodeShape.getTargetClass().toString()));
        hl.add(Utils.getReadOnlyTextField("Support Threshold: ", ExtractionView.getSupport().toString()));
        hl.add(Utils.getReadOnlyTextField("Confidence Threshold: ", ExtractionView.getConfidence() * 100 + "%"));
    }

    private void setupPropertyShapeInfo(HorizontalLayout hl) {
        //vl.add(new H4("Property Shape (PS) Info:"));
        hl.add(Utils.getReadOnlyTextField("PS PATH: ", propertyShape.getPath()));
        hl.add(Utils.getReadOnlyTextField("PS IRI: ", nodeShape.getIri().toString()));
    }

    private void setupStatus(HorizontalLayout hl) {
        if (propertyShape.getPruneFlag()) {
            Span status = new Span(createIcon(VaadinIcon.FILE_REMOVE), new Span("To be Pruned!"));
            status.getElement().getThemeList().add("badge error");
            status.setSizeFull();
            status.setHeightFull();
            hl.add(status);
        } else {
            Span status = new Span(createIcon(VaadinIcon.FLAG), new Span("Not to be Pruned!"));
            status.getElement().getThemeList().add("badge success");
            status.setSizeFull();
            hl.add(status);
        }
    }

    private void extractEntitiesNotHavingFocusProperty() {
        nodeShape.getTargetClass();
        nodeShape.getSupport();
        propertyShape.getPath();
        List<BindingSet> result = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractEntitiesNotHavingFocusProperty(nodeShape.getTargetClass(), propertyShape.getPath()));
        propCoverageInfoParagraph.setText("There are total " + nodeShape.getSupport() + " entities of " + nodeShape.getTargetClass().getLocalName() + " class, and " +
                " there exists " + result.size() + " entities not having " + propertyShape.getPath() + " property. Click the button 'Show Entities' below to retrieve the list of entities not having this property. " +
                "We also provide an option to insert this property for each entity (in case this is the case of missing information) by automatically constructing a query. ");
        propCoverageQueryButton.addClickListener(buttonClickEvent -> {
            createDialogueToShowEntitiesHavingMissingProperty(result);
        });
    }

    private void setupTypesScopeOfPs(HorizontalLayout hl) {
        HashMap<String, Integer> typeToEntityCount = new HashMap<>();
        //extract types
        List<BindingSet> resultTypes = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractTypesOfPs(nodeShape.getTargetClass().stringValue(), propertyShape.getPath()));
        for (BindingSet bindings : resultTypes) { //bindings : ?types
            typeToEntityCount.put(bindings.getValue("types").stringValue(), 0);
        }
        //extract entity count of types
        typeToEntityCount.keySet().forEach(type -> {
            List<BindingSet> resultEntityCount = graphExplorer.runSelectQuery(QueryUtil.buildQueryToComputeEntityCountForTypeOfPs(type, propertyShape.getPath()));
            for (BindingSet bindings : resultEntityCount) { //bindings : ?entityCount
                typeToEntityCount.put(type, Integer.parseInt(bindings.getValue("entityCount").stringValue()));
            }
        });
        Select<String> select = new Select<>();
        select.setLabel("Types Scope (other classes having this property)");
        select.setWidthFull();
        select.setPlaceholder(nodeShape.getTargetClass().stringValue() + " (" + typeToEntityCount.get(nodeShape.getTargetClass().stringValue()) + ") ");
        List<String> values = new ArrayList<>();
        Utils.sortMapDescending(typeToEntityCount).forEach((k, v) -> {
            values.add(k + " (" + v + ")");
        });
        select.setItems(values);
        hl.add(select);
    }

    private void setupGrid() {
        if (propertyShape.getHasOrList()) {
            psConstraintsGrid.setVisible(false);
            psOrItemsConstraintsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            psOrItemsConstraintsGrid.addColumn(ShaclOrListItem::getNodeKind).setHeader(Utils.boldHeader("sh:NodeKind")).setResizable(true).setAutoWidth(true);
            psOrItemsConstraintsGrid.addColumn(ShaclOrListItem::getDataTypeOrClass).setHeader(Utils.boldHeader("sh:Class or sh:dataType")).setResizable(true).setAutoWidth(true);
            psOrItemsConstraintsGrid.addColumn(ShaclOrListItem::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(false).setSortable(true);
            psOrItemsConstraintsGrid.addColumn(ShaclOrListItem::getConfidenceInPercentage).setHeader(Utils.boldHeader("Confidence")).setResizable(true).setAutoWidth(false).setComparator(ShaclOrListItem::getConfidence);

            //Declare an undefined flag
            boolean flag = false;
            for (ShaclOrListItem item : propertyShape.getShaclOrListItems()) {
                if (item.getDataTypeOrClass() == null || Objects.equals(item.getDataTypeOrClass(), "http://shaclshapes.org/undefined")) {
                    item.setDataTypeOrClass("Undefined");
                    flag = true;
                }
            }
            psOrItemsConstraintsGrid.addColumn(new ComponentRenderer<>(Button::new, (button, shaclOrListItem) -> {
                String objType = shaclOrListItem.getDataTypeOrClass();
                String nodeKind = shaclOrListItem.getNodeKind();
                button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
                if (nodeKind.equals("IRI")) {
                    button.addClickListener(e -> {
                        List<BindingSet> output;
                        if (objType.equals("Undefined")) {
                            output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractEntitiesHavingUndefinedShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath()));
                        } else {
                            output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractEntitiesHavingSpecificShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath(), shaclOrListItem.getDataTypeOrClass()));
                        }
                        createDialogueToShowEntitiesWithPropAndObject(output);
                    });

                    button.setIcon(new Icon(VaadinIcon.LIST));
                    button.setText("List Entities");
                }
            })).setHeader(setHeaderWithInfoLogo("Show Entities", "Retrieve list of entities having specified object type."));
            //FIXME: extract entities having specified object type (i.e., value of sh:class constraint)

            if (flag) {
                psOrItemsConstraintsGrid.addColumn(new ComponentRenderer<>(Button::new, (button, shaclOrListItem) -> {
                    String objType = shaclOrListItem.getDataTypeOrClass();
                    String nodeKind = shaclOrListItem.getNodeKind();
                    if (!objType.equals("Undefined")) {
                        button.setVisible(false);
                    }
                    button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
                    if (nodeKind.equals("IRI")) {
                        button.addClickListener(e -> {
                            if (objType.equals("Undefined")) {
                                List<BindingSet> output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractObjectsHavingUndefinedShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath()));
                                createDialogueToShowEntities(output);
                            }
                        });
                        button.setIcon(new Icon(VaadinIcon.LIST));
                        button.setText("List Objects (IRIs)");
                    }
                })).setHeader(setHeaderWithInfoLogo("Show Objects", "Retrieve list of objects (IRIs) having undefined type."));
            }
            psOrItemsConstraintsGrid.setItems(propertyShape.getShaclOrListItems());
        } else {
            psOrItemsConstraintsGrid.setVisible(false);

            //Declare an undefined flag
            boolean flag = false;
            if (propertyShape.getDataTypeOrClass() == null) {
                propertyShape.setDataTypeOrClass("Undefined");
                flag = true;
            }
            String nodeKind = propertyShape.getNodeKind();

            psConstraintsGrid.addColumn(PS::getPath).setHeader(Utils.boldHeader("Property Path")).setResizable(true).setAutoWidth(true);
            psConstraintsGrid.addColumn(PS::getNodeKind).setHeader(Utils.boldHeader("sh:NodeKind")).setResizable(true).setAutoWidth(true);
            psConstraintsGrid.addColumn(PS::getDataTypeOrClass).setHeader(Utils.boldHeader("sh:Class or sh:dataType")).setResizable(true).setAutoWidth(true);
            psConstraintsGrid.addColumn(PS::getSupport).setHeader(Utils.boldHeader("Support")).setResizable(true).setAutoWidth(true).setSortable(true);
            psConstraintsGrid.addColumn(PS::getConfidenceInPercentage).setHeader(Utils.boldHeader("Confidence")).setResizable(true).setAutoWidth(true).setComparator(PS::getConfidence);
            if (nodeKind.equals("IRI")) {
                psConstraintsGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ps) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        List<BindingSet> output;
                        if (ps.getDataTypeOrClass().equals("Undefined")) {
                            output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractEntitiesHavingUndefinedShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath()));
                        } else {
                            output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractEntitiesHavingSpecificShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath(), ps.getDataTypeOrClass()));
                        }
                        createDialogueToShowEntitiesWithPropAndObject(output);
                    });
                    button.setIcon(new Icon(VaadinIcon.LIST));
                    button.setText("List Entities");
                })).setHeader(setHeaderWithInfoLogo("Show Entities", "Retrieve list of entities having specified object type."));


                if (flag) {
                    psConstraintsGrid.addColumn(new ComponentRenderer<>(Button::new, (button, ps) -> {
                        if (!ps.getDataTypeOrClass().equals("Undefined")) {
                            button.setVisible(false);
                        }
                        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
                        button.addClickListener(e -> {
                            if (ps.getDataTypeOrClass().equals("Undefined")) {
                                List<BindingSet> output = graphExplorer.runSelectQuery(QueryUtil.buildQueryToExtractObjectsHavingUndefinedShClass(nodeShape.getTargetClass().stringValue(), propertyShape.getPath()));
                                createDialogueToShowEntities(output);
                            }
                        });
                        button.setIcon(new Icon(VaadinIcon.LIST));
                        button.setText("List Object (IRIs)");
                    })).setHeader(setHeaderWithInfoLogo("Show Objects", "Retrieve list of objects (IRIs) having undefined type."));
                }
            }
            psConstraintsGrid.setItems(propertyShape);
        }
    }

    private void setupTextArea() {
        this.psSyntaxTextArea.setValue(prepareModelForNsAndPs());
        this.copySyntaxButton.setIcon(VaadinIcon.COPY.create());
        this.copySyntaxButton.getElement().setProperty("title", "Does not work on Safari!");
        this.copySyntaxButton.addClickListener(e -> {
            String value = this.psSyntaxTextArea.getValue();
            System.out.println(value);
            UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", value);
        });
    }

    private void setupActionButtons() {
        //Button A
        buttonA.addClickListener(e -> this.generateDialogWithQueryForPropertyShape(nodeShape, propertyShape));

        //Button B
        buttonB.addClickListener(e -> this.validateEntitiesData(nodeShape, propertyShape));
    }

    private void validateEntitiesData(NS ns, PS ps) {
        //here query all the entities and the properties, store the results in a model and then validate against the shape.
        String sparqlQuery = QueryUtil.extractSparqlQueryWithFullEntitiesData(ns, ps);
        List<Triple> tripleList = queryGraph(sparqlQuery);
        Model dataModel = prepareJenaModelForValidation(tripleList);
        Graph dataGraph = dataModel.getGraph();
        Shapes shapes = Shapes.parse(currNsPsModel.getGraph());
        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);

        ShLib.printReport(report);
        if (report.conforms()) {
            notifyMessage("Conforms!");
        } else {
            notifyError("Does not conforms");
        }
        for (ReportEntry re : ShaclValidator.get().validate(shapes, dataGraph).getEntries()) {
            String line = re.source() + "|" + re.focusNode() + "|" + re.resultPath() + "|" + re.value() + "|" + re.sourceConstraintComponent() + "|" + re.message();
            System.out.println(line);
        }
    }

    // -------------------------    Methods   -----------------------------

    private void generateDialogWithQueryForPropertyShape(NS ns, PS ps) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Query");
        String sparqlQuery = QueryUtil.extractSparqlQuery(ns, ps);

        dialog.getHeader().add(createHeaderLayout(ps.getLocalNameFromIri()));
        createFooter(dialog);
        VerticalLayout dialogLayout = createDialogLayout(ps.getLocalNameFromIri(), sparqlQuery);
        dialog.add(dialogLayout);
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.open();
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
        descriptionArea = new TextArea("SPARQL Query");
        descriptionArea.setValue(sparqlQuery);

        VerticalLayout fieldLayout = new VerticalLayout(paragraph, nsTitle, psTitle, descriptionArea);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "1000px").set("max-width", "100%");

        return fieldLayout;
    }

    private void createFooter(Dialog dialog) {
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button executeQueryButton = new Button("Execute Query", e -> dialog.close());
        executeQueryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(executeQueryButton);

        executeQueryButton.addClickListener(buttonClickEvent -> {
            List<Triple> tripleList = queryGraph(descriptionArea.getValue());
            //simplifyOutput(tripleList);
            createDialogueToShowTriples(tripleList);
        });
    }

    private List<Triple> queryGraph(String query) {
        return graphExplorer.runQuery(query);
    }

    private Model prepareJenaModelForValidation(List<Triple> tripleList) {
        Model model = ModelFactory.createDefaultModel();
        for (Triple triple : tripleList) {
            Statement s = ResourceFactory.createStatement(
                    ResourceFactory.createResource(wab(triple.getSubject())),
                    ResourceFactory.createProperty(wab(triple.getPredicate())),
                    ResourceFactory.createResource(wab(triple.getObject()))
            );
            model.add(s);
        }
        System.out.println(model.size());
        System.out.println("model");
        return model;
    }

    //Wrap angle brackets
    private String wab(String val) {
        return "<" + val + "> ";
    }


    private String prepareModelForNsAndPs() {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
        model.setNsPrefix("qse", "http://shaclshapes.org/");

        Statement nsType = ResourceFactory.createStatement(ResourceFactory.createResource((this.nodeShape.getIri().toString())), ResourceFactory.createProperty((RDF.type.toString())), ResourceFactory.createResource((SHACL.NODE_SHAPE.toString())));
        Statement nsTarget = ResourceFactory.createStatement(ResourceFactory.createResource((this.nodeShape.getIri().toString())), ResourceFactory.createProperty((SHACL.TARGET_CLASS.toString())), ResourceFactory.createResource((this.nodeShape.getTargetClass().toString())));
        Statement nsPs = ResourceFactory.createStatement(ResourceFactory.createResource((this.nodeShape.getIri().toString())), ResourceFactory.createProperty((SHACL.PROPERTY.toString())), ResourceFactory.createResource((this.propertyShape.getIri().toString())));
        Statement psPath = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((SHACL.PATH.toString())), ResourceFactory.createResource((this.propertyShape.getPath())));
        Statement psType = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty(RDF.type.toString()), ResourceFactory.createResource((SHACL.PROPERTY_SHAPE.toString())));

        model.add(nsType);
        model.add(nsTarget);
        model.add(nsPs);
        model.add(psPath);
        model.add(psType);

        if (this.propertyShape.getHasOrList()) {
            RDFList list = model.createList(new RDFNode[]{});
            List<Resource> resources = new ArrayList<>();
            for (ShaclOrListItem item : this.propertyShape.getShaclOrListItems()) {
                Resource child = model.createResource();
                Statement psNodeKind = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource((SHACL.IRI.toString())));
                if (item.getDataTypeOrClass() != null) { //sometimes object type is not defined
                    Statement psNodeType;
                    if (item.getNodeKind().equals("IRI")) {
                        psNodeType = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.CLASS.toString())), ResourceFactory.createResource((item.getDataTypeOrClass())));
                    } else {
                        psNodeType = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.DATATYPE.toString())), ResourceFactory.createResource((item.getDataTypeOrClass())));
                    }
                    model.add(psNodeType);
                }
                Statement psSupport = ResourceFactory.createStatement(child, ResourceFactory.createProperty((Constants.SUPPORT)), ResourceFactory.createTypedLiteral(item.getSupport().toString()));
                Statement psConfidence = ResourceFactory.createStatement(child, ResourceFactory.createProperty((Constants.CONFIDENCE)), ResourceFactory.createTypedLiteral(item.getConfidence().toString()));
                model.add(psNodeKind);
                //model.add(psSupport);
                //model.add(psConfidence);
                resources.add(child);
            }
            for (Resource element : resources) { // add each of these resources onto the end of the list
                list = list.with(element);
            }
            model.add(ResourceFactory.createResource((this.propertyShape.getIri().toString())), model.createProperty(SHACL.OR.toString()), list); // relate the root to the list

        } else {

            if (this.propertyShape.getDataTypeOrClass() != null) {
                if (!this.propertyShape.getDataTypeOrClass().equals("Undefined")) {
                    Statement psNodeType = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((SHACL.DATATYPE.toString())), ResourceFactory.createResource((this.propertyShape.getDataTypeOrClass())));
                    model.add(psNodeType);
                }

            }
            if (this.propertyShape.getNodeKind().equals("IRI")) {
                Statement psNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.IRI.toString()));
                model.add(psNodeKind);
            }

            if (this.propertyShape.getNodeKind().equals("Literal")) {
                Statement psNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.LITERAL.toString()));
                model.add(psNodeKind);
            }

            Statement psSupport = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((Constants.SUPPORT)), ResourceFactory.createTypedLiteral(this.propertyShape.getSupport().toString()));
            Statement psConfidence = ResourceFactory.createStatement(ResourceFactory.createResource((this.propertyShape.getIri().toString())), ResourceFactory.createProperty((Constants.CONFIDENCE)), ResourceFactory.createTypedLiteral(this.propertyShape.getConfidence().toString()));

            //model.add(psSupport);
            //model.add(psConfidence);
        }

        this.currNsPsModel = model;
        OutputStream out = new ByteArrayOutputStream();
        TurtleFormatter formatter = new TurtleFormatter(FormattingStyle.DEFAULT);
        formatter.accept(model, out);
        return out.toString();
    }


    private void createDialogueToShowTriples(List<Triple> tripleList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Triples");

        //dialog.getFooter().add(createFilterButton(dialog));
        VerticalLayout dialogLayout = createDialogContentForShowingTriples(tripleList);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }

    private void createDialogueToShowEntitiesWithPropAndObject(List<BindingSet> tripleList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Entities (" + tripleList.size() + ")");
        //dialog.getFooter().add(createFilterButton(dialog));
        VerticalLayout dialogLayout = createDialogContentForShowingEntitiesWithPropAndObject(tripleList);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }

    private void createDialogueToShowEntities(List<BindingSet> tripleList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Entities (" + tripleList.size() + ")");

        //dialog.getFooter().add(createFilterButton(dialog));
        VerticalLayout dialogLayout = createDialogContentForShowingEntities(tripleList);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }

    private void createDialogueToShowEntitiesHavingMissingProperty(List<BindingSet> tripleList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Entities (" + tripleList.size() + ")");

        //dialog.getFooter().add(createFilterButton(dialog));
        VerticalLayout dialogLayout = createDialogContentForShowingEntitiesHavingMissingProperty(tripleList);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }

    private VerticalLayout createDialogContentForShowingEntitiesWithPropAndObject(List<BindingSet> result) {
        List<Triple> tripleList = new ArrayList<>();
        result.forEach(bindings -> {
            tripleList.add(new Triple(bindings.getBinding("subject").getValue().stringValue(),
                    bindings.getBinding("predicate").getValue().stringValue(),
                    bindings.getBinding("object").getValue().stringValue()));
        });

        Grid<Triple> grid = new Grid<>(Triple.class, false);

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Triple::getSubject).setHeader("Subject (Entity IRI)").setResizable(true).setSortable(true);
        grid.addColumn(Triple::getPredicate).setHeader("Property").setResizable(true).setSortable(true);
        grid.addColumn(Triple::getObject).setHeader("Object").setResizable(true).setSortable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, triple) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryToUpdateTriple(triple));
            button.setIcon(new Icon(VaadinIcon.EDIT));
        })).setHeader(setHeaderWithInfoLogo("Edit", "A SPARQL Query to edit this triple will be constructed."));


        grid.addColumn(new ComponentRenderer<>(Button::new, (button, triple) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryToRemoveTriple(triple));
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader(setHeaderWithInfoLogo("Delete", "A SPARQL Query to remove this triple will be constructed."));


        grid.getStyle().set("width", "1500px").set("max-width", "100%");
        grid.setItems(tripleList);

        VerticalLayout dialogLayout = new VerticalLayout(grid);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("min-width", "1500px").set("max-width", "100%").set("height", "100%");

        return dialogLayout;
    }

    private VerticalLayout createDialogContentForShowingEntities(List<BindingSet> tripleList) {
        List<Triple> subjectList = new ArrayList<>();
        tripleList.forEach(bindings -> {
            subjectList.add(new Triple(bindings.getBinding("val").getValue().stringValue(), "", ""));
        });

        Grid<Triple> grid = new Grid<>(Triple.class, false);

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Triple::getSubject).setHeader("Entity IRI").setResizable(true).setSortable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, triple) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryToAddType(triple));
            button.setIcon(new Icon(VaadinIcon.PLUS));
        })).setHeader(setHeaderWithInfoLogo("INSERT TYPE", "A SPARQL Query to insert type of this entity will be constructed."));


        grid.getStyle().set("width", "1500px").set("max-width", "100%");
        grid.setItems(subjectList);
        Paragraph paragraph = new Paragraph("IRIs having undefined type");
        VerticalLayout dialogLayout = new VerticalLayout(paragraph, grid);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("min-width", "1500px").set("max-width", "100%").set("height", "100%");

        return dialogLayout;
    }

    private VerticalLayout createDialogContentForShowingEntitiesHavingMissingProperty(List<BindingSet> tripleList) {
        List<Triple> subjectList = new ArrayList<>();
        tripleList.forEach(bindings -> {
            subjectList.add(new Triple(bindings.getBinding("entity").getValue().stringValue(), "", ""));
        });

        Grid<Triple> grid = new Grid<>(Triple.class, false);

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Triple::getSubject).setHeader("Entity IRI").setResizable(true).setSortable(true);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, triple) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> this.generateQueryToAddProperty(triple));
            button.setIcon(new Icon(VaadinIcon.PLUS));
        })).setHeader(setHeaderWithInfoLogo("INSERT Property", "A SPARQL Query to insert property for this entity will be constructed."));


        grid.getStyle().set("width", "1500px").set("max-width", "100%");
        grid.setItems(subjectList);
        Paragraph paragraph = new Paragraph("Entities not having property ' " + propertyShape.getPath() + "' are shown below. " +
                "If you want to insert this property for a given entity, click on the + icon to generate the INSERT Query. ");
        VerticalLayout dialogLayout = new VerticalLayout(paragraph, grid);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("min-width", "1500px").set("max-width", "100%").set("height", "100%");

        return dialogLayout;
    }


    private VerticalLayout createDialogContentForShowingTriples(List<Triple> tripleList) {
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

    private void generateQueryToAddProperty(Triple triple) {
        String insertPart = "INSERT { \n <" + triple.getSubject() + ">  <" + propertyShape.getPath() + ">  <VALUE_TO_ADD> \n } \n";
        String tripleClause = "WHERE { <"+ triple.getSubject()  +">  a  <"+nodeShape.getTargetClass().stringValue() + ">  } \n";
        String updateQuery = "\n" + insertPart + tripleClause + "\n\n";
        DialogUtil.getDialogWithHeaderAndFooter("SPARQL Query to add property for entity ", updateQuery, "Here is the insert query, you can update the desired value by replacing 'VALUE_TO_ADD' and copy this query to execute it on your graph!");
    }

    private void generateQueryToAddType(Triple triple) {
        String insertPart = "INSERT { \n <" + triple.getSubject() + ">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  <VALUE_TO_ADD> \n } \n";
        String tripleClause = "WHERE {} ";
        String updateQuery = "\n" + insertPart + tripleClause + "\n\n";
        DialogUtil.getDialogWithHeaderAndFooter("SPARQL Query to add type of IRI ", updateQuery, "Here is the insert query, you can update the desired value by replacing 'VALUE_TO_ADD' and copy this query to execute it on your graph!");
    }

    private void generateQueryToRemoveTriple(Triple triple) {
        String delPart = "DELETE { \n <" + triple.getSubject() + ">  <" + triple.getPredicate() + ">  <" + triple.getObject() + "> \n } \n";
        String tripleClause = " WHERE { \n  <" + triple.getSubject() + ">  <" + triple.getPredicate() + ">  <" + triple.getObject() + "> \n } ";
        String updateQuery = "\n\n" + delPart + tripleClause + "\n\n";

        DialogUtil.getDialogWithHeaderAndFooter("SPARQL Query to Delete Triple ", updateQuery, "Here is the delete query, you can copy this query to execute it on your graph!");
    }

    private void generateQueryToUpdateTriple(Triple triple) {
        String delPart = "DELETE { \n <" + triple.getSubject() + ">  <" + triple.getPredicate() + ">  <" + triple.getObject() + ">  \n} \n";
        String insertPart = "INSERT { \n <" + triple.getSubject() + ">  <" + triple.getPredicate() + ">  <VALUE_TO_REPLACE> \n } \n";
        String tripleClause = " WHERE { \n  <" + triple.getSubject() + ">  <" + triple.getPredicate() + ">  <" + triple.getObject() + "> \n } ";
        String updateQuery = "\n\n" + delPart + insertPart + tripleClause + "\n\n";

        DialogUtil.getDialogWithHeaderAndFooter("SPARQL Query to Update Entity Information ", updateQuery, "Here is the update query, you can update the desired value by replacing 'VALUE_TO_REPLACE' and copy this query to execute it on your graph!");
    }

    private String buildSyntaxForNsAndPs() {
        String nsType = wab(this.nodeShape.getIri().toString()) + " " + wab(RDF.type.toString()) + " " + wab(SHACL.NODE_SHAPE.toString());
        String nsTarget = wab(this.nodeShape.getIri().toString()) + " " + wab(SHACL.TARGET_CLASS.toString()) + " " + wab(this.nodeShape.getTargetClass().toString());
        String nsPs = wab(this.nodeShape.getIri().toString()) + " " + wab(SHACL.PROPERTY.toString()) + " " + wab(this.propertyShape.getIri().toString());

        String psPath = wab(this.propertyShape.getIri().toString()) + " " + wab(SHACL.PATH.toString()) + " " + wab(this.propertyShape.getPath());
        String psNodeKind = wab(this.propertyShape.getIri().toString()) + " " + wab(SHACL.NODE_KIND.toString()) + " " + wab(this.propertyShape.getNodeKind());
        String psNodeType = wab(this.propertyShape.getIri().toString()) + " " + wab(SHACL.DATATYPE.toString()) + " " + wab(this.propertyShape.getDataTypeOrClass());
        String psSupport = wab(this.propertyShape.getIri().toString()) + " " + wab(Constants.SUPPORT) + " " + this.propertyShape.getSupport();
        String psConfidence = wab(this.propertyShape.getIri().toString()) + " " + wab(Constants.CONFIDENCE) + " " + this.propertyShape.getConfidence();
        return nsType + " . \n" + nsTarget + " . \n" + nsPs + " . \n" + psPath + " .\n" + psNodeKind + " .\n" + psNodeType + " .\n" + psSupport + " .\n" + psConfidence + " .\n";
    }

    private static void simplifyOutput(List<Triple> tripleList) {
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
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }
}
