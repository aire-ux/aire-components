package io.sunshower.zephyr.configuration;

import static java.lang.String.format;

import com.aire.ux.Aire;
import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.DefaultUserInterface;
import com.aire.ux.UserInterface;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.actions.DefaultActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringComponentInclusionManager;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import io.sunshower.crypt.DefaultSecretService;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.zephyr.ZephyrApplication;
import io.sunshower.zephyr.ui.i18n.AireResourceBundleResolver;
import io.sunshower.zephyr.ui.i18n.InternationalizationBeanPostProcessor;
import io.sunshower.zephyr.ui.i18n.ResourceBundleResolver;
import io.zephyr.api.ModuleEvents;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.FactoryServiceDefinition;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.launch.KernelOptions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ZephyrCoreConfiguration extends WebSecurityConfigurerAdapter
    implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

  private static final AtomicBoolean initialized;
  private static final String LOGIN_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error";
  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGOUT_SUCCESS_URL = "/login";

  static {
    initialized = new AtomicBoolean();
  }

  private ServiceRegistration<UserInterface> result;

  @Bean
  public static UserInterface userInterface(
      ExtensionRegistry extensionRegistry, AccessQueue accessQueue, ActionManager actionManager) {
    return Aire.setUserInterface(
        new DefaultUserInterface(extensionRegistry, accessQueue, actionManager));
  }

  @Bean
  public static ActionManager actionManager() {
    return new DefaultActionManager();
  }

  @Bean(name = "threadPoolTaskExecutor")
  public static ThreadPoolTaskExecutor executor() {
    val executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(8);
    return executor;
  }

  @Bean
  public static ResourceBundleResolver resourceBundleResolver() {
    return new AireResourceBundleResolver();
  }

  @Bean
  public static BeanPostProcessor internationalizationBeanPostProcessor(
      ResourceBundleResolver resolver, ApplicationContext context) {
    return new InternationalizationBeanPostProcessor(resolver, context);
  }

  @Bean
  public org.apache.commons.configuration2.Configuration zephyrConfigurationSource()
      throws IOException, ConfigurationException {
    val rootDirectory = KernelOptions.getKernelRootDirectory();
    val cfg = rootDirectory.toPath().resolve(Paths.get("config"));
    log.info("Attempting to resolve base configuration from {}", cfg.toAbsolutePath());
    if (!Files.exists(cfg)) {
      log.info("File {} does not exist--attempting to create it", cfg);
      try {
        Files.createDirectories(cfg);
      } catch (IOException ex) {
        log.error("Error.  Failed to create directory {}.  Reason: {}", cfg, ex.getMessage());
      }
    } else {
      if (!Files.isDirectory(cfg)) {
        log.error("Error: file {} exists, but it is not a directory", cfg);
      }
    }

    val file = new File(cfg.toFile(), "aire.properties").toPath();

    if (!Files.exists(file)) {
      log.info("Properties file {} does not exist--attempting to create it", file);
      try {
        Files.createFile(file);
        log.info("Successfully created {}", file);
      } catch (IOException ex) {
        log.error(
            "Application properties file {} "
                + "does not exist and could not be created.  Reason: {}. Can't continue",
            file,
            ex.getMessage());
      }
    }

    if (!Files.isRegularFile(file)) {
      throw new IllegalArgumentException(format("Error: file %s is not a file", file));
    }

    log.info("Successfully resolved configuration file {}", file);

    val parameters = new Parameters();
    return new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
        .configure(parameters.fileBased().setFile(file.toFile()).setThrowExceptionOnMissing(false))
        .getConfiguration();
  }

  @Bean
  @ConditionalOnProperty("ui-service.init.listener")
  public ConfigureUIServiceInitListener configureUIServiceInitListener(
      SecretService service, org.apache.commons.configuration2.Configuration configuration) {
    return new ConfigureUIServiceInitListener(service, configuration);
  }

  @Bean(name = "crypt.keeper.secret.service")
  public SecretService cryptkeeperSecretService() throws IOException {
    val path = KernelOptions.getKernelRootDirectory().toPath().resolve("security/auth/secrets");

    log.info("Locating administrator crypt at: {}", path);
    if (!(Files.exists(path))) {
      log.info("Administrator crypt directory {} does not exist. Attempting to create it", path);
      try {
        Files.createDirectories(path);
      } catch (IOException ex) {
        log.error("Failed to create crypt directory: {}.  Reason: {}", path, ex.getMessage());
        throw ex;
      }
    }
    return new DefaultSecretService(path.toFile(), ZephyrApplication.getCondensation());
  }

  @Bean
  public ComponentInclusionManager componentInclusionManager() {
    return new SpringComponentInclusionManager();
  }

  @Bean
  public ExtensionRegistry extensionRegistry(AccessQueue queue, ComponentInclusionManager manager) {
    return new SpringExtensionRegistry(
        queue, () -> VaadinService.getCurrent().getContext(), manager);
  }

  @Bean(name = "applicationEventMulticaster")
  public ApplicationEventMulticaster applicationEventMulticaster(ThreadPoolTaskExecutor executor) {
    val result = new SimpleApplicationEventMulticaster();
    result.setTaskExecutor(executor);
    return result;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();
    return new InMemoryUserDetailsManager(user);
  }

  private void registerServices(
      Module module, ConfigurableApplicationContext context, Kernel kernel) {
    log.info("Registering UserInterface service");
    result =
        kernel
            .getServiceRegistry()
            .register(
                module,
                new FactoryServiceDefinition<>(
                    UserInterface.class,
                    "aire:user-interface",
                    () -> context.getBean(UserInterface.class)));

    kernel.addEventListener(
        (type, event) -> {
          val ui = UI.getCurrent();
          if (ui != null) {
            ui.access(
                () -> {
                  ui.getPage().reload();
                });
          }
        },
        ModuleEvents.STOPPED,
        ModuleEvents.STARTED);
    log.info("Successfully registered UserInterface service");
  }

  @Override
  public void destroy() throws Exception {
    if (result != null) {
      result.dispose();
    }
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (!initialized.get()) {
      val context = event.getApplicationContext();
      val kernel = context.getBean(Kernel.class);
      val module = context.getBean(Module.class);
      registerServices(module, context, kernel);
      initialized.set(true);
    }
  }

  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .requestCache()
        .requestCache(new SecurityRequestCache())
        .and()
        .authorizeRequests()
        .requestMatchers(SecurityUtils::isFrameworkInternalRequest)
        .permitAll() // (3)
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage(LOGIN_URL)
        .permitAll()
        .loginProcessingUrl(LOGIN_PROCESSING_URL)
        .failureUrl(LOGIN_FAILURE_URL)
        .and()
        .logout()
        .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers(
            "/VAADIN/**",
            "/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/aire/initialize/**",
            "/offline-page.html",
            "/icons/**",
            "/images/**",
            "/frontend/**",
            "/webjars/**",
            "/h2-console/**",
            "/frontend-es5/**",
            "/frontend-es6/**");
  }
}
