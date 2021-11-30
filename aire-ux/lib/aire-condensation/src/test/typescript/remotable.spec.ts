import {Receive, Remotable} from "@condensation/remotable";
import {Property, RootElement} from "@condensation/root-element";
import {Condensation} from "@condensation/condensation";

test('remotable should work with constructor arguments', () => {

  @RootElement
  class TestDTO {

  }

  @Remotable
  class TestReceiver {
    constructor(@Receive dto:TestDTO) {

    }
  }

  const defs = Condensation
      .remoteRegistry
      .resolve(TestReceiver)
      .definitions;

  expect(defs.length).toBe(1);
  expect(defs[0].index).toBe(0);
});

test('remotable should allow a value to be constructed', () => {

  @RootElement
  class TestDTO {
    // @Property
    // name: string | undefined;

  }

  @Remotable
  class TestReceiver {
    private name: string | undefined;
    constructor(@Receive dto:TestDTO) {
      // this.name = dto.name;
    }
  }

  // const receiver = Condensation.construct(TestReceiver, `
  //   {
  //     name: "Josiah"
  //   }
  // `);

});