package io.sunshower.zephyr.ui.controls;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.button.Button;
import io.sunshower.zephyr.AireUITest;
import java.util.Set;
import lombok.val;

@AireUITest
class MenuForestTest {

  @ViewTest
  void ensureSelectorWorksOnRoot(@View MenuForest forest) {
    forest.createRoot(new Button());
    assertEquals(1, forest.select("vaadin-button").size());
  }

  @ViewTest
  void ensureDescendentSelectorWorksAsExpected(@View MenuForest forest) {
    forest.createRoot(new Button());
    assertEquals(1, forest.select("aire-menu-forest > aire-menu-tree > vaadin-button").size());
  }

  @ViewTest
  void ensureSelectingMenuTreeAtIndexWorks(@View MenuForest forest) {
    forest.createRoot(new Button());
    val tree = forest.createRoot(new Button());
    assertEquals(Set.of(tree), forest.select("aire-menu-forest > aire-menu-tree:nth-child(2)"));
  }

  @ViewTest
  void ensureAddingMenuItemWorks(@View MenuForest forest) {
    Button b;
    val menu = forest.createRoot(new Button("Hello"));
    val tree = menu.createChild(b = new Button("world"))
        .createChild(new Button("Sup"));
    assertEquals(b, forest.selectSingle("aire-menu-forest > aire-menu-tree vaadin-button[text=world]").get());
  }
}
