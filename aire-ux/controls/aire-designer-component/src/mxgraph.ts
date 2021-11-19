import '@aire-ux/mxgraph';

export type mxConfiguration = {
  mxLoadResources: boolean;
  mxForceIncludes: boolean;
  mxLoadStylesheets: boolean;
  mxResourceExtension: string;
  mxProductionMode: boolean;
  mxBasePath: string;
};

/* eslint-disable */
// @ts-ignore
const factory = (cfg: Partial<mxConfiguration>) =>
  mxgraph({
    mxLoadResources: cfg.mxLoadResources || false,
    mxForceIncludes: cfg.mxForceIncludes || false,
    mxLoadStylesheets: cfg.mxLoadStylesheets || true,
    mxResourceExtension: cfg.mxResourceExtension || '.txt',
    mxProductionMode: cfg.mxProductionMode || true,
    mxBasePath: cfg.mxBasePath,
  });
/* eslint-enable */

export default factory;
