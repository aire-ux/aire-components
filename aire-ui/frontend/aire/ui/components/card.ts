import {css, customElement, html, LitElement, property} from "lit-element";
import {HTMLTemplateResult} from "lit";

@customElement('aire-card')
export class Card extends LitElement {

  static styles = css`
    article {
      width: 196px;
      height: 132px;
      margin-top: 32px;
      margin-left:32px;
      border-radius: 8px;
      position: relative;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      transition: box-shadow .3s;

    }
    
    div.icon {
      width: 40px;
      height: 40px;
      position: absolute;
      border-radius: 8px;
      top: -20px;
      left: 20px;
      z-index: 1;
      background-color: white;
      transition: box-shadow .3s;
    }
    
    div.header {
      height:20px;
      margin-left:60px;
    }
    div.footer {
      height: 32px;
    }
    
    div.content {
      flex: 1 1;
    }
  `

  @property({reflect: true, attribute: true})
  private color: string | undefined = '#660066';

  render(): HTMLTemplateResult {
    return html`
      <style>
        article {
          border: 1px solid ${this.color};
        }
        article:hover {
          box-shadow: 0 0 11px rgba(66,00,66,.3);
        }
        
        article:hover div.icon {
          box-shadow: 0 0 11px rgba(66,00,66,.3);
        }
        
        div.icon {
          border: 1px solid ${this.color}
        }
        div.footer {
          border-top: 1px solid ${this.color}
        }
      </style>
      
      <article part="content">
        <div class="icon"></div>
        
        <div class="header" part="header">
          
        </div>
        <div class="content" part="content">
          
        </div>
        
        <div class="footer" part="footer">
          <slot name="footer">

          </slot>
        </div>
      </article>

    `;
  }

}