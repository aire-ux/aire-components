package com.aire.ux.theme;


public interface Decorator<T> {

  Class<T> getTarget();

  void decorate(T value);

}
