import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';

@customElement('ps-view')
export class PsView extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
          height: 100%;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout style="width: 100%; height: 100%;">
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);">
  <h3 id="title" style="align-self: stretch; flex-grow: 1; flex-shrink: 1; text-align:center;">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout style="width: 90%; flex-grow: 1; flex-shrink: 1; flex-basis: auto; margin-left: 5%; margin-right: 5%;" id="contentVerticalLayout">
  <h4 style="align-self: center;">Property Shape Analysis Dashboard</h4>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayout" style="align-self: stretch;"></vaadin-horizontal-layout>
  <h2>Entities Inspection:</h2>
  <div id="inspectionDivPsView">
   <vaadin-horizontal-layout theme="spacing">
    <h4 id="matchedEntitiesHeading" style="align-self: stretch; flex-grow: 0;">N entities matched this property</h4>
    <vaadin-button tabindex="0" theme="secondary" id="buttonA" style="align-self: flex-end; flex-grow: 0;">
     Inspect
    </vaadin-button>
   </vaadin-horizontal-layout>
   <p id="entitiesInspectionInfoParagraph">Inspect all entities of type T having property P. You can edit the entities.</p>
   <vaadin-horizontal-layout theme="spacing">
    <h4 id="missingPropertiesHeading">N entities of X type are missing Y property</h4>
    <vaadin-button theme="secondary" id="propCoverageQueryButton" tabindex="0" style="align-self: flex-end;">
     Inspect
    </vaadin-button>
   </vaadin-horizontal-layout>
   <p id="propCoverageInfoParagraph">Paragraph</p>
  </div>
  <h2>Shape Constraints and Graph Data Inspection:</h2>
  <p>This property shape has following constraints where you can check conformance of each constraint with the graph. We show support and confidence of each PS constraint along with options to retrieve or edit the entities corresponding to each constraints.</p>
  <vaadin-grid style="align-self: stretch; flex-grow: 0; flex-shrink: 1; max-height: 20%;" is-attached multi-sort-priority="prepend" id="psConstraintsGrid"></vaadin-grid>
  <vaadin-grid id="psOrItemsConstraintsGrid" style="flex-grow: 0; flex-shrink: 1; align-self: stretch; max-height: 25%;" is-attached multi-sort-priority="prepend"></vaadin-grid>
  <h4>Shape Syntax (SHACL)</h4>
  <vaadin-text-area id="psSyntaxTextArea" style="align-self: stretch; flex-grow: 0;"></vaadin-text-area>
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
}
