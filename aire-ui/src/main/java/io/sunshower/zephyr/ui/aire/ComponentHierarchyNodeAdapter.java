package io.sunshower.zephyr.ui.aire;

import com.aire.ux.condensation.mappings.LRUCache;
import com.aire.ux.test.NodeAdapter;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.BeanUtil;
import io.sunshower.arcus.reflect.Reflect;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

public class ComponentHierarchyNodeAdapter implements NodeAdapter<Element> {

  private final LRUCache<Class<?>, Map<String, PropertyDescriptor>> attributeCache;

  public ComponentHierarchyNodeAdapter() {
    attributeCache = new LRUCache<>(100);
  }

  static List<PropertyDescriptor> getPropertyDescriptors(Class<?> type) {
    try {
      return BeanUtil.getBeanPropertyDescriptors(type);

    } catch (IntrospectionException e) {
      return Collections.emptyList();
    }
  }

  /**
   * last resort
   *
   * @param name the name to convert
   * @return the mapped mutator name
   */
  static String methodNameFrom(String name, Class<?> type) {
    val isBoolean = type.equals(boolean.class) || type.equals(Boolean.class);
    final String prefix;
    if (isBoolean) {
      prefix = "is";
    } else {
      prefix = "get";
    }
    return prefix + name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
  }

  @NonNull
  @Override
  public List<Element> getChildren(Element current) {
    if (current == null) {
      return Collections.emptyList();
    }
    return new ElementList(current);
  }

  @Nullable
  @Override
  public Element getParent(Element current) {
    if (current == null) {
      return null;
    }
    return current.getParent();
  }

  @Override
  public Element setChildren(
      @NonNull Element current, @NonNull Collection<? extends Element> children) {
    removeChildren(current);
    for (val child : children) {
      current.appendChild(child);
    }
    return current;
  }

  @Nullable
  @Override
  public String getAttribute(@NonNull Element current, @NonNull String key) {
    if (current.hasAttribute(key)) {
      return current.getAttribute(key);
    }
    val componentType = current.getClass();

    val componentProperties = readAndCacheProperties(componentType);
    val result = getAttributeFrom(current, key, componentType, componentProperties);
    if (result != null) {
      return result;
    }
    return null;
  }

  @Override
  public Element setAttribute(Element node, String key, String value) {
    node.setAttribute(key, value);
    return node;
  }

  @Override
  public boolean hasAttribute(Element c, String key) {
    return getAttribute(c, key) != null;
  }

  @Override
  public Element clone(Element value) {
    val result = Reflect.instantiate(value.getClass());
    value
        .getAttributeNames()
        .forEach(
            attribute -> {
              result.setAttribute(attribute, value.getAttribute(attribute));
            });
    return result;
  }

  @Override
  public String getType(Element n) {
    try {
      return n.getTag();
    } catch (UnsupportedOperationException ex) {
      if (Element.class.equals(n.getClass())) {
        return "text";
      }
    }
    throw new UnsupportedOperationException(
        "Unknown element type: " + n + " (" + n.getClass() + ")");
  }

  @Override
  public void setState(@NonNull Element element, @NonNull NodeAdapter.State state) {
    element.setAttribute("state", state.toSymbol().name());
  }

  @Override
  public boolean hasState(@NonNull Element element, @NonNull NodeAdapter.State state) {
    val attr = element.getAttribute("state");
    if (attr != null) {
      return attr.equals(state.toSymbol().name());
    }
    return false;
  }

  @Nullable
  @Override
  public Element getSucceedingSibling(@NonNull Element element) {
    val parent = element.getParent();
    if (parent != null) {
      return parent.getChild(parent.indexOfChild(element) + 1);
    }
    return null;
  }

  @Override
  public State stateFor(String name) {
    for (val state : ElementState.values()) {
      if (name.equals(state.toSymbol().name())) {
        return state;
      }
    }
    return null;
  }

  private void removeChildren(Element el) {
    for (int i = 0; i < el.getChildCount(); i++) {
      el.removeChild(0);
    }
  }

  @Nullable
  private String getAttributeFrom(
      @NonNull Object current,
      @NonNull String key,
      Class<?> componentType,
      Map<String, PropertyDescriptor> componentProperties) {
    if (componentProperties.containsKey(key)) {
      val descriptor = componentProperties.get(key);
      var result = getAndCastPropertyToString(descriptor, current);
      if (result != null) {
        return result;
      } else {
        result =
            readDirectly(
                descriptor.getName(), descriptor.getPropertyType(), componentType, current);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  private String readDirectly(
      String name, Class<?> propertyType, Class<?> componentType, Object value) {
    val getterName = methodNameFrom(name, propertyType);
    try {
      val method = componentType.getMethod(getterName);
      val result = method.invoke(value);
      return String.class.equals(propertyType) ? (String) result : String.valueOf(result);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      return null;
    }
  }

  private String getAndCastPropertyToString(PropertyDescriptor descriptor, Object current) {
    if (descriptor.getName().equals("text") && Element.class.equals(current.getClass())) {
      return ((Element) current).getText();
    }
    val method = descriptor.getReadMethod();
    if (method == null) {
      return null;
    }
    try {
      return String.valueOf(method.invoke(descriptor, current));
    } catch (IllegalAccessException | InvocationTargetException e) {
      // todo idk when/if this is going to be a problem
      throw new IllegalStateException(e);
    }
  }

  private Map<String, PropertyDescriptor> readAndCacheProperties(Class<?> elementType) {
    return attributeCache.computeIfAbsent(
        elementType,
        (k) ->
            getPropertyDescriptors(elementType).stream()
                .reduce(
                    new HashMap<>(),
                    (result, descriptor) -> {
                      result.put(descriptor.getName(), descriptor);
                      return result;
                    },
                    (acc, v) -> {
                      acc.putAll(v);
                      return acc;
                    }));
  }
}

final class ElementList extends AbstractList<Element> {

  final Element source;

  ElementList(Element source) {
    this.source = source;
  }

  @Override
  public Element get(int i) {
    return source.getChild(i);
  }

  @Override
  public int size() {
    return source.getChildCount();
  }
}
