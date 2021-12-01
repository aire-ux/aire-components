import { Receive, Remotable } from "@condensation/remotable";
import { Property, RootElement } from "@condensation/root-element";
import { Condensation } from "@condensation/condensation";

test("remotable should work with constructor arguments", () => {
  @RootElement
  class TestDTO {}

  @Remotable
  class TestReceiver {
    constructor(@Receive(TestDTO) dto: TestDTO) {}
  }

  const defs = Condensation.remoteRegistry.resolve(TestReceiver).definitions;

  expect(defs.length).toBe(1);
  expect(defs[0].index).toBe(0);
});

test("remotable should allow a value to be constructed", () => {
  @RootElement
  class Person {
    @Property(String)
    name: string | undefined;
  }

  @RootElement
  class Pet {
    @Property(String)
    name: string | undefined;

    @Property(Person)
    momma: Person | undefined;

    sayHenlo(): string {
      return "Mommymommymommy!";
    }
  }

  @Remotable
  class TestReceiver {
    name: string | undefined;

    constructor(
      @Receive(Pet) public readonly pet: Pet,
      @Receive(Person) public readonly dto: Person
    ) {
      this.name = dto.name;
    }

    set(@Receive(Person) dto: Person): void {
      console.log(dto);
    }
  }

  const ctx = Condensation.newContext();
  const receiver = ctx.create<TestReceiver>(
    TestReceiver,
    `
  {
    "name": "Flances",
      "momma": {
        "name": "Wab"
      }
  }
  `,
    `{
    "name": "Josiah"
  }`
  );
  expect(receiver.name).toBe("Josiah");
  expect(receiver.dto.name).toBe("Josiah");
  expect(receiver.pet.sayHenlo()).toBe("Mommymommymommy!");
  expect(receiver.pet?.momma?.name).toBe("Wab");
});

test("ensure array is deserializable", () => {
  @RootElement
  class Person {
    @Property(String)
    name: string | undefined;
  }

  @RootElement
  class Group {
    @Property(Person)
    members: Person[] | undefined;
  }

  const group = Condensation.deserializerFor<Group>(Group).read([
    {
      name: "Josiah",
    },
    {
      name: "Lisa",
    },
    {
      name: "Alejandro",
    },

    {
      name: "Tiff",
    },
  ]);

  expect(group.members?.length).toBe(4);
});
