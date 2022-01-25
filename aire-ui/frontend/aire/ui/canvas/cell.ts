import {RootElement, Property} from "@aire-ux/aire-condensation";
import {Node} from "@antv/x6";

export class Cell {

}

@RootElement
export class Vertex implements Node.Metadata {

  @Property(Number)
  x: number | undefined;

  @Property(Number)
  y: number | undefined;

  @Property(Number)
  width: number | undefined;

  @Property(Number)
  height : number | undefined;

  @Property(String)
  label: string | undefined;
}