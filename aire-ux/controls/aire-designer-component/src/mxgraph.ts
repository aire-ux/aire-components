import "@aire-ux/mxgraph";
import {mxGraphExportObject, mxGraphOptions} from '@aire-ux/mxgraph'
import {Property, Receive, Remotable, RootElement} from "@aire-ux/aire-condensation";

export type aireCanvasOptions = Partial<mxGraphOptions>

@RootElement
export class GraphConfiguration {
  @Property({
    type:String,
    read: {
      alias: 'load-resources'
    }
  })
  loadResources: string;


  @Property({
    type:Boolean,
    read: {
      alias: 'force-includes'
    }
  })
  forceIncludes: string;


  @Property({
    type:Boolean,
    read: {
      alias: 'load-stylesheets'
    }
  })
  loadStylesheets: boolean;


  @Property({
    type:String,
    read: {
      alias: 'resource-extension'
    }
  })
  resourceExtension: boolean;


  @Property({
    type:Boolean,
    read: {
      alias: 'production-mode'
    }
  })
  productionMode: boolean;


  @Property({
    type:String,
    read: {
      alias: 'base-path'
    }
  })
  basePath: string;
}

declare global {
  interface Window {
    mx: mxGraphExportObject;
  }
}



@Remotable
export class MxGraphManager {

  constructor(@Receive(GraphConfiguration) readonly configuration: GraphConfiguration) {
    if(!window.mx) {
      window.mx = construct(configuration);
    }
  }
}

function construct(cfg: GraphConfiguration) {
  // @ts-ignore
  return mxgraph({
    mxLoadResources: cfg.loadResources || true,
    mxForceIncludes: cfg.forceIncludes || false,
    mxLoadStylesheets: cfg.loadStylesheets || true,
    mxResourceExtension: cfg.resourceExtension || '.txt',
    mxBasePath: cfg.basePath
  });

}


// import '@aire-ux/mxgraph'
// // @ts-ignore
// import * as mx from '@aire-ux/mxgraph';
//
// export type mxConfiguration = {
//   mxLoadResources: boolean;
//   mxForceIncludes: boolean;
//   mxLoadStylesheets: boolean;
//   mxResourceExtension: string;
//   mxProductionMode: boolean;
//   mxBasePath: string;
// };
// console.log(mx);
//
// /* eslint-disable */
// const factory = (cfg: Partial<mxConfiguration>) : mx.mxGraphExportObject =>
//   // @ts-ignore
//
//   mxgraph({
//     mxLoadResources: cfg.mxLoadResources || false,
//     mxForceIncludes: cfg.mxForceIncludes || false,
//     mxLoadStylesheets: cfg.mxLoadStylesheets || true,
//     mxResourceExtension: cfg.mxResourceExtension || '.txt',
//     mxProductionMode: cfg.mxProductionMode || true,
//     mxBasePath: cfg.mxBasePath,
//   });
// /* eslint-enable */
//
// export default factory;
