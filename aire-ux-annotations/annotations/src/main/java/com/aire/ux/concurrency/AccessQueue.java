package com.aire.ux.concurrency;

import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;

public interface AccessQueue {

  void enqueue(Command command);

  void drain(VaadinSession session);
}
