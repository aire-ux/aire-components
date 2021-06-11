package com.aire.ux.parsers.ast;

import com.aire.ux.parsers.ast.AbstractSyntaxTree.Order;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.val;
import org.junit.jupiter.api.Test;

class AbstractSyntaxTreeTest extends TestCase {

  @Test
  void ensureRewritingTreeWorks() {
    val root = node(1, node(2, node(3)));


    val tree = new AbstractSyntaxTree<>(root);
    val newTree = tree.rewrite(node -> List.of(node(node.getValue() * 2), node(node.getValue() * 2)));
    System.out.println(tree);
    System.out.println(newTree);
  }

  private <T, U> SyntaxNode<T, U> node(T value, SyntaxNode<T, U> ...children) {
    val n = new AbstractSyntaxNode<T, U>(Symbol.symbol(String.valueOf(value)), null, value);
    n.setChildren(Arrays.asList(children));
    return n;
  }

  @Test
  void ensureIterationOrderIsCorrect() {
    val selector = parser.parse("div.hello > *.world");
    val preorderStream =
        StreamSupport.stream(selector.getSyntaxTree().iterate(Order.Pre).spliterator(), false)
            .collect(Collectors.toList());

    val postorderStream =
        StreamSupport.stream(selector.getSyntaxTree().iterate(Order.Post).spliterator(), false)
            .collect(Collectors.toList());

    val iter = preorderStream.iterator();
    val piter = postorderStream.iterator();
    while (piter.hasNext()) {
      //      System.out.println("PRE: " + iter.next());
      System.out.println("POS: " + piter.next());
    }
    System.out.println(selector.getSyntaxTree());
  }
}
