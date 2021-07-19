import {customElement, html, LitElement, property, PropertyValues} from 'lit-element';

// @ts-ignore
import style from "../styles/aire-button.scss"
import {dynamicallyThemeable} from "@aire-ux/aire-theme-decorators";
import {isOutsideRootDir} from "@web/dev-server-rollup/dist/utils";

@customElement('aire-button')
@dynamicallyThemeable
export class AireButton extends LitElement {

  /**
   * set the style classes for this element
   */
  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  /**
   * set the type of this button
   */
  @property({
    reflect: true,
    attribute: true
  })
  type: String;

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    let nodes = document.querySelectorAll('aire-button');
    nodes.forEach(node => {
      console.log(node.querySelector('button'));
    });
  }


  render() {
    return html`
      <button
          type="${this.type}"
          @click="${this.click}"
          class="${this.classes}"
          mdbRipple
      >

        <slot></slot>
      </button>
    `;
  }
}
