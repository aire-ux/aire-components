package com.aire.ux.test;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.val;

/**
 * adapts tree-like structures to structures that are understood by the planner
 *
 * @param <T> the type of the tree-like structure
 */
public interface NodeAdapter<T> {


  /**
   * id constant
   */
  String ID = "id";

  /**
   * class constant
   */
  String CLASS = "class";

  /**
   * map a tree-like structure to a tree-like structure
   *
   * @param root the root of the first tree
   * @param hom  the hom functor used to perform the mapping
   * @param f    the morphism T -> U
   * @param <U>  the type of the second structure
   * @return a "hierarchy" encoded by <code>hom</code>
   */
  default <U> U map(
      @Nonnull final T root,
      @Nonnull final NodeAdapter<U> hom,
      @Nonnull final BiFunction<T, NodeAdapter<U>, U> f) {
    return hom.setChildren(f.apply(root, hom),
        getChildren(root).stream().map(c -> map(c, hom, f)).collect(
            Collectors.toList()));
  }

  /**
   * @param current the root node
   * @param initial the initial value to reduce over
   * @param f       the reducer function
   * @param <U>     the type-parameter of the result
   * @return the hierarchy reduced over the reducer function
   */
  default <U> U reduce(@Nonnull final T current, @Nonnull final U initial,
      @Nonnull final BiFunction<T, U, U> f) {
    val stack = new ArrayDeque<T>();
    stack.push(current);
    var result = initial;
    while (!stack.isEmpty()) {
      val c = stack.pop();
      result = f.apply(c, result);
      for (val child : getChildren(c)) {
        stack.push(child);
      }
    }
    return result;
  }

  /**
   * @param current the node to retrieve the children of
   * @return the children, or an empty list if none exist
   */
  @Nonnull
  List<T> getChildren(T current);

  /**
   * @param current  the current node to set the children of
   * @param children the new children
   * @return the node
   */
  T setChildren(@Nonnull T current, @Nonnull Collection<? extends T> children);

  /**
   * @param current the node to retrieve the attribute value for
   * @param key     the attribute key
   * @return the attribute value, or null
   */
  @Nullable
  String getAttribute(@Nonnull T current, @Nonnull String key);

  /**
   * @param current the current node to retrieve the ID of
   * @return the node's ID, or null
   */
  @Nullable
  default String getId(T current) {
    return getAttribute(current, ID);
  }

  /**
   * @param current the node to retrieve the class of
   * @return the node's class value, or null if it isn't set
   */
  default String getClass(T current) {
    return getAttribute(current, CLASS);
  }


  /**
   *
   * @param node the node to set the attribute on
   * @param key the attribute key
   * @param value the attribute value
   * @return the node
   */
  T setAttribute(T node, String key, String value);

  /**
   *
   * @param c the node to determine if the attribute exists for
   * @param key the attribute key
   * @return true if the key exists
   */
  boolean hasAttribute(T c, String key);

  /**
   *
   * @param value the value to clone
   * @return a complete scalar copy (i.e. the children are not preserved)
   */
  T clone(T value);
}
