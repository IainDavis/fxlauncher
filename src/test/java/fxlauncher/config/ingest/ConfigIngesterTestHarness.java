package fxlauncher.config.ingest;

import static fxlauncher.testutils.CollectionTools.asSet;
import static fxlauncher.testutils.CollectionTools.assertMapEquals;
import static fxlauncher.testutils.LoggingTools.initTestLog;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import org.mockito.Mock;
import org.mockito.Spy;

import fxlauncher.config.LauncherOption;
import fxlauncher.downstream.DownstreamParameters;

public abstract class ConfigIngesterTestHarness {

    protected static final Logger log = initTestLog(getLogger(ArgsIngesterTestHarness.class.getName()));

    @Mock
    protected BiConsumer<LauncherOption, String> ingestOp;

    @Spy
    private DownstreamParameters observableOverflowParameters;

    // Collection of LauncherOption, String pairs expected to be passed to
    // LauncherConfig::setOption
    private final List<OptionPair> expectedConfig = new ArrayList<>();

    // Collections expected to match the captured downstream parameters
    private final Map<String, String> expectedNamed = new HashMap<>();
    private final List<String> expectedUnnamed = new ArrayList<>();
    private final List<String> expectedRaw = new ArrayList<>();

    protected void expectIngestOpCalledWith(LauncherOption opt, String value) {
	expectedConfig.add(new OptionPair(opt, value));
    }

    protected void expectDownstreamNamed(String key, String value) {
	expectedNamed.put(key, value);
	expectedRaw.add(String.format("--%s=%s", key, value));
    }

    protected void expectDownstreamUnnamed(String arg) {
	expectedUnnamed.add(arg);
	expectedRaw.add(arg);
    }

    protected void assertExpectedDownstreamParams() {
	assertAll(() -> assertMapEquals(expectedNamed, observableOverflowParameters.getNamed()),
		() -> assertEquals(expectedUnnamed, observableOverflowParameters.getUnnamed()),
		// wrapping in Set because passing through the Properties type loses any
		// ordering that exists in the original properties file.
		() -> assertEquals(asSet(expectedRaw), asSet(observableOverflowParameters.getRaw())),
		() -> assertEquals(asSet(expectedRaw), asSet(observableOverflowParameters.getArgs())));
    }

    protected void assertNoConfigCaptured() {
	assertAll(() -> assertEquals(emptyMap(), observableOverflowParameters.getNamed()),
		() -> assertEquals(emptyList(), observableOverflowParameters.getRaw()),
		() -> assertEquals(emptyList(), observableOverflowParameters.getRaw()),
		() -> assertArrayEquals(new String[0], observableOverflowParameters.getArgs()));
    }

    protected void assertEmptyDownstreamParams() {
	assertAll(() -> assertMapEquals(emptyMap(), observableOverflowParameters.getNamed()),
		() -> assertEquals(emptyList(), observableOverflowParameters.getUnnamed()),
		// wrapping in Set because passing through the Properties type loses any
		// ordering that exists in the original properties file.
		() -> assertEquals(emptyList(), observableOverflowParameters.getRaw()),
		() -> assertArrayEquals(new String[0], observableOverflowParameters.getArgs()));

    };

    protected void verifyExpectedIngestOpCalls() {
	for (OptionPair pair : expectedConfig) {
	    verify(ingestOp, times(1)).accept(pair.getOption(), pair.getValue());
	}
    }

    protected void verifyNoIngestOpCalls() {
	verify(ingestOp, never()).accept(any(LauncherOption.class), anyString());
    }

    protected static class OptionPair {
	private LauncherOption option;
	private String value;

	private OptionPair(LauncherOption option, String value) {
	    this.option = option;
	    this.value = value;
	}

	protected LauncherOption getOption() {
	    return option;
	}

	protected String getValue() {
	    return value;
	}
    }
}
