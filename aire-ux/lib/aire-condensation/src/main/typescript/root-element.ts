import "reflect-metadata";
import { Class } from "@condensation/types";
import { Condensation } from "@condensation/condensation";

export type RootElementConfiguration = {
  alias: string;
};

export function Configuration<T>(cfg: RootElementConfiguration): any {
  const reg = Condensation.typeRegistry;
  return function (type: Class<T>): Class<T> {
    if (!reg.contains(type)) {
      RootElement(type);
    }
    reg.configure(type, cfg);
    return type as Class<T>;
  };
}

export function RootElement<T>(type: Class<T>): Class<T> {
  const reg = Condensation.typeRegistry;
  if (!reg.contains(type)) {
    reg.register(type);
  }
  return type;
}

type Cfg = {
  alias: string;
};
export type ReadConfiguration = {} & Cfg;

export type WriteConfiguration = {} & Cfg;

export type PropertyConfiguration = {
  type: Class<any>;
  read: ReadConfiguration;
  write: WriteConfiguration;
};

function isConfiguration<T>(
  cfg: PropertyConfiguration | Class<T>
): cfg is PropertyConfiguration {
  return (cfg as PropertyConfiguration).type !== undefined;
}

export function Property<T>(
  configuration?: PropertyConfiguration | Class<T>
): any {
  return function (target: Class<T>, propertyName: string) {
    const ctor = target.constructor as Class<T>;
    RootElement(ctor);
    let reg = Condensation.typeRegistry,
      propertyCtor = Reflect.getMetadata("design:type", target, propertyName),
      type: Class<T>;
    if (configuration) {
      if (isConfiguration(configuration)) {
        type = configuration.type;
      } else {
        type = configuration;
      }
    } else {
      type = propertyCtor;
    }

    reg.defineProperty(ctor, propertyName, {
      ...configuration,
      type: type,
    } as PropertyConfiguration);
  };
}
