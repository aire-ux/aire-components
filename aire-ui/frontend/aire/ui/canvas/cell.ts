import {Edge} from "@antv/x6";

export type NodeIdentifier = string | number | symbol;
export type EdgeDefinition = {
  source: string;
  target: string;
  template: Omit<Edge.Metadata, "source" | "target">;
}

export type NodeDefinition = {


}
