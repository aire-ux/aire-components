import {Receive, Remotable} from "@condensation/remotable";
import {RootElement} from "@condensation/root-element";
import {Condensation} from "../../main/typescript/condensation";

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