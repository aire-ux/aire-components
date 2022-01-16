package io.sunshower.zephyr.configuration;

import io.zephyr.spring.embedded.EmbeddedSpringConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EmbeddedSpringConfiguration.class)
public class EmbeddedZephyrConfiguration {}
