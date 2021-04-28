package com.aire.ux.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;

public class Node {

  private final String type;
  private final List<Node> children;
  private final Map<String, String> attributes;

  public Node(String type) {
    this.type = type;
    this.children = new ArrayList<>();
    this.attributes = new HashMap<>();
  }


  public void addChildren(Collection<? extends Node> children) {
    this.children.addAll(children);
  }

  public void addChild(Node child) {
    this.children.add(child);
  }


  public List<Node> setChildren(Collection<? extends Node> children) {
    val result = new ArrayList<>(this.children);
    this.children.clear();
    this.children.addAll(children);
    return result;
  }

  public String setAttribute(String attribute, String value) {
    return attributes.put(attribute, value);
  }

  public boolean hasAttribute(String attribute) {
    return attributes.containsKey(attribute);
  }

  /**
   * Builder methods
   */
  public Node children(Node... children) {
    setChildren(List.of(children));
    return this;
  }

  public Node attribute(String key, String value) {
    setAttribute(key, value);
    return this;
  }

  public Node child(Node child) {
    return children(this);
  }

  @Override
  public String toString() {
    val result = new StringBuilder();
    int depth = 0;
    writeNode(this, result, depth);
    return result.toString();
  }

  private void writeNode(Node node, StringBuilder result, int depth) {
    val indent = " ".repeat(depth);
    result.append(indent).append("<%s".formatted(node.type));
    if (!node.attributes.isEmpty()) {
      for (val kv : node.attributes.entrySet()) {
        result.append(" ").append(kv.getKey()).append("=\"").append(kv.getValue()).append("\"");
      }
    }
    result.append(">").append("\n");

    for (val child : node.children) {
      writeNode(child, result, depth + 1);
    }
    result.append(indent).append("</%s>".formatted(node.type)).append("\n");

  }


}
