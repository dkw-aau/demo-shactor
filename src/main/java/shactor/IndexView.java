package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import shactor.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import static shactor.utils.Utils.*;

@Tag("index-view")
@JsModule("./index-view.ts")
@Route("")
public class IndexView extends LitTemplate {
    @Id("tabSheet")
    private TabSheet tabSheet;
    @Id("footerImageLeftDiv")
    private Div footerImageLeftDiv;
    @Id("footerImageRightDiv")
    private Div footerImageRightDiv;
    @Id("footerRightImage")
    private Image footerRightImage;
    @Id("footerLeftImage")
    private Image footerLeftImage;

    public static enum Category {
        EXISTING_FILE_BASED,
        UPLOAD_FILE_BASED,
        CONNECT_END_POINT,
        ANALYZE_SHAPES
    }

    public static Category category;
    public static String graphURL = "";
    public static String endPointRepo = "";


    public IndexView() {
        Utils.setFooterImagesPath(footerLeftImage, footerRightImage);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_BORDERED);
        tabSheet.add("Use Existing Datasets", getTabOneLayout());
        tabSheet.add("Upload Graph", getTabTwoLayout());
        tabSheet.add("Connect to SPARQL Endpoint", getTabThreeLayout());
        tabSheet.add("Analyze SHACL Shapes", getTabFourLayout());
    }


    private VerticalLayout getTabOneLayout() {
        VerticalLayout vl = getVerticalLayout();
        Select<String> datasetsSelection = configureAndGetSelectField();
        vl.add(datasetsSelection);

        Button continueButton = getPrimaryButton("Continue");

        RadioButtonGroup<String> rbg = getRadioButtonGroup("Select QSE Type:", new ArrayList<>(Arrays.asList("Exact", "Approximate")));
        vl.add(rbg);
        continueButton.addClickListener(buttonClickEvent -> {
            //Utils.notifyMessage(rbg.getValue());
            category = Category.EXISTING_FILE_BASED;
            graphURL = Utils.getDatasetsAddresses().get(datasetsSelection.getValue());
            continueButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        vl.add(continueButton);
        return vl;
    }

    private VerticalLayout getTabTwoLayout() {
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter Graph URL (in .NT Format)");

        Button uploadGraphButton = getPrimaryButton("Upload Graph");
        vl.add(textField);
        vl.add(uploadGraphButton);

        uploadGraphButton.addClickListener(buttonClickEvent -> {
            Notification.show("URL: " + textField.getValue());
            graphURL = textField.getValue();
            category = Category.UPLOAD_FILE_BASED;
            uploadGraphButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }

    private VerticalLayout getTabThreeLayout() {
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter address of a SPARQL endpoint");
        TextField textFieldRepo = getTextField("Enter name of a repository (for GraphDB)");
        Button graphEndpointButton = getPrimaryButton("Connect");
        vl.add(textField);
        vl.add(textFieldRepo);
        vl.add(graphEndpointButton);

        textField.setValue("http://10.92.0.34:7200/");
        textFieldRepo.setValue("DBPEDIA_ML");

        graphEndpointButton.addClickListener(buttonClickEvent -> {
            graphURL = textField.getValue();
            endPointRepo = textFieldRepo.getValue();
            category = Category.CONNECT_END_POINT;
            //Utils.notify("Not Implemented Yet!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            graphEndpointButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }

    private VerticalLayout getTabFourLayout() {
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter Shapes File URL (in .TTL format)");

        Button shapesUploadButton = getPrimaryButton("Upload");
        vl.add(textField);
        vl.add(shapesUploadButton);

        shapesUploadButton.addClickListener(buttonClickEvent -> {
            graphURL = textField.getValue();
            category = Category.ANALYZE_SHAPES;
            Utils.notify("Not Implemented Yet!", NotificationVariant.LUMO_ERROR, Notification.Position.TOP_CENTER);
            //shapesUploadButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }

    // Helper Methods
    //graphURL = "/Users/kashifrabbani/Documents/GitHub/data/CityDBpedia.nt";
    //graphURL = "/Users/kashifrabbani/Documents/GitHub/data/DBpedia/DBpediaCityAndTown.nt";
    //graphURL = "/Users/kashifrabbani/Documents/GitHub/data/toy/uni0-lubm.nt";
    // Server: /home/ubuntu/datasets/dbpedia_ml.nt
    //graphURL = "/home/ubuntu/datasets/dbpedia_ml.nt";
}
