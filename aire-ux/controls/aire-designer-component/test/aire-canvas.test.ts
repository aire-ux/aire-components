import { html, fixture, expect } from '@open-wc/testing';

import { AireCanvas } from '../src/AireCanvas.js';
// import '../aire-button.js';

describe('AireCanvas', () => {
  it('has a default title "Hey there" and counter 5', async () => {
    const el = await fixture<AireCanvas>(html`<aire-canvas></aire-canvas>`);
    expect(el.graph).not.to.be.null
  });
});
