package com.aire.mock;

public interface SpyService {

  <T> T apply(T value);

  <T> void deactivate(T value);
}
