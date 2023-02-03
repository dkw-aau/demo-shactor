package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Route;

/**
 * A Designer generated component for the ps-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("ps-view")
@CssImport(value = "./grid.css", themeFor = "vaadin-grid")
@JsModule("./ps-view.ts")
@Route("/ps-view")
public class PsView extends LitTemplate {

    /**
     * Creates a new PsView.
     */
    public PsView() {
        // You can initialise any data required for the connected UI components here.
    }

}
