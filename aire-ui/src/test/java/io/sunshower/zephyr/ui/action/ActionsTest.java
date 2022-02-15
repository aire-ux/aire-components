package io.sunshower.zephyr.ui.action;

import com.aire.ux.test.Routes;
import com.aire.ux.test.View;
import com.aire.ux.test.ViewTest;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.action.views.TestActionView;

@AireUITest
@Routes(scanClassPackage = TestActionView.class)
class ActionsTest {

  @ViewTest
  void ensureActionCanBeRegisteredWithCorrectComponents(@View TestActionView view) {}
}
