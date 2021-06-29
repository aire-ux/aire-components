package com.aire.ux.theme.context;


final class ThreadLocalContextHolderStrategy extends AbstractThreadLocalContextHolderStrategy {
  ThreadLocalContextHolderStrategy() {
    super(new ThreadLocal<>());
  }
}
