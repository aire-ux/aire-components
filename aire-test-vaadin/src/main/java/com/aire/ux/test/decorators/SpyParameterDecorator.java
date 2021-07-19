package com.aire.ux.test.decorators;

import com.aire.mock.SpyService;
import com.aire.ux.test.Context;
import com.aire.ux.test.Context.Mode;
import com.aire.ux.test.ParameterDecorator;
import com.aire.ux.test.Utilities;
import com.aire.ux.test.vaadin.TestFrame;
import java.util.ServiceLoader;
import lombok.val;

@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
public class SpyParameterDecorator implements ParameterDecorator {

  @Override
  public <T> T decorate(T value, Context context, TestFrame frame) {
    if (frame.hasSpy(value)) {
      return value;
    }
    if (Utilities.isMode(context, Mode.Mock)) {
      val services =
          ServiceLoader.load(SpyService.class, Thread.currentThread().getContextClassLoader());
      for (val service : services) {
        return frame.addSpy(service.apply(value));
      }
    }
    return value;
  }

  @Override
  public <T> void deactivate(T value, Context context, TestFrame frame) {

    if (Utilities.isMode(context, Mode.Mock)) {
      val services =
          ServiceLoader.load(SpyService.class, Thread.currentThread().getContextClassLoader());
      for (val service : services) {
        service.deactivate(value);
        frame.removeSpy(value);
        return;
      }
    }
  }
}
