import {LitElement, html, css, customElement} from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/grid/src/vaadin-grid.js';

@customElement('extraction-view')
export class ExtractionView extends LitElement {
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
 <vaadin-vertical-layout style="width: 100%; height: 100%;">
  <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
   <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto;" id="contentVerticalLayout">
   <h4 style="align-self: center; width: 80%; margin-bottom: 0%;">Shapes Pruning &amp; Cleaning</h4>
   <h5 style="width: 80%; margin-left: 10%; align-self: flex-start; margin-top: 0%;">SHACTOR has extracted SHACL shapes for the chosen classes. You have the following options:</h5>
   <vaadin-horizontal-layout style="align-self: flex-start; margin-left: 10%; margin-right: 10%;">
    <vaadin-button id="downloadShapesButton" style="margin-right: var(--lumo-space-l);" tabindex="0">
      Download SHACL Shapes 
    </vaadin-button>
    <vaadin-button tabindex="0" id="readShapesStatsButton" style="margin-right: var(--lumo-space-l);">
      Read Shapes Statistics 
    </vaadin-button>
    <vaadin-button tabindex="0" style="margin-right: var(--lumo-space-l);" id="readShactorLogsButton">
      Read SHACTOR Logs 
    </vaadin-button>
   </vaadin-horizontal-layout>
   <h5 style="margin-left: 10%; margin-bottom: 0%;">You can set the Confidence and Support thresholds to prune shapes.</h5>
   <vaadin-horizontal-layout style="align-self: stretch; margin-left: 10%; margin-right: 10%;">
    <vaadin-text-field label="Support" placeholder="10" id="supportTextField" style="margin-right: 2%; flex-grow: 0;" type="text"></vaadin-text-field>
    <vaadin-text-field label="Confidence (Percentage %)" placeholder="25" type="text" id="confidenceTextField" style="margin-right: 2%; flex-grow: 0; flex-shrink: 1; width: 20%;"></vaadin-text-field>
    <vaadin-button id="startPruningButton" style="flex-grow: 0; flex-shrink: 1; align-self: flex-end;" tabindex="0" theme="primary">
      Start Pruning 
    </vaadin-button>
   </vaadin-horizontal-layout>
   <vaadin-grid id="shapesGrid" style="margin-right: 10%; align-self: stretch; margin-left: 10%; flex-grow: 0; margin-top: 2%;" is-attached multi-sort-priority="prepend"></vaadin-grid>
   <h5 id="propertyShapesGridInfo" style="margin-left: 10%; margin-bottom: 0%;">Heading 5</h5>
   <vaadin-grid id="propertyShapesGrid" style="align-self: stretch; margin-left: 10%; margin-right: 10%; flex-grow: 0;" is-attached multi-sort-priority="prepend"></vaadin-grid>
   <vaadin-button theme="primary" id="downloadPrunedShapesButton" style="align-self: flex-end; margin-left: 10%; margin-right: 10%;" tabindex="0">
    Download Pruned SHACL Shapes
   </vaadin-button>
  </vaadin-vertical-layout>
  <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);">
   <h6 style="align-self: center; margin: var(--lumo-space-l);">Credits: Kashif Rabbani, Matteo Lissandrini, Katja Hose</h6>
   <h6 id="footer_credits_uni" style="align-self: center; margin: var(--lumo-space-l); flex-grow: 1; padding: var(--lumo-space-m);"> Aalborg University, Denmark</h6>
  </vaadin-horizontal-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
    }

    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
}
