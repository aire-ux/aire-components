package io.sunshower.zephyr;

import com.aire.ux.test.Adapter;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.RegisterComponentExtension;
import com.aire.ux.test.spring.EnableSpring;
import io.sunshower.zephyr.aire.AireVaadinOverrideAutoConfiguration;
import io.sunshower.zephyr.configuration.EmbeddedZephyrConfiguration;
import io.sunshower.zephyr.ui.aire.ComponentHierarchyNodeAdapter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@AireTest
@Inherited
@EnableSpring
@SpringBootTest(
    classes = {
      AireVaadinOverrideAutoConfiguration.class,
      EmbeddedZephyrConfiguration.class,
      AireUITestConfiguration.class
    })
@ExtendWith(RegisterComponentExtension.class)
@Adapter(ComponentHierarchyNodeAdapter.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AireUITest {}
