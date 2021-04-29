package com.aire.ux.test;


import static org.junit.jupiter.api.Assertions.assertEquals;

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
    val result = Node.getAdapter().reduce(r, 0, (c, n) -> n + 1);
    assertEquals(3, result);

  }

  @Test
  void ensureMappingWorks() {
    val r = Nodes.node("p").attribute("class", "red").children(
        Nodes.node("a").attribute("href", "www.google.com")
            .children(Nodes.node("h1").content("hello world, how "
                + "\nare you?"))
    );
    val adapter = Node.getAdapter();

    var count = Node.getAdapter()
        .reduce(r, 0, (c, n) -> adapter.hasAttribute(c, "hello") ? n + 1 : n);
    assertEquals(0, count);
    val t = Node.getAdapter().map(r, Node.getAdapter(), (node, hom) ->
        hom.setAttribute(node, "hello", "world")
    );
    count = Node.getAdapter().reduce(t, 0, (c, n) -> adapter.hasAttribute(c, "hello") ? n + 1 : n);
    assertEquals(3, count);
    System.out.println(t);
  }

}