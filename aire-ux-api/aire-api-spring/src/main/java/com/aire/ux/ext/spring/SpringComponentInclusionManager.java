package com.aire.ux.ext.spring;

import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.ComponentInclusionVoter;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.Registration;
import com.aire.ux.RouteDefinition;
import com.aire.ux.ext.ExtensionRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.concurrent.ThreadSafe;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@ThreadSafe
public class SpringComponentInclusionManager
    implements ComponentInclusionManager, ApplicationContextAware {

  private final List<ComponentInclusionVoter> componentInclusionVoters;
  private Supplier<ExtensionRegistry> extensionRegistrySupplier;

  public SpringComponentInclusionManager() {
    this.componentInclusionVoters = new ArrayList<>();
  }

  @Override
  public Registration disableRoutes(Collection<RouteDefinition> routeDefinitions) {
    val registry = extensionRegistrySupplier.get();
    synchronized (registry) {
      for (val routeDefinition : routeDefinitions) {
        registry.unregister(routeDefinition);
      }
      return () -> {
        enableRoutes(routeDefinitions);
      };
    }
  }

  @Override
  public Registration enableRoutes(Collection<RouteDefinition> routeDefinitions) {
    val registry = extensionRegistrySupplier.get();
    synchronized (registry) {
      for (val routeDefinition : routeDefinitions) {
        registry.register(routeDefinition);
      }
      return () -> {
        disableRoutes(routeDefinitions);
      };
    }
  }

  @Override
  public Registration register(ComponentInclusionVoter voter) {
    synchronized (componentInclusionVoters) {
      componentInclusionVoters.add(voter);
      return () -> componentInclusionVoters.remove(voter);
    }
  }

  @Override
  public boolean decide(ExtensionDefinition<?> extension) {
    synchronized (componentInclusionVoters) {
      return componentInclusionVoters.stream().allMatch(voter -> voter.decide(extension));
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.extensionRegistrySupplier = () -> applicationContext.getBean(ExtensionRegistry.class);
  }
}
