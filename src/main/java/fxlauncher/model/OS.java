package fxlauncher.model;

import static fxlauncher.model.GenericPathLabel.ALLUSERS;
import static fxlauncher.model.GenericPathLabel.USERLIB;
import static java.util.logging.Logger.getLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Represents an operating-system supported by FxLauncher Also provides resolution of OS-specific
 * generic paths USERLIB and ALLUSERS
 *
 * @author idavis1
 */
public enum OS {
  WIN,
  MAC,
  LINUX,
  OTHER,
  ;

  private static final Logger log = getLogger(OS.class.getName());
  public static final OS current;

  private final Path home = Paths.get(System.getProperty("user.home"));

  /* Using Suppliers to resolve these paths at get-time, rather than at constructor-time
   * means that any changes in the ALLUSERSPROFILE environment variable after this class
   * is loaded is reflected in the returned result. That's probably not super useful in
   * real-use scenarios, but it makes testing on a non-Windows platform much easier.
   */
  private final Map<GenericPathLabel, Supplier<Path>> genericPaths =
      new EnumMap<GenericPathLabel, Supplier<Path>>(GenericPathLabel.class);

  private OS() {

    switch (this.name().toLowerCase()) {
      case "mac":
        genericPaths.put(USERLIB, () -> home.resolve("Library").resolve("Application Support"));
        genericPaths.put(ALLUSERS, () -> Paths.get("/Library/Application Support"));
        break;
      case "win":
        genericPaths.put(USERLIB, () -> home.resolve("AppData").resolve("Local"));
        genericPaths.put(
            ALLUSERS,
            () -> Paths.get(Optional.ofNullable(System.getenv("ALLUSERSPROFILE")).orElse("")));
        break;
      default:
        genericPaths.put(USERLIB, () -> home);
        genericPaths.put(ALLUSERS, () -> Paths.get("/usr/local/share"));
        break;
    }
  }

  /**
   * Fetch the generic path for a supported sentinel value and the OS represented by this instance
   *
   * @param label the sentinel value, as an enum
   * @return the {@link Path} object for the provided sentinel value.
   */
  public Path getGenericPath(GenericPathLabel label) {
    return genericPaths.get(label).get();
  }

  // initializer runs at class-load time
  static {
    String os = System.getProperty("os.name", "generic").toLowerCase();

    if ((os.contains("mac")) || (os.contains("darwin"))) current = MAC;
    else if (os.contains("win")) current = WIN;
    else if (os.contains("nux")) current = LINUX;
    else current = OTHER;
    log.finer(String.format("Current operating system is: %s", MAC));
  }
}
