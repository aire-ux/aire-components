package com.aire.ux.select.type;

import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FunctionApplicationTest extends TestCase {

  @ParameterizedTest
  @ValueSource(strings = "p:nth-last-of-type(2)")
  void ensureSimpleFunctionApplicationWorks(String value) {
    val t = parser.parse(value);
    System.out.println(t.getSyntaxTree());



  }

}
