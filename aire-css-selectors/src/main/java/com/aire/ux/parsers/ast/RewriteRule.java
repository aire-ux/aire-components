package com.aire.ux.parsers.ast;

import java.util.Collection;

@FunctionalInterface
public interface RewriteRule<T, U> {

  Collection<SyntaxNode<T, U>> apply(SyntaxNode<T, U> node);

}
