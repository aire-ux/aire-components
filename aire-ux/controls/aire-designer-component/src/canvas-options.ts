import {mxGraphOptions} from "@aire-ux/mxgraph";
import {Property, RootElement} from "@aire-ux/aire-condensation";

@RootElement
export default class CanvasOptions implements mxGraphOptions {

  @Property({
    type: String,
    read: {
      alias: 'base-path'
    }
  })
  public mxBasePath: string | undefined;


  public get basePath() : string | undefined {
    return this.mxBasePath;
  }


  @Property({
    type: String,
    read: {
      alias: 'image-base-path'
    }
  })
  public mxImageBasePath: string | undefined;



}