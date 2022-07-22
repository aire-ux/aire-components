package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;
import com.aire.ux.test.vaadin.scenarios.routes.SecondaryLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;
import java.util.List;
import lombok.val;

@AireTest
@RouteLocation(scanPackage = "com.aire.ux.test.vaadin.scenarios.routes")
class ComponentHierarchyNodeAdapterTest {

  @ViewTest
  @Navigate("main")
  void ensureCssSelectorsWork(@Select("body > section") MainLayout layout) {
    assertNotNull(layout);
  }

  @ViewTest
  @Navigate("main")
  void ensureCssSelectorWorksOnCollectionType(
      @Select("span:nth-child(-n + 2)") List<Span> children) {
    assertNotNull(children);
  }

  @ViewTest
  @Navigate("main")
  void ensureItemIsSelectable(@Context TestContext context, @Select Checkbox checkbox) {
    assertNotNull(context);
    checkbox.setValue(true);
    val selected = context.selectFirst(Checkbox.class);
    assertNotNull(selected);
    assertTrue(selected.get().getValue());
  }

  @ViewTest
  @Navigate("main")
  void ensureNavigationFromTestContextWorks(@Context TestContext context) {
    var result = context.select(SecondaryLayout.class);
    assertTrue(result.isEmpty());
    context.navigate("secondary");
    result = context.select(SecondaryLayout.class);
    assertFalse(result.isEmpty());
  }

  @ViewTest
  @Navigate("main")
  void ensureCssSelectorWorksOnCollectionTypeOfElements(
      @Select("body > section span") List<Element> children) {
    assertNotNull(children);
    assertEquals(2, children.size());
    assertTrue(children.stream().allMatch(t -> t.getTag().equals("span")));
  }
}
