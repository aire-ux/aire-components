package com.aire.ux.test;

import com.aire.ux.test.Context.Mode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface TestContext {

  <T> Optional<T> selectFirst(String selector, Class<T> type);

  /**
   * @param types the collection of types to select. If this list is empty or null, return
   *              <i>all</i> matching types
   * @return the matching types, or the component hierarchy if no types are specified
   */
  @Nonnull
  List<?> selectComponents(Class<?>... types);

  /**
   * @param types the collection of types to select. If this list is empty or null, return
   *              <i>all</i> matching types
   * @return the matching types, or the component hierarchy if no types are specified
   */
  @Nonnull
  List<Element> selectElements(Class<?>... types);

  /**
   * @param type the type to select
   * @param <T>  the type-parameter of the type
   * @return a (possibly empty) list of matching elements
   */
  <T> List<T> select(Class<T> type);

  /**
   * @param type the type to match
   * @param <T>  the type-parameter of the element class
   * @return the first matching element
   * @throws java.util.NoSuchElementException if no element is found
   */
  <T extends Component> Optional<T> selectFirst(Class<T> type);

  /**
   * @param selector the item to match
   * @return a (possibly empty) list of matching elements
   */
  List<?> selectComponents(String selector);

  void navigate(String route);

  void navigate(Class<? extends Component> route);

  /**
   * @param contextClass the context-class to resolve
   * @param mode         the mode (mock, spy, none) to apply to the context variable
   * @param <T>          the type-parameter of the context class
   * @return the context class
   * @throws java.util.NoSuchElementException if the context class is not available from any
   *                                          provider
   */
  <T> T resolve(Class<T> contextClass, Mode mode);

  default <T> T resolve(Class<T> contextClass) {
    return resolve(contextClass, Mode.None);
  }

  <T extends Component> TestContext downTo(Class<T> contextClass);

  <T extends Component> TestContext downTo(T value);

  void flush();
}
