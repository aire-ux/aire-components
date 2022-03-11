package com.aire.ux.annotations;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;

public class TestAccessQueue implements AccessQueue {

  @Override
  public void enqueue(Command command) {
    command.execute();
  }

  @Override
  public void drain(VaadinSession session) {}
}
