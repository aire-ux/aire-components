import {generateStream, lex, TokenType} from "@condensation/lexer";

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
  expect(next.type).toBe(TokenType.Identifier);
  expect(next.start).toBe(0);
  expect(next.end).toBe(value.length);
  expect(next.value).toBe(value);
});

test('a lexer must recognize multiple types of values', () => {
  const value = '$helloWorld    how are you',
      stream = lex(value),
      tokens = [...stream],
      expected = [
        TokenType.Identifier,
        TokenType.Whitespace,
        TokenType.Identifier,
        TokenType.Whitespace,
        TokenType.Identifier,
        TokenType.Whitespace,
        TokenType.Identifier
      ];

  for (let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }


});

test('a lexer must recognize a property path', () => {
  const value = 'hello.world.how.are.you',
      result = lex(value),
      tokens = [...result],
      expected = [
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
      ]

  for (let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }

});

test('a lexer must recognize a numeric id', () => {
  const value = '09124 hello . world',
      result = lex(value),
      tokens = [...result],
      expected = [
        TokenType.Number,
        TokenType.Whitespace,
        TokenType.Identifier,
        TokenType.Whitespace,
        TokenType.PropertySeparator,
        TokenType.Whitespace,
        TokenType.Identifier
      ];

  for (let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }
})

test('a lexer must recognize a namespace', () => {
  const value = 'global::hello.world',
      result = lex(value),
      tokens = [...result],
      expected = [
        TokenType.Identifier,
        TokenType.NamespaceSeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier
      ];

  for (let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }

})

test('a lexer must recognize parens', () => {
  const value = 'global::hello.world()',
      result = lex(value),
      tokens = [...result],
      expected = [
        TokenType.Identifier,
        TokenType.NamespaceSeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier,
        TokenType.OpenParenthesis,
        TokenType.CloseParenthesis
      ];

  for (let i = 0; i < expected.length; i++) {
    expect(tokens[i].type).toBe(expected[i]);
  }

})

test('a lexer must recognize parens with parameters', () => {
  const value = 'global::hello.world(${hello}, ${world})',
      result = lex(value),
      tokens = [...result],
      expected = [
        TokenType.Identifier,
        TokenType.NamespaceSeparator,
        TokenType.Identifier,
        TokenType.PropertySeparator,
        TokenType.Identifier,
        TokenType.OpenParenthesis,
        TokenType.ParameterOpen,
        TokenType.Identifier,
        TokenType.ParameterClose,
        TokenType.ParameterSeparator,
        TokenType.Whitespace,
        TokenType.ParameterOpen,
        TokenType.Identifier,
        TokenType.ParameterClose,
        TokenType.CloseParenthesis
      ];

  for (let i = 0; i < expected.length; i++) {
    console.log(tokens[i]);
    expect(tokens[i].type).toBe(expected[i]);
  }

});

test('generateStream must work', () => {
  const value = 'global::hello.world(${hello}, ${world})',
      values = generateStream(value);
  expect(values.next().value.type).toBe(TokenType.Identifier);
  expect(values.next().value.type).toBe(TokenType.NamespaceSeparator);
})
