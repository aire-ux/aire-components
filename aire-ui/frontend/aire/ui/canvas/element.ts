import {RootElement, Property} from "@aire-ux/aire-condensation";
import {Cell} from "@antv/x6";
import Properties = Cell.Properties;

export class Element {

}

@RootElement
export class Vertex {


  @Property()
  private x: number | undefined;

  @Property()
  private y: number | undefined;

  @Property()
  private width: number | undefined;

  @Property()
  private height : number | undefined;

  @Property()
  private label: string | undefined;
}