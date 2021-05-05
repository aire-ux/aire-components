package com.aire.ux.plan.evaluators;

import static com.aire.ux.test.Nodes.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.test.Node;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AttributeSelectorEvaluatorFactoryTest extends EvaluatorFactoryTestCase {

  private Node node;

  @Test
  void ensureSingleStringMatchSelectorWorks() {
    val node = node("a").attribute("href", "google.com");
    val result = eval("a[href]", node);
    assertEquals(1, result.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "a[href=one]",
      "a[href='one']",
      "a[href=\"one\"]",
  })
  void ensureStrictEqualityMatches(String selector) {
    node = node("root").children(
        node("a").attribute("href", "one"),
        node("a").attribute("href", "two")
    );
    val result = eval(selector, node);
    assertEquals(result.size(), 1);
  }


  @ParameterizedTest
  @ValueSource(strings = {
      "[href=one]",
      "[href='one']",
      "[href=\"one\"]",
      "a[href=one]",
      "a[href='one']",
      "a[href=\"one\"]",
      "[href = one]",
      "[href =  'one']",
      "[href = \"one\" ]",
      "a[href= one ]",
      "a[ href= 'one' ]",
      "a[ href= \"one\"]",
  })
  void ensureStrictEqualityDoesntMatchSeparatedValues(String selector) {
    node = node("root").children(
        node("a").attribute("href", "one four"),
        node("a").attribute("href", "two four")
    );
    val result = eval(selector, node);
    assertEquals(result.size(), 0);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "a[href~=the-googs]",
      "*[href~=the-googs]",
      "[href~= 'the-googs' ]"
  })
  void ensureAttributeListMatchingWorks(String selector) {
    node = node("root").children(
        node("a").attribute("href", "the-googs not-the-googs"),
        node("a").attribute("href", "lolwat")
    );
    val result = eval(selector, node);
    assertEquals(result.size(), 1);
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return new AttributeSelectorEvaluatorFactory();
  }
}