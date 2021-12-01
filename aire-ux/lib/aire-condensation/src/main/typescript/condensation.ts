import TypeRegistry from "@condensation/type-registry";
import RemoteRegistry from "@condensation/remote-registry";
import { Class } from "@condensation/types";
import {
  Deserializer,
  StringDeserializer,
  TypeRegistrationDeserializer,
} from "@condensation/deserializer";

export type Format = "json";

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
