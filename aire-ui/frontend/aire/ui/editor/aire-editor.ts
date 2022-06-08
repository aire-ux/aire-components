import {css, customElement, LitElement, PropertyValues, query} from "lit-element";
import {terraform} from '@sunshower/breeze-lang'
import {EditorView} from "@codemirror/view";

@customElement('aire-editor')
export class AireEditor extends LitElement {

  // view: EditorView | undefined;


  @query("#editor")
  host: HTMLDivElement | undefined;




  static styles = css`
    div.cm-editor {
      margin-top: 4px;
      width:100%;
      height: 100%;
    }
  `;

  private view: EditorView | undefined;

  connectedCallback() {
    super.connectedCallback();
    this.view = terraform(this.shadowRoot!);
  }



  disconnectedCallback() {
    super.disconnectedCallback();
  }

}