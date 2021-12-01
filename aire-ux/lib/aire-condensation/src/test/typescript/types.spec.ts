import { Region, allocate } from "@condensation/types";

test("a pointer should be referable by its address", () => {
  class A {
    sayHello(): string {
      return "waddup";
    }
  }

  const region = new Region(),
    value = new A(),
    pointer = allocate(value, region);
  expect(pointer.sayHello()).toBe("waddup");
  expect(pointer.address.value).toBe(0);
});
