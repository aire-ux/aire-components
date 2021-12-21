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

export interface Lookup {
  lookup<T>(name: string | number): T;
}

export type Invocation = {
  context: Lookup
  invoke: (...args: string[]) => any
}

const expectToken = (
    ctx: string,
    token: Token,
    ...expected: TokenType[]
): void => {
  if (expected.indexOf(token.type) === -1) {
    throw new Error(`Expected one of [${[...expected].map(e => TokenType[e]).join(',')}].  Got ${TokenType[token.type]} at (${token.start, token.end}) '${ctx.substr(token.start, token.end)}'`);
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



enum SegmentType {
  Property,
  Invocation
}

type Segment = {
  name: string;
  next: Segment | undefined;
  segmentType: SegmentType;
  formalParameterNames: string[] | undefined;
}
type Path = {
  segment: Segment;
}

const ws = (iter: PushbackIterator): void => {
  let token: Token;
  do {
    token = iter.next();
  } while (token.type === TokenType.Whitespace);
  if(token.type !== TokenType.EOF) {
    iter.unread(token);
  }
}


const readParameterNames = (seq: string, iter: PushbackIterator): string[] => {
  let result = [];

  for (; ;) {
    ws(iter);
    if (iter.peek().type === TokenType.CloseParenthesis) {
      break;
    }
    expect(seq, iter, TokenType.ParameterOpen);
    ws(iter);

    result.push(expect(seq, iter, TokenType.Identifier).value);
    ws(iter);
    expect(seq, iter, TokenType.ParameterClose);
    ws(iter);
    const next = iter.peek();
    if (next.type === TokenType.ParameterSeparator) {
      expect(seq, iter, TokenType.ParameterSeparator);
      continue;
    }
    if (next.type === TokenType.CloseParenthesis) {
      break;
    }
  }
  ws(iter);
  return result;
}

const readPath = (seq: string, iter: PushbackIterator): Path => {
  let root: Segment | null = null,
      current: Segment | null = null;
  while (iter.hasNext()) {
    ws(iter);
    const id = expect(seq, iter, TokenType.Identifier);
    ws(iter);
    let type: SegmentType,
        peek = iter.peek();

    if(peek.type === TokenType.PropertySeparator) {
      expect(seq, iter, TokenType.PropertySeparator);
      const value = {
        name: id.value,
        next: undefined,
        formalParameterNames: undefined,
        segmentType: SegmentType.Property,
      }
      if(!root) {
        root = value as Segment;
        current = value as Segment;
      } else {
        (current as Segment).next = value as Segment;
        current = (current as Segment).next as Segment;
      }
    }
    else if(peek.type == TokenType.OpenParenthesis) {
      expect(seq, iter, TokenType.OpenParenthesis);
      ws(iter);
      const paramNames = readParameterNames(seq, iter);
      const value = {
        name: id.value,
        next:undefined,
        formalParameterNames: paramNames,
        segmentType: SegmentType.Invocation
      }
      if(!root) {
        root = value as Segment;
        current = value as Segment;
      } else {
        (current as Segment).next = value as Segment;
        current = (current as Segment).next as Segment;
      }
      ws(iter);
      expect(seq, iter, TokenType.CloseParenthesis);
      ws(iter);
      if(iter.peek().type === TokenType.PropertySeparator) {
        expect(seq, iter, TokenType.PropertySeparator);
      }
    }
    else if(peek.type == TokenType.EOF) {
      const value = {
        name: id.value,
        next: undefined,
        formalParameterNames: undefined,
        segmentType: SegmentType.Property,
      }
      if(!root) {
        root = value as Segment;
        current = value as Segment;
      } else {
        (current as Segment).next = value as Segment;
        current = (current as Segment).next as Segment;
      }
      break;
    }
  }

  return {
    segment: root as Segment
  }
}


export const parse = (seq: string): Invocation => {
  const iter = stream(seq);
  ws(iter);
  const namespace = expect(
      seq,
      iter,
      TokenType.Identifier,
      TokenType.Number
  );
  ws(iter);
  const next = iter.peek();
  if (next.type === TokenType.NamespaceSeparator) {
    expect(seq, iter, TokenType.NamespaceSeparator);
  }

  const path = readPath(seq, iter);
  return null as any;
}