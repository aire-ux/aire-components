package io.sunshower.zephyr.configuration;

import io.zephyr.common.io.FilePermissionChecker.Type;
import io.zephyr.common.io.Files;
import io.zephyr.kernel.launch.KernelOptions;
import java.io.File;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import picocli.CommandLine;

@Slf4j
public class ApplicationArgumentsFileProvider implements FileProvider {

  private final ApplicationArguments arguments;

  public ApplicationArgumentsFileProvider(ApplicationArguments arguments) {
    this.arguments = arguments;
  }

  @Override
  public File createFile() {
    val options = new KernelOptions();
    new CommandLine(options).parseArgs(arguments.getSourceArgs());

    try {
      return Files.check(options.getHomeDirectory(), Type.DELETE, Type.READ, Type.WRITE);
    } catch (AccessDeniedException ex) {
      log.warn(
          "Error:  file {} is not accessible.  Reason: {}",
          options.getHomeDirectory(),
          ex.getMessage());
    }
    try {
      log.info("Attempting to use Zephyr default home directory...");
      val result = KernelOptions.getKernelRootDirectory();
      log.info("Using Zephyr default home directory {}...", result);
      return result;
    } catch (AccessDeniedException ex) {
      throw new IllegalStateException(ex);
    } catch (IllegalStateException ex) {
      // we're bootstrapping--need to ignore this for now
      val file = new File(System.getProperty("user.dir"), ".zephyr");
      if (!(file.exists() || file.mkdir())) {
        throw new IllegalStateException("Could not create directory " + file);
      }
      try {
        Files.check(file, Type.DELETE, Type.READ, Type.WRITE);
        return file;
      } catch (AccessDeniedException ex2) {
        log.error("Exhausted locations for Zephyr Home.  Reason: {}", ex2.getMessage());
      }
    }
    throw new IllegalStateException(
        "Could not locate a suitable home directory for Zephyr.  Cannot continue");
  }
}
