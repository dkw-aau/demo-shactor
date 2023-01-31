var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/tabsheet/src/vaadin-tabsheet.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
let IndexView = class IndexView extends LitElement {
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
<vaadin-vertical-layout id="contentVerticalLayout">
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; flex-grow: 0;" id="header">
  <h3 id="title" style="align-self: stretch; flex-grow: 1; flex-shrink: 1; text-align:center;">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout style="align-self: center;">
  <h6 style="align-self: center; width: 80%;">About SHACTOR:</h6>
  <p style="align-self: center; width: 80%;">SHACTOR is a system for extracting quality SHACL shapes from very large Knowledge Graphs (KGs), analyzing them to find spurious shapes constraints, and finding erroneous triples in the KG. The SHACL shapes represent prominent data patterns within KG but are likely to contain some spurious constraints extracted due to the presence of erroneous data in the KG. Given a KG having millions of triples and thousands of classes, SHACTOR parses the KG using our efficient and scalable shapes extraction algorithm and outputs SHACL shapes constraints. Further, it uses the concepts of support and confidence to prune the spurious shape constraints. You can use SHACTOR to extract, analyze, and clean SHACL shape constraints from very large KGs, it helps you find and correct errors in the KG by automatically generating SPARQL queries for your KG. SHACTOR will take you through different steps of shapes extraction and show information about the graph. Please begin by starting the process of shapes extraction:</p>
  <vaadin-tabsheet id="tabSheet" style="flex-grow: 0; align-self: stretch; width: 100%; flex-shrink: 0; margin: var(--lumo-space-xl);"></vaadin-tabsheet>
 </vaadin-vertical-layout>
 <vaadin-vertical-layout theme="spacing" style="width: 100%; height: 100%;">
  <div id="footer" style="flex-grow: 0; align-self: stretch; flex-shrink: 0;">
   <div id="footerImageLeftDiv">
    <img id="footerLeftImage">
   </div>
   <div id="footerAboutDiv">
    <p id="footerAboutParagraph">Authors: Kashif Rabbani, Matteo Lissandrini, and Katja Hose</p>
   </div>
   <div id="footerImageRightDiv">
    <img id="footerRightImage">
   </div>
  </div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
    }
    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
};
IndexView = __decorate([
    customElement('index-view')
], IndexView);
export { IndexView };
//# sourceMappingURL=index-view.js.map