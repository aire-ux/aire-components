import {css, customElement, html, LitElement, PropertyValues, query} from "lit-element";
import {EditorView, gutter, lineNumbers} from "@codemirror/view";
import {EditorState} from "@codemirror/state";

@customElement('aire-editor')
export class AireEditor extends LitElement {

  view: EditorView | undefined;


  @query("#editor")
  host: HTMLDivElement | undefined;



  static styles = css`
    div.cm-editor {
      width:100%;
      height:100%;
    }
  `;

  connectedCallback() {
    super.connectedCallback();
    const state = EditorState.create({
      extensions: [lineNumbers()]
    });
    const view = new EditorView({
      state: state,
      parent: this.shadowRoot!,
    });

  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  protected firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);
    this.view = new EditorView({
      root: this.shadowRoot!
    });
  }
}