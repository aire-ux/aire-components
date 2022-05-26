package io.sunshower.zephyr;

import io.zephyr.kernel.core.KernelPackageReexportConstraintSetProvider;
import java.util.Set;
import lombok.NonNull;
import org.springframework.core.io.ResourceLoader;

public class AireKernelPackageReexportConstraintSetProvider
    implements KernelPackageReexportConstraintSetProvider {

  @Override
  public Mode getMode() {
    return Mode.Include;
  }

  @Override
  public Set<String> getPackages() {
    return Set.of(
        "com.vaadin.*",
        "com.aire.ux.*",
        "javax.inject.*",
        "javax.annotation.*",
        "io.sunshower.zephyr.management.*",
        "org.springframework.*",
        "io.zephyr.*",
        "io.zephyr.kernel.concurrency.*",
        "io.zephyr.kernel.core.actions.plugin.*",
        "io.zephyr.cli.*",
        "io.sunshower.gyre.*",
        "io.sunshower.lang.*",
        "io.sunshower.zephyr.aire.*",
        "io.sunshower.zephyr.ui.*",
        "io.sunshower.zephyr");
  }

  @Override
  public int compareTo(@NonNull KernelPackageReexportConstraintSetProvider other) {
    ResourceLoader loader;
    return Integer.compare(getPrecedence(), other.getPrecedence());
  }
}
