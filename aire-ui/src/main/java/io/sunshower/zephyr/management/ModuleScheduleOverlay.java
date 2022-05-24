package io.sunshower.zephyr.management;

import ch.qos.logback.classic.Logger;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.sunshower.gyre.Pair;
import io.sunshower.lang.events.Event;
import io.sunshower.zephyr.log.LogbackRemoteAppender;
import io.sunshower.zephyr.spring.Dynamic;
import io.sunshower.zephyr.ui.canvas.Canvas;
import io.sunshower.zephyr.ui.canvas.CanvasReadyEvent;
import io.sunshower.zephyr.ui.canvas.CellAttributes;
import io.sunshower.zephyr.ui.canvas.actions.AddVertexTemplateAction;
import io.sunshower.zephyr.ui.canvas.actions.AddVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.ConnectVerticesAction;
import io.sunshower.zephyr.ui.canvas.actions.SetAllCellAttributesAction;
import io.sunshower.zephyr.ui.components.Overlay;
import io.sunshower.zephyr.ui.components.Terminal;
import io.zephyr.kernel.Coordinate;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.concurrency.Process;
import io.zephyr.kernel.concurrency.TaskEvents;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.module.ModuleLifecycle;
import io.zephyr.kernel.module.ModuleLifecycle.Actions;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

@Slf4j
@PermitAll
public class ModuleScheduleOverlay extends Overlay
    implements ComponentEventListener<CanvasReadyEvent> {

  private static final CellAttributes stoppedAttributes;
  private static final CellAttributes startedAttributes;

  static {
    stoppedAttributes = new CellAttributes();
    val body = stoppedAttributes.addAttribute("body", new HashMap<String, Serializable>());
    body.put("strokeWidth", 3);
    body.put("stroke", "#990000");

    startedAttributes = new CellAttributes();
    val normalbody = startedAttributes.addAttribute("body", new HashMap<String, Serializable>());
    normalbody.put("strokeWidth", 3);
    normalbody.put("stroke", "#77CCA4");
  }

  private final Mode mode;
  private final Kernel kernel;
  private final Module module;
  private final Canvas canvas;
  private final Actions action;
  private final StopWatch stopWatch;
  private final Registration onReadyRegistration;
  private final Terminal terminal;
  private Process<String> process;
  private LogbackRemoteAppender appender;

  @Dynamic
  public ModuleScheduleOverlay(
      @Dynamic Mode mode,
      @Dynamic Kernel kernel,
      @Dynamic Module module,
      @Dynamic ModuleLifecycle.Actions action) {
    this.mode = mode;
    this.kernel = kernel;
    this.action = action;
    this.module = module;
    addHeader();
    val body = addBody();
    this.canvas = body.fst;
    this.terminal = body.snd;
    this.onReadyRegistration = canvas.addOnCanvasReadyListener(this);
    addFooter();
    this.stopWatch = new StopWatch();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    this.configureAppender();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.removeAppender();
  }

  @Override
  public void onComponentEvent(CanvasReadyEvent event) {
    computeSchedule(canvas)
        .thenAccept(
            v -> {
              log.info(
                  "Computed reduction schedule in {} milliseconds",
                  stopWatch.getLastTaskTimeMillis());
              log.info("Gyre quiesced");
              terminal.flush();
            });
  }

  private Pair<Canvas, Terminal> addBody() {
    val content = getContent();
    content.getElement().getStyle().set("margin-bottom", "0");

    val layout = new VerticalLayout();
    layout.setHeightFull();

    layout.setWidthFull();
    layout.setPadding(false);
    content.add(layout);

    val canvas = new Canvas();
    layout.add(canvas);

    val terminal = new Terminal();
    layout.add(terminal);
    return Pair.of(canvas, terminal);
  }

  private CompletableFuture<Void> computeSchedule(Canvas canvas) {
    return canvas
        .invoke(AddVertexTemplateAction.class, TopologyView.defaultTaskTemplate)
        .toCompletableFuture()
        .thenAccept(
            v -> {
              log.info("Computing reduction schedule...");
              stopWatch.start();
              val descriptor =
                  LifecycleTasks.createProcess(
                      kernel,
                      canvas.getModel(),
                      TopologyView.defaultEdgeTemplate,
                      TopologyView.defaultTaskTemplate,
                      action,
                      module.getCoordinate());
              process = descriptor.getProcess();
              process.setMode(Process.Mode.UserspaceAllocated);
              stopWatch.stop();
              log.info("Successfully computed reduction schedule...");
              canvas
                  .invoke(AddVerticesAction.class, descriptor.getVertices())
                  .then(e -> canvas.invoke(ConnectVerticesAction.class, descriptor.getEdges()));
            });
  }

  private void addHeader() {
    val header = getHeader();
    header.add(
        new H1(
            String.format(
                "%s execution graph for %s",
                mode == Mode.Executing ? "Executing" : "Planning",
                module.getCoordinate().toCanonicalForm())));
    header.add(getCloseButton());
  }

  private void addFooter() {
    val cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());
    cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
    cancelButton.addClickListener(
        click -> {
          this.close();
          Notification.show(
              "Cancelled Lifecycle Change for %s".formatted(module.getCoordinate()),
              5000,
              Position.TOP_STRETCH);
        });

    val applyButton = new Button("Execute", VaadinIcon.CURLY_BRACKETS.create());
    configureFooter(cancelButton, applyButton);
    getFooter().add(cancelButton, applyButton);
  }

  private Registration configureFooter(Button cancelButton, Button applyButton) {
    return applyButton.addClickListener(
        (ComponentEventListener<ClickEvent<Button>>)
            e -> {
              val startedReg =
                  process.addEventListener(
                      TaskEvents.TASK_STARTING,
                      (type, event) -> {
                        val coordinate = getCoordinate(event);
                        log.info("Starting task: {}", coordinate);
                        val attributes = attributesFor(coordinate);
                        canvas
                            .getUI()
                            .ifPresent(
                                ui -> {
                                  ui.accessSynchronously(
                                      () -> {
                                        canvas.invoke(SetAllCellAttributesAction.class, attributes);
                                        ui.push();
                                      });
                                });
                      });

              val completeReg =
                  process.addEventListener(
                      TaskEvents.TASK_COMPLETE,
                      (type, event) -> {
                        log.info("Task {} completed", event);
                      });

              kernel
                  .getScheduler()
                  .submit(process)
                  .toCompletableFuture()
                  .thenRun(
                      () -> {
                        completeReg.dispose();
                        startedReg.dispose();
                        getUI()
                            .ifPresent(
                                ui -> {
                                  ui.accessSynchronously(
                                      () -> {
                                        getFooter().remove(applyButton, cancelButton);
                                        val button = new Button("Close", VaadinIcon.CLOSE.create());
                                        button.addClickListener(close -> close());
                                        getFooter().add(button);
                                        ui.push();
                                      });
                                });
                      });
            });
  }

  /**
   * omfg
   *
   * @param event
   * @return
   */
  @SuppressFBWarnings
  @SuppressWarnings({"PMD", "unchecked"})
  private Coordinate getCoordinate(Event<?> event) {

    try {
      val method = event.getClass().getMethod("getTarget");
      method.trySetAccessible();
      val result = method.invoke(event);
      val resultMethod = result.getClass().getMethod("getCoordinate");
      resultMethod.trySetAccessible();
      return (Coordinate) resultMethod.invoke(result);
    } catch (Exception ex) {
      log.warn("Error: {}", ex);
      return null;
    }
  }

  private List<CellAttributes> attributesFor(Coordinate coordinate) {
    final CellAttributes attributes;
    if (action == Actions.Activate) {
      attributes = startedAttributes.clone();
    } else {
      attributes = stoppedAttributes.clone();
    }
    return canvas
        .getModel()
        .findVertex(v -> Objects.equals(v.getKey(), coordinate.toCanonicalForm()))
        .stream()
        .findAny()
        .map(
            v -> {
              attributes.setId(v.getId());
              return List.of(attributes);
            })
        .orElse(Collections.emptyList());
  }

  private void removeAppender() {
    val rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    if (appender != null) {
      rootLogger.detachAppender(appender);
      appender.stop();
    }
  }

  private void configureAppender() {
    val rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    appender = new LogbackRemoteAppender(terminal, rootLogger.getLoggerContext());
    rootLogger.addAppender(appender);
    appender.start();
  }

  public enum Mode {
    Scheduling,
    Executing
  }
}
