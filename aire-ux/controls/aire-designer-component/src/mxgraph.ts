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
const factory = (cfg: Partial<mxConfiguration>) =>
  // @ts-ignore
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
