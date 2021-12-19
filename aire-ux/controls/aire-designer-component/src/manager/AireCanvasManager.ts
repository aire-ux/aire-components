import '@aire-designer/globals';
import {AireCanvas} from '@aire-designer/AireCanvas';
import {Receive, Remotable} from "@aire-ux/aire-condensation";
import CanvasOptions from "@aire-designer/canvas-options";


@Remotable
export class AireCanvasManager {
  private readonly canvas: Map<string, AireCanvas>;

  constructor() {
    if (window.GlobalAireCanvasManager) {
      throw new Error("Error: AireCanvasManager has already been instantiated");
    }
    this.canvas = new Map<string, AireCanvas>();
  }

  initializeGraph(@Receive(CanvasOptions) options: CanvasOptions) : void {

  }
}
