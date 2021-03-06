import {css, customElement, html, LitElement} from "lit-element";

@customElement('aire-application-layout')
export class ApplicationLayout extends LitElement {

  static styles = css`
    main > section {
      position: relative;
      display: flex;
    }
    main > section > slot[name="content"] {
      display: flex;
      flex-grow:1;
      margin-left:6px; // account for gutter
    }
  `;

  render() {
    return html`
      <main part="main">
        <!--
        top slot
        typically used for primary (context-free)
        navigation
        
        Default width is 100% of parent
        -->
        <slot
            name="top"
            part="top">
        </slot>

        <section part="content-parent">

          <!--
          left navigation
          
          Default is 100% height - (height (top))
          
          Width is 40px;
          -->
          <slot
              name="navigation"
              part="navigation">
          </slot>


          <!--
          primary content
          
          default is height - top(height)
          width = width - nav(width)
          -->
          <slot name="content"></slot>


        </section>


        <!--
          should be a footer element
        -->
        <slot name="bottom" part="bottom"></slot>

      </main>`
  }
}