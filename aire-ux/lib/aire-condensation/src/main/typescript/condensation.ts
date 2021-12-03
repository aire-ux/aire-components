import TypeRegistry from "@condensation/type-registry";
import RemoteRegistry, {InvocationType} from "@condensation/remote-registry";
import {Address, allocate, Class, Pointer, Region,} from "@condensation/types";
import {
  Deserializer,
  StringDeserializer,
  TypeRegistrationDeserializer,
} from "@condensation/deserializer";

export type Format = "json";

export interface Context {
  region: Region;

  create<T>(t: Class<T>, ...args: string[]): Pointer<T>;

  locate<T>(address: Address): T | null;

  move<T>(address: Address, target: Region): Pointer<T> | null;

  invoke<T, U>(address: Address, op: string, ...args: string[]): U | null;

  delete<T>(address: Address): T | null;

  addressOf<T>(t: T): Address | null;
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
  constructor(readonly region = new Region()) {
  }

  create<T>(t: Class<T>, ...args: string[]): Pointer<T> {
    const actualParams = this.formalParams(t, 'constructor', ...args);
    return allocate(new t(...actualParams) as T, this.region);
  }


  addressOf<T>(t: T): Address {
    return this.region.addressOf(t);
  }

  delete<T>(address: Address): T | null {
    return this.region.delete(address);
  }

  invoke<T, U>(address: Address, op: string, ...args: string[]): U | null {
    const v = this.locate(address) as Pointer<T>;
    if (!v) {
      throw new Error(`Null pointer exception at ${address} while trying to invoke ${op}`);
    }
    const operation = (v as any) [op] as any;
    if (!operation) {
      throw new Error(`Type ${typeof v} has no method named '${op}'`);
    }
    const formals = this.formalParams((Object.getPrototypeOf(v)).constructor, 'method', ...args);
    return operation.apply(v, formals);
  }

  locate<T>(address: Address): T | null {
    return this.region.values[address.value];
  }

  move<T>(address: Address, target: Region): Pointer<T> | null {
    const v = this.delete(address) as T;
    return allocate(v, target);
  }

  private formalParams<T>(t: Class<T>, type: InvocationType, ...args: string[]): any[] {
    const remotes = Condensation.remoteRegistry,
        remote = remotes.resolve(t),
        ctorArgs = remote.definitions.filter(
            (definition) => definition.invocationType === type
        );
    if (ctorArgs.length !== args.length) {
      throw new Error(
          `Error: ${type} argument count mismatch.  Expected ${ctorArgs.length}, got ${args.length}`
      );
    }
    ctorArgs.sort((lhs, rhs) => lhs.index - rhs.index);
    return ctorArgs.map((def, idx) => {
      const doc = args[idx],
          jsonValue = JSON.parse(doc);
      return Condensation.deserializerFor(def.type).read(jsonValue);
    });
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

export namespace Condensation {
}
Condensation.registry = new TypeRegistry();
Condensation.remoteRegistry = new RemoteRegistry();

register({
  type: String,
  deserializer: new StringDeserializer(),
});
