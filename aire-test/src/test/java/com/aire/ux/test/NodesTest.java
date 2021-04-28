package com.aire.ux.test;

import lombok.val;
import org.junit.jupiter.api.Test;

class NodesTest {

  @Test
  void ensureParentChildApiMakesSense() {
    val r = Nodes.node("p").attribute("class", "red").children(
        Nodes.node("a").attribute("href", "www.google.com").children(Nodes.node("h1"))
    ).toString();
    System.out.println(r);
  }

}