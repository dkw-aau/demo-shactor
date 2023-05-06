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
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
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
  <h4 style="align-self: center;">Property Shape Analysis Dashboard</h4>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayout" style="align-self: stretch;"></vaadin-horizontal-layout>
  <h4>Entity Exploration</h4>
  <p>Here you can explore all the entities having this focus property and click on the button below to retrieve list of all entities along with an option to generate delete query (in case you want to delete a triple).</p>
  <vaadin-button tabindex="0" theme="secondary" id="buttonA">
    Build and Execute Query 
  </vaadin-button>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayoutTwo" style="align-self: flex-start; width: 50%;"></vaadin-horizontal-layout>
  <h4>Selected Property Shape Info:</h4>
  <p>This property shape has following constraints where you can check conformance of each constraint with the graph. We show support and confidence of each PS constraint along with options to retrieve or edit the entities corresponding to each constraints.</p>
  <vaadin-grid style="align-self: stretch; flex-grow: 0; flex-shrink: 1; max-height: 15%;" is-attached multi-sort-priority="prepend" id="psConstraintsGrid"></vaadin-grid>
  <vaadin-grid id="psOrItemsConstraintsGrid" style="flex-grow: 0; flex-shrink: 1; align-self: stretch; max-height: 15%;" is-attached multi-sort-priority="prepend"></vaadin-grid>
  <h4>Property Coverage</h4>
  <p id="propCoverageInfoParagraph">Paragraph</p>
  <vaadin-button theme="secondary" id="propCoverageQueryButton" tabindex="0">
    Show Entities 
  </vaadin-button>
  <h4>Shape Syntax (SHACL)</h4>
  <vaadin-text-area label="Edit SHACL Syntax" id="psSyntaxTextArea" style="align-self: stretch; flex-grow: 0;"></vaadin-text-area>
  <vaadin-button theme="icon" aria-label="Add new" id="copySyntaxButton" tabindex="0">
   <iron-icon icon="lumo:plus"></iron-icon>
  </vaadin-button>
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