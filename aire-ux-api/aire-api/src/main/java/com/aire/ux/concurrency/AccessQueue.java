package com.aire.ux.concurrency;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.server.VaadinSession;
import java.util.function.Consumer;

public interface AccessQueue extends UIInitListener, SessionInitListener, SessionDestroyListener {

  @Override
  default void sessionDestroy(SessionDestroyEvent event) {}

  @Override
  default void sessionInit(SessionInitEvent event) throws ServiceException {}

  @Override
  default void uiInit(UIInitEvent event) {}

  default void drain(UI session) {}

  default void broadcast(Target target, Consumer<Object> command) {}

  void enqueue(Command command);

  void drain(VaadinSession session);

  enum Target {
    UI,
    Both,
    Session,
  }
}
