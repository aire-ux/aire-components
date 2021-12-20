export type Invocation = {
  readonly qualifiedName: string;
  readonly formalParameters: string[]
  readonly next: Invocation | undefined;
}

/**
 *
 * @param invocation the invocation to perform
 *
 */
// export function invoke<T>(
//     invocation: Invocation
// ) : T {
//
//   let ctx = Condensation.defaultContext();
//   let inv = invocation,
//       target = window;
//   while(inv.next) {
//     const qnamePath = inv.qualifiedName.split('.');
//     target = locate(qnamePath, target);
//     if(typeof target === 'function') {
//       Condensation.
//     }
//   }
//
//
//
// }
//

export function locate(qnamePath: string[], v: any): [any, any] {
  let host = v,
      property = undefined,
      size = qnamePath.length;
  for (let i = 0; i < size; i++) {
    const pathSegment = qnamePath[i];
    property = host[pathSegment];
    if (!property) {
      throw new Error(`Error: no property ${property} on ${host}`);
    }
    if (i === size - 1) {
      return [host, property];
    }
    host = property;
  }
  throw new Error(`Error: no property at ${qnamePath.join('.')} reachable from ${host}`);
}