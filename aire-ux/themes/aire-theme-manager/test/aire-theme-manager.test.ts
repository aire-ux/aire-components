import {expect, fixture, html} from '@open-wc/testing';
import {customElement, LitElement} from 'lit-element';
import {html as litHtml, TemplateResult} from 'lit-html';
import AireThemeManager, {Registration} from "../src/AireThemeManager";
import {PageStyleDefinition} from "../src/PageStyleDefinition";
import { encode } from 'js-base64';

const styleUrl = (content: string) =>
    "data:text/css;base64," + encode(content)

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
    await fixture<TestElement>(html`
      <test-element></test-element>`
    );
    let styleDefinition = new PageStyleDefinition({
      mode: 'page',
      source: 'inline',
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
      if (registration) {
        registration.remove();
        const el = document.querySelector('test-element') as Element,
            style = window.getComputedStyle(el, 'background-color');
        expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgba(0,0,0,0)")
      }
    }
  });


  /**
   * verify that a style can be added and removed via URL
   */
  it('installs a CSS from a URL correctly', async () => {

    await fixture<TestElement>(html`
      <test-element></test-element>`
    );
    let styleDefinition = new PageStyleDefinition({
      mode: 'page',
      source: 'remote',
      content: styleUrl(`
          test-element {
            background-color: red !important;
          }
      `)
    });
    let registration: Registration | null = null;
    try {
      registration = await themeManager.addStyleDefinition(styleDefinition);
      const el = document.querySelector('test-element') as Element,
          style = window.getComputedStyle(el, 'background-color');
      expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgb(255,0,0)")
    } finally {
      if (registration) {
        registration.remove();
        const el = document.querySelector('test-element') as Element,
            style = window.getComputedStyle(el, 'background-color');
        expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgba(0,0,0,0)")
      }
    }
  });
})
