import {Class} from "@condensation/types";
import {RootElementConfiguration} from "@condensation/root-element";


export type TypeRegistration<T> = {
  alias: string
}

export default class TypeRegistry {

  private readonly types: Map<Class<any>, TypeRegistration<any>>;

  constructor() {
    this.types = new Map<Class<any>, TypeRegistration<any>>();
  }

  public register<T>(type: Class<T>): void {
    this.types.set(type, {alias: type.name});
  }


  public contains<T>(type: Class<T>): boolean {
    return this.types.has(type);
  }

  public configure<T>(type: Class<T>, cfg: RootElementConfiguration): void {
    let [t, registration] = this.check(type);
    registration = {...cfg};
    this.types.set(t, registration);
  }


  public resolveConfiguration<T>(type: Class<T>): TypeRegistration<T> {
    const [_, registration] = this.check(type);
    return registration;
  }

  private check<T>(type: Class<T>) : [Class<T>, TypeRegistration<T>] {
    const toConfigure = this.types.get(type);
    if (!toConfigure) {
      throw new Error(`Error: Attempting to configure type ${type}, but it has not been registered.  
      Have you annotated ${type} with '@RootElement'?`);
    }
    return [type, toConfigure] as [Class<T>, TypeRegistration<T>];
  }
}