package com.aire.ux.plan;

import com.aire.ux.test.NodeAdapter;
import java.util.Set;

public interface Evaluator {

  /**
   *
   * @param workingSet the working set to compute the cost over
   * @param hom the hom functor mapping the type T to a hierarchical monoid
   * @param <T> the type of the selector hierarchy
   * @return the cost of this evaluation node
   */
  default <T> int computeCost(Set<T> workingSet, NodeAdapter<T> hom) {
    return 0;
  }


  /**
   *
   * @param workingSet actually perform the evaluation
   * @param hom the hom functor mapping the type T to a hierarchical monoid
   * @param <T> the type of the selector hierarchy
   * @return the cost of this evaluation node
   */
  <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom);
}
