package com.aire.ux.parsers.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.parsers.ast.AbstractSyntaxTree.Order;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.val;
import org.junit.jupiter.api.Test;

class AbstractSyntaxTreeTest extends TestCase {


  @Test
  void ensureIterationOrderIsCorrect() {
    val selector = parser.parse("div.hello > *.world");
    val preorderStream = StreamSupport.stream(
        selector.getSyntaxTree().iterate(Order.Pre).spliterator(),
        false).collect(Collectors.toList());

    val postorderStream = StreamSupport.stream(
        selector.getSyntaxTree().iterate(Order.Post).spliterator(),
        false).collect(Collectors.toList());

    val iter = preorderStream.iterator();
    val piter = postorderStream.iterator();
    while(piter.hasNext()) {
//      System.out.println("PRE: " + iter.next());
      System.out.println("POS: " + piter.next());
    }
    System.out.println(selector.getSyntaxTree());
  }

}