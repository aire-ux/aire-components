package com.aire.ux.test;

import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.impl.AbstractNodeStateProvider;
import io.sunshower.arcus.selectors.test.NodeAdapter;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentHierarchyNodeAdapter implements NodeAdapter<Element> {

  @NotNull
  @Override
  public List<Element> getChildren(@NotNull Element current) {
    return new ChildList(current);
  }

  @Nullable
  @Override
  public Element getParent(@NotNull Element current) {
    return current.getParent();
  }

  @Override
  public Element setChildren(
      @NotNull Element current, @NotNull Collection<? extends Element> children) {
    int i = 0;
    val iter = children.iterator();
    while (iter.hasNext()) {
      current.setChild(i++, iter.next());
    }
    return current;
  }

  @Nullable
  @Override
  public String getAttribute(@NotNull Element current, @NotNull String key) {
    return current.getAttribute(key);
  }

  @Override
  public Element setAttribute(Element node, String key, String value) {
    node.setAttribute(key, value);
    return node;
  }

  @Override
  public boolean hasAttribute(Element c, String key) {
    return c.hasAttribute(key);
  }

  @Override
  public Element clone(Element value) {
    return value; // todo: see what needs to happen here
  }

  @Override
  public String getType(Element n) {
    if (n.getStateProvider() instanceof AbstractNodeStateProvider) {
      return n.getTag();
    }
    return "text";
  }

  @Override
  public void setState(@NotNull Element element, @NotNull State state) {
    throw new UnsupportedOperationException("Not supported yet");
  }

  @Override
  public boolean hasState(@NotNull Element element, @NotNull State state) {
    throw new UnsupportedOperationException("Not supported yet");
  }

  @Nullable
  @Override
  public Element getSucceedingSibling(@NotNull Element element) {
    val parent = element.getParent();
    if (parent == null) {
      return null;
    }
    val idx = parent.indexOfChild(element);
    if (idx < parent.getChildCount()) {
      return parent.getChild(idx + 1);
    }
    return null;
  }

  @Override
  public State stateFor(String name) {
    throw new UnsupportedOperationException("Not supported yet");
  }
}

final class ChildList extends AbstractList<Element> {
  final Element parent;

  ChildList(Element parent) {
    this.parent = parent;
  }

  @Override
  public Element get(int index) {
    return parent.getChild(index);
  }

  @Override
  public int size() {
    return parent.getChildCount();
  }
}
