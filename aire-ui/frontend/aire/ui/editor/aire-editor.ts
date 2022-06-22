import {css, customElement, LitElement, PropertyValues, query} from "lit-element";
import {terraform} from '@sunshower/breeze-lang'
import {EditorView} from "@codemirror/view";
import {Receive, Remotable, Remote,} from "@aire-ux/aire-condensation";


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

  private contents: string | undefined;

  connectedCallback() {
    super.connectedCallback();
    this.view = terraform(this.shadowRoot!);
    this.refreshContents();
  }


  protected updated(_changedProperties: PropertyValues) {
    super.updated(_changedProperties);
    this.refreshContents();
  }


  private refreshContents() : void {
    const state = this.view?.state!;
    const view = this.view!;
    const contents = this.contents;

    const tx = state.update({
      changes: {
        from: 0,
        to: state.doc.length,
        insert: contents
      }
    });
    view.dispatch(tx);

  }

  disconnectedCallback() {
    super.disconnectedCallback();
    this.view?.destroy();
  }

  @Remote
  public getContents(): string | undefined {
    return this.view?.state.doc.toString();
  }

  @Remote
  public setContents(@Receive(String) contents: string): void {
    this.contents =
        contents.charAt(0) === '"'
        && contents.charAt(contents.length - 1) === '"'
            ? contents.slice(1, -1) : contents;
    this.requestUpdate();
  }

}