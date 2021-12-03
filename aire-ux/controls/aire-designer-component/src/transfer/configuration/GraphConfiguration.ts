import { Property, RootElement } from '@condensation/index';

@RootElement
export default class GraphConfiguration {
  @Property({
    type: String,
    read: {
      alias: 'load-resources',
    },
  })
  loadResources: string;

  @Property({
    type: Boolean,
    read: {
      alias: 'force-includes',
    },
  })
  forceIncludes: string;

  @Property({
    type: Boolean,
    read: {
      alias: 'load-stylesheets',
    },
  })
  loadStylesheets: boolean;

  @Property({
    type: String,
    read: {
      alias: 'resource-extension',
    },
  })
  resourceExtension: boolean;

  @Property({
    type: Boolean,
    read: {
      alias: 'production-mode',
    },
  })
  productionMode: boolean;

  @Property({
    type: String,
    read: {
      alias: 'base-path',
    },
  })
  basePath: string;
}
