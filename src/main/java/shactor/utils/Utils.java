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
import cs.utils.ConfigManager;
import cs.utils.Tuple2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        map.put("WATDIV", new Tuple2<>("http://10.92.0.34:7200/", "WATDIV_1B"));
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
            key = new String(Files.readAllBytes(Paths.get( "google_api_key.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }
}
