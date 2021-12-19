/**
 * alias for a constructor type
 */

export type Class<T> = new (...args: any[]) => T;

/**
 *
 */
export type ParameterDecorator = (
  target: any,
  propertyKey: PropertyKey,
  index: number
) => any;

/**
 * represents a location in a region (memory arena)
 */
export class Address {
  public readonly value: number;
  constructor(region: Region | number) {
    if (isRegion(region)) {
      this.value = region.address();
    } else {
      this.value = region;
    }
  }
}

/**
 * a value that is resolvable on either the client-side or the server-side
 */
export type Pointer<T> = {
  readonly value: T;
  readonly address: Address;
} & T;

/**
 * guard allowing use of pointers as addresses
 * @param t the value to check
 */
export function isPointer<T>(t: Pointer<T> | Address) {
  return (t as Pointer<T>).address !== undefined;
}

/**
 * allocate a value into a pointer
 * @param value the value to allocate
 * @param region the region to allocate into
 */
export function allocate<T>(value: T, region = DefaultRegion): Pointer<T> {
  const address = new Address(region);
  region.values[address.value] = value;
  const handler = {
    get: (target: T, prop: keyof (T | Pointer<T>), receiver: T) => {
      const property = value[prop];
      if (prop === "value") {
        return value;
      }
      if (prop === "address") {
        return address;
      }
      if (property) {
        return property;
      }
      return Reflect.get(target as Pointer<T>, prop, receiver);
    },
  } as ProxyHandler<Pointer<T>>;
  return new Proxy(value as Pointer<T>, handler);
}

/**
 * a contiguous set of memory locations
 */
export class Region {
  values: any[];
  constructor() {
    this.values = [];
  }

  address(): number {
    return this.values.length;
  }

  addressOf<T>(t: T): Address {
    return new Address(this.values.indexOf(t));
  }

  move<T>(ptr: Pointer<T>, target: Region): Pointer<T> | undefined {
    const result = this.delete(ptr.address) as Pointer<T>;
    if (result) {
      return allocate(ptr.value, target);
    }
    return undefined;
  }

  delete<T>(address: Address): T | null {
    let vs = this.values,
      value = vs[address.value];

    if (value) {
      return vs.splice(address.value, 1)[0] as T;
    }
    return null;
  }
}

export const DefaultRegion = new Region();

function isRegion(r: Region | number): r is Region {
  return (r as Region).values !== undefined;
}
