var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/button/src/vaadin-button.js';
let SelectionView = class SelectionView extends LitElement {
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
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
  <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto;" id="contentVerticalLayout">
  <h4 style="align-self: center; width: 80%; margin-bottom: 0%;">About:</h4>
  <p style="margin-right: 10%; margin-left: 10%; align-self: stretch;">SHACTOR is a framework for extracting quality SHACL shapes from very large Knowledge Graphs (KGs), analyzing them to find spurious shapes constraints, and finding erroneous triples in the KG. The SHACL shapes represent prominent data patterns within KG but are likely to contain some spurious constraints extracted due to the presence of erroneous data in the KG. Given a KG having millions of triples and thousands of classes, SHACTOR parses the KG using our efficient and scalable shapes extraction algorithm and outputs SHACL shapes constraints. Further, it uses the concepts of support and confidence to prune the spurious shape constraints. You can use SHACTOR to extract, analyze, and clean SHACL shape constraints from very large KGs, it helps you find and correct errors in the KG by automatically generating SPARQL queries for your KG.</p>
  <p style="margin-right: 10%; margin-left: 10%; align-self: stretch;">SHACTOR will take you through different steps of shapes extraction and show information about the graph. Please begin by starting the process of shapes extraction:</p>
  <vaadin-button id="startShapesExtractionButton" style="align-self: stretch; margin-left: 10%; margin-right: 10%;" tabindex="0">
    Start Shapes Extraction 
  </vaadin-button>
  <h5 id="graphInfo" style="align-self: center; width: 80%;">graphInfo</h5>
  <vaadin-text-field placeholder="Filter Classes" id="searchField" style="width: 80%; align-self: flex-start; margin-left: 10%;" type="text">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <vaadin-grid id="vaadinGrid" style="width: 80%; align-self: center; flex-grow: 0;" is-attached multi-sort-priority="prepend"></vaadin-grid>
  <vaadin-button theme="secondary" id="completeShapesExtractionButton" style="align-self: stretch; margin-left: 10%; margin-right: 10%;" tabindex="0">
    Complete Shapes Extraction 
  </vaadin-button>
  <br>
 </vaadin-vertical-layout>
 <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);">
  <h6 style="align-self: center; margin: var(--lumo-space-l);">Credits: Kashif Rabbani, Matteo Lissandrini, Katja Hose</h6>
  <h6 id="footer_credits_uni" style="align-self: center; margin: var(--lumo-space-l); flex-grow: 1; padding: var(--lumo-space-m);"> Aalborg University, Denmark</h6>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
    }
    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
};
SelectionView = __decorate([
    customElement('selection-view')
], SelectionView);
export { SelectionView };
//# sourceMappingURL=selection-view.js.map