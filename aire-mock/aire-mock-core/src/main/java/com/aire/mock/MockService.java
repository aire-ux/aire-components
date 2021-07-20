package com.aire.mock;

public interface MockService {

  <T> T apply(T value);

  <T> void deactivate(T value);
}
