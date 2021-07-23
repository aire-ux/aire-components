import {expect, fixture, html} from '@open-wc/testing';
import {customElement, LitElement} from 'lit-element';
import {html as litHtml, TemplateResult} from 'lit-html';
import AireThemeManager, {Registration} from "../src/AireThemeManager";
import {StyleElementDefinition} from "../src/StyleElementDefinition";
import {encode} from 'js-base64';



describe('AireThemeManager', () => {
  let themeManager: AireThemeManager;
  beforeEach(() => {
    themeManager = new AireThemeManager();
  })

})
