package com.aire.ux.test;

import static java.lang.Math.max;

import lombok.val;
import org.junit.jupiter.api.Test;

class NodesTest {

  @Test
  void ensureParentChildApiMakesSense() {
    val r = Nodes.node("p").attribute("class", "red").children(
        Nodes.node("a").attribute("href", "www.google.com")
            .children(Nodes.node("h1").content("hello world, how "
                + "\nare you?"))
    );
    new NodeHierarchyWalker<Node>().walk(r, Node.getAdapter(),
        new NodeVisitor<Node>() {
          int depth = 0;
          @Override
          public void openNode(Node node, NodeAdapter<Node> adapter) {
            val indent = " ".repeat(depth);
            System.out.println(indent + node.getType());
            depth += 1;
          }

          @Override
          public void closeNode(Node node, NodeAdapter<Node> adapter) {
            val indent = " ".repeat(max(depth, 0));
            System.out.println(indent + node.getType());
            depth =- 1;
          }
        });
  }

}