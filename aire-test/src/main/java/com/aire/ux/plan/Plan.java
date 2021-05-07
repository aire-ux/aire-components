package com.aire.ux.plan;

import com.aire.ux.test.NodeAdapter;
import java.util.List;
import java.util.Set;

public interface Plan extends AutoCloseable {

  <T extends Evaluator> List<T> getEvaluators(Class<T> evaluatorType);

  <T> Set<T> evaluate(T tree, NodeAdapter<T> hom);
}
