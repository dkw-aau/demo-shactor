package shactor;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import shactor.utils.ChartsUtil;

/**
 * A Designer generated component for the taxonomy-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("taxonomy-view")
@RouteAlias("taxonomy")
@JsModule("./taxonomy-view.ts")
@Route("/taxonomy-view")
public class TaxonomyView extends LitTemplate {

    @Id("contentVerticalLayout")
    private VerticalLayout contentVerticalLayout;

    public TaxonomyView() {
        contentVerticalLayout.add(ChartsUtil.buildTaxonomyTree(SelectionView.getParser()));
    }

}
