package com.aire.ux.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.Component;
import lombok.val;
import org.junit.jupiter.api.Test;

class ComponentPathTest {

  @Test
  void ensureComponentPathIsConstructable() {
    val component = new ExtensionTree(HostComponent.class, mock(ExtensionRegistry.class));
    assertEquals(":host", component.getSegment());
  }

  @Test
  void ensureChildrenAreCorrect() {
    val component = new ExtensionTree(HostComponent.class, mock(ExtensionRegistry.class));
    val children = component.slotsWithin(":host");
    assertEquals(2, children.size());
  }

  static class ChildComponent extends Component {}

  @Host("host")
  static class HostComponent extends Component {

    @Slot(":child1")
    private ChildComponent child;

    @Slot(":child2")
    private ChildComponent child2;
  }
}
