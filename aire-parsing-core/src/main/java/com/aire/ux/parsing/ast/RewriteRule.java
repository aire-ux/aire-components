package com.aire.ux.parsing.ast;

@FunctionalInterface
public interface RewriteRule<T, U> {

  SyntaxNode<T, U> apply(SyntaxNode<T, U> node);
}
