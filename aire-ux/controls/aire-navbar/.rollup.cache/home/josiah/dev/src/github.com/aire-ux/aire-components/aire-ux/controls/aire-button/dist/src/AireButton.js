import { __decorate } from "tslib";
import { customElement, html, LitElement, property } from 'lit-element';
import { dynamicallyThemeable } from "@aire-ux/aire-theme-decorators";
let AireNavbar = class AireNavbar extends LitElement {
    render() {
        return html `
      <navbar
          type="${this.type}"
          @click="${this.click}"
          class="${this.classes}"
      >
        <slot></slot>
      </navbar>
    `;
    }
};
__decorate([
    property({
        reflect: true,
        attribute: true
    })
], AireNavbar.prototype, "classes", void 0);
__decorate([
    property({
        reflect: true,
        attribute: true
    })
], AireNavbar.prototype, "type", void 0);
AireNavbar = __decorate([
    customElement('aire-navbar'),
    dynamicallyThemeable
], AireNavbar);
export { AireNavbar };
//# sourceMappingURL=AireNavbar.js.map