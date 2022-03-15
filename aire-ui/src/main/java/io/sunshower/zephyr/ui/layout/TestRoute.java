package io.sunshower.zephyr.ui.layout;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Badge;
import io.sunshower.zephyr.ui.components.Badge.Mode;
import io.sunshower.zephyr.ui.components.Card;
import io.sunshower.zephyr.ui.components.Card.Slot;
import io.sunshower.zephyr.ui.components.DefinitionList;
import io.sunshower.zephyr.ui.components.Panel;
import lombok.val;

@Route(value = "test", layout = MainView.class)
public class TestRoute extends Panel {

  public TestRoute() {
    createContent();
  }

  private void createContent() {

    val card = new Card();
    card.setTitle("Heroku");
    card.setIcon(VaadinIcon.ARCHIVES.create());

    card.add(Slot.Header, new Text("Heroku"));
    val dl =
        new DefinitionList()
            .key("Version")
            .value(new Badge(Mode.Contrast, "1.0"))
            .key("Name")
            .value(new Badge(Mode.Contrast, "Heroku"));
    card.add(Slot.Content, dl);

    card.add(Slot.Footer, new Badge(Mode.Success, "Active"));

    //    card.add(Slot.Content, new
    //    card.getHeader().add(new Text("Heroku"));

    add(card);
  }
}
