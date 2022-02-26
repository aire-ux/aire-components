package io.sunshower.zephyr.core.modules;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import com.aire.ux.UIExtension;
import io.zephyr.kernel.Module;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

@SuppressWarnings("PMD")
public class AireComponentScanner implements Callable<Set<Class<?>>>, Supplier<Set<Class<?>>> {

  private final Module module;

  AireComponentScanner(@NonNull Module module) {
    this.module = module;
  }

  @Override
  public Set<Class<?>> call() {
    val scanner = createScanner();
    return scanner.get(TypesAnnotated.with(UIExtension.class).asClass(module.getClassLoader()));
  }

  private Reflections createScanner() {
    val classloader = module.getClassLoader();
    val configuration = new ConfigurationBuilder().setClassLoaders(new ClassLoader[] {classloader});

    for (val pkg : classloader.getDefinedPackages()) {
      configuration.forPackage(pkg.getName(), classloader);
    }
    return new Reflections(configuration);
  }

  @Override
  public Set<Class<?>> get() {
    return call();
  }
}
