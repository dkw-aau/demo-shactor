package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Tag("index-view")
@JsModule("./index-view.ts")
@Route("")
public class IndexView extends LitTemplate {
    
    @Id("graphUrl")
    private TextField graphUrl;
    @Id("uploadGraphButton")
    private Button uploadGraphButton;
    @Id("graphEndpointUrl")
    private TextField graphEndpointUrl;
    @Id("graphEndpointButton")
    private Button graphEndpointButton;
    
    public static String graphURL = "";
    @Id("vaadinSelect")
    private Select<String> vaadinSelect;
    
    @Id("continueButton")
    private Button continueButton;
    
    public IndexView() {
        
        vaadinSelect.setItems("DBpedia", "LUBM", "YAGO-4", "WikiData");
        vaadinSelect.setValue("DBpedia");
        
        continueButton.addClickListener(buttonClickEvent -> {
            graphURL = "/Users/kashifrabbani/Documents/GitHub/data/CityDBpedia.nt";
            continueButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
        
        uploadGraphButton.addClickListener(buttonClickEvent -> {
            Notification.show( "URL: " + graphUrl.getValue());
            graphURL = graphUrl.getValue();
            graphEndpointButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
    
        graphEndpointButton.addClickListener(buttonClickEvent -> {
            graphEndpointButton.getUI().ifPresent(ui -> ui.navigate("selection-view"));
        });
    }
}
