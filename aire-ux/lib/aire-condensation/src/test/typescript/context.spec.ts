import { Condensation, Context } from "@condensation/condensation";
import { Remotable } from "../../main/typescript/remotable";
import { Property, RootElement } from "../../main/typescript/root-element";

let context: Context;

beforeEach(() => {
  context = Condensation.newContext();
});

// @RootElement
// class Pet {
//   @Property(String)
//   name: string;
// }
//
// @RootElement
// class PersonDTO {
//   @Property(String)
//   firstName: string;
//
//   @Property(String)
//   lastName: string;
//
//   @Property(Pet)
//   pets: Pet[];
// }
//
// @Remotable
// class Person {}

test("objects should be allocatable in a context", () => {});
