import {css, customElement, html, LitElement, property,} from 'lit-element';

// @ts-ignore
import {styles} from '../themes/base/aire-button'

/**
 *
 */
export type Variant = '';

/**
 * defaults to text
 */
export type Type =
/**
 * text: text button.  Normal importance
 * the default value
 */
    'text'
    /**
     * outlined:
     * medium importance
     */
    | 'outlined'

    /**
     * contained: high importance
     */
    | 'contained'

    /**
     * toggle--typically used with other
     * buttons to indicate state
     */
    | 'toggle';

export type Size =
    'x-small'
    | 'small'
    | 'medium'
    | 'large'
    | 'x-large'
    | 'cta';


@customElement('aire-button')
export class AireButton extends LitElement {


  static get styles() {
    return css`${styles}`;
  }




  @property({
    reflect: true,
    attribute: true
  })
  disabled: boolean;


  @property({
    reflect: true,
    attribute: true
  })
  type: Type;

  render() {
    return html`
      <button ?disabled="${this.disabled}" type="${this.type || 'default'}">
        <slot></slot>
      </button>
    `;
  }
}
