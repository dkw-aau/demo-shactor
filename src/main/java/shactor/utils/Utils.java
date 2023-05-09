package shactor.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import cs.utils.Tuple2;
import de.atextor.turtle.formatter.FormattingStyle;
import de.atextor.turtle.formatter.TurtleFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static VerticalLayout getVerticalLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return verticalLayout;
    }

    public static TextField getTextField(String label) {
        TextField textField = new TextField();
        textField.setWidth("50%");
        textField.setLabel(label);
        return textField;
    }

    public static Paragraph getParagraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        paragraph.setClassName("bold-paragraph");
        return paragraph;
    }

    public static TextField getReadOnlyTextField(String label, String value) {
        TextField textField = new TextField();
        textField.setReadOnly(true);
        textField.setLabel(label);
        textField.setValue(value);
        textField.setWidth("50%");
        textField.addThemeName("label-design");
        return textField;
    }


    public static Button getPrimaryButton(String label) {
        Button primaryButton = new Button(label);
        primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return primaryButton;
    }

    public static Button getSecondaryButton(String label) {
        Button primaryButton = new Button(label);
        primaryButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        return primaryButton;
    }

    public static RadioButtonGroup<String> getRadioButtonGroup(String label, List<String> items) {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setWidth("50%");
        //radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel(label);
        radioGroup.setItems(items);
        radioGroup.setValue(items.get(0));
        return radioGroup;
    }

    public static void notify(String message, NotificationVariant notificationVariant, Notification.Position position) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(position);
    }


    public static void notifyError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
    }

    public static void notifyMessage(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setPosition(Notification.Position.MIDDLE);
    }

    public static void setFooterImagesPath(Image footerLeftImage, Image footerRightImage) {
        footerLeftImage.setSrc("./images/DKW-Logo.png");
        footerRightImage.setSrc("./images/aau.png");
    }

    public static void setIconForButtonWithToolTip(Button button, VaadinIcon icon, String toolTip) {
        button.setIcon(new Icon(icon));
        button.setText("");
        button.setTooltipText(toolTip);
    }


    public static Component setHeaderWithInfoLogo(String headerTitle, String headerDetails) {
        Span span = new Span(Utils.boldHeader(headerTitle));
        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.getElement().setAttribute("title", headerDetails);
        icon.getStyle().set("height", "var(--lumo-font-size-m)").set("color", "var(--lumo-contrast-70pct)").set("margin-right", "10px");

        HorizontalLayout layout = new HorizontalLayout(span, icon);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);

        return layout;
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


    public static LinkedHashMap<String, Integer> sortMapDescending(HashMap<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static Html boldHeader(String label) {
        return new Html("<div style='font-weight: bold;'>" + label + "</div>");
    }

    public static boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public static Select<String> configureAndGetSelectField() {
        Select<String> selectField = new Select<>();
        selectField.setWidth("50%");
        selectField.setLabel("Select from existing datasets");
        selectField.setItems(getDatasetsAddresses().keySet());
        selectField.setValue("DBpedia-CityData");
        return selectField;
    }

    public static HashMap<String, String> getDatasetsAddresses() {
        HashMap<String, String> map = new HashMap<>();
        map.put("DBpedia-CityData", "/Users/kashifrabbani/Documents/GitHub/data/CityDBpedia.nt");
        map.put("LUBM-Mini", "/Users/kashifrabbani/Documents/GitHub/lubm-uba/output/lubm-sf-1.nt");
        map.put("DBpedia", "/home/ubuntu/datasets/dbpedia_ml.nt");
        map.put("LUBM", "/home/ubuntu/datasets/lubm.n3");
        map.put("YAGO-4", "/home/ubuntu/datasets/yago.n3");
        map.put("WATDIV", "/home/ubuntu/datasets/WATDIV.n3");
        return map;
    }

    public static HashMap<String, Tuple2<String, String>> getDatasetsEndpointDetails() {
        HashMap<String, Tuple2<String, String>> map = new HashMap<>();
        map.put("DBpedia-CityData", new Tuple2<>("http://10.92.0.34:7200/", "DBpediaCityData"));
        map.put("LUBM-Mini", new Tuple2<>("http://10.92.0.34:7200/", "LUBM-ScaleFactor-1"));
        map.put("DBpedia", new Tuple2<>("http://10.92.0.34:7200/", "DBPEDIA_ML"));
        map.put("LUBM", new Tuple2<>("http://10.92.0.34:7200/", "LUBM"));
        map.put("YAGO-4", new Tuple2<>("http://10.92.0.34:7200/", "Yago_EngWiki"));
        //map.put("WATDIV", new Tuple2<>("http://10.92.0.34:7200/", "WATDIV_1B"));
        return map;
    }

    public static Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }


    public static String readKey() {
        String key = null;
        try {
            key = new String(Files.readAllBytes(Paths.get("google_api_key.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static List<String> sortMapByValuesDesc(HashMap<String, Integer> map) {
        // Create a list of entries from the map
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        // Sort the list in descending order based on the values
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create a list of keys sorted by their corresponding values
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            keys.add(entry.getKey());
        }

        return keys;
    }


    public static String formatWithCommas(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }

    public static List<String> getTopKeysFromMap(Set<String> keyset, int thershold) {
        List<String> firstNKeys = new ArrayList<>();
        int i = 0;
        for (String key : keyset) {
            firstNKeys.add(key);
            i++;
            if (i == thershold) {
                break;
            }
        }
        return firstNKeys;
    }

    public static String constructModelForGivenNodeShapesAndTheirPropertyShapes(Set<NS> nodeShapes) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
        model.setNsPrefix("qse", "http://shaclshapes.org/");
        for (NS ns : nodeShapes) {
            Statement nsType = ResourceFactory.createStatement(ResourceFactory.createResource((ns.getIri().toString())), ResourceFactory.createProperty((RDF.type.toString())), ResourceFactory.createResource((SHACL.NODE_SHAPE.toString())));
            Statement nsTarget = ResourceFactory.createStatement(ResourceFactory.createResource((ns.getIri().toString())), ResourceFactory.createProperty((SHACL.TARGET_CLASS.toString())), ResourceFactory.createResource((ns.getTargetClass().toString())));
            model.add(nsType);
            model.add(nsTarget);
            for (PS ps : ns.getPropertyShapes()) {
                Statement nsPs = ResourceFactory.createStatement(ResourceFactory.createResource((ns.getIri().toString())), ResourceFactory.createProperty((SHACL.PROPERTY.toString())), ResourceFactory.createResource((ps.getIri().toString())));
                Statement psPath = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.PATH.toString())), ResourceFactory.createResource((ps.getPath())));
                Statement psType = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty(RDF.type.toString()), ResourceFactory.createResource((SHACL.PROPERTY_SHAPE.toString())));
                model.add(nsPs);
                model.add(psPath);
                model.add(psType);

                if (ps.getHasOrList()) {
                    RDFList list = model.createList(new RDFNode[]{});
                    List<Resource> resources = new ArrayList<>();

                    List<ShaclOrListItem> cleanItems = new ArrayList<>();
                    for (ShaclOrListItem item : ps.getShaclOrListItems()) {
                        if (item.getDataTypeOrClass() != null && !item.getDataTypeOrClass().equals("Undefined")) {
                            cleanItems.add(item);
                        }
                    }
                    if (cleanItems.size() > 1) {
                        for (ShaclOrListItem item : cleanItems) {
                            Resource child = model.createResource();
                            Statement psNodeType;
                            Statement psNodeKind;
                            if (item.getNodeKind().equals("IRI")) {
                                psNodeKind = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource((SHACL.IRI.toString())));
                                psNodeType = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.CLASS.toString())), ResourceFactory.createResource((item.getDataTypeOrClass())));
                            } else {
                                psNodeKind = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource((SHACL.LITERAL.toString())));
                                psNodeType = ResourceFactory.createStatement(child, ResourceFactory.createProperty((SHACL.DATATYPE.toString())), ResourceFactory.createResource((item.getDataTypeOrClass())));
                            }
                            model.add(psNodeKind);
                            model.add(psNodeType);
                            resources.add(child);
                        }
                        for (Resource element : resources) { // add each of these resources onto the end of the list
                            list = list.with(element);
                        }
                        model.add(ResourceFactory.createResource((ps.getIri().toString())), model.createProperty(SHACL.OR.toString()), list); // relate the root to the list
                    } else {
                        ShaclOrListItem item = cleanItems.get(0);
                        if (item.getDataTypeOrClass() != null) {
                            if (!item.getDataTypeOrClass().equals("Undefined")) {
                                Statement itemNodeType = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.DATATYPE.toString())), ResourceFactory.createResource((item.getDataTypeOrClass())));
                                model.add(itemNodeType);
                            }
                        }
                        if (item.getNodeKind() != null) {
                            if (item.getNodeKind().equals("IRI")) {
                                Statement itemNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.IRI.toString()));
                                model.add(itemNodeKind);
                            }
                            if (item.getNodeKind().equals("Literal")) {
                                Statement itemNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.LITERAL.toString()));
                                model.add(itemNodeKind);
                            }
                        }
                    }
                } else {
                    if (ps.getDataTypeOrClass() != null) {
                        if (!ps.getDataTypeOrClass().equals("Undefined")) {
                            Statement psNodeType = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.DATATYPE.toString())), ResourceFactory.createResource((ps.getDataTypeOrClass())));
                            model.add(psNodeType);
                        }
                    }
                    if (ps.getNodeKind() != null) {
                        if (ps.getNodeKind().equals("IRI")) {
                            Statement psNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.IRI.toString()));
                            model.add(psNodeKind);
                        }
                        if (ps.getNodeKind().equals("Literal")) {
                            Statement psNodeKind = ResourceFactory.createStatement(ResourceFactory.createResource((ps.getIri().toString())), ResourceFactory.createProperty((SHACL.NODE_KIND.toString())), ResourceFactory.createResource(SHACL.LITERAL.toString()));
                            model.add(psNodeKind);
                        }
                    }
                }
            }
        }

        OutputStream out = new ByteArrayOutputStream();
        TurtleFormatter formatter = new TurtleFormatter(FormattingStyle.DEFAULT);
        formatter.accept(model, out);
        return out.toString();
    }

}
