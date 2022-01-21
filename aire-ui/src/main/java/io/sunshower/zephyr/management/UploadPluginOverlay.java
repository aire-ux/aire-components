package io.sunshower.zephyr.management;

import static java.nio.file.Files.copy;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import io.sunshower.zephyr.ui.components.Overlay;
import io.zephyr.common.io.Files;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.module.ModuleInstallationGroup;
import io.zephyr.kernel.module.ModuleInstallationRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class UploadPluginOverlay extends Overlay implements ComponentEventListener<SucceededEvent> {

  private final Kernel kernel;
  private Upload upload;
  private MultiFileMemoryBuffer buffer;

  @Inject
  public UploadPluginOverlay(Kernel kernel) {
    this.kernel = kernel;
    addHeader();
    addContent();
    addFooter();
  }

  private void addFooter() {

    val menubar = new MenuBar();
    menubar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE);

    val cancel = new Button("Cancel", VaadinIcon.CLOSE.create());
    cancel.addClickListener(event -> this.cancel());

    val install = new Button("Upload", VaadinIcon.UPLOAD_ALT.create());
    install.addClickListener(this::onSuccess);
    menubar.addItem(cancel);
    menubar.addItem(install);
    getFooter().add(menubar);
  }

  private void addContent() {
    buffer = new MultiFileMemoryBuffer();
    upload = new Upload(buffer);
    upload.getStyle().set("height", "100%");
    upload.setAutoUpload(false);
    upload.addSucceededListener(this);
    addContent(upload);
  }

  private void addHeader() {
    val header = getHeader();
    header.add(new H1("Upload Modules"));
    header.add(getCloseButton());
  }

  private void onSuccess(ClickEvent<Button> event) {
    try {
      val uploaded = new HashSet<Path>();
      val directory = createOutputDirectory().toFile();
      copyFiles(uploaded, directory);
      installFiles(uploaded);
      close();
    } catch (IOException ex) {
      log.warn("Failed to install module.  Reason: {} ", ex.getMessage());
      log.debug("Full trace: ", ex);
    } finally {
      //      buffer = null;
    }
  }

  private void installFiles(Set<Path> uploaded) {

    val group = new ModuleInstallationGroup();
    try {
      for (val upload : uploaded) {
        val request = new ModuleInstallationRequest();
        request.setLocation(upload.toUri().toURL());
        group.add(request);
      }
      kernel.getModuleManager().prepare(group).commit().toCompletableFuture().get();
    } catch (Throwable ex) {
      log.warn("Somehow a local filesystem URL was malformed.  Reason at debug");
      log.debug("Reason: ", ex);
    }
  }

  private void copyFiles(Set<Path> uploaded, File directory) throws IOException {
    for (val file : buffer.getFiles()) {
      val target = new File(directory, file);
      val dest = Files.doCheck(target.toPath());
      uploaded.add(dest);
      copy(buffer.getInputStream(file), dest, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private Path createOutputDirectory() throws IOException {
    val dest = kernel.getFileSystem().getPath("uploaded");
    java.nio.file.Files.createDirectories(dest);
    return dest;
  }

  @Override
  public void onComponentEvent(SucceededEvent event) {
  }
}
