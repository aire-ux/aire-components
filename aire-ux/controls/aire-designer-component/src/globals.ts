import {Context} from "@aire-ux/aire-condensation";
import {AireCanvasManager} from "@aire-designer/manager/AireCanvasManager";

declare global {
  interface Window {
    GlobalContext: Context | undefined;
    GlobalAireCanvasManager: AireCanvasManager | undefined;
  }
}
