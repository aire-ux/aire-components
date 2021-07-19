package com.aire.ux.mockito;

import com.aire.mock.MockService;
import org.mockito.Mockito;

public class MockitoMockService implements MockService {

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
