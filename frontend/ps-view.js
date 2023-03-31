var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
let PsView = class PsView extends LitElement {
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
 <vaadin-vertical-layout style="width: 90%; flex-grow: 1; flex-shrink: 1; flex-basis: auto; margin-left: 5%; margin-right: 5%;" id="contentVerticalLayout">
  <h4 style="align-self: center;">PS Analysis Dashboard - SHACTOR (Step 4/4)</h4>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayout" style="align-self: stretch;"></vaadin-horizontal-layout>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayoutTwo" style="align-self: stretch;"></vaadin-horizontal-layout>
  <vaadin-horizontal-layout id="statusHorizontalLayout" style="align-self: center;"></vaadin-horizontal-layout>
  <h4>Selected Property Shape Info:</h4>
  <p>This property shape has following constraints where you can check conformance of each constraint with the graph. We show support and confidence of each PS constraint along with options to retrieve or edit the entities corresponding to each constraints.</p>
  <vaadin-grid style="align-self: stretch; flex-grow: 0; flex-shrink: 1; max-height: 15%;" is-attached multi-sort-priority="prepend" id="psConstraintsGrid"></vaadin-grid>
  <vaadin-grid id="psOrItemsConstraintsGrid" style="flex-grow: 0; flex-shrink: 1; align-self: stretch; max-height: 15%;" is-attached multi-sort-priority="prepend"></vaadin-grid>
  <h4>Property Shape Syntax (SHACL)</h4>
  <vaadin-text-area label="Edit SHACL Syntax" id="psSyntaxTextArea" style="align-self: stretch; flex-grow: 0;"></vaadin-text-area>
  <vaadin-button theme="icon" aria-label="Add new" id="copySyntaxButton" tabindex="0">
   <iron-icon icon="lumo:plus"></iron-icon>
  </vaadin-button>
  <h4>Other possible actions:</h4>
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <div style="width: 33%; align-self: flex-start;">
    <h5>Entities of PS</h5>
    <p>Fetch all entities (as triples) extracting this PS:</p>
    <vaadin-button tabindex="0" theme="primary" id="buttonA">
      Build and Execute Query 
    </vaadin-button>
   </div>
   <div style="width: 33%; align-self: flex-start;">
    <h5>Validation</h5>
    <p>Validate PS constraints against its entities data:</p>
    <vaadin-button tabindex="0" theme="primary" id="buttonB">
      Start Validation 
    </vaadin-button>
   </div>
   <div style="width: 33%; align-self: flex-start;">
    <h5>Cleaning </h5>
    <p>TODO</p>
    <vaadin-button tabindex="0" theme="primary" id="buttonC">
      Action? 
    </vaadin-button>
   </div>
  </vaadin-horizontal-layout>
 </vaadin-vertical-layout>
 <br>
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
PsView = __decorate([
    customElement('ps-view')
], PsView);
export { PsView };
//# sourceMappingURL=ps-view.js.map