# Overview

Condensation is a library that allows transparent client-server communication for @aire-ux/component
widgets.

## Usage

### Define the AireServlet

```
    @Bean public ServletRegistrationBean<AireDesignerServlet> aireServlet() {
        return new ServletRegistrationBean(AireDesignerServlet, "context");  // servlet will be registered at "/web-context/context"
    }
```

### DTO

Define a data-transfer object:

```typescript
@RootElement
class Address {
  @Property({
    type: Number,
    read: {
      alias: "room-count",
    },
  })
  roomCount: number;

  @Property(String)
  city: string;
}

@RootElement
class Person {
  @Property(String)
  name: string | undefined;
  @Property({
    type: Address,
    read: {
      alias: "home-addresses",
    },
  })
  addresses: Address[];
}

@RootElement
class Group {
  @Property(Person)
  members: Person[] | undefined;
}
```

### Define a Remotable Object

```typescript
import { Receive, Remotable } from "./remotable";

@Remotable
class RemotableGroup {
  /**
   * @param group the group to construct this from
   */
  constructor(@Receive(Group) group: Group) {}

  addMember(@Receive(Person) member: Person): void {
    this.group.members.push(member); // addMember can be called from the server-side
  }
}

const group = Condensation.newContext().bind(RemotableGroup);
```
