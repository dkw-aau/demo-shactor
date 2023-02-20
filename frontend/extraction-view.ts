import {LitElement, html, css, customElement} from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/charts/src/vaadin-chart.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/button/src/vaadin-button.js';

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
<vaadin-vertical-layout style="flex-direction: column;" id="contentMainLayout">
 <vaadin-vertical-layout style="flex-direction: column; align-self: stretch; flex-grow: 1;">
  <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
   <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 0; flex-basis: auto;" id="contentVerticalLayout">
   <vaadin-horizontal-layout theme="spacing" style="margin-right: 5%; margin-left: 5%; flex-grow: 0; align-self: stretch;">
    <vaadin-vertical-layout theme="spacing" style="flex-grow: 0; flex-shrink: 0; width: 65%;">
     <h5 style="margin-bottom: 0%;">Shapes Analysis and Knowledge Graph Cleaning</h5>
     <p style="align-self: flex-start; margin-top: 0%;">SHACTOR has extracted SHACL shapes for the chosen classes. You have the following options:</p>
     <vaadin-horizontal-layout style="align-self: stretch;" id="actionButtonsHorizontalLayout">
      <vaadin-button tabindex="0" id="readShapesStatsButton" style="margin-right: var(--lumo-space-l);" theme="primary">
        Read Shapes Statistics 
      </vaadin-button>
      <vaadin-button tabindex="0" style="margin-right: var(--lumo-space-l);" id="readShactorLogsButton" theme="primary">
        Read SHACTOR Logs 
      </vaadin-button>
      <vaadin-button theme="primary" aria-label="Add new" id="taxonomyVisualizationButton" tabindex="0">
       <iron-icon icon="lumo:plus"></iron-icon>
      </vaadin-button>
     </vaadin-horizontal-layout>
     <h5 style="margin-bottom: 0%; align-self: stretch;">You can set the Confidence and Support thresholds to analyze shapes.</h5>
     <vaadin-horizontal-layout style="align-self: stretch;">
      <vaadin-text-field label="Support" placeholder="10" id="supportTextField" style="margin-right: 2%; flex-grow: 0;" type="text"></vaadin-text-field>
      <vaadin-text-field label="Confidence (Percentage %)" placeholder="25" type="text" id="confidenceTextField" style="margin-right: 2%; flex-grow: 0; flex-shrink: 1; width: 20%;"></vaadin-text-field>
      <vaadin-button id="startPruningButton" style="flex-grow: 0; flex-shrink: 1; align-self: flex-end; margin-right: 2%;" tabindex="0" theme="primary">
        Analyze Shapes 
      </vaadin-button>
      <vaadin-button theme="primary" id="downloadPrunedShapesButton" style="align-self: flex-end; flex-shrink: 1; flex-grow: 0;" tabindex="0">
        Download Reliable Shapes 
      </vaadin-button>
     </vaadin-horizontal-layout>
    </vaadin-vertical-layout>
    <vaadin-chart type="column" tooltip="" id="knowledgeGraphStatsPieChart" style="flex-grow: 0; flex-shrink: 0; width: 35%; align-self: stretch;" additional-options=""></vaadin-chart>
   </vaadin-horizontal-layout>
   <h2 id="headingPieCharts" style="align-self: stretch; margin-right: 5%; margin-left: 5%;">Shapes Statistical Analysis</h2>
   <vaadin-horizontal-layout style="align-self: stretch; margin-left: 5%; margin-right: 5%; flex-shrink: 1; flex-grow: 1; margin-bottom: 0%; align-items: stretch; flex-wrap: nowrap;">
    <vaadin-chart type="pie" tooltip="" id="defaultShapesStatsPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsBySupportPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsByConfidencePieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsByBothPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
   </vaadin-horizontal-layout>
   <h2 id="headingNodeShapesAnalysis" style="margin-left: 5%;">Node Shapes Analysis</h2>
   <p id="nsGridRadioButtonInfo" style="margin-left: 5%;">All: Show all node shapes, Below : Show node shapes having support/confidence lower than specified threshold, Above: Show node shapes having support/confidence above than specified threshold.</p>
   <vaadin-radio-group id="vaadinRadioGroup" style="align-self: stretch; margin-left: 5%; margin-right: 5%;"></vaadin-radio-group>
   <vaadin-grid id="shapesGrid" style="margin-right: 5%; align-self: stretch; margin-left: 5%; flex-grow: 0;" is-attached multi-sort-priority="prepend"></vaadin-grid>
   <h5 id="propertyShapesGridInfo" style="margin-left: 5%; margin-bottom: 0%; align-self: stretch;">Property Shapes Analysis</h5>
   <p id="psGridRadioButtonInfo" style="margin-left: 5%;">All: Show all property shapes, Below : Show property shapes having support/confidence lower than specified threshold, Above: Show property shapes having support/confidence above than specified threshold.</p>
   <vaadin-radio-group id="psVaadinRadioGroup" style="margin-right: 5%; margin-left: 5%;"></vaadin-radio-group>
   <vaadin-grid id="propertyShapesGrid" style="align-self: stretch; margin-left: 5%; margin-right: 5%; flex-grow: 0;" is-attached multi-sort-priority="prepend"></vaadin-grid>
   <vaadin-button theme="primary" id="downloadSelectedShapesButton" style="align-self: center; margin-left: 5%; margin-right: 5%;" tabindex="0">
     Download Selected Shapes 
   </vaadin-button>
  </vaadin-vertical-layout>
  <vaadin-vertical-layout style="align-self: stretch; flex-direction: row; align-items: flex-end; flex-shrink: 0; flex-wrap: wrap; align-content: flex-end;" id="contentFooter">
   <h6 style="flex-shrink: 1; align-self: center; flex-grow: 1;">Authors: Kashif Rabbani, Matteo Lissandrini, and Katja Hose</h6>
  </vaadin-vertical-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
    }

    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
}
