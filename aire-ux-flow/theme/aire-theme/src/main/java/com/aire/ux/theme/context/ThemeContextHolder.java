package com.aire.ux.theme.context;

import io.sunshower.arcus.reflect.Reflect;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import lombok.extern.java.Log;
import lombok.val;

/** */
@Log
public class ThemeContextHolder {

  /** the strategy name to use. May be a fully-qualified class name of a themecontextstrategy */
  private static String strategyName;
  /** the current theme strategy */
  private static ThemeContextHolderStrategy strategy;

  static {
    initialize();
  }

  /**
   * set the strategy name for this
   *
   * @param name
   */
  public static void setStrategyName(@Nonnull final String name) {
    Objects.requireNonNull(name);
    strategyName = name;
    initialize();
  }

  /** @return the active theme context strategy */
  public static ThemeContextHolderStrategy getStrategy() {
    return strategy;
  }

  /**
   * set the strategy to use from the enumeration of defaults
   *
   * @param strategy the strategy to use. Use {@code setStrategyName if you need a class name}
   */
  public static void setStrategy(@Nonnull Strategy strategy) {
    Objects.requireNonNull(strategy);
    setStrategyName(strategy.name());
  }

  /** @return the current theme context */
  public static ThemeContext getContext() {
    return strategy.getContext();
  }

  /** @param context to make current */
  public static void setContext(@Nonnull ThemeContext context) {
    Objects.requireNonNull(context);
    strategy.setContext(context);
  }

  public static Strategy getStrategyType() {
    for (val strategyType : Strategy.values()) {
      if (strategyType.name().equals(strategyName)) {
        return strategyType;
      }
    }
    return Strategy.ClassName;
  }

  /** clear the theme context for the current thread */
  public static void clearContext() {
    Objects.requireNonNull(strategy);
    strategy.clearContext();
  }

  private static void initialize() {
    if (strategyName == null) {
      strategyName = Strategy.ThreadLocal.name();
    }

    if (is(strategyName, Strategy.ThreadLocal)) {
      strategy = new ThreadLocalContextHolderStrategy();
    } else if (is(strategyName, Strategy.InheritableThreadLocal)) {
      strategy = new InheritableThreadLocalContextHolderStrategy();
    } else if (is(strategyName, Strategy.Global)) {
      strategy = new GlobalThemeContextHolderStrategy();
    } else if (is(strategyName, Strategy.ServiceLoader)) {
      val loader =
          ServiceLoader.load(
                  ThemeContextHolderStrategy.class, Thread.currentThread().getContextClassLoader())
              .iterator();

      if (!loader.hasNext()) {
        throw new IllegalStateException(
            "Attempted to load ContextHolderStrategy from service loader, but there were no ContextHolderStrategies available");
      }

      val result = loader.next();
      if (loader.hasNext()) {
        log.log(
            Level.WARNING,
            "More than one ThemeContextStrategy available from the service loader--only using the first ({0}).",
            result.getClass());
        log.log(
            Level.WARNING,
            "If this is not the desired behavior, you may set the class-name explicitly via setStrategyName()");
      }
      strategy = result;
    } else {
      try {
        strategy =
            (ThemeContextHolderStrategy)
                Reflect.instantiate(
                    Class.forName(
                        strategyName, true, Thread.currentThread().getContextClassLoader()));
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Error: no class named '%s'", e);
      }
    }
  }

  /**
   * determine if this strategy name is the same as the provided strategy
   *
   * @param strategyName the name
   * @param strategy the strategy
   * @return true if this strategy is the same as the strategy name
   */
  private static boolean is(String strategyName, Strategy strategy) {
    return strategy.name().equals(strategyName);
  }

  public enum Strategy {
    /** apply this theme context strategy to the entire system */
    Global,

    /** use a thread-local theme */
    ThreadLocal,

    /** attempt to load a theme context strategy from the serviceloader */
    ServiceLoader,

    /** use an inheritable thread local (default) */
    InheritableThreadLocal,

    /** get-only */
    ClassName,
  }
}
