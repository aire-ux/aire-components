package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.AfterNavigationEvent;
import java.util.Collection;

public interface CrumbResolver {

  Collection<Component> resolve(Breadcrumb crumb, AfterNavigationEvent event);
}
