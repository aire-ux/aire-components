import { expect, fixture, html } from '@open-wc/testing';
import { customElement, LitElement } from 'lit-element';
import { html as litHtml, TemplateResult } from 'lit-html';
import { adoptStyles } from 'lit';
import * as AireThemeManager from '../src/AireThemeManager.js';
import { Aire } from '../src/AireThemeManager.js';
import Mode = Aire.Mode;
import EventType = Aire.EventType;

@customElement('test-element')
export class TestElement extends LitElement {
  connectedCallback() {
    super.connectedCallback();
    Aire.addStyleChangeListener(EventType.CustomStyleAdded, event =>
      adoptStyles(this.shadowRoot as ShadowRoot, [event.detail.styleDefinition])
    );
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
  afterEach(() => {
    const selector = 'link:not(link[crossorigin])';
    const links = document.querySelectorAll(selector);
    for (let i = 0; i < links.length; i += 1) {
      links[i].remove();
    }
  });

  it('installs a link correctly', async () => {
    const style = {
      id: 'styleid',
      url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0=',
    };
    const selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0);
    AireThemeManager.Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1);
  });

  it('removes its links correctly', async () => {
    const style = {
      id: 'styleid',
      mode: Mode.Global,
      url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0=',
    };
    const selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0);
    AireThemeManager.Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1);
    AireThemeManager.Aire.uninstallStyles();
    expect(document.querySelectorAll(selector).length).to.eq(0);
  });

  it('installs constructable styles correctly', async () => {
    await fixture<TestElement>(html` <test-element></test-element>`);
    const style = {
      id: 'styleid',
      mode: Mode.Constructable,
      url: 'data:text/css;base64,ZGl2IHsKICAgIGNvbG9yOiByZWQ7CiAgICBkaXNwbGF5OiBmbGV4OwogICAgeDonMjAwcHgnOwp9==',
    };
    AireThemeManager.Aire.installStyles([style]);
    AireThemeManager.Aire.uninstallStyles([style]);
  });
});
