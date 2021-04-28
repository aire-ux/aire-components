package com.aire.ux.plan;

import java.util.List;

public interface Plan {

  <T extends Evaluator> List<T>
  getEvaluators(Class<T> evaluatorType);
}
