package com.aire.ux.parsers.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbstractSyntaxNode<T, U> implements SyntaxNode<T, U> {

  /** immutable state */
  final Symbol symbol;

  final U source;
  final T value;
  final Map<String, String> properties;
  final List<SyntaxNode<T, U>> children;

  /** private state */
  private String content;

  public AbstractSyntaxNode(Symbol symbol, U source, T value) {
    this(symbol, source, null, value, new ArrayList<>());
  }

  /**
   * @param symbol the associated symbol (element type)
   * @param source the language element this was retrieved from
   * @param content the String content (if any)
   * @param value the actual value node (if any)
   */
  public AbstractSyntaxNode(Symbol symbol, U source, String content, T value) {
    this(symbol, source, content, value, new ArrayList<>());
  }

  public AbstractSyntaxNode(
      Symbol symbol, U source, String content, T value, List<SyntaxNode<T, U>> children) {
    this.symbol = symbol;
    this.source = source;
    this.content = content;
    this.value = value;
    this.children = children;
    this.properties = new LinkedHashMap<>();
  }

  @Override
  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public U getSource() {
    return source;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String getProperty(String key) {
    return properties.get(key);
  }

  @Override
  public boolean hasProperty(String key) {
    return properties.containsKey(key);
  }

  @Override
  public String setProperty(String key, String value) {
    return properties.put(key, value);
  }

  @Override
  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(properties);
  }

  @Override
  public String clearProperty(String key) {
    return properties.remove(key);
  }

  @Override
  public void addChildren(List<SyntaxNode<T, U>> children) {
    this.children.addAll(children);
  }

  @Override
  public List<SyntaxNode<T, U>> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  @Override
  public boolean addChild(SyntaxNode<T, U> child) {
    return children.add(child);
  }
}
