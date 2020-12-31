package fxlauncher.config;

import static fxlauncher.config.LauncherOption.ACCEPT_DOWNGRADE;
import static fxlauncher.config.LauncherOption.ARTIFACTS_REPO_URL;
import static fxlauncher.config.LauncherOption.CACHE_DIR;
import static fxlauncher.config.LauncherOption.CONFIG_FILE;
import static fxlauncher.config.LauncherOption.HEADLESS;
import static fxlauncher.config.LauncherOption.IGNORE_SSL;
import static fxlauncher.config.LauncherOption.LINGERING_UPDATE_SCREEN;
import static fxlauncher.config.LauncherOption.LOG_FILE;
import static fxlauncher.config.LauncherOption.MANIFEST_FILE;
import static fxlauncher.config.LauncherOption.MANIFEST_URL;
import static fxlauncher.config.LauncherOption.OFFLINE;
import static fxlauncher.config.LauncherOption.OVERRIDES_URL;
import static fxlauncher.config.LauncherOption.PRELOAD_NATIVE_LIBS;
import static fxlauncher.config.LauncherOption.STOP_ON_UPDATE_ERROR;
import static fxlauncher.config.LauncherOption.WHATS_NEW_URL;
import static fxlauncher.model.GenericPathLabel.ALLUSERS;
import static fxlauncher.model.GenericPathLabel.USERLIB;
import static fxlauncher.model.OS.LINUX;
import static fxlauncher.model.OS.MAC;
import static fxlauncher.model.OS.OTHER;
import static fxlauncher.model.OS.WIN;
import static fxlauncher.model.lifecycle.LifecyclePhase.LOAD_EMBEDDED_CONFIG;
import static fxlauncher.model.lifecycle.LifecyclePhase.STARTUP;
import static fxlauncher.testutils.CollectionTools.asArray;
import static fxlauncher.testutils.CollectionTools.asSet;
import static fxlauncher.testutils.CollectionTools.generateSet;
import static fxlauncher.testutils.LoggingTools.initTestLog;
import static fxlauncher.testutils.ReflectionTools.setCurrentOS;
import static java.util.Collections.emptySet;
import static java.util.logging.Logger.getLogger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import fxlauncher.model.GenericPathLabel;
import fxlauncher.model.OS;
import fxlauncher.model.lifecycle.LifecyclePhase;

public class LauncherConfigTestHarness {

  protected static final OS startingOS = OS.current;

  protected static final Logger log =
      initTestLog(getLogger(LauncherConfigTestHarness.class.getName()));

  @BeforeEach
  void setupSuite() {
    // setting the LifecyclePhase to something other than "STARTUP" ensures the
    // Set/Unset properties of each LauncherOption instance work correctly
    LifecyclePhase.setCurrent(LOAD_EMBEDDED_CONFIG);
    LauncherOption.forEach(opt -> opt.recordOptionSet(STARTUP));
    LauncherConfig.restoreDefaults();
  }

  @AfterEach
  void restoreStartingOS() throws Exception {
    setCurrentOS(startingOS);
  }

  @SuppressWarnings("serial")
  protected static final Map<LauncherOption, Set<String>> validValuesMap =
      new EnumMap<LauncherOption, Set<String>>(LauncherOption.class) {
        {
          put(CONFIG_FILE, asSet("non-default.launcher.properties"));
          put(OVERRIDES_URL, asSet("http://some.url.for/overrides"));
          put(MANIFEST_URL, asSet("http://some.url.for/a-manifest"));
          put(MANIFEST_FILE, asSet("non-default.app.xml"));
          put(ARTIFACTS_REPO_URL, asSet("https://some.url.for/app-artifacts"));
          put(CACHE_DIR, asSet("./non-default/home/directory"));
          put(LOG_FILE, asSet("./non-default/home/directory/mylog"));
          put(IGNORE_SSL, asSet("true", "false"));
          put(OFFLINE, asSet("true", "false"));
          put(STOP_ON_UPDATE_ERROR, asSet("true", "false"));
          put(ACCEPT_DOWNGRADE, asSet("true", "false"));
          put(PRELOAD_NATIVE_LIBS, asSet("some,libs,to,import"));
          put(HEADLESS, asSet("true", "false"));
          put(WHATS_NEW_URL, asSet("http://some.url.for/whats-new.file"));
          put(LINGERING_UPDATE_SCREEN, asSet("true", "false"));
        }
      };

  @SuppressWarnings("serial")
  protected static final Map<OS, Map<GenericPathLabel, String[]>> cacheDirResolutionMap =
      new EnumMap<OS, Map<GenericPathLabel, String[]>>(OS.class) {
        {
          put(
              WIN,
              new EnumMap<GenericPathLabel, String[]>(GenericPathLabel.class) {
                {
                  put(
                      ALLUSERS,
                      asArray(
                          "ALLUSERS/TEST/PATH",
                          WIN.getGenericPath(ALLUSERS).resolve("TEST").resolve("PATH").toString()));
                  put(
                      USERLIB,
                      asArray(
                          "USERLIB/TEST/PATH",
                          WIN.getGenericPath(USERLIB).resolve("TEST").resolve("PATH").toString()));
                }
              });
          put(
              MAC,
              new EnumMap<GenericPathLabel, String[]>(GenericPathLabel.class) {
                {
                  put(
                      ALLUSERS,
                      asArray(
                          "ALLUSERS/TEST/PATH",
                          MAC.getGenericPath(ALLUSERS).resolve("TEST").resolve("PATH").toString()));
                  put(
                      USERLIB,
                      asArray(
                          "USERLIB/TEST/PATH",
                          MAC.getGenericPath(USERLIB).resolve("TEST").resolve("PATH").toString()));
                  ;
                }
              });
          put(
              LINUX,
              new EnumMap<GenericPathLabel, String[]>(GenericPathLabel.class) {
                {
                  put(
                      ALLUSERS,
                      asArray(
                          "ALLUSERS/TEST/PATH",
                          LINUX
                              .getGenericPath(ALLUSERS)
                              .resolve("TEST")
                              .resolve("PATH")
                              .toString()));
                  put(
                      USERLIB,
                      asArray(
                          "USERLIB/TEST/PATH",
                          LINUX
                              .getGenericPath(USERLIB)
                              .resolve("TEST")
                              .resolve("PATH")
                              .toString()));
                }
              });
          put(
              OTHER,
              new EnumMap<GenericPathLabel, String[]>(GenericPathLabel.class) {
                {
                  put(
                      ALLUSERS,
                      asArray(
                          "ALLUSERS/TEST/PATH",
                          OTHER
                              .getGenericPath(ALLUSERS)
                              .resolve("TEST")
                              .resolve("PATH")
                              .toString()));
                  put(
                      USERLIB,
                      asArray(
                          "USERLIB/TEST/PATH",
                          OTHER
                              .getGenericPath(USERLIB)
                              .resolve("TEST")
                              .resolve("PATH")
                              .toString()));
                }
              });
        }
      };

  private static final Set<String> NOT_A_URL_SET =
      generateSet("", null, "not-a-url", "fakeScheme://some.domain/and/path?query=none&query=none");

  private static final Set<String> BLANK_SET = generateSet("", null);

  // treating null and the empty string as implicitly true
  private static final Set<String> NOT_A_BOOL_SET =
      generateSet("https://this.isnt/a/bool", "Scooby-Doo");

  @SuppressWarnings("serial")
  protected static final Map<LauncherOption, Set<String>> invalidValuesMap =
      new EnumMap<LauncherOption, Set<String>>(LauncherOption.class) {
        {
          put(CONFIG_FILE, BLANK_SET);
          put(OVERRIDES_URL, NOT_A_URL_SET);
          put(MANIFEST_URL, NOT_A_URL_SET);
          put(MANIFEST_FILE, BLANK_SET);
          put(ARTIFACTS_REPO_URL, NOT_A_URL_SET);
          put(CACHE_DIR, BLANK_SET);
          put(LOG_FILE, BLANK_SET);
          put(IGNORE_SSL, NOT_A_BOOL_SET);
          put(OFFLINE, NOT_A_BOOL_SET);
          put(STOP_ON_UPDATE_ERROR, NOT_A_BOOL_SET);
          put(ACCEPT_DOWNGRADE, NOT_A_BOOL_SET);
          put(PRELOAD_NATIVE_LIBS, BLANK_SET);
          put(HEADLESS, NOT_A_BOOL_SET);
          put(WHATS_NEW_URL, NOT_A_URL_SET);
          put(LINGERING_UPDATE_SCREEN, NOT_A_BOOL_SET);
        }
      };

  protected static List<Arguments> getTestSetOptionArgs() {
    List<Arguments> argsList = new ArrayList<>();
    LauncherOption.forEach(
        opt -> {
          validValuesMap
              .getOrDefault(opt, emptySet())
              .forEach(arg -> argsList.add(Arguments.of(opt, arg)));
        });
    return argsList;
  }

  // return cartesian product of Enum types OS and GenericPathLabel
  protected static final List<Arguments> cacheDirResolverTestArgs() {
    List<Arguments> argsList = new ArrayList<>();
    for (OS os : OS.values()) {
      for (GenericPathLabel path : GenericPathLabel.values()) {
        argsList.add(Arguments.of(os, path));
      }
    }
    return argsList;
  }

  protected static final List<Arguments> getInvalidInputTestArgs() {
    List<Arguments> argsList = new ArrayList<>();

    Stream.of(LauncherOption.values())
        .forEach(
            opt -> {
              invalidValuesMap
                  .getOrDefault(opt, emptySet())
                  .forEach(arg -> argsList.add(Arguments.of(opt, arg)));
            });
    return argsList;
  }

  protected static final List<Arguments> getBooleanEdgeCaseArgs() {
    List<Arguments> argsList = new ArrayList<>();
    Stream.of(LauncherOption.values())
        .filter(opt -> opt.getValidator() == Validator.BOOL)
        .forEach(
            opt -> {
              argsList.add(Arguments.of(opt, ""));
              argsList.add(Arguments.of(opt, " "));
              argsList.add(Arguments.of(opt, "null"));
            });
    return argsList;
  }
}
