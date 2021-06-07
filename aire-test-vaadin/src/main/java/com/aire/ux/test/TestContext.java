package com.aire.ux.test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface TestContext {

  /**
   * @param types the collection of types to select. If this list is empty or null, return
   *     <i>all</i> matching types
   * @return the matching types, or the component hierarchy if no types are specified
   */
  @Nonnull
  List<?> selectComponents(Class<?>... types);

  /**
   * @param types the collection of types to select. If this list is empty or null, return
   *     <i>all</i> matching types
   * @return the matching types, or the component hierarchy if no types are specified
   */
  @Nonnull
  List<Element> selectElements(Class<?>... types);

  /**
   * @param type the type to select
   * @param <T> the type-parameter of the type
   * @return a (possibly empty) list of matching elements
   */
  <T> List<T> select(Class<T> type);

  /**
   * @param type the type to match
   * @param <T> the type-parameter of the element class
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
}
