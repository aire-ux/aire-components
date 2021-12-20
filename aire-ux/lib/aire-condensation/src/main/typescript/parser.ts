/**
 * parse an invocation into a readable format
 *
 * invocation: (<namespace> '::')? <invocationPath>
 * invocationPath: <ws>? (<invocationOrProperty><separator>)+
 * invocationOrProperty: invocation | property
 * invocation: <openParen>(ws?) paramList (ws?) <closeParam>
 * openParen: '('
 * paramList: (<parameter>',')?<parameter>
 * closeParam: ')'
 * property: <identifier>
 */
import {PushbackIterator, stream, Token, TokenType} from "@condensation/lexer";


export type Invocation = {
  region: number | string;
}

const expectToken = (
    ctx: string,
    token: Token,
    ...expected: TokenType[]
): void => {
  if (expected.indexOf(token.type) === -1) {
    throw new Error(`Expected one of [${[...expected].map(e => TokenType[e]).join(',')}].  Got ${token.type} at (${token.start, token.end}) '${ctx.substr(token.start, token.end)}'`);
  }
}

const expect = (
    ctx: string,
    iter: PushbackIterator,
    ...expected: TokenType[]): Token => {
  if (!iter.hasNext()) {
    throw new Error(`Unexpected end of token stream.  Expected one of [${[...expected].map(e => TokenType[e]).join(',')}]`);
  }
  const next = iter.next();
  expectToken(ctx, next, ...expected);
  return next;
}

export const parse = (seq: string) => {
  const iter = stream(seq),
      namespace = expect(
          seq,
          iter,
          TokenType.Identifier,
          TokenType.Number
      ),
      next = iter.peek();
  if (next.type === TokenType.NamespaceSeparator) {
    expect(seq, iter, TokenType.NamespaceSeparator);
  }
}