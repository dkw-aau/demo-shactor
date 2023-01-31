package shactor.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

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
    
    public static Select<String> configureAndGetSelectField() {
        Select<String> selectField = new Select<>();
        selectField.setWidth("50%");
        selectField.setLabel("Select from existing datasets");
        selectField.setItems("DBpedia", "LUBM", "YAGO-4", "WikiData");
        selectField.setValue("DBpedia");
        return selectField;
    }
    
    public static Button getPrimaryButton(String label) {
        Button primaryButton = new Button(label);
        primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return primaryButton;
    }
    
    public static RadioButtonGroup getRadioButtonGroup(String label, List<String> items) {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setWidth("50%");
        //radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel(label);
        radioGroup.setItems(items);
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
    
    public   static void setFooterImagesPath(Image footerLeftImage, Image footerRightImage) {
        footerLeftImage.setSrc("./images/DKW-Logo.png");
        footerRightImage.setSrc("./images/aau.png");
    }
}
