import {LitElement, html, css, customElement} from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('selection-view')
export class SelectionView extends LitElement {
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
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
  <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto;" id="contentVerticalLayout">
  <h4 style="align-self: center; width: 80%; margin-bottom: 0%;">About:</h4>
  <h5 style="width: 80%; margin-left: 10%; align-self: flex-start; margin-top: 0%;">SHACTOR will take you through different steps of shapes extraction and show information about the graph. Please begin by starting the process of shapes extraction:</h5>
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
}
