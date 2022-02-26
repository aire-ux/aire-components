import {Cell, Edge} from "@antv/x6";
import {Events} from "@antv/x6/lib/common/events";
import {Attr} from "@antv/x6/lib/registry";
import EventArgs = Cell.EventArgs;
import CellAttrs = Attr.CellAttrs;

export type NodeIdentifier = string | number | symbol;
export type EdgeDefinition = {
  id: string;
  source: string;
  target: string;
  template: Omit<Edge.Metadata, "source" | "target">;
}

export type NodeDefinition = {}


export type InvocationTarget = 'Graph' | 'graph';
export type Invocation = {
  target: InvocationTarget;
  body: any;
}

export type CellAttributes = {
  id: string;
  attributes: CellAttrs;

}

export type ListenerDefinition = {

  id: string;
  category: string;
  type: string;
  /**
   * whatever we're listening for
   */
  key: Events.EventNames<EventArgs>;

  /**
   * the target event to dispatch
   */
  targetEventType: string;

}

export type ListenerRegistration = Omit<ListenerDefinition,
    'targetEventType'>
