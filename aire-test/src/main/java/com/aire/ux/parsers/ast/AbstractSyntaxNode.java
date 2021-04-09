package com.aire.ux.parsers.ast;

import com.sun.source.doctree.DocTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;

public class AbstractSyntaxNode implements SyntaxNode {

  /** immutable state */
  final Symbol symbol;

  final Element source;
  final DocTree comment;
  final List<SyntaxNode> children;
  final Map<String, String> properties;

  /** private state */
  private String content;

  public AbstractSyntaxNode(Symbol symbol, Element source, DocTree comment) {
    this(symbol, source, null, comment, new ArrayList<>());
  }

  /**
   * @param symbol the associated symbol (element type)
   * @param source the language element this was retrieved from
   * @param content the String content (if any)
   * @param comment the actual comment node (if any)
   */
  public AbstractSyntaxNode(Symbol symbol, Element source, String content, DocTree comment) {
    this(symbol, source, content, comment, new ArrayList<>());
  }

  public AbstractSyntaxNode(
      Symbol symbol, Element source, String content, DocTree comment, List<SyntaxNode> children) {
    this.symbol = symbol;
    this.source = source;
    this.content = content;
    this.comment = comment;
    this.children = children;
    this.properties = new LinkedHashMap<>();
  }

  @Override
  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public DocTree getComment() {
    return comment;
  }

  @Override
  public Element getSource() {
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
  public List<SyntaxNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  @Override
  public boolean addChild(SyntaxNode child) {
    return children.add(child);
  }
}
