export type VertexTemplate = {
  name: string;
  width: number;
  height: number;
  attrs: Map<String, Map<String, any>>;
  selectors: Array<{ tagName: string, selector: string }>
}