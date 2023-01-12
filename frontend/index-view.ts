import {LitElement, html, css, customElement} from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/select/src/vaadin-select.js';
import '@vaadin/list-box/src/vaadin-list-box.js';
import '@vaadin/item/src/vaadin-item.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('index-view')
export class IndexView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; align-items: center; justify-content: center;">
 <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; align-items: stretch; align-self: center;" id="header">
  <h3 id="title" style="align-self: center; flex-grow: 1; padding: var(--lumo-space-s); flex-shrink: 1; text-align:center">SHACTOR : SHapes ExtrACTOR from very large Knowledge Graphs</h3>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto;">
  <br>
  <h6 style="align-self: center; margin: var(--lumo-space-s); margin-left: var(--lumo-space-m); width: 50%;">SHACTOR is a tool which helps extracting SHACL shapes from very large Knowledge Graphs. Please select one of the following options.</h6>
  <br>
  <vaadin-select value="Item one" id="vaadinSelect" style="align-self: flex-start; width: 50%; margin-left: 25%;" label="Select from existing datasets">
   <template>
    <vaadin-list-box>
     <vaadin-item>
       Item one 
     </vaadin-item>
     <vaadin-item>
       Item two 
     </vaadin-item>
     <vaadin-item>
       Item three 
     </vaadin-item>
    </vaadin-list-box>
   </template>
  </vaadin-select>
  <br>
  <vaadin-button theme="primary" id="continueButton" style="margin-left: 25%; align-self: flex-start;" tabindex="0">
    Continue 
  </vaadin-button>
  <br>
  <vaadin-text-field label="Enter Graph URL (in .NT format)" id="graphUrl" style="align-self: center; flex-grow: 0; margin: var(--lumo-space-s); padding: var(--lumo-space-m); flex-shrink: 0; width: 50%;" type="text" name="graphUrl">
    Text 
  </vaadin-text-field>
  <vaadin-button theme="primary" id="uploadGraphButton" style="align-self: flex-start; margin-left: 25%;" tabindex="0">
    Upload Graph 
  </vaadin-button>
  <h6 style="align-self: flex-start; flex-grow: 0; margin-left: 25%;">Or, if you want to connect to a SPARQL endpoint</h6>
  <vaadin-text-field label="Enter address of a SPARQL endpoint" id="graphEndpointUrl" style="align-self: center; flex-grow: 0; margin: var(--lumo-space-s); padding: var(--lumo-space-m); width: 50%;" type="text" name="graphEndpointUrl">
    Text 
  </vaadin-text-field>
  <vaadin-button theme="primary" id="graphEndpointButton" style="align-self: flex-start; margin-left: 25%;" tabindex="0">
    Connect 
  </vaadin-button>
  <h6 style="margin-left: 25%;">Or, if you already have SHACL shapes, upload them here to analyze with SHACTOR</h6>
  <vaadin-text-field label="Enter Shapes File URL (in .TTL format)" style="align-self: center; width: 50%; margin: var(--lumo-space-s); padding: var(--lumo-space-m);" type="text"></vaadin-text-field>
  <vaadin-button theme="primary" style="margin-left: 25%;" tabindex="0">
   Upload Shapes
  </vaadin-button>
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
