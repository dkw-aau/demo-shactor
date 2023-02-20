var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
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
  <h4 style="align-self: center; width: 80%; margin-bottom: 0%;">SHACTOR (Step 2/3)</h4>
  <p style="margin-right: 10%; margin-left: 10%; align-self: stretch;">SHACTOR will take you through different steps of shapes extraction and show information about each step. Here you can see the information about first phase:</p>
  <h5 id="graphInfo" style="align-self: center; width: 80%;">graphInfo</h5>
  <vaadin-text-field placeholder="Search Class Names" id="searchField" style="width: 80%; align-self: flex-start; margin-left: 10%;" type="text">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <h6 style="align-self: center; width: 80%;">Table showing classes along with instance count of each class extracted in the first phase of shapes extraction.</h6>
  <vaadin-grid id="vaadinGrid" style="width: 80%; align-self: center; flex-grow: 1;" is-attached multi-sort-priority="prepend"></vaadin-grid>
  <vaadin-checkbox id="graphStatsCheckBox" style="width: 80%; align-self: center;" type="checkbox" value="on">
    Compute statistics of the graph? (It can take a bit longer) 
  </vaadin-checkbox>
  <vaadin-button theme="primary" id="completeShapesExtractionButton" style="align-self: flex-start; margin-left: 10%; margin-right: 10%; flex-grow: 1;" tabindex="0">
    Go to Next Step 
  </vaadin-button>
  <br>
  <br>
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
SelectionView = __decorate([
    customElement('selection-view')
], SelectionView);
export { SelectionView };
//# sourceMappingURL=selection-view.js.map