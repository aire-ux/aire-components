package com.aire.ux.plan;

import com.aire.ux.test.NodeAdapter;
import java.util.List;

public interface Plan {

  <T extends Evaluator> List<T> getEvaluators(Class<T> evaluatorType);

  <T> List<T> evaluate(T tree, NodeAdapter<T> hom);
}
