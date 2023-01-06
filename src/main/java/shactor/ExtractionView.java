package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.Route;

@Tag("extraction-view")
@JsModule("./extraction-view.ts")
@Route("/extraction-view")
public class ExtractionView extends LitTemplate {
    
    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;
    
    public ExtractionView() {
    
    }
}
