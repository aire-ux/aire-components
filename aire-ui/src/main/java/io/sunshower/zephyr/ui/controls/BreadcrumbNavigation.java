package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.gyre.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.val;

public class BreadcrumbNavigation extends Nav implements AfterNavigationObserver {

  public BreadcrumbNavigation() {
    getStyle().set("position", "absolute");
    getStyle().set("left", "80px");
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterNavigation(AfterNavigationEvent event) {
    removeAll();
    val target = locateRouteTarget(event);
    val links = collectLinks(target, event);
    val iter = links.listIterator(links.size());
    while (iter.hasPrevious()) {
      add(iter.previous());
      if (iter.hasPrevious()) {
        add(" / ");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<Component> collectLinks(Pair<Breadcrumb, Class<? extends Component>> target,
      AfterNavigationEvent event) {

    if (target != null) {
      val results = new ArrayList<Component>();
      addBreadcrumbChain(event, results, target.snd);
      var host = target.fst.host();
      while (host != null) {
        if (!RouterLayout.class.equals(host)) {
          addBreadcrumbChain(event, results, (Class<? extends Component>) host);
          val bc = host.getAnnotation(Breadcrumb.class);
          if (bc != null) {
            host = bc.host();
          }
        } else {
          break;
        }
      }
      return results;
    }
    return Collections.emptyList();
  }

  private void addBreadcrumbChain(AfterNavigationEvent event, List<Component> results,
      Class<? extends Component> host) {
    val breadcrumb = host.getAnnotation(Breadcrumb.class);
    if (hasResolver(breadcrumb)) {
      val resolver = Instantiator.get(UI.getCurrent()).getOrCreate(breadcrumb.resolver());
      results.addAll(resolver.resolve(breadcrumb, event));
    } else {
      results.add(new RouterLink(breadcrumb.name(), host));
    }
  }

  private boolean hasResolver(Breadcrumb breadcrumb) {
    return !CrumbResolver.class.equals(breadcrumb.resolver());
  }

  private Pair<Breadcrumb, Class<? extends Component>> locateRouteTarget(
      AfterNavigationEvent event) {
    val chain = event.getActiveChain();
    val iterator = chain.listIterator();
    while (iterator.hasNext()) {
      val previous = iterator.next();
      val element = previous.getElement();
      val component = element.getComponent();
      if (component.isPresent()) {
        val type = component.get().getClass();
        if (type.isAnnotationPresent(Breadcrumb.class)) {
          return Pair.of(type.getAnnotation(Breadcrumb.class), type);
        }
      }
    }
    return null;
  }
}
