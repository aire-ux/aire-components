import {ScriptElementDefinition} from "../src/ScriptElementDefinition";
import {ScriptDefinition} from "../src/Theme";
import AireThemeManager from "../src/AireThemeManager";
import {expect} from "@open-wc/testing";


describe('Script Definition', () => {

  let themeManager: AireThemeManager;

  beforeEach(() => {
    themeManager = new AireThemeManager();
  })


  it('installs a simple inline script correctly', async() => {

    let properties: ScriptDefinition = {
      source: 'inline',
      content: `
        var element = document.createElement('div');
        element.id = "test-element";
        document.body.appendChild(element);
      `
    }

    let script = new ScriptElementDefinition(properties);
    await script.install(themeManager);
    expect(document.getElementById('test-element')).to.exist
  });

})