import {customElement, html, LitElement, property} from 'lit-element';

// @ts-ignore
import style from "../styles/aire-navbar.scss"
import {dynamicallyThemeable} from "@aire-ux/aire-theme-decorators";

@customElement('aire-navbar')
@dynamicallyThemeable
export class AireNavbar extends LitElement {

  /**
   * set the style classes for this element
   */
  @property({
    reflect: true,
    attribute: true
  })
  classes: String;


  /**
   * set the type of this navbar
   */
  @property({
    reflect: true,
    attribute: true
  })
  type: String;


  render() {
    return html`
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <slot></slot>
      </nav>
    `;
  }
}
