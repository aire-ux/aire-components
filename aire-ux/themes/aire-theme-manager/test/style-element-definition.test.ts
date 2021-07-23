import {expect, fixture, html} from '@open-wc/testing';
import AireThemeManager, {Registration} from "../src/AireThemeManager";
import {StyleElementDefinition} from "../src/StyleElementDefinition";
import {customElement, LitElement} from "lit-element";
import {html as litHtml, TemplateResult} from "lit-html";
import {StyleDefinition, ThemeDefinition} from "../src/Theme";
import {styleUrl} from "./utilities";


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

describe('PageStyleDefinition', () => {
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
    let styleDefinition = new StyleElementDefinition({
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
      expect(
          style
              .backgroundColor
              .replace(/\s/g, ""))
          .to
          .equal("rgb(255,0,0)"
          )
    } finally {
      if (registration) {
        registration.remove();
        const el = document
                .querySelector('test-element') as Element,
            style = window
                .getComputedStyle(el, 'background-color');

        expect(style
            .backgroundColor
            .replace(/\s/g, ""))
            .to
            .equal("rgba(0,0,0,0)")
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
    let styleDefinition = new StyleElementDefinition({
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
      expect(
          style
              .backgroundColor
              .replace(/\s/g, ""))
          .to
          .equal("rgb(255,0,0)")
    } finally {
      if (registration) {
        registration.remove();

        const el = document
                .querySelector('test-element') as Element,

            style = window
                .getComputedStyle(el, 'background-color');

        expect(style
            .backgroundColor
            .replace(/\s/g, "")).to.equal("rgba(0,0,0,0)")
      }
    }
  });


  /**
   * verify that a constructable style can be added and removed via URL
   */
  it('installs a constructable stylesheet correctly', async () => {
    await fixture<TestElement>(html`
      <test-element></test-element>`
    );
    const styleDef: StyleDefinition = {
      source: 'remote',
      mode: 'constructable',
      content: `
          div {
            background-color: red !important;
          }
      `,
      urlLoader: (url, method) => {
        return new Promise((resolve, reject) => {
          resolve(url);
        });
      },
    }

    const themeDefinition: ThemeDefinition = {
      styles: [styleDef],
      installationInstructions: {
        applicableTo: {
          matchingTagNames: ['test-element']
        }
      }
    }

    const themeManager = new AireThemeManager();
    await themeManager.installTheme(themeDefinition);


    try {

      const el = document.querySelector('test-element') as Element,
          sr = el.shadowRoot as ShadowRoot,
          child = sr.querySelector('div'),
          style = window.getComputedStyle(child as HTMLDivElement);
      expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgb(255,0,0)")
    } finally {
      themeManager.removeTheme();
      const el = document.querySelector('test-element') as Element,
          sr = el.shadowRoot as ShadowRoot,
          child = sr.querySelector('div'),
          style = window.getComputedStyle(child as HTMLDivElement);
      expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgba(0,0,0,0)")
    }
  });


  it('should install a stylesheet into the shadow dom correctly', async () => {


    await fixture<TestElement>(html`
      <test-element></test-element>`
    );
    let element = document.querySelector('test-element');
    expect(element).to.exist
    let styleDefinition = new StyleElementDefinition({
      source: 'inline',
      mode: 'constructable',
      content: styleUrl(`
          div {
            background-color: red !important;
          }
      `),
    });
    await styleDefinition.applyToElement(themeManager, element as Element);
    const el = document.querySelector('test-element') as Element,
        sr = el.shadowRoot as ShadowRoot,
        child = sr.querySelector('div'),
        style = window.getComputedStyle(child as HTMLDivElement);
    expect(style.backgroundColor.replace(/\s/g, "")).to.equal("rgb(255,0,0)")
  });


  it('should install a stylesheet into the shadow dom correctly', async () => {


    await fixture<TestElement>(html`
      <test-element></test-element>`
    );
    let element = document.querySelector('test-element');
    expect(element).to.exist
    let styleDefinition = new StyleElementDefinition({
      source: 'inline',
      mode: 'constructable',
      content: styleUrl(`
          div {
            background-color: red !important;
          }
      `),
    });
    await styleDefinition.applyToElement(themeManager, element as Element);
    const el = document.querySelector('test-element') as Element,
        sr = el.shadowRoot as ShadowRoot,
        child = sr.querySelector('div'),
        style = window.getComputedStyle(child as HTMLDivElement);
    expect(style
        .backgroundColor
        .replace(/\s/g, ""))
        .to
        .equal("rgb(255,0,0)")
  });
})
