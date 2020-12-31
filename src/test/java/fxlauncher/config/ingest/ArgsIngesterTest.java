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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArgsIngesterTest extends ArgsIngesterTestHarness {

    @DisplayName("Correctly handles a single command-line arguments")
    @Test
    public void simpleHappyPathTest() {
	setArgs("--cache-dir=some-directory");
	expectIngestOpCalledWith(CACHE_DIR, "some-directory");

	ingester.ingest();

	verifyExpectedIngestOpCalls();
	assertEmptyDownstreamParams();
    }

    @DisplayName("Sets all launcher options based on command-line arguments")
    @Test
    public void allOptsHappyPathTest() {
	setArgs(ALL_ARGS);

	expectIngestOpCalledWith(CONFIG_FILE, "test-config-file");
	expectIngestOpCalledWith(OVERRIDES_URL, "https://test.override/url");
	expectIngestOpCalledWith(MANIFEST_URL, "https://test.manifest/url");
	expectIngestOpCalledWith(MANIFEST_FILE, "test-manifest-file");
	expectIngestOpCalledWith(ARTIFACTS_REPO_URL, "https://test.artifacts/repo/url");
	expectIngestOpCalledWith(CACHE_DIR, "test-cache-dir");
	expectIngestOpCalledWith(LOG_FILE, "test-log-file");
	expectIngestOpCalledWith(OFFLINE, null);
	expectIngestOpCalledWith(STOP_ON_UPDATE_ERROR, null);
	expectIngestOpCalledWith(ACCEPT_DOWNGRADE, null);
	expectIngestOpCalledWith(PRELOAD_NATIVE_LIBS, "list,of,native,libraries");
	expectIngestOpCalledWith(HEADLESS, null);
	expectIngestOpCalledWith(WHATS_NEW_URL, "https://whats.new/url");
	expectIngestOpCalledWith(LINGERING_UPDATE_SCREEN, null);

	ingester.ingest();

	verifyExpectedIngestOpCalls();
	assertEmptyDownstreamParams();
    }

    @Test
    public void downstreamParamsTest() {
	setArgs("--cache-dir=some-directory", "--downstream-param=downstream-param-value", "--downstream-flag",
		"downstream-arg");

	expectIngestOpCalledWith(CACHE_DIR, "some-directory");

	expectDownstreamNamed("downstream-param", "downstream-param-value");
	expectDownstreamUnnamed("--downstream-flag");
	expectDownstreamUnnamed("downstream-arg");

	ingester.ingest();

	verifyExpectedIngestOpCalls();
	assertExpectedDownstreamParams();
    }
}
