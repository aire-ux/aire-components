import {css, customElement, LitElement, PropertyValues, query} from "lit-element";
import {EditorView, lineNumbers} from "@codemirror/view";
import {EditorState} from "@codemirror/state";
import {basicSetup} from "@codemirror/basic-setup";
// @ts-ignore
import {parser} from "@sunshower/breeze-lang"
import {
  foldInside,
  foldNodeProp, HighlightStyle,
  indentNodeProp,
  LanguageSupport,
  LRLanguage, syntaxHighlighting
} from "@codemirror/language"
import {styleTags, tags as t} from "@lezer/highlight"
import {completeFromList} from "@codemirror/autocomplete"


@customElement('aire-editor')
export class AireEditor extends LitElement {

  view: EditorView | undefined;


  @query("#editor")
  host: HTMLDivElement | undefined;


  static styles = css`
    div.cm-editor {
      margin-top: 4px;
      width:100%;
      height: 100%;
    }
  `;

  connectedCallback() {
    super.connectedCallback();
    let parserWithMetadata = parser.configure({
      props: [
        styleTags({
          Identifier: t.variableName,
          Boolean: t.bool,
          String: t.string,
          LineComment: t.lineComment,
          Number: t.number,
          "{ }": t.paren
        }),
        indentNodeProp.add({
          Block: context => {
            // context.node.
            let count = 0;
            for(let node = context.node; !!node; node = node.parent) {
              count++;
            }
            return 2 * count;

            // return context.column(context.node.from) + context.unit - 15
          }
        }),
        foldNodeProp.add({
          // block: foldInside
          Block: foldInside,
          Function: foldInside
        })
      ]
    })
    const myHighlightStyle = HighlightStyle.define([
      {tag: t.keyword, color: "orange", fontStyle: 'bold'},
      {tag: t.definitionKeyword, color: "green", fontStyle: 'bold'},
      {tag: t.string, color: "blue", fontStyle: 'italic'},
      {tag: t.definitionKeyword, color: "green"},

      {tag: t.bool, color: "red", fontStyle: 'bold'},
      {tag: t.comment, color: "#f5d", fontStyle: "italic"}
    ])


    const exampleLanguage = LRLanguage.define({
      parser: parserWithMetadata,
      languageData: {
        commentTokens: {line: "//"}
      }
    });

    const exampleCompletion = exampleLanguage.data.of({

      autocomplete: completeFromList([
        {label: "resource", type: "definition"},
        {label: "provider", type: "keyword"},
        {label: "module", type: "keyword"},
        {label: "variable", type: "keyword"},
        {label: "output", type: "keyword"},
        {label: "average", type: "function"},
        {label: "var", type: "keyword"},
        {label: "max", type: "function"},
        {label: "min", type: "function"}
        // {label: "car", type: "function"},
        // {label: "cdr", type: "function"}
      ])
    })
    const languageSupport = new LanguageSupport(exampleLanguage, [exampleCompletion])


    const state = EditorState.create({
      extensions: [basicSetup, lineNumbers(), languageSupport],
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