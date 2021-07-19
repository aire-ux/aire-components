import { html } from 'lit-html';
import '../aire-navbar.js';
export default {
    title: 'AireNavbar',
    component: 'aire-navbar',
    argTypes: {
        title: { control: 'text' },
        counter: { control: 'number' },
        textColor: { control: 'color' },
    },
};
const Template = ({ title = 'Hello world', counter = 5, textColor, slot, }) => html `
  <aire-navbar
    style="--aire-navbar-text-color: ${textColor || 'black'}"
    .title=${title}
    .counter=${counter}
  >
    ${slot}
  </aire-navbar>
`;
export const Regular = Template.bind({});
export const CustomTitle = Template.bind({});
CustomTitle.args = {
    title: 'My title',
};
export const CustomCounter = Template.bind({});
CustomCounter.args = {
    counter: 123456,
};
export const SlottedContent = Template.bind({});
SlottedContent.args = {
    slot: html `<p>Slotted content</p>`,
};
SlottedContent.argTypes = {
    slot: { table: { disable: true } },
};
//# sourceMappingURL=index.stories.js.map