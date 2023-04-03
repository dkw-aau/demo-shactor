package shactor.utils;

import com.storedobject.chart.SOChart;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class DialogUtil {
    public static Button actionButton;

    public static void getDialogWithHeaderAndFooter(String title, String textAreaText, String infoParagraphText) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Dialog");
        dialog.getHeader().add(getHeaderTitle(title));
        createFooter(dialog, "Execute");
        VerticalLayout dialogLayout = createDialogLayout(textAreaText, infoParagraphText);
        dialog.add(dialogLayout);
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.open();
    }

    public static Dialog getDialogToDisplayChartWithHeaderAndFooter(String title, SOChart chart) {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Dialog");
        dialog.getHeader().add(getHeaderTitle(title));
        createFooter(dialog, "Execute");
        VerticalLayout dialogLayout = createDialogLayoutForChart("Chart", chart);
        dialog.add(dialogLayout);
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        //dialog.open();
        return dialog;
    }

    private static H2 getHeaderTitle(String title) {
        H2 headline = new H2(title);
        headline.getStyle().set("padding-bottom", "0px");
        headline.addClassName("draggable");
        headline.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold").set("cursor", "move").set("padding", "var(--lumo-space-m) 0").set("flex", "1");
        return headline;
    }

    private static VerticalLayout createDialogLayout(String textAreaText, String paragraphText) {
        Paragraph paragraph = new Paragraph(paragraphText);
        TextArea descriptionArea = new TextArea();
        descriptionArea.setValue(textAreaText);
        VerticalLayout fieldLayout = new VerticalLayout(paragraph, descriptionArea);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "1200px").set("max-width", "100%");
        return fieldLayout;
    }

    private static VerticalLayout createDialogLayoutForChart(String label, SOChart chart) {
        Paragraph paragraph = new Paragraph(label);
        VerticalLayout fieldLayout = new VerticalLayout(paragraph, chart);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "1000px").set("max-width", "100%");
        return fieldLayout;
    }

    private static void createFooter(Dialog dialog, String buttonLabel) {
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        actionButton = new Button(buttonLabel, e -> dialog.close());
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(cancelButton);
        //dialog.getFooter().add(actionButton);
        //actionButton.addClickListener(buttonClickEvent -> {});
    }

}
