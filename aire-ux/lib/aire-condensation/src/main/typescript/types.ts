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
