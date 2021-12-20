import {lex, TokenType} from "@condensation/lexer";

test('a lexer must recognize whitespace', () => {
  const value = ' ',
      stream = lex(value),
      next = [...stream][0];
  expect(next.type).toBe(TokenType.Whitespace);
  expect(next.start).toBe(0);
  expect(next.end).toBe(1);
  expect(next.value).toBe(value);
});

test('a lexer must recognize identifiers', () => {
  const value = '$helloWorld',
      stream = lex(value),
      next = [...stream][0];
  expect(next.type).toBe(TokenType.Property);
  expect(next.start).toBe(0);
  expect(next.end).toBe(value.length);
  expect(next.value).toBe(value);
});

test('a lexer must recognize multiple types of values', () => {
  const value = '$helloWorld    how are you',
      stream = lex(value),
      tokens = [...stream],
      expected = [
        TokenType.Property,
        TokenType.Whitespace,
        TokenType.Property,
        TokenType.Whitespace,
        TokenType.Property,
        TokenType.Whitespace,
        TokenType.Property
      ];

  for(let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }


});