/**
 * alias for a constructor type
 */
import { add } from "husky";

export type Class<T> = new (...args: any[]) => T;

/**
 *
 */
export type ParameterDecorator = (
  target: any,
  propertyKey: PropertyKey,
  index: number
) => any;

export type Pointer<T> = {
  readonly address: Address;
} & T;

export function allocate<T>(value: T, region = DefaultRegion): Pointer<T> {
  const address = new Address(region);
  region.values[address.value] = value;
  const handler = {
    get: (target: T, prop: keyof (T | Pointer<T>), receiver: T) => {
      const property = value[prop];
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
