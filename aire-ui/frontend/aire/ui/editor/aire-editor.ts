import {css, customElement, LitElement, PropertyValues, query} from "lit-element";
import {terraform} from '@sunshower/breeze-lang'
import {EditorView} from "@codemirror/view";
import {Dynamic, Receive, Remotable, Remote} from "@aire-ux/aire-condensation";

@Remotable
@customElement('aire-editor')
export class AireEditor extends LitElement {

  // view: EditorView | undefined;


  @query("#editor")
  host: HTMLDivElement | undefined;




  static styles = css`
    div.cm-editor {
      margin-top: 4px;
      height: calc(100% - 4px);
    }
  `;

  private view: EditorView | undefined;

  connectedCallback() {
    super.connectedCallback();
    this.view = terraform(this.shadowRoot!);
  }



  disconnectedCallback() {
    super.disconnectedCallback();
    this.view?.destroy();
  }

  @Remote
  public getContents() : string | undefined {
    return this.view?.state.doc.toString();
  }

  @Remote
  public setContents(@Receive(Dynamic) contents: string) : void {
    const state = this.view?.state!;
    const tx = state.update({
      changes:{
        from: 0,
        to: state.doc.length,
        insert: contents
      }
    });
    this.view?.dispatch(tx);
  }

}