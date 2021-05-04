package com.aire.ux.plan.evaluators;

import com.aire.ux.plan.DefaultPlanContext;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.test.Node;
import org.junit.jupiter.api.BeforeEach;

public abstract class EvaluatorFactoryTestCase extends TestCase {

  protected Node tree;
  protected PlanContext context;
  protected EvaluatorFactory factory;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    factory = createFactory();
    context = DefaultPlanContext.getInstance();
  }

  protected abstract EvaluatorFactory createFactory();


}
