package com.aire.ux.parsers.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;

public interface SyntaxNode {

  Symbol getSymbol();

  DocTree getComment();

  Element getSource();

  String getContent();

  List<SyntaxNode> getChildren();

  boolean hasChildren();

  boolean addChild(SyntaxNode child);

  void setContent(String content);

  String getProperty(String key);

  boolean hasProperty(String key);

  String setProperty(String key, String value);

  Map<String, String> getProperties();

  String clearProperty(String key);
}
