import {expect} from '@open-wc/testing';

import * as AireThemeManager from '../src/AireThemeManager.js';

import {
  mock,
  SinonMock
} from 'sinon';


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
    for(let i = 0; i < links.length; i++) {
      links[i].remove();
    }
  });


  it('installs a link correctly', async () => {
    let style = {
          id: 'styleid',
          url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0='
        },
        selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0)
    AireThemeManager.Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1)
  });


  it('removes its links correctly', async () => {
    let style = {
          id: 'styleid',
          url: 'data:text/css;base64,ZGl2LnJlZCB7CmNvbG9yOiByZWQ7Cn0='
        },
        selector = 'link:not(link[crossorigin])';
    expect(document.querySelectorAll(selector).length).to.eq(0)
    AireThemeManager.Aire.installStyles([style]);
    expect(document.querySelectorAll(selector).length).to.eq(1)
    AireThemeManager.Aire.uninstallStyles();
    expect(document.querySelectorAll(selector).length).to.eq(0)
  });

});
