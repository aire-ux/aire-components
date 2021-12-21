import {parse} from "@condensation/parser";

test('a parser must parse', () => {
  (window as any).hello = 'world';

  parse('hello::world.whatever.cool.beans');
})

test('a parser must parse an invocation', () => {
  (window as any).hello = 'world';

  parse('hello::world.whatever.cool.beans (${first}, ${second})');
})
