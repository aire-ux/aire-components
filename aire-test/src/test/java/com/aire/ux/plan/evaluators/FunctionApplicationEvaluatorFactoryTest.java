package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;

import com.aire.ux.plan.EvaluatorFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

class FunctionApplicationEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  @Test
  void ensureSimpleSelectorWorks() {
    val node = node("html")
        .children(
            node("head"),
            node("body").child(
                node("ul")
                    .children(
                        node("li").content("test"),
                        node("li").content("hello")
                    )

            )
        );

    val results = eval(":nth-child(1)", node);
    assertContainsTypes(results, "head", "ul", "li");
  }

  @Test
  void ensureFunctionApplicationWorks() {
    val node = node("html").child(node("body").child(node("div")));

    val result = eval("html :nth-child(1)", node);
    assertContainsTypes(result, "body", "div");
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
    val results = eval("html > body *.select *:nth-child(1)[first$=ue]", node);
    assertContainsTypes(results, "span", "li");
  }

  @Test
  void ensureRepeatedFunctionApplicationWorks() {
    val node = node(
        "span"
    ).children(
        node("span").attribute("sup", "coolbeans").child(
            node("span").attribute("class", "test").child(
                node("span").attribute("class", "selected"))
        )
    );

    val result = eval(
        "span > span[sup $=beans]:nth-child(1) > span.test:nth-child(1).test:nth-child(1).test",
        node);
    System.out.println(result);
    assertContainsTypes(result, "span");
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new FunctionApplicationEvaluatorFactory();
  }
}
