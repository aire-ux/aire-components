package com.aire.ux.theme.servlet.servlet4;

import com.aire.ux.test.spring.EnableSpring;
import com.aire.ux.theme.decorators.EnableThemes;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@Documented
@EnableSpring
@EnableThemes
@ExtendWith(MockitoExtension.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@SpringJUnitWebConfig(classes = AireServletConfiguration.class)
public @interface EnableAireThemeServlet {}
