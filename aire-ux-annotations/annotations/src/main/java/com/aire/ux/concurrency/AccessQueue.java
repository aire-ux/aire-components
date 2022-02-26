package com.aire.ux.concurrency;

import com.vaadin.flow.server.Command;

public interface AccessQueue {

  void enqueue(Command command);

}
