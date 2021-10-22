package com.aire.ux.condensation;

import com.aire.ux.condensation.json.Value;
import com.aire.ux.parsing.ast.SyntaxNode;
import com.aire.ux.parsing.core.Token;

public interface TypeBinder {

  <T> T instantiate(Class<T> type);

  PropertyScanner getPropertyScanner();

  <T> TypeDescriptor<T> descriptorFor(Class<T> type);

  <T> T bind(Class<T> type, SyntaxNode<Value<?>, Token> root);
}
