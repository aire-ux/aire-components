import { Condensation, Context } from "@condensation/condensation";
import { Property, RootElement } from "@condensation/root-element";
import { Receive, Remotable } from "@condensation/remotable";
import { allocate, Region } from "@condensation/types";

let context: Context;

beforeEach(() => {
  context = Condensation.newContext();
});

test("moving an object to a different context should result in the object being available in the target and unavailable in the source", () => {
  @Remotable
  class Type {}

  const source = new Region(),
    target = new Region();

  const type = allocate<Type>(Type, source);
  expect(source.addressOf(type)).toBeDefined();
  source.move(type, target);
  expect(source.addressOf(type).value).toEqual(-1);
  expect(target.addressOf(type)).toBeDefined();
});

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

    public setFirstNameFrom(@Receive(Person) person: Person): void {
      this.firstName = person.firstName;
    }
  }
  const value = context.create<RemotePerson>(
    RemotePerson,
    `{
    "firstName": "Josiah"
  }`
  );

  expect(value.firstName).toBe("Josiah");
});

test("objects should be invocable by their handles", () => {
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

    public setFirstNameFrom(
      @Receive(Person) person: Person
    ): string | undefined {
      this.firstName = person.firstName;
      return this.firstName;
    }
  }
  const value = context.create<RemotePerson>(
    RemotePerson,
    `{
    "firstName": "Josiah"
  }`
  );

  expect(value.firstName).toBe("Josiah");
  const result = context.invoke(
    value.address,
    "setFirstNameFrom",
    `{
    "firstName": "Lisa"
  }`
  );
  expect(result).toBe("Lisa");
  expect((context.locate(value.address) as RemotePerson).firstName).toBe(
    "Lisa"
  );
});
