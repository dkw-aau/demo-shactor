var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/charts/src/vaadin-chart.js';
import '@vaadin/split-layout/src/vaadin-split-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
let ExtractionView = class ExtractionView extends LitElement {
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
<vaadin-vertical-layout style="flex-direction: column;" id="contentMainLayout">
 <vaadin-vertical-layout style="flex-direction: column; align-self: stretch; flex-grow: 1;">
  <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
   <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout class="content" style="flex-grow: 1; flex-shrink: 0; flex-basis: auto;" id="contentVerticalLayout">
   <vaadin-split-layout id="splitLayout" style="align-self: stretch; margin-left: 5%; margin-right: 5%;">
    <vaadin-vertical-layout theme="spacing" style="flex-grow: 1; flex-shrink: 1; width: 60%;">
     <h4>SHACTOR (Step 3/4)</h4>
     <p style="align-self: flex-start; margin-top: 0%;">SHACTOR has extracted SHACL shapes for the chosen classes. You have the following options:</p>
     <vaadin-horizontal-layout style="align-self: stretch;" id="actionButtonsHorizontalLayout"></vaadin-horizontal-layout>
     <h5 style="margin-bottom: 0%; align-self: stretch;">You can set the Confidence and Support thresholds to analyze shapes.</h5>
     <vaadin-horizontal-layout style="align-self: stretch;" id="pruningParamsHorizontalLayout">
      <vaadin-text-field label="Support" placeholder="10" id="supportTextField" style="margin-right: 2%; flex-grow: 0;" type="text"></vaadin-text-field>
      <vaadin-text-field label="Confidence (Percentage %)" placeholder="25" type="text" id="confidenceTextField" style="margin-right: 2%; flex-grow: 0; flex-shrink: 1; width: 20%;"></vaadin-text-field>
      <vaadin-button id="startPruningButton" style="flex-grow: 0; flex-shrink: 1; align-self: flex-end; margin-right: 2%;" tabindex="0" theme="primary">
        Analyze Shapes 
      </vaadin-button>
     </vaadin-horizontal-layout>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout id="graphStatsVerticalLayout" style="flex-grow: 0; flex-shrink: 1; width: 40%; flex-direction: column;">
     <p class="bold-paragraph" font-weight="bold" id="graphStatsHeading">Knowledge Graph Statistics</p>
    </vaadin-vertical-layout>
   </vaadin-split-layout>
   <h2 id="headingPieCharts" style="align-self: stretch; margin-right: 5%; margin-left: 5%;">Shapes Statistical Analysis</h2>
   <vaadin-horizontal-layout theme="spacing" id="soChartsContainerHorizontalLayout" style="align-self: stretch; margin-left: 5%; margin-right: 5%;">
    <vaadin-vertical-layout theme="spacing" id="vl1">
     <p class="bold-paragraph" style="align-self: center;">Default Shapes Analysis</p>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="spacing" id="vl2">
     <p class="bold-paragraph" style="align-self: center;">Shapes Analysis by Support</p>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="spacing" id="vl3">
     <p class="bold-paragraph" style="align-self: center;">Shapes Analysis by Confidence</p>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="spacing" id="vl4">
     <p class="bold-paragraph" style="align-self: center;">By Support and Confidence</p>
    </vaadin-vertical-layout>
   </vaadin-horizontal-layout>
   <vaadin-horizontal-layout style="align-self: stretch; margin-left: 5%; margin-right: 5%; flex-shrink: 1; flex-grow: 1; margin-bottom: 0%; align-items: stretch; flex-wrap: nowrap; width: 100%;" id="chartsContainerHorizontalLayout">
    <vaadin-chart type="pie" tooltip="" id="defaultShapesStatsPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsBySupportPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsByConfidencePieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
    <vaadin-chart type="pie" tooltip="" id="shapesStatsByBothPieChart" style="align-self: flex-start; flex-shrink: 1; flex-grow: 1;"></vaadin-chart>
   </vaadin-horizontal-layout>
   <h2 id="headingNodeShapesAnalysis" style="margin-left: 5%;">Node Shapes Analysis</h2>
   <p id="nsGridRadioButtonInfo" style="margin-left: 5%;">All: Show all node shapes, Below : Show node shapes having support/confidence lower than specified threshold, Above: Show node shapes having support/confidence above than specified threshold.</p>
   <vaadin-radio-group id="vaadinRadioGroup" style="align-self: stretch; margin-left: 5%; margin-right: 5%;"></vaadin-radio-group>
   <vaadin-text-field placeholder="Filter Node Shapes" id="nsSearchField" style="width: 90%; align-self: flex-start; margin-left: 5%;" type="text">
    <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
   </vaadin-text-field>
   <vaadin-grid id="shapesGrid" style="margin-right: 5%; align-self: stretch; margin-left: 5%; flex-grow: 0;" is-attached multi-sort-priority="prepend"></vaadin-grid>
   <h5 id="propertyShapesGridInfo" style="margin-left: 5%; margin-bottom: 0%; align-self: stretch;">Property Shapes Analysis</h5>
   <p id="psGridRadioButtonInfo" style="margin-left: 5%;">All: Show all property shapes, Below : Show property shapes having support/confidence lower than specified threshold, Above: Show property shapes having support/confidence above than specified threshold.</p>
   <vaadin-radio-group id="psVaadinRadioGroup" style="margin-right: 5%; margin-left: 5%;"></vaadin-radio-group>
   <vaadin-text-field placeholder="Filter Property Shapes" id="psSearchField" style="width: 90%; align-self: flex-start; margin-left: 5%;" type="text">
    <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
   </vaadin-text-field>
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
};
ExtractionView = __decorate([
    customElement('extraction-view')
], ExtractionView);
export { ExtractionView };
//# sourceMappingURL=extraction-view.js.map