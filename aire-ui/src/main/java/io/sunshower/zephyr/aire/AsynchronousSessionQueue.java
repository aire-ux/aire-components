package io.sunshower.zephyr.aire;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.FutureAccess;
import com.vaadin.flow.server.VaadinSession;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.val;

public class AsynchronousSessionQueue implements AccessQueue {

  private final ConcurrentLinkedQueue<Command> commands;

  public AsynchronousSessionQueue() {
    commands = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void enqueue(Command command) {
    val vaadinSession = VaadinSession.getCurrent();
    if(vaadinSession != null) {
      vaadinSession.access(command);
    } else {
      commands.add(command);
    }
  }

  public void drain(VaadinSession session) {
    val accessQueue = session.getPendingAccessQueue();
    val iterator = commands.iterator();
    while (iterator.hasNext()) {
      accessQueue.offer(new FutureAccess(session, iterator.next()));
      iterator.remove();
    }
  }
}
