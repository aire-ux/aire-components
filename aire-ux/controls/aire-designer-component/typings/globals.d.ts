import {mxGraphExportObject} from '@aire-ux/mxgraph';

declare global {
  interface Window {
    mx: mxGraphExportObject;
  }
}