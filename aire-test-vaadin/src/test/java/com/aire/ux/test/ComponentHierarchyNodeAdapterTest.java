package com.aire.ux.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.vaadin.scenarios.routes.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;
import java.util.List;
import lombok.val;

@AireTest
@Routes(scanPackage = "com.aire.ux.test.vaadin.scenarios.routes")
class ComponentHierarchyNodeAdapterTest {


  @ViewTest(navigateTo = "main")
  void ensureCssSelectorsWork(@Select("body > section") MainLayout layout) {
    assertNotNull(layout);
  }

  @ViewTest(navigateTo = "main")
  void ensureCssSelectorWorksOnCollectionType(
      @Select("span:nth-child(-n + 2)") List<Span> children) {
    assertNotNull(children);
  }

  @ViewTest(navigateTo = "main")
  void ensureItemIsSelectable(@Context TestContext context, @Select Checkbox checkbox) {
    assertNotNull(context);
    checkbox.setValue(true);
    val selected = context.selectFirst(Checkbox.class);
    assertNotNull(selected);
    assertTrue(selected.getValue());
  }


  @ViewTest(navigateTo = "main")
  void ensureCssSelectorWorksOnCollectionTypeOfElements(
      @Select("body > section span") List<Element> children) {
    assertNotNull(children);
    assertEquals(2, children.size());
    assertTrue(children.stream().allMatch(t -> t.getTag().equals("span")));
  }
}