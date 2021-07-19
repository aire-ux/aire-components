package com.aire.ux.mockito;

import com.aire.mock.SpyService;
import org.mockito.Mockito;

public class MockitoSpyService implements SpyService {

  @Override
  @SuppressWarnings("unchecked")
  public <T> T apply(T value) {
    return (T) Mockito.mock(value.getClass());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void deactivate(T value) {
    Mockito.reset(value);
  }
}
