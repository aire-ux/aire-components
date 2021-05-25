package com.aire.ux.parsers.ast;

import java.util.List;
import java.util.Map;

/**
 * Base class for a generic, homogeneous AST
 *
 * @param <T> the value contained (usually a symbol-like structure)
 * @param <U> the relevant source object (such as an element)
 */
public interface SyntaxNode<T, U> {

  Symbol getSymbol();

  T getValue();

  U getSource();

  String getContent();

  List<SyntaxNode<T, U>> getChildren();

  boolean hasChildren();

  boolean addChild(SyntaxNode<T, U> child);

  void setContent(String content);

  String getProperty(String key);

  boolean hasProperty(String key);

  String setProperty(String key, String value);

  Map<String, String> getProperties();

  String clearProperty(String key);

  void addChildren(List<SyntaxNode<T, U>> children);

  SyntaxNode<T, U> getChild(int i);

  SyntaxNode<T, U> removeChild(int i);

  SyntaxNode<T, U> removeChild(SyntaxNode<T, U> i);

  List<SyntaxNode<T, U>> clearChildren();
}
