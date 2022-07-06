package io.sunshower.zephyr.ui.aire;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import io.sunshower.arcus.selectors.css.CssSelectorParser;
import io.sunshower.arcus.selectors.plan.DefaultPlanContext;
import io.sunshower.arcus.selectors.test.NodeAdapter;
import java.util.List;
import java.util.stream.Collectors;

public final class DOM {

  private static final CssSelectorParser parser;
  private static final NodeAdapter<Element> adapter;

  static {
    parser = new CssSelectorParser();
    adapter = new ComponentHierarchyNodeAdapter();
  }

  private final Component root;

  public DOM(Component root) {
    this.root = root;
  }

  public static DOM $(Component root) {
    return new DOM(root);
  }

  public List<Element> elements(String selector) {
    return parser
        .parse(selector)
        .plan(DefaultPlanContext.getInstance())
        .evaluate(root.getElement(), adapter)
        .stream()
        .toList();
  }

  public List<Component> querySelector(String selector) {
    return elements(selector).stream()
        .flatMap(t -> t.getComponent().stream())
        .collect(Collectors.toList());
  }
}
