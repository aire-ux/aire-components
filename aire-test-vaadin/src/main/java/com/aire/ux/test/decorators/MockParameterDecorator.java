package com.aire.ux.test.decorators;

import com.aire.mock.MockService;
import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.ParameterDecorator;
import com.aire.ux.test.Utilities;
import com.aire.ux.test.vaadin.TestFrame;
import java.util.ServiceLoader;
import lombok.val;

public class MockParameterDecorator implements ParameterDecorator {

  @Override
  public <T> T decorate(T value, Context context, TestFrame frame) {
    if(frame.hasMock(value)) {
      return value;
    }
    if (Utilities.isMode(context, Mode.Mock)) {
      val services = ServiceLoader
          .load(MockService.class, Thread.currentThread().getContextClassLoader());
      for (val service : services) {
        val result = service.apply(value);
        frame.addMock(result);
        return result;
      }
    }
    return value;
  }

  @Override
  public <T> void deactivate(T value, Context context, TestFrame frame) {
    if (Utilities.isMode(context, Mode.Mock)) {
      val services = ServiceLoader
          .load(MockService.class, Thread.currentThread().getContextClassLoader());
      for (val service : services) {
        service.deactivate(value);
        frame.removeMock(value);
        return;
      }
    }
  }
}
