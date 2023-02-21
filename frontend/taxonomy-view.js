var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
let TaxonomyView = class TaxonomyView extends LitElement {
    static get styles() {
        return css `
      :host {
          display: block;
          height: 100%;
      }
      `;
    }
    render() {
        return html `
<vaadin-vertical-layout style="width: 100%; height: 100%;">
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);">
  <h3 id="title" style="align-self: stretch; flex-grow: 1; flex-shrink: 1; text-align:center;">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto;" id="contentVerticalLayout"></vaadin-vertical-layout>
 <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);">
  <h6 style="flex-shrink: 1; align-self: center; flex-grow: 1;text-align:center;">Authors: Kashif Rabbani, Matteo Lissandrini, and Katja Hose</h6>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
    }
    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
};
TaxonomyView = __decorate([
    customElement('taxonomy-view')
], TaxonomyView);
export { TaxonomyView };
//# sourceMappingURL=taxonomy-view.js.map