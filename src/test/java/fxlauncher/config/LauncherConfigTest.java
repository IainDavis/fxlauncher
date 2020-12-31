package fxlauncher.config;

import static fxlauncher.config.LauncherOption.CACHE_DIR;
import static fxlauncher.testutils.ReflectionTools.setCurrentOS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import fxlauncher.except.FXLauncherConfigException;
import fxlauncher.model.GenericPathLabel;
import fxlauncher.model.OS;

@DisplayName("LauncherConfigTest")
public class LauncherConfigTest extends LauncherConfigTestHarness {

  @DisplayName("Simple set-and-retrieve tests...")
  @ParameterizedTest(name = "LauncherConfig correctly retrieves {0} as \"{1}\"")
  @MethodSource("fxlauncher.config.LauncherConfigTestHarness#getTestSetOptionArgs")
  void testSetOption(LauncherOption opt, String value) {
    // before set
    assertFalse(opt.isSet());
    assertEquals(LauncherConfig.getOption(opt), opt.getDefault());

    LauncherConfig.setOption(opt, value);
    String actual = LauncherConfig.getOption(opt);

    // after set
    assertTrue(opt.isSet());
    assertEquals(value, actual);
  }

  @DisplayName("CACHE_DIR resolver resolves correctly for...")
  @ParameterizedTest(name = "OS {0} and Generic Path {1}")
  @MethodSource("fxlauncher.config.LauncherConfigTestHarness#cacheDirResolverTestArgs")
  void testCacheDirResolver(OS os, GenericPathLabel label) throws Exception {
    setCurrentOS(os);
    String unresolved = cacheDirResolutionMap.get(os).get(label)[0];
    String resolved = cacheDirResolutionMap.get(os).get(label)[1];

    LauncherConfig.setOption(CACHE_DIR, unresolved);
    assertEquals(resolved, LauncherConfig.getOption(CACHE_DIR));
  }

  @DisplayName("During validation...")
  @ParameterizedTest(name = "{0} rejects invalid input value \"{1}\"")
  @MethodSource("fxlauncher.config.LauncherConfigTestHarness#getInvalidInputTestArgs")
  void validationTests(LauncherOption opt, String input) {

    assertThrows(FXLauncherConfigException.class, () -> LauncherConfig.setOption(opt, input));
  }

  @DisplayName("Boolean option...")
  @ParameterizedTest(name = "{0} correctly interprets edge-case \"{1}\" as true")
  @MethodSource("fxlauncher.config.LauncherConfigTestHarness#getBooleanEdgeCaseArgs")
  void booleanEdgeCaseTest(LauncherOption opt, String value) {
    value = value.equals("null") ? null : value;
    LauncherConfig.setOption(opt, value);
    String actual = LauncherConfig.getOption(opt);

    // after set
    assertTrue(opt.isSet());
    assertEquals("true", actual);
  }
}
