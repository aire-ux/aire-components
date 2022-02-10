package io.sunshower.zephyr.ui.controls;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser;
import com.aire.ux.test.NodeAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;
import io.sunshower.zephyr.ui.aire.ComponentHierarchyNodeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

@Tag("aire-menu-forest")
@JsModule("./aire/ui/controls/menu-forest.ts")
@CssImport("./styles/aire/ui/controls/menu-forest.css")
public class MenuForest extends HtmlContainer {

  private static final PlanContext planContext;
  private static final CssSelectorParser selectorParser;
  private static final NodeAdapter<Element> componentNodeAdapter;

  static {
    selectorParser = new CssSelectorParser();
    planContext = DefaultPlanContext.getInstance();
    componentNodeAdapter = new ComponentHierarchyNodeAdapter();
  }

  private final List<MenuTree> trees;

  public MenuForest() {
    trees = new ArrayList<>();
  }

  public MenuTree createRoot(Component component) {
    val tree = new MenuTree(component);
    trees.add(tree);
    add(tree);
    return tree;
  }


  public MenuTree locate(String selector) {
    val result = selectSingle(selector);
    if (result.isPresent()) {
    }
    return null;
  }


  public Optional<Component> selectSingle(String selector) {
    return select(selector).stream().findFirst();

  }

  public Set<Component> select(String selector) {
    return selectorParser
        .parse(selector)
        .plan(planContext)
        .evaluate(this.getElement(), componentNodeAdapter)
        .results().stream().flatMap(c -> c.getComponent().stream())
        .collect(Collectors.toUnmodifiableSet());
  }
}
