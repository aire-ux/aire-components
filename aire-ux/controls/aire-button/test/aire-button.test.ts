import { html, fixture, expect } from '@open-wc/testing';

import * as sinon from 'sinon';

import { AireButton } from '../src/AireButton.js';

describe('AireButton', () => {
  it('can be clicked', async () => {
    const f = sinon.fake();
    const el = await fixture<AireButton>(
      html`<aire-button @click="${f}"></aire-button>`
    );
    el.click();
    expect(f.calledOnce).to.be.true;
  });

  it('can have subchildren', async () => {
    const el = await fixture<AireButton>(
      html`<aire-button><div>Hello</div></aire-button>`
    );
    expect(el).shadowDom.to.be.accessible();
    expect(el?.querySelector('div')).to.exist;
  });
});
