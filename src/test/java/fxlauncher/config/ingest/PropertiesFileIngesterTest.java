package fxlauncher.config.ingest;

import static fxlauncher.config.LauncherOption.ACCEPT_DOWNGRADE;
import static fxlauncher.config.LauncherOption.ARTIFACTS_REPO_URL;
import static fxlauncher.config.LauncherOption.CACHE_DIR;
import static fxlauncher.config.LauncherOption.CONFIG_FILE;
import static fxlauncher.config.LauncherOption.HEADLESS;
import static fxlauncher.config.LauncherOption.LINGERING_UPDATE_SCREEN;
import static fxlauncher.config.LauncherOption.LOG_FILE;
import static fxlauncher.config.LauncherOption.MANIFEST_FILE;
import static fxlauncher.config.LauncherOption.MANIFEST_URL;
import static fxlauncher.config.LauncherOption.OFFLINE;
import static fxlauncher.config.LauncherOption.OVERRIDES_URL;
import static fxlauncher.config.LauncherOption.PRELOAD_NATIVE_LIBS;
import static fxlauncher.config.LauncherOption.STOP_ON_UPDATE_ERROR;
import static fxlauncher.config.LauncherOption.WHATS_NEW_URL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PropertiesFileIngesterTest extends PropertiesFileIngesterTestHarness {

    @DisplayName("Happy-path test of ingesting properties and passing overflow to downstream parameters object")
    @Test
    void ingestsTestProperties() {
	setResourceName("/test.launcher.properties");

	// predict expected outcomes...
	// values below defined in default test properties file
	expectIngestOpCalledWith(CONFIG_FILE, "test-config-file");
	expectIngestOpCalledWith(OVERRIDES_URL, "https://test.override/url");
	expectIngestOpCalledWith(MANIFEST_URL, "https://test.manifest/url");
	expectIngestOpCalledWith(MANIFEST_FILE, "test-manifest-file");
	expectIngestOpCalledWith(ARTIFACTS_REPO_URL, "https://test.artifacts/repo/url");
	expectIngestOpCalledWith(CACHE_DIR, "test-cache-dir");
	expectIngestOpCalledWith(LOG_FILE, "test-log-file");
	expectIngestOpCalledWith(OFFLINE, "");
	expectIngestOpCalledWith(STOP_ON_UPDATE_ERROR, "");
	expectIngestOpCalledWith(ACCEPT_DOWNGRADE, "");
	expectIngestOpCalledWith(PRELOAD_NATIVE_LIBS, "list,of,native,libraries");
	expectIngestOpCalledWith(HEADLESS, "");
	expectIngestOpCalledWith(WHATS_NEW_URL, "https://whats.new/url");
	expectIngestOpCalledWith(LINGERING_UPDATE_SCREEN, "");

	expectDownstreamNamed("downstream-prop", "downstream-prop-value");
	expectDownstreamUnnamed("--downstream-flag");

	// initiate test
	ingester.ingest();

	// verify expected flow occurred
	assertTrue(fileWasFound);
	verifyHappyPath();
	// confirm end-state is as expected
	assertExpectedDownstreamParams();
    }

    @DisplayName("Unhappy-path test for missing properties file")
    @Test
    void fileNotFoundTest() {
	setResourceName("/this-file-doesnt-exist");

	ingester.ingest();

	assertFalse(fileWasFound);
	verifyNotFoundPath();
	assertNoConfigCaptured();
    }

    @DisplayName("Unhappy path-test for properties file with malformed input")
    @Test
    void invalidFileTest() {
	setResourceName("/invalid.launcher.properties");

	ingester.ingest();

	assertTrue(fileWasFound);
	verifyNotFoundPath();
	assertNoConfigCaptured();
    }
}
