package com.aire.ux.plan;

import com.aire.ux.test.NodeAdapter;
import java.util.List;
import java.util.Set;

public interface Evaluator {


  <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom);
}
