import {parse, SegmentType} from "@condensation/parser";

test("a parser must parse", () => {
  (window as any).hello = "world";

  parse("hello::world.whatever.cool.beans");
});

test("a parser must parse an invocation", () => {
  (window as any).hello = "world";

  parse("hello::world.whatever.cool.beans (${first}, ${second})");
});

test("a parser must parse an extended invocation", () => {
  (window as any).hello = "world";

  parse("hello::world.whatever.cool.beans (${first}, ${second}).bean.schnorp");
});

test("a parser must parse an extended invocation multiple times", () => {
  (window as any).hello = "world";

  parse(
    "hello::world.whatever.cool.beans (${first}, ${second}).bean.schnorp().poobles(${third}, ${fourth}, ${fifth})"
  );
});


test('a parser must parse references and values', () => {
  const {path, namespace} = parse('hello::world.1234.beans()');
  expect(namespace).toBe("hello");
  expect(path.segment.segmentType).toBe(SegmentType.Property);
  const next = path.segment.next;
  expect(next?.segmentType).toBe(SegmentType.Reference);
})