import {Cell, Edge} from "@antv/x6";
import {Events} from "@antv/x6/lib/common/events";
import EventArgs = Cell.EventArgs;

export type NodeIdentifier = string | number | symbol;
export type EdgeDefinition = {
  id: string;
  source: string;
  target: string;
  template: Omit<Edge.Metadata, "source" | "target">;
}

export type NodeDefinition = {}


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
