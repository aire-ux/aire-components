package com.aire.ux.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.val;

public class Node {

  static final String EMPTY_CONTENT = "".intern();
  private final String type;
  private final String content;
  private final List<Node> children;
  private final Map<String, String> attributes;

  public Node(String type) {
    this(type, EMPTY_CONTENT);
  }

  public Node(String type, String content) {
    this(type, content, new ArrayList<>(), new LinkedHashMap<>());
  }

  Node(String type, String content, List<Node> children, Map<String, String> attributes) {
    this.type = type;
    this.content = content;
    this.children = children;
    this.attributes = attributes;
  }

  public static NodeAdapter<Node> getAdapter() {
    return new NodeNodeAdapter();
  }

  public Node setContent(String content) {
    return new Node(type, content, children, attributes);
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

  public Node id(String id) {
    return attribute("id", id);
  }

  public Node attribute(String key, String value) {
    setAttribute(key, value);
    return this;
  }

  public Node child(Node child) {
    return children(this);
  }

  public Node content(String content) {
    return setContent(content);
  }

  @Override
  public String toString() {
    val result = new StringBuilder();
    int depth = 0;
    writeNode(this, result, depth);
    return result.toString();
  }

  boolean hasContent() {
    return content != EMPTY_CONTENT;
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
    if (node.hasContent()) {
      val content = node.content.split("\\R");
      val ind = indent + " ";
      for (val c : content) {
        result.append(ind).append(c).append("\n");
      }
    }

    for (val child : node.children) {
      writeNode(child, result, depth + 1);
    }
    result.append(indent).append("</%s>".formatted(node.type)).append("\n");

  }

  public String getAttribute(String key) {
    return attributes.get(key);
  }

  public String getType() {
    return type;
  }

  public static class NodeNodeAdapter implements NodeAdapter<Node> {

    @Override
    public List<Node> getChildren(Node current) {
      return current.children;
    }

    @Override
    public Node setChildren(Node current, Collection<? extends Node> children) {
      current.setChildren(children);
      return current;
    }

    @Override
    public String getAttribute(Node current, String key) {
      return current.getAttribute(key);
    }

    @Override
    public Node setAttribute(Node node, String key, String value) {
      node.setAttribute(key, value);
      return node;
    }

    @Override
    public boolean hasAttribute(Node c, String key) {
      return c.attributes.containsKey(key);
    }

    @Override
    public Node clone(Node value) {
      return new Node(value.type, value.content, new ArrayList<>(), value.attributes);
    }
  }


}
