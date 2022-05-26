import {css, customElement, html, LitElement, property} from 'lit-element';


@customElement('aire-split-panel')
export class SplitPanel extends LitElement {


  static styles = css`
    section {
      width: 100%;
      height: 100%;
      display: flex;
    }
    
    section.horizontal {
      flex-direction: row;
    }
    
    section.vertical {
      flex-direction: column;
    }
    
    section article:first-of-type {
      border-right: 1px solid #AAA;
    }
    
    article {
      flex: 1 1;
      padding: var(--lumo-space-l);
    }
    
  `

  @property({
    reflect: true,
    attribute: 'type'
  })
  private type: Type | undefined = 'horizontal';


  render() {
    return html`
      <section class="${this.type}">
        <article>
          <slot name="first"></slot>
        </article>
        <article>
          <slot name="second"></slot>
        </article>
      </section>
    `;
  }
}

type Type = 'horizontal' | 'vertical'
