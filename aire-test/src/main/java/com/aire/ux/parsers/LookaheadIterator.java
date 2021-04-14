package com.aire.ux.parsers;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.annotation.Nonnull;
import lombok.val;

public interface LookaheadIterator<T> extends Iterator<T> {

  public static <T> LookaheadIterator<T> wrap(Iterator<T> iterator) {
    return new DefaultLookaheadIterator<>(iterator);
  }

  T peek();

  void pushBack(T next);

  void pushBack(T... all);
}

final class DefaultLookaheadIterator<T> implements LookaheadIterator<T> {

  final Stack<T> lookback;
  final Iterator<T> delegate;

  DefaultLookaheadIterator(@Nonnull Iterator<T> delegate) {
    this.delegate = delegate;
    this.lookback = new Stack<>();
  }

  @Override
  public T peek() {
    if (!hasNext()) {
      throw new NoSuchElementException("Reached end of iterator");
    }
    val result = next();
    lookback.push(result);
    return result;
  }

  @Override
  public void pushBack(T next) {
    lookback.push(next);
  }

  @Override
  public void pushBack(T... all) {
    for (val t : all) {
      pushBack(t);
    }
  }

  @Override
  public boolean hasNext() {
    return !lookback.isEmpty() || delegate.hasNext();
  }

  @Override
  public T next() {
    if (!lookback.isEmpty()) {
      return lookback.pop();
    }
    return delegate.next();
  }
}
