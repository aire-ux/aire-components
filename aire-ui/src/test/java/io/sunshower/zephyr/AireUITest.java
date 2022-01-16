package io.sunshower.zephyr;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.spring.EnableSpring;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

@AireTest
@EnableSpring
@SpringBootTest
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AireUITest {}
