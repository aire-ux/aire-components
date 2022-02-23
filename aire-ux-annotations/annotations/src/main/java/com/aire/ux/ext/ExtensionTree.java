package com.aire.ux.ext;

import static java.lang.String.format;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.HasElement;
import io.sunshower.gyre.CompactTrieMap;
import io.sunshower.gyre.RegexStringAnalyzer;
import io.sunshower.gyre.TrieMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.val;

public class ExtensionTree {

  private final Class<?> type;
  private final String root;
  private final ExtensionRegistry registry;
  private final TrieMap<String, SlotDefinition> slots;

  public ExtensionTree(@Nonnull Class<?> type, @Nonnull ExtensionRegistry registry) {
    this.type = type;
    this.root = getRoot(type);
    this.registry = registry;
    this.slots = new CompactTrieMap<>(new RegexStringAnalyzer(":"));
    buildTree();
  }

  private void buildTree() {
    defineProperties();
  }

  private void defineProperties() {
    for (var c = type; !Objects.equals(Object.class, c); c = c.getSuperclass()) {
      definePropertiesIn(new StringBuilder(root), c);
    }
  }

  private void definePropertiesIn(StringBuilder pathBuilder, Class<?> c) {
    for (val field : c.getDeclaredFields()) {
      if (field.isAnnotationPresent(Slot.class)) {
        val levelPath = new StringBuilder(pathBuilder);
        val slot = field.getAnnotation(Slot.class);
        levelPath.append(slot.value());
        val s = levelPath.toString();
        val slotDefinition = new SlotDefinition(s, new FieldProperty(field));
        slots.put(s, slotDefinition);

        val fieldType = field.getType();
        if (fieldType.isAnnotationPresent(Host.class)) {
          val host = fieldType.getAnnotation(Host.class);
          val name = normalize(host.value());
          levelPath.append(name);
          for (var h = c; !Objects.equals(Object.class, h); h = h.getSuperclass()) {
            definePropertiesIn(levelPath, h);
          }
        }
      }
    }
  }


  private String getRoot(Class<?> type) {
    val result = type.getAnnotation(Host.class);
    if (result == null) {
      throw new IllegalArgumentException(
          format("Error: please annotate type '%s' with @Host", type));
    }
    return normalize(result.value());
  }


  private String normalize(String value) {
    if (value.charAt(0) != ':') {
      return ':' + value;
    }
    return value;
  }

  public String getSegment() {
    return root;
  }

  public List<SlotDefinition> slotsWithin(String s) {
    return slots.level(s);
  }

  public Optional<SlotDefinition> slotsAt(String path) {
    return Optional.ofNullable(slots.get(path));
  }

  public Optional<? extends HasElement> componentAt(String path, HasElement root) {
    val segments = getPath(path).iterator();
    if (segments.hasNext()) {
      var type = registry.typeOf(root);
      verifyPath(segments.next(), root, type);
      while (segments.hasNext()) {
        var element = search(segments.next(), type, root);
        if (!segments.hasNext()) {
          return Optional.ofNullable(element);
        }
      }
    }
    return Optional.empty();
  }

  private HasElement search(String slotName, Class<?> type, HasElement instance) {
    for (var c = type; !(c == null || Objects.equals(c, Object.class)); c = c.getSuperclass()) {
      for (val property : c.getDeclaredFields()) {
        val slot = property.getAnnotation(Slot.class);
        if(slot == null) {
          continue;
        }
        if (Objects.equals(normalize(slotName), slot.value())) {
          try {
            if (property.trySetAccessible()) {
              return (HasElement) property.get(instance);
            } else {
              throw new IllegalArgumentException(
                  String.format("Error: property '%s' is not accessible", property));
            }
          } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
          }
        }
      }
    }
    return null;
  }

  private void verifyPath(String next, HasElement root, Class<?> type) {
    val host = type.getAnnotation(Host.class);
    if (!Objects.equals(host.value(), next)) {
      throw new IllegalArgumentException(
          String.format("Error: expected '%s', but got '%s' (component-type: '%s')", host.value(),
              next, type));
    }
  }

  private List<String> getPath(String path) {
    val nonNormalized = List.of(path.split(":"));
    val result = new ArrayList<String>(nonNormalized.size());
    for (val n : nonNormalized) {
      if (!n.isBlank()) {
        result.add(n);
      }
    }
    return result;
  }

}
