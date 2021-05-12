package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.EvaluatorFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

class FunctionApplicationEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  @Test
  void ensureFunctionApplicationWorks() {
    val node = node("html").child(node("body").child(node("div")));

    val result = eval("html:nth-child(0)", node);
    System.out.println(result);
  }

  @Test
  void ensureNestedSelectionsWork() {
    val node =
        node("html")
            .child(
                node("body")
                    .children(
                        node("section")
                            .children(
                                node("div")
                                    .attribute("class", "select")
                                    .children(
                                        node("span"),
                                        node("ul")
                                            .children(
                                                node("li").attribute("first", "true"),
                                                node("li").attribute("second", "true"),
                                                node("li").attribute("third", "true")))),
                        node("article")
                            .attribute("class", "select")
                            .children(
                                node("span").attribute("first", "true"),
                                node("span").attribute("second", "true"))));

    val results = eval("html > body *.select:nth-child(1)", node);
    assertEquals(2, results.size());
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new FunctionApplicationEvaluatorFactory();
  }
}
