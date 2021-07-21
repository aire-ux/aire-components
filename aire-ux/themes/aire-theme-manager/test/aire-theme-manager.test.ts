import {expect, fixture, html} from '@open-wc/testing';
import {customElement, LitElement} from 'lit-element';
import {html as litHtml, TemplateResult} from 'lit-html';
import AireThemeManager, {Registration} from "../src/AireThemeManager";
import {PageStyleDefinition} from "../src/PageStyleDefinition";


@customElement('test-element')
export class TestElement extends LitElement {
  connectedCallback() {
    super.connectedCallback();
  }

  protected render(): TemplateResult {
    return litHtml`
      <div>
        hello
      </div>
    `;
  }
}


describe('AireThemeManager', () => {
  let themeManager: AireThemeManager;
  beforeEach(() => {
    themeManager = new AireThemeManager();
  })

  /**
   * ensure that a simple stylesheet with text content is installed
   */
  it("installs a simple CSS stylesheet correctly", async () => {
    const element = await fixture<TestElement>(html`
      <test-element></test-element>`
    );

    let styleDefinition = new PageStyleDefinition({
      content: `
        test-element {
          background-color: red !important;
        }
        `
    });
    let registration: Registration | null = null;
    try {
      registration = await themeManager.addStyleDefinition(styleDefinition);
      const el = document.querySelector('test-element') as Element,
          style = window.getComputedStyle(el, 'background-color');
      expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgb(255,0,0)")
    } finally {
      if(registration) {
        registration.remove();
        const el = document.querySelector('test-element') as Element,
            style = window.getComputedStyle(el, 'background-color');
        expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgba(0,0,0,0)")
      }
    }
  });
})
