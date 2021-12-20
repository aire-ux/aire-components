import {locate} from "@condensation/invoker";
import {Condensation, Receive, Remotable} from "@condensation/index";

test('an invoker must locate a property correctly', () => {

  class B {

  }

  class A {
    b: B | undefined;
  }

  let a = new A(),
      b = new B();

  a.b = b;

  const [host, prop] = locate('b'.split('.'), a);

  expect(host).toBe(a);
  expect(prop).toBe(b);

});

test('ensure invoking a method makes sense', () => {
  @Remotable
  class A {
    sayHello(@Receive(String) name: string): string {
      return `Hello ${name}`;
    }
  }
  const a = Condensation.defaultContext().create(A);
      // prog = `(function(){Condensation.defaultContext().invoke(${a.address}, 'Josiah')})()`
  const r = eval(`Condensation.defaultContext()`);
  console.log(r);


});
