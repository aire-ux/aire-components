import { Class } from "@condensation/types";
import { Condensation } from "@condensation/condensation";

export function Remotable<T>(type: Class<T>): Class<T> {
  Condensation.remoteRegistry.register(type);
  return type;
}

export function Receive<T>(type: Class<T>) {
  return <U>(target: Class<U>, key: PropertyKey, index: number) => {
    if (!key) {
      Condensation.remoteRegistry.defineParameter(target, {
        type: type,
        index: index,
        invocationType: "constructor",
        invocationTarget: "constructor",
      });
    } else {
      Condensation.remoteRegistry.defineParameter((target as any).constructor, {
        type: type,
        index: index,
        invocationTarget: key,
        invocationType: "method",
      });
    }
  };
}
