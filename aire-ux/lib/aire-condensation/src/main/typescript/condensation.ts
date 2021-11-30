import TypeRegistry from "@condensation/type-registry";


export type Format = 'json';


/**
 * root context for all operations
 */
export class Condensation {


  static registry: TypeRegistry;

  static get typeRegistry() : TypeRegistry {
    return Condensation.registry;
  }

}

export namespace Condensation {

}
Condensation.registry = new TypeRegistry();
