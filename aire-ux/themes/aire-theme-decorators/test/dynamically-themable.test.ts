import {expect, fixture, html} from '@open-wc/testing';


import {mock, SinonMock} from 'sinon';
import {customElement, LitElement} from 'lit-element';
import {html as litHtml, TemplateResult} from "lit-html";
import {Aire} from '@aire-ux/aire-theme-manager';
import {dynamicallyThemeable} from "../src/dynamicallyThemeable";
import Mode = Aire.Mode;

@customElement('test-element')
@dynamicallyThemeable
export class TestElement extends LitElement {

  connectedCallback() {
    super.connectedCallback();
    console.log("SUP");
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
  let element: HTMLElement,
      mockedElement: SinonMock | null = null;


  before(() => {
    const createElement = document.createElement;
    document.createElement = (name: string, options?: ElementCreationOptions): HTMLElement => {
      element = createElement.call(document, name, options);
      if (name === 'link') {
        mockedElement = mock(element);
        return element;
      }
      return element;
    }
  });


  afterEach(() => {
    let selector = 'link:not(link[crossorigin])',
        links = document.querySelectorAll(selector);
    for (let i = 0; i < links.length; i++) {
      links[i].remove();
    }
  });


  it('installs a link correctly', async () => {
    let style = {
          order: 0,
          id: 'styleid',
          url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0='
        },
        selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0)
    Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1)
  });


  it('removes its links correctly', async () => {
    let style = {
          id: 'styleid',
          order: 0,
          mode: Mode.Global,
          url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0='
        },
        selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0)
    Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1)
    Aire.uninstallStyles();
    expect(document.querySelectorAll(selector).length).to.eq(0)
  });

  it('installs constructable styles correctly', async () => {
    const el = await fixture<TestElement>(
        html`
          <test-element></test-element>`
    );
    let style = {
      id: 'styleid',
      order: 0,
      mode: Mode.Constructable,
      url: 'data:text/css;base64,ZGl2IHsKICAgIGNvbG9yOiByZWQ7CiAgICBkaXNwbGF5OiBmbGV4OwogICAgeDonMjAwcHgnOwp9='
    };
    Aire.installStyles([style]);
  });


});
