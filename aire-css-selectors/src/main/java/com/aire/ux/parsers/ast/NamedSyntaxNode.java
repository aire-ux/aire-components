package com.aire.ux.parsers.ast;

import java.util.List;
import lombok.Getter;
import lombok.val;

public class NamedSyntaxNode<T, U> extends AbstractSyntaxNode<T, U> {

  @Getter final String name;

  public NamedSyntaxNode(
      String name,
      Symbol symbol,
      U source,
      T value,
      String content,
      List<SyntaxNode<T, U>> children) {
    super(symbol, source, content, value, children);
    this.name = name;
  }

  public NamedSyntaxNode(String name, Symbol symbol, U source, T value, String content) {
    super(symbol, source, content, value);
    this.name = name;
  }

  public NamedSyntaxNode(String name, Symbol symbol, U source, T value) {
    this(name, symbol, source, value, null);
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
