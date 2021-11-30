import {Condensation} from "@condensation/condensation";
import {Configuration, Property, RootElement} from "@condensation/root-element";
import {PropertyDefinition} from "../../main/typescript/type-registry";


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

});


test('properties should be available on a type', () => {
  @RootElement
  class TestElement {
    @Property()
    name: string | undefined;
  }

  const cfg = Condensation.typeRegistry.resolveConfiguration(TestElement);
  expect(cfg?.properties?.size).toBe(1);
  const prop: PropertyDefinition = cfg.properties?.get('name') as PropertyDefinition;
  expect(prop?.realName).toBe('name');
  expect(prop?.readAlias).toBe('name');
  expect(prop?.writeAlias).toBe('name');
})