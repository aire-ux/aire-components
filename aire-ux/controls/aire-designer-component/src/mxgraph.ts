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
  private loadResources: string;


  @Property({
    type:Boolean,
    read: {
      alias: 'force-includes'
    }
  })
  private forceIncludes: string;


  @Property({
    type:Boolean,
    read: {
      alias: 'force-includes'
    }
  })
  private loadStylesheets: boolean;


  @Property({
    type:String,
    read: {
      alias: 'resource-extension'
    }
  })
  private resourceExtension: boolean;


  @Property({
    type:Boolean,
    read: {
      alias: 'production-mode'
    }
  })
  private productionMode: boolean;


  @Property({
    type:String,
    read: {
      alias: 'base-path'
    }
  })
  private basePath: string;
}

@Remotable
class MxGraphManager {

  constructor(@Receive(GraphConfiguration) readonly configuration: GraphConfiguration) {
  }

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
