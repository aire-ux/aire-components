import * as c from '@condensation/condensation';


test('condensation', () => {

  expect(c).not.toBeFalsy();
  expect(c.default('Josiah')).toEqual('Hello, Josiah');


})