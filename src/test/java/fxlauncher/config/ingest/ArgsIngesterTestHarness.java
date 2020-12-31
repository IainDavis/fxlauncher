package fxlauncher.config.ingest;

import static java.util.stream.Collectors.joining;
import static org.mockito.Mockito.lenient;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.mockito.InjectMocks;
import org.mockito.Mock;

public abstract class ArgsIngesterTestHarness extends ConfigIngesterTestHarness {

    @InjectMocks
    protected ArgsIngester ingester;

    @Mock
    protected Supplier<String[]> argsSupplier;

    protected static final String[] ALL_ARGS = { "--config-file=test-config-file",
	    "--overrides-url=https://test.override/url", "--manifest-url=https://test.manifest/url",
	    "--manifest-file=test-manifest-file", "--artifacts-repo-url=https://test.artifacts/repo/url",
	    "--cache-dir=test-cache-dir", "--log-file=test-log-file", "--ignore-ssl", "--offline",
	    "--stop-on-update-error", "--accept-downgrade", "--preload-native-libs=list,of,native,libraries",
	    "--headless", "--whats-new-url=https://whats.new/url", "--lingering-update-screen", };

    protected void setArgs(String... args) {
	log.info("Setting arguments for test: " + Stream.of(args).collect(joining("\", \"", "[\"", "\"]")));
	lenient().when(argsSupplier.get()).then(invocation -> args);
    }
}
