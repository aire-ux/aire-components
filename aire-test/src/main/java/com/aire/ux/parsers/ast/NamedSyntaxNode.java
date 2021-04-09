package com.aire.ux.parsers.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;
import javax.lang.model.element.Element;
import lombok.Getter;
import lombok.val;

public class NamedSyntaxNode extends AbstractSyntaxNode {

  @Getter final String name;

  public NamedSyntaxNode(
      String name,
      Symbol symbol,
      Element source,
      DocTree comment,
      String content,
      List<SyntaxNode> children) {
    super(symbol, source, content, comment, children);
    this.name = name;
  }

  public NamedSyntaxNode(
      String name, Symbol symbol, Element source, DocTree comment, String content) {
    super(symbol, source, content, comment);
    this.name = name;
  }

  public NamedSyntaxNode(String name, Symbol symbol, Element source, DocTree comment) {
    this(name, symbol, source, comment, null);
  }

  public String toString() {
    val content = getContent();

    return """
        %s[symbol:%s, name: %s]{%s}
        """
        .strip()
        .formatted(
            getClass().getSimpleName(),
            symbol,
            name,
            content == null ? null : content.replaceAll("\\n", " "));
  }
}
