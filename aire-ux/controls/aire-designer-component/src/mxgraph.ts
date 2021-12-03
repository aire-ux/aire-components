import { mxGraphExportObject, mxGraphOptions } from '@aire-ux/mxgraph';
import { Receive, Remotable } from '@aire-ux/aire-condensation';
import GraphConfiguration from '@aire-designer/transfer/configuration/GraphConfiguration';

export type aireCanvasOptions = Partial<mxGraphOptions>;

declare global {
  interface Window {
    mx: mxGraphExportObject;
  }
}

/* eslint-disable */
function construct(cfg: GraphConfiguration) {
  // @ts-ignore
  return mxgraph({
    mxLoadResources: cfg.loadResources || true,
    mxForceIncludes: cfg.forceIncludes || false,
    mxLoadStylesheets: cfg.loadStylesheets || true,
    mxResourceExtension: cfg.resourceExtension || '.txt',
    mxProductionMode: cfg.productionMode || true,
    mxBasePath: cfg.basePath,
  });
}
/* eslint-enable */

@Remotable
export class MxGraphManager {
  constructor(
    @Receive(GraphConfiguration) readonly configuration: GraphConfiguration
  ) {
    if (!window.mx) {
      window.mx = construct(configuration);
    }
  }
}
