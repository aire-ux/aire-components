package com.aire.ux.theme.context;

final class InheritableThreadLocalContextHolderStrategy
    extends AbstractThreadLocalContextHolderStrategy {

  InheritableThreadLocalContextHolderStrategy() {
    super(new InheritableThreadLocal<>(), new InheritableThreadLocal<>());
  }
}
