import {Class} from "@condensation/types";
import {Condensation} from "@condensation/condensation";


export function Remotable<T>(type: Class<T>): Class<T> {
  Condensation.remoteRegistry.register(type);
  return type;
}

export function Receive(type: Class<any>, key: PropertyKey, index: number) {
  Condensation.remoteRegistry.defineParameter(type, {index});

}
