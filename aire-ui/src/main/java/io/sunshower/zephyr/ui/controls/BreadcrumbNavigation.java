package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.gyre.Pair;
import java.util.ArrayList;
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
    val links = collectLinks(target);
    val iter = links.listIterator(links.size());
    while (iter.hasPrevious()) {
      add(iter.previous());
      if (iter.hasPrevious()) {
        add(" / ");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<RouterLink> collectLinks(Pair<Breadcrumb, Class<? extends Component>> target) {
    val results = new ArrayList<RouterLink>();
    if (target != null) {
      val routerLink = new RouterLink(target.fst.name(), target.snd);
      results.add(routerLink);
      var host = target.fst.host();
      while (host != null) {
        if (!RouterLayout.class.equals(host)) {
          val breadcrumb = host.getAnnotation(Breadcrumb.class);
          results.add(new RouterLink(breadcrumb.name(), (Class<? extends Component>) host));
          val bc = host.getAnnotation(Breadcrumb.class);
          if (bc != null) {
            host = bc.host();
          }
        } else {
          host = null;
        }
      }
    }
    return results;
  }


  private Pair<Breadcrumb, Class<? extends Component>> locateRouteTarget(
      AfterNavigationEvent event) {
    val chain = event.getActiveChain();
    val iterator = chain.listIterator(chain.size());
    while (iterator.hasPrevious()) {
      val previous = iterator.previous();
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
