package com.aire.ux;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.FutureAccess;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinSession;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import lombok.val;

public class AsynchronousSessionQueue implements AccessQueue {

  private final WeakHashMap<Integer, UI> uiReferences;
  private final ConcurrentLinkedQueue<Command> commands;
  private final Set<WeakReference<VaadinSession>> sessionReferences;
  private final ReferenceQueue<VaadinSession> sessionReferenceQueue;

  public AsynchronousSessionQueue() {
    commands = new ConcurrentLinkedQueue<>();
    uiReferences = new WeakHashMap<>();
    sessionReferences = new HashSet<>();
    sessionReferenceQueue = new ReferenceQueue<>();
  }

  @Override
  public void drain(UI session) {
    synchronized (this) {
      val iterator = commands.iterator();
      while (iterator.hasNext()) {
        session.access(iterator.next());
        iterator.remove();
      }
    }
  }

  @Override
  public void enqueue(Command command) {
    synchronized (this) {
      val vaadinSession = VaadinSession.getCurrent();
      if (vaadinSession != null) {
        vaadinSession.access(command);
      } else {
        commands.add(command);
      }
    }
  }

  public void drain(VaadinSession session) {
    synchronized (this) {
      val accessQueue = session.getPendingAccessQueue();
      val iterator = commands.iterator();
      while (iterator.hasNext()) {
        accessQueue.offer(new FutureAccess(session, iterator.next()));
        iterator.remove();
      }
    }
  }

  public void broadcast(Target target, Consumer<Object> command) {
    synchronized (this) {
      if (target == Target.UI || target == Target.Both) {
        for (val ui : uiReferences.values()) {
          if (ui != null) {
            command.accept(ui);
          }
        }
      }
      if (target == Target.Session || target == Target.Both) {
        val iter = sessionReferences.iterator();
        while (iter.hasNext()) {
          val next = iter.next();
          val g = next.get();
          if (g == null) {
            iter.remove();
          } else {
            g.getPendingAccessQueue().offer(new FutureAccess(g, (Command) () -> command.accept(g)));
          }
        }
      }
    }
  }

  @Override
  public void sessionDestroy(SessionDestroyEvent event) {
    synchronized (this) {
      val iterator = sessionReferences.iterator();
      while (iterator.hasNext()) {
        val next = iterator.next();
        if (Objects.equals(next.get(), event.getSession())) {
          iterator.remove();
        }
      }
    }
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException {
    synchronized (this) {
      val session = event.getSession();
      sessionReferences.add(new WeakReference<>(session, sessionReferenceQueue));
      drain(session);
    }
  }

  @Override
  public void uiInit(UIInitEvent event) {
    synchronized (this) {
      val ui = event.getUI();
      uiReferences.put(ui.getUIId(), ui);
      drain(ui);
    }
  }
}
