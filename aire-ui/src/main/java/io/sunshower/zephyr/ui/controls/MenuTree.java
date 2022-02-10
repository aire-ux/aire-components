package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import lombok.val;

@ToString
@Tag("aire-menu-tree")
public class MenuTree extends HtmlContainer {

  private final MenuNode root;

  public MenuTree(MenuNode root) {
    this.root = root;
    add(root.component);
  }

  public MenuTree(Component component) {
    this(new MenuNode(component));
  }

  public MenuTree createChild(Component component) {
    val result = new MenuTree(component);
    add(result);
    return result;
  }


  private static final class MenuNode {

    private final Component component;
    private final List<MenuNode> children;

    private MenuNode(Component component) {
      this(component, new ArrayList<>(0));
    }

    private MenuNode(Component component, List<MenuNode> children) {
      this.component = component;
      this.children = children;
    }
  }
}
