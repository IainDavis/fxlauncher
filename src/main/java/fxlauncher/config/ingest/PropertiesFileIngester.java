package fxlauncher.config.ingest;

import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import fxlauncher.config.LauncherOption;
import fxlauncher.tools.io.ClasspathResourceFetcher;

/**
 * Implementation of {@link ConfigurationIngester} that reads values in from a Java properties file.
 *
 * @author idavis1
 */
public class PropertiesFileIngester extends ConfigurationIngester {

  private static final Logger log = getLogger(PropertiesFileIngester.class.getName());

  private final Properties props = new Properties();

  /*
   * using Supplier<String> to provide the configuration filename allows
   * resolution of the value when _ingest() is invoked, rather than at or before
   * execution of the constructor. This is useful when another
   * ConfigurationIngester changes the configuration state after this object is
   * constructed, but before its _ingest() method runs. We always want to run
   * _ingest() based on the current state at the time of execution. In the case
   * where this value is not expected to be changed, a String can be provided and
   * this class will construct the appropriate Supplier for it.
   */
  private Supplier<String> resourceNameSupplier = () -> LauncherOption.CONFIG_FILE.getDefault();

  // Using a functional interface here allows Mockito to inject a mock
  private Function<String, ClasspathResourceFetcher> fetcherFactory = ClasspathResourceFetcher::new;

  public PropertiesFileIngester(String resourceName) {
    super();
    this.resourceNameSupplier = () -> resourceName;
  }

  public PropertiesFileIngester(Supplier<String> resourceNameSupplier) {
    super();
    this.resourceNameSupplier = resourceNameSupplier;
  }

  // invoked by superclass -- ingest properties from the named resource
  @Override
  protected List<String> _ingest() {
    loadEmbeddedProps(resourceNameSupplier.get());
    Map<Object, Object> propsMap = new HashMap<>(props);
    propsMap.forEach((key, value) -> matchAndExtract(key, value));
    return props.entrySet().stream().map(PropertiesFileIngester::formatProperty).collect(toList());
  }

  private void loadEmbeddedProps(String resourceName) {
    log.info(NOTIFY_MSG.apply(resourceName));
    Optional<InputStream> fetched = fetcherFactory.apply(resourceName).fetch();

    if (fetched.isPresent()) {
      log.info(FOUND_MSG.apply(resourceName));
      try {
        props.load(fetched.get());
      } catch (IOException | IllegalArgumentException iae) {
        log.warning(INVALID_MSG.apply(resourceName));
        iae.printStackTrace();
      }
    } else {
      log.info(NOT_FOUND_MSG.apply(resourceName));
    }
  }

  private void matchAndExtract(Object keyObj, Object valueObj) {
    String key = keyObj.toString();
    String value = valueObj.toString();

    log.finer(INGEST_PROP_MSG.apply(key, value));
    boolean found = false;
    for (LauncherOption opt : LauncherOption.values()) {
      if (key.equals(opt.getLabel())) {
        log.finer(MATCHED_OPT_MSG.apply(key, opt.toString()));
        ingestOp.accept(opt, value);
        props.remove(key);
        return;
      }
    }

    log.finer(UNMATCHED_OPT_MSG.apply(key));
  }

  private static String formatProperty(Map.Entry<Object, Object> property) {
    return (property.getValue() == null || property.getValue().toString().trim().equals(""))
        ? UNNAMED_PROP_FMT.apply(property)
        : NAMED_PROP_FMT.apply(property);
  }

  // just giving convenient names to some simple but cumbersome String formatting
  // logic to improve readability above this comment
  private static final UnaryOperator<String> NOTIFY_MSG =
      resourceName -> String.format("Looking for embedded properties resource: %s", resourceName);
  private static final UnaryOperator<String> FOUND_MSG =
      resourceName -> String.format("Found embedded properties file %s", resourceName);
  private static final UnaryOperator<String> INVALID_MSG =
      resourceName ->
          String.format(
              "Failed to load embedded properties file '%s'. Check file format/syntax",
              resourceName);
  private static final UnaryOperator<String> NOT_FOUND_MSG =
      resourceName -> String.format("No embedded properties file '%s' found", resourceName);
  private static final BinaryOperator<String> INGEST_PROP_MSG =
      (key, value) ->
          String.format("Attempting to property with key=%s and value=%s...", key, value);
  private static final BinaryOperator<String> MATCHED_OPT_MSG =
      (key, opt) -> String.format("Matched property key '%s' to LauncherOption '%s'...", key, opt);
  private static final UnaryOperator<String> UNMATCHED_OPT_MSG =
      key ->
          String.format(
              "No LauncherOption found to match property key '%s'. Passing along to downstream app",
              key);

  private static final Function<Map.Entry<Object, Object>, String> NAMED_PROP_FMT =
      prop -> String.format("--%s=%s", prop.getKey().toString(), prop.getValue().toString());
  private static final Function<Map.Entry<Object, Object>, String> UNNAMED_PROP_FMT =
      prop -> String.format("--%s", prop.getKey().toString());
}
