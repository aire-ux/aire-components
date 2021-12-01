import TypeRegistry from "@condensation/type-registry";
import RemoteRegistry from "@condensation/remote-registry";
import { Class } from "@condensation/types";
import {
  Deserializer,
  StringDeserializer,
  TypeRegistrationDeserializer,
} from "@condensation/deserializer";

export type Format = "json";

export interface Context {
  create<T>(t: Class<T>, ...args: string[]): T;
}

/**
 * root context for all operations
 */
export class Condensation {
  static registry: TypeRegistry;
  static remoteRegistry: RemoteRegistry;

  static deserializerConfigurations: Map<Class<any>, Deserializer<any>> =
    new Map<Class<any>, Deserializer<any>>();

  static get typeRegistry(): TypeRegistry {
    return Condensation.registry;
  }

  static deserializerFor<T>(type: Class<T>): Deserializer<T> {
    const result = this.deserializerConfigurations.get(type);
    if (result) {
      return result;
    }
    const cfg = Condensation.registry.resolveConfiguration<T>(type);
    return new TypeRegistrationDeserializer<T>(type, cfg);
  }

  static newContext(): Context {
    return new DefaultCondensationContext();
  }
}

class DefaultCondensationContext implements Context {
  create<T>(t: Class<T>, ...args: string[]): T {
    const remotes = Condensation.remoteRegistry,
      remote = remotes.resolve(t);
    if (!remote) {
      throw new Error(
        `Type ${t.name} is not remotable (did you forget to annotate it with @Remotable?)`
      );
    }
    const ctorArgs = remote.definitions.filter(
      (definition) => definition.invocationType === "constructor"
    );
    if (ctorArgs.length !== args.length) {
      throw new Error(
        `Error: Constructor argument count mismatch.  Expected ${ctorArgs.length}, got ${args.length}`
      );
    }
    ctorArgs.sort((lhs, rhs) => lhs.index - rhs.index);
    const actualParams = ctorArgs.map((def, idx) => {
      const doc = args[idx],
        jsonValue = JSON.parse(doc);
      return Condensation.deserializerFor(def.type).read(jsonValue);
    });
    return new t(...actualParams) as T;
  }
}

export type RegistrationDefinition = {
  type: Class<any>;
  deserializer: Deserializer<any>;
};

export function register(...registrations: RegistrationDefinition[]) {
  for (let reg of registrations) {
    Condensation.deserializerConfigurations.set(reg.type, reg.deserializer);
  }
}

export namespace Condensation {}
Condensation.registry = new TypeRegistry();
Condensation.remoteRegistry = new RemoteRegistry();

register({
  type: String,
  deserializer: new StringDeserializer(),
});
