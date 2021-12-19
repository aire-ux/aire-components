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

test("ensure base use-case works", () => {
  @RootElement
  class GraphConfiguration {
    @Property({
      type: String,
      read: {
        alias: "load-resources",
      },
    })
    loadResources: string | undefined;

    @Property({
      type: Boolean,
      read: {
        alias: "force-includes",
      },
    })
    forceIncludes: string | undefined;

    @Property({
      type: Boolean,
      read: {
        alias: "force-includes",
      },
    })
    loadStylesheets: boolean | undefined;

    @Property({
      type: String,
      read: {
        alias: "resource-extension",
      },
    })
    resourceExtension: boolean | undefined;

    @Property({
      type: Boolean,
      read: {
        alias: "production-mode",
      },
    })
    productionMode: boolean | undefined;

    @Property({
      type: String,
      read: {
        alias: "base-path",
      },
    })
    basePath: string | undefined;
  }

  @Remotable
  class MxGraphManager {
    constructor(
      @Receive(GraphConfiguration) readonly configuration: GraphConfiguration
    ) {}
  }

  const mgr = Condensation.newContext().create<MxGraphManager>(
    MxGraphManager,
    `{
      "load-resources": "loading them resources"
  }`
  );
  expect(mgr.configuration?.loadResources).toEqual("loading them resources");
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
  expect(group.members?.map((m) => m.name)).toEqual([
    "Josiah",
    "Lisa",
    "Alejandro",
    "Tiff",
  ]);
});

test("pointers should be invocable", () => {
  @RootElement
  class Person {
    @Property(String)
    name: string | undefined;
  }

  @Remotable
  class MxGraphManager {
    person: Person | undefined;

    public init(@Receive(Person) person: Person): void {
      this.person = person;
    }
  }

  const ctx = Condensation.newContext(),
    mgr = ctx.create<MxGraphManager>(MxGraphManager);

  ctx.invoke(
    mgr,
    "init",
    `
    {
      "name": "Josiah"
    }
  `
  );

  expect(mgr.person?.name).toBe("Josiah");
});
