import '@aire-designer/manager/AireCanvasManager';
import {AireCanvasManager} from "@aire-designer/manager/AireCanvasManager";
import {Condensation} from "@aire-ux/aire-condensation";

export {AireDesigner} from './AireDesigner.js';
export {AireCanvas} from './AireCanvas.js';

if (!window.context) {
  window.GlobalContext = Condensation.newContext();
}

if (!window.GlobalAireCanvasManager) {
  window.GlobalAireCanvasManager = window.GlobalContext?.create(AireCanvasManager);
}

