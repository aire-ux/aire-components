package com.aire.ux.plan;

import com.aire.ux.test.NodeAdapter;
import java.util.List;

public interface Evaluator {

  <T> List<T> evaluate(List<T> workingSet, NodeAdapter<T> hom);
}
