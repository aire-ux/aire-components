import { Class } from "@condensation/types";
import {
  PropertyConfiguration,
  RootElementConfiguration,
} from "@condensation/root-element";

export type PropertyDefinition = {
  type: Class<any>;
  realName: string;
  readAlias: string;
  writeAlias: string;
};

export type TypeRegistration<T> = {
  alias: string;
  properties?: Map<string, PropertyDefinition>;
};

export default class TypeRegistry {
  private readonly types: Map<Class<any>, TypeRegistration<any>>;

  constructor() {
    this.types = new Map<Class<any>, TypeRegistration<any>>();
  }

  public register<T>(type: Class<T>): void {
    if (!this.types.has(type)) {
      this.types.set(type, { alias: type.name });
    }
  }

  public contains<T>(type: Class<T>): boolean {
    return this.types.has(type);
  }

  public configure<T>(type: Class<T>, cfg: RootElementConfiguration): void {
    let [t, registration] = this.check(type);
    registration = { ...cfg };
    this.types.set(t, registration);
  }

  public resolveConfiguration<T>(type: Class<T>): TypeRegistration<T> {
    const [_, registration] = this.check(type);
    return registration;
  }

  private check<T>(type: Class<T>): [Class<T>, TypeRegistration<T>] {
    const toConfigure = this.types.get(type);
    if (!toConfigure) {
      throw new Error(`Error: Attempting to configure type ${type}, but it has not been registered.  
      Have you annotated ${type} with '@RootElement'?`);
    }
    return [type, toConfigure] as [Class<T>, TypeRegistration<T>];
  }

  defineProperty<T>(
    target: Class<T>,
    propertyName: string,
    configuration: PropertyConfiguration
  ) {
    const [type, registration] = this.check(target);
    registration.properties =
      registration.properties || new Map<string, PropertyDefinition>();
    registration.properties.set(
      propertyName,
      readPropertyDefinition(propertyName, configuration)
    );
  }
}

function readPropertyDefinition(
  name: string,
  cfg: PropertyConfiguration
): PropertyDefinition {
  return {
    type: cfg.type,
    realName: name,
    readAlias: cfg?.read?.alias || name,
    writeAlias: cfg?.write?.alias || name,
  };
}
