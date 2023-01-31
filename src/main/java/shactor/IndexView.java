package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Tag("index-view")
@JsModule("./index-view.ts")
@Route("")
public class IndexView extends LitTemplate {
    public static String graphURL = "";

    @Id("tabSheet")
    private TabSheet tabSheet;
    @Id("footer")
    private Div footer;
    @Id("footerImageLeftDiv")
    private Div footerImageLeftDiv;
    @Id("footerImageRightDiv")
    private Div footerImageRightDiv;
    @Id("footerRightImage")
    private Image footerRightImage;
    @Id("footerLeftImage")
    private Image footerLeftImage;
    
    
    public IndexView() {
        footerLeftImage.setSrc("./images/DKW-Logo.png");
        footerRightImage.setSrc("./images/aau.png");
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.add("Use Existing Datasets", getTabOneLayout());
        tabSheet.add("Upload Graph", getTabTwoLayout());
        tabSheet.add("Connect to SPARQL Endpoint", getTabThreeLayout());
        tabSheet.add("Analyze SHACL Shapes", getTabFourLayout());
    }
    
    private VerticalLayout getTabOneLayout(){
        VerticalLayout vl = getVerticalLayout();
        vl.add(configureAndGetSelectField());
        Button continueButton = getPrimaryButton("Continue");
        continueButton.addClickListener(buttonClickEvent -> {
            graphURL = "/Users/kashifrabbani/Documents/GitHub/data/CityDBpedia.nt";
            // Server: /home/ubuntu/data/dbpedia_ml.nt
            //graphURL = "/home/ubuntu/data/dbpedia_ml.nt";
            continueButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        vl.add(continueButton);
        return vl;
    }
    
    private VerticalLayout getTabTwoLayout(){
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter Graph URL (in .NT Format)");
        
        Button uploadGraphButton = getPrimaryButton("Upload Graph");
        vl.add(textField);
        vl.add(uploadGraphButton);
    
        uploadGraphButton.addClickListener(buttonClickEvent -> {
            Notification.show( "URL: " + textField.getValue());
            graphURL = textField.getValue();
            uploadGraphButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }
    
    private VerticalLayout getTabThreeLayout(){
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter address of a SPARQL endpoint");
        Button graphEndpointButton = getPrimaryButton("Connect");
        vl.add(textField);
        vl.add(graphEndpointButton);
    
        graphEndpointButton.addClickListener(buttonClickEvent -> {
            graphURL = textField.getValue();
            Notification.show( "Not Implemented Yet!" );
            //graphEndpointButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }
    private VerticalLayout getTabFourLayout(){
        VerticalLayout vl = getVerticalLayout();
        TextField textField = getTextField("Enter Shapes File URL (in .TTL format)");
        
        Button shapesUploadButton = getPrimaryButton("Upload");
        vl.add(textField);
        vl.add(shapesUploadButton);
    
        shapesUploadButton.addClickListener(buttonClickEvent -> {
            graphURL = textField.getValue();
            Notification.show( "Not Implemented Yet!" );
            //shapesUploadButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        return vl;
    }
    
    // Helper Methods
    
    private VerticalLayout getVerticalLayout(){
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return verticalLayout;
    }
    
    private TextField getTextField(String label){
        TextField textField = new TextField();
        textField.setWidth("50%");
        textField.setLabel(label);
        return textField;
    }
    
    private Select<String> configureAndGetSelectField(){
        Select<String> selectField = new Select<>();
        selectField.setWidth("50%");
        selectField.setLabel("Select from existing datasets");
        selectField.setItems("DBpedia", "LUBM", "YAGO-4", "WikiData");
        selectField.setValue("DBpedia");
        return selectField;
    }
    
    private Button getPrimaryButton(String label) {
        Button primaryButton = new Button(label);
        primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return primaryButton;
    }
    
}
