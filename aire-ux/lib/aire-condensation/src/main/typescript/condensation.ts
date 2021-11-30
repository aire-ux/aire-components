import TypeRegistry from "@condensation/type-registry";
import RemoteRegistry from "@condensation/remote-registry";


export type Format = 'json';


/**
 * root context for all operations
 */
export class Condensation {


  static registry: TypeRegistry;
  static remoteRegistry: RemoteRegistry;

  static get typeRegistry() : TypeRegistry {
    return Condensation.registry;
  }

}

export namespace Condensation {

}
Condensation.registry = new TypeRegistry();
Condensation.remoteRegistry = new RemoteRegistry();
