import {mxGraphExportObject, mxGraphOptions} from '@aire-ux/mxgraph'

export type aireCanvasOptions = Partial<mxGraphOptions>
export default function initializeAireCanvas(

) : void {

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
