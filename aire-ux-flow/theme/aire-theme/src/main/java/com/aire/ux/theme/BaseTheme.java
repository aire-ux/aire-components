package com.aire.ux.theme;

import static java.util.Objects.requireNonNull;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.vaadin.flow.component.HasElement;
import io.sunshower.arcus.reflect.Reflect;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.val;

public abstract class BaseTheme implements Theme {


  static final int MAX_CACHE_SIZE = 50;

  private final String id;

  private final List<ThemeResource> themeResources;
  private final WeakReference<ClassLoader> classloader;
  private final Map<Class<?>, Decorator<?>> decorators;
  private final Map<Object, Decorator> decoratorCache = new LinkedHashMap<>() {

    @Override
    protected boolean removeEldestEntry(Entry<Object, Decorator> eldest) {
      return this.size() >= MAX_CACHE_SIZE;
    }

  };


  protected BaseTheme(
      @Nonnull final String id,
      @Nonnull final ClassLoader classloader
  ) {
    this(id, classloader, Collections.emptyList());
  }

  protected BaseTheme(
      @Nonnull final String id,
      @Nonnull final ClassLoader classloader,
      @Nonnull final Collection<ThemeResource> resources
  ) {
    this.id = requireNonNull(id);
    this.decorators = new HashMap<>();
    this.themeResources = new ArrayList<>(resources);
    this.classloader = new WeakReference<>(requireNonNull(classloader));
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public InputStream openResource(String path) {
    val cl = classloader.get();
    if (cl == null) {
      throw new IllegalStateException("Classloader is null--theme is unavailable");
    }
    return cl.getResourceAsStream(path);
  }

  @Override
  public List<ThemeResource> getThemeResources() {
    return themeResources;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends HasElement> void apply(T value) {
    val cached = decoratorCache.get(value);
    if (cached != null) {
      cached.decorate(value);
    } else {
      for (Class<?> type = value.getClass(); type != null; type = type.getSuperclass()) {
        val decorator = (Decorator) decorators.get(type);
        if (decorator != null) {
          decoratorCache.put(value, decorator);
          decorator.decorate(value);
        }
      }
    }
  }

  protected <T> void register(Class<T> type) {
    for (Class<?> t = type; t != null; t = t.getSuperclass()) {
      if (t.isAnnotationPresent(NoDecorate.class)) {
        if (t.isAnnotationPresent(Decorate.class)) {
          registerDecorator(t.getAnnotation(Decorate.class), t);
        } else {
          break;
        }
      }
      if (t.isAnnotationPresent(Decorate.class)) {
        registerDecorator(t.getAnnotation(Decorate.class), t);
      }
    }

  }

  private void registerDecorator(Decorate decorationDefinition, Class<?> t) {
    val definition = decorationDefinition.value();
    if (!Objects.equals(definition, Decorator.class)) {
      decorators.computeIfAbsent(t, (k) -> Reflect.instantiate(definition));
    }
  }
}
