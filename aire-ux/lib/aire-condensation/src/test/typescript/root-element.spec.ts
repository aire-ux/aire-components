import {Condensation} from "@condensation/condensation";
import {Configuration, RootElement} from "@condensation/root-element";


test('root element should be able to register a type with the type registry', () => {
  @RootElement
  class TestElement {

  }

  expect(Condensation.typeRegistry.contains(TestElement)).toBeTruthy();
  expect(Condensation.typeRegistry.resolveConfiguration(TestElement).alias).toBe("TestElement");
});


test('root element should be registerable with an alias', () => {

  @RootElement
  @Configuration({alias: 'root-element'})
  class TestElement {

  }

  const cfg = Condensation.typeRegistry.resolveConfiguration(TestElement);
  expect(cfg.alias).toBe('root-element');

})