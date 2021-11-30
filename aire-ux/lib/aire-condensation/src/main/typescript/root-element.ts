import {Class} from "@condensation/types";
import {Condensation} from "@condensation/condensation";


export type RootElementConfiguration = {
  alias: string
}

export function Configuration<T>(cfg: RootElementConfiguration): any {
  const reg = Condensation.typeRegistry;
  return function (type: Class<T>): Class<T> {
    if (!reg.contains(type)) {
      RootElement(type);
    }
    reg.configure(type, cfg);
    return type as Class<T>;
  }
}

export function RootElement<T>(type: Class<T>): Class<T> {
  const reg = Condensation.typeRegistry;
  if (!reg.contains(type)) {
    reg.register(type);
  }
  return type;
}