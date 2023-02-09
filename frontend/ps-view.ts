import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';

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
 <vaadin-vertical-layout class="content" style="width: 90%; flex-grow: 1; flex-shrink: 1; flex-basis: auto; margin-left: 5%; margin-right: 5%;">
  <h4 style="align-self: center;">PS Analysis Dashboard</h4>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayout" style="align-self: stretch;"></vaadin-horizontal-layout>
  <vaadin-horizontal-layout theme="spacing" id="infoHorizontalLayoutTwo" style="align-self: stretch;"></vaadin-horizontal-layout>
  <h4>Selected Property Shape Info:</h4>
  <vaadin-grid style="align-self: stretch; flex-grow: 0; height: 15%; flex-shrink: 0;" is-attached multi-sort-priority="prepend" id="psConstraintsGrid"></vaadin-grid>
  <h4>Property Shape Syntax (SHACL)</h4>
  <vaadin-text-area label="SHACL Syntax" id="psSyntaxTextArea" style="align-self: stretch;"></vaadin-text-area>
  <h4>Actions</h4>
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <div style="width: 33%; align-self: flex-end;">
    <p>Generate SPARQL query to get entities (triples) extracting this property shape constraint. </p>
    <vaadin-button tabindex="0" theme="primary" id="buttonA">
      Execute Query 
    </vaadin-button>
   </div>
   <div style="width: 33%; align-self: flex-end;">
    <p>Generate SPARQL Query to get erroneous triples </p>
    <vaadin-button tabindex="0" theme="primary" id="buttonB">
      Execute Query 
    </vaadin-button>
   </div>
   <div style="width: 33%; align-self: flex-end;">
    <p>Clean the Graph... How?</p>
    <vaadin-button tabindex="0" theme="primary" id="buttonC">
      Execute Query 
    </vaadin-button>
   </div>
  </vaadin-horizontal-layout>
 </vaadin-vertical-layout>
 <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color: var(--lumo-contrast-10pct);"></vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
