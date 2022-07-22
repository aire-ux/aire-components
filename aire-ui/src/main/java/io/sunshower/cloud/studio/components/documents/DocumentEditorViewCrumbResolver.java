package io.sunshower.cloud.studio.components.documents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.AfterNavigationEvent;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.CrumbResolver;
import java.util.Collection;
import java.util.Collections;

public class DocumentEditorViewCrumbResolver implements CrumbResolver {

  @Override
  public Collection<Component> resolve(Breadcrumb crumb, AfterNavigationEvent event) {
    return Collections.emptyList();
  }
}
