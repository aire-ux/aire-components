import {customElement, html, LitElement} from "lit-element";

@customElement('aire-application-layout')
export class ApplicationLayout extends LitElement {

  render() {
    return html`
      <main>
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

        <article>
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


        </article>


        <!--
          should be a footer element
        -->
        <slot name="bottom"></slot>

      </main>`
  }
}