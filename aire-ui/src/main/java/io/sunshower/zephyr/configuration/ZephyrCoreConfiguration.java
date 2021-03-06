package io.sunshower.zephyr.configuration;

import static java.lang.String.format;

import com.aire.ux.Aire;
import com.aire.ux.ComponentInclusionManager;
import com.aire.ux.DefaultUserInterface;
import com.aire.ux.Registration;
import com.aire.ux.UserInterface;
import com.aire.ux.actions.ActionManager;
import com.aire.ux.actions.DefaultActionManager;
import com.aire.ux.concurrency.AccessQueue;
import com.aire.ux.ext.ExtensionRegistry;
import com.aire.ux.ext.spring.SpringComponentInclusionManager;
import com.aire.ux.ext.spring.SpringExtensionRegistry;
import com.aire.ux.zephyr.ZephyrService;
import com.vaadin.flow.server.VaadinService;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.cloud.studio.git.DirectoryBackedWorkspaceService;
import io.sunshower.cloud.studio.workflows.ModuleDrivenWorkflowService;
import io.sunshower.cloud.studio.workflows.WorkflowService;
import io.sunshower.crypt.DefaultSecretService;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.lang.Suppliers;
import io.sunshower.model.api.Session;
import io.sunshower.zephyr.ZephyrApplication;
import io.sunshower.zephyr.security.AireRealmAggregator;
import io.sunshower.zephyr.security.CompositeRealmManager;
import io.sunshower.zephyr.security.CryptkeeperAuthenticationProvider;
import io.sunshower.zephyr.ui.i18n.AireResourceBundleResolver;
import io.sunshower.zephyr.ui.i18n.InternationalizationBeanPostProcessor;
import io.sunshower.zephyr.ui.i18n.ResourceBundleResolver;
import io.zephyr.api.ServiceRegistration;
import io.zephyr.api.ServiceRegistry;
import io.zephyr.kernel.Module;
import io.zephyr.kernel.core.FactoryServiceDefinition;
import io.zephyr.kernel.core.Kernel;
import io.zephyr.kernel.launch.KernelOptions;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j
@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)

public class ZephyrCoreConfiguration extends WebSecurityConfigurerAdapter
    implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

  private static final AtomicBoolean initialized;
  private static final String LOGIN_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error";
  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGOUT_SUCCESS_URL = "/login";

  private final List<Registration> registrations;

  static {
    initialized = new AtomicBoolean();
  }

  public ZephyrCoreConfiguration() {
    registrations = new ArrayList<>();
  }

  private ServiceRegistration<UserInterface> userInterfaceRegistration;

  @Bean
  @ZephyrService(type = UserInterface.class)
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
  public BeanPostProcessor internationalizationBeanPostProcessor(
      ResourceBundleResolver resolver, ApplicationContext context) {
    return new InternationalizationBeanPostProcessor(resolver, context);
  }

  @Bean
  public Session createSession() {
    return new Session();
  }

  @Bean
  public WorkspaceService workspaceService(Kernel kernel) {
    val cache =
        Suppliers.memoize(
            () -> {
              val file = kernel.getFileSystem().getPath("workspaces");
              try {
                if (!Files.exists(file)) {
                  log.info("Initializing workspace service at {}", file);
                  Files.createDirectory(file);
                }
              } catch (IOException ex) {
                log.warn(
                    "Error creating workspace directory {}.  Reason: {}", file, ex.getMessage());
              }
              return file.toFile();
            });
    return new DirectoryBackedWorkspaceService(cache, ZephyrApplication.getCondensation());
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  public Path aireConfigurationFile() throws AccessDeniedException {
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
    return file;
  }

  /**
   * this implementation is referenced by security.views.UserInfoPage, so you must update it there
   * when you're changing it. Sort of dictated by the configuration API
   *
   * @param file the path to the configuration file
   * @return the configuration
   * @throws ConfigurationException if something happens
   */
  @Bean
  public org.apache.commons.configuration2.Configuration zephyrConfigurationSource(
      @Named("aireConfigurationFile") Path file) throws ConfigurationException {
    val parameters = new Parameters();
    val builder =
        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
            .configure(
                parameters.fileBased().setFile(file.toFile()).setThrowExceptionOnMissing(false));
    builder.setAutoSave(true);
    return builder.getConfiguration();
  }

  @Bean
  @ConditionalOnProperty("ui-service.init.listener")
  public ConfigureUIServiceInitListener configureUIServiceInitListener(
      CompositeRealmManager manager,
      org.apache.commons.configuration2.Configuration configuration) {
    return new ConfigureUIServiceInitListener(manager, configuration);
  }

  @Bean
  public File realmDirectory() throws IOException {
    val path = KernelOptions.getKernelRootDirectory().toPath().resolve("security/auth/realm");
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
    return path.toFile();
  }

  @Bean(name = "crypt.keeper.secret.service")
  public SecretService cryptkeeperSecretService(@Named("realmDirectory") File realmDirectory) {
    return new DefaultSecretService(realmDirectory, ZephyrApplication.getCondensation());
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
  public AireRealmAggregator userDetailsService(CompositeRealmManager realmManager) {
    return new AireRealmAggregator(realmManager);
  }

  @Override
  public UserDetailsService userDetailsService() {
    return getApplicationContext().getBean(AireRealmAggregator.class);
  }

  @Bean
  public CompositeRealmManager compositeRealmManager(
      Kernel kernel,
      @Named("realmDirectory") File realm,
      org.apache.commons.configuration2.Configuration configuration) {
    return new CompositeRealmManager(kernel, realm.toPath(), configuration);
  }

  @Bean
  @ZephyrService(type = WorkflowService.class)
  public WorkflowService workflowService() {
    return new ModuleDrivenWorkflowService();
  }

  private void registerServices(
      Module module, ConfigurableApplicationContext context, Kernel kernel) {
    val ui = context.getBean(UserInterface.class);
    val serviceRegistry = kernel.getServiceRegistry();

    if (context instanceof BeanDefinitionRegistry registry) {
      registerAnnotatedServices(module, context, registry, serviceRegistry);
    }

    //    kernel.addEventListener(
    //        (type, event) -> {
    //          ui.reload();
    //        },
    //        ModuleEvents.STOPPED,
    //        ModuleEvents.STARTED,
    //        ModuleEvents.INSTALLED,
    //        ModuleEvents.REMOVED);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void registerAnnotatedServices(
      Module module,
      ConfigurableApplicationContext context,
      BeanDefinitionRegistry registry,
      ServiceRegistry serviceRegistry) {

    log.info("Registering Zephyr Services");
    val beanNames = context.getBeanNamesForAnnotation(ZephyrService.class);
    val serviceAnnotationName = ZephyrService.class.getName();

    for (val name : beanNames) {
      val definition = registry.getBeanDefinition(name);
      if (definition instanceof AnnotatedBeanDefinition def) {

        val annotationAttributes =
            def.getFactoryMethodMetadata().getAllAnnotationAttributes(serviceAnnotationName);
        if (annotationAttributes != null) {
          val type = (Class<?>) annotationAttributes.getFirst("type");
          if (type != null) {
            log.info("Registering service [bean name: {}, type: {}]...", name, type);
            serviceRegistry.register(
                module, new FactoryServiceDefinition(type, () -> context.getBean(name)));
          }
        }
      }
    }
  }

  @Override
  public void destroy() throws Exception {
    for (val registration : registrations) {
      try {
        registration.close();
      } catch (Exception ex) {
        log.warn("Error attempting to close registration: {}", ex.getMessage());
      }
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

  @Bean
  public AuthenticationProvider defaultLocalRealmAuthenticationProvider() {
    return new CryptkeeperAuthenticationProvider(
        getApplicationContext().getBean(AireRealmAggregator.class));
  }

  @Bean
  public LogoutHandler logoutHandler(SecretService service, ApplicationContext context) {
    return new VaultDestroyingLogoutHandler(service, context);
  }

  @Override
  public void configure(AuthenticationManagerBuilder builder) {
    val ctx = getApplicationContext();
    val providerNames = ctx.getBeanNamesForType(AuthenticationProvider.class);
    for (val providerName : providerNames) {
      builder.authenticationProvider(ctx.getBean(providerName, AuthenticationProvider.class));
    }
  }
}
