import {Condensation, Context} from "@condensation/condensation";
import {Property, RootElement} from "@condensation/root-element";
import {Receive, Remotable} from "../../main/typescript/remotable";

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

test("objects should be allocatable in a context", () => {
  @RootElement
  class Person {
    @Property(String)
    firstName: string | undefined;
  }

  @Remotable
  class RemotePerson {
    firstName: string | undefined;

    constructor(@Receive(Person) readonly person: Person) {
      this.firstName = person.firstName;
    }

    public setFirstNameFrom(@Receive(Person) person: Person) : void {
      this.firstName = person.firstName;
    }
  }
  const value = context.create<RemotePerson>(RemotePerson, `{
    "firstName": "Josiah"
  }`);

  expect(value.firstName).toBe("Josiah")

});

test('objects should be invocable by their handles', () => {

  @RootElement
  class Person {
    @Property(String)
    firstName: string | undefined;
  }

  @Remotable
  class RemotePerson {
    firstName: string | undefined;

    constructor(@Receive(Person) readonly person: Person) {
      this.firstName = person.firstName;
    }

    public setFirstNameFrom(@Receive(Person) person: Person) : string | undefined {
      this.firstName = person.firstName;
      return this.firstName;
    }

  }
  const value = context.create<RemotePerson>(RemotePerson, `{
    "firstName": "Josiah"
  }`);

  expect(value.firstName).toBe("Josiah")
  const result = context.invoke(value.address, 'setFirstNameFrom', `{
    "firstName": "Lisa"
  }`);
  expect(result).toBe("Lisa");
  expect((context.locate(value.address) as RemotePerson).firstName).toBe("Lisa");
})
