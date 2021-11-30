import {Class} from "@condensation/types";


type ParameterDefinition = {
  index: number;
}
type RemoteInvocationRegistration = {
  definitions: ParameterDefinition[];
}

export default class RemoteRegistry {
  readonly mappings: Map<Class<any>, RemoteInvocationRegistration>

  constructor() {
    this.mappings = new Map<Class<any>, RemoteInvocationRegistration>();
  }

  register<T>(type: Class<T>): RemoteInvocationRegistration {
    if (!this.mappings.has(type)) {
      this.mappings.set(type, {definitions: []})
    }
    return this.mappings.get(type) as RemoteInvocationRegistration;
  }


  defineParameter(type: Class<any>, definition: ParameterDefinition) {
    this.register(type).definitions.push(definition);
  }

  resolve<T>(key: Class<T>) : RemoteInvocationRegistration {
    return this.mappings.get(key) as RemoteInvocationRegistration;
  }
}
