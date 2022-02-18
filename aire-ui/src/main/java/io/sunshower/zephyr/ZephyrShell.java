package io.sunshower.zephyr;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;

@Push(PushMode.MANUAL)
@PWA(name = "Zephyr Shell for Aire", shortName = "Zephyr")
public class ZephyrShell implements AppShellConfigurator {}
