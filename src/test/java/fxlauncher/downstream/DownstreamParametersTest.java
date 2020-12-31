package fxlauncher.downstream;

import static fxlauncher.testutils.CollectionTools.assertMapEquals;
import static fxlauncher.testutils.CollectionTools.generateList;
import static fxlauncher.testutils.CollectionTools.generateMap;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DownstreamParametersTest {

	private DownstreamParameters params = new DownstreamParameters();

	@DisplayName("Test that inputs are merged into appropriate categories")
	@Test
	public void testGenericMergeNoCollisions() {
		String[] keys = { "key" };
		String[] values = { "myvalue" };
		Map<String, String> expectedNamed = generateMap(keys, values);
		List<String> expectedUnnamed = generateList("--flag", "argument");
		String[] expectedArgs = { "--key=myvalue", "--flag", "argument" };
		List<String> expectedRaw = generateList(expectedArgs);

		assertMapEquals(emptyMap(), params.getNamed());

		params.merge("--key=myvalue", false);
		params.merge("--flag", false);
		params.merge("argument", false);

		assertMapEquals(expectedNamed, params.getNamed());
		assertEquals(expectedUnnamed, params.getUnnamed());
		assertArrayEquals(expectedArgs, params.getArgs());
		assertEquals(expectedRaw, params.getRaw());
	}

	@DisplayName("Generic merge merges named parameters correctly...")
	@ParameterizedTest(name = "when overwrite is {0}")
	@ValueSource(booleans = { true, false })
	public void testGenericMergeOfNamedParameters(boolean overwriting) {
		String[] keys = { "key" }, values1 = { "firstvalue" }, values2 = { "secondvalue" };

		Map<String, String> afterOverwrite = generateMap(keys, values1);
		Map<String, String> beforeOverwrite = generateMap(keys, values2);
		String[] finalArgs = { "--key=firstvalue", "--key=secondvalue" };
		List<String> finalRaw = Arrays.asList(finalArgs);

		assertMapEquals(Collections.emptyMap(), params.getNamed());

		params.merge("--key=firstvalue", overwriting);
		assertMapEquals(afterOverwrite, params.getNamed());

		params.merge("--key=secondvalue", overwriting);
		assertMapEquals(overwriting ? beforeOverwrite : afterOverwrite, params.getNamed());

		assertEquals(finalRaw, params.getRaw());
		assertArrayEquals(finalArgs, params.getArgs());
	}

	@DisplayName("mergeOverwriting() behaves the same as generic merge with overwrite=true")
	@Test
	void testMergeOverwriting() {
		DownstreamParameters params2 = new DownstreamParameters();

		String[] keys = { "key" }, values1 = { "firstvalue" }, values2 = { "secondvalue" };

		Map<String, String> afterOverwrite = generateMap(keys, values1);
		Map<String, String> beforeOverwrite = generateMap(keys, values2);
		String[] finalArgs = { "--key=firstvalue", "--key=secondvalue" };
		List<String> finalRaw = Arrays.asList(finalArgs);

		assertMapEquals(params.getNamed(), params2.getNamed());

		params.merge("--key=firstvalue", true);
		params2.mergeOverwriting("--key=firstvalue");
		assertMapEquals(params.getNamed(), params2.getNamed());

		params.merge("--key=secondvalue", true);
		params2.mergeOverwriting("--key=secondvalue");
		assertMapEquals(params.getNamed(), params2.getNamed());

		assertEquals(params.getUnnamed(), params2.getUnnamed());
		assertEquals(params.getRaw(), params2.getRaw());
		assertArrayEquals(params.getArgs(), params2.getArgs());
	}

	@DisplayName("mergeIfNotPresent() behaves the same as generic merge with overwrite=false")
	@Test
	void testMergeIfNotPresent() {
		DownstreamParameters params2 = new DownstreamParameters();

		String[] keys = { "key" }, values1 = { "firstvalue" }, values2 = { "secondvalue" };

		Map<String, String> afterOverwrite = generateMap(keys, values1);
		Map<String, String> beforeOverwrite = generateMap(keys, values2);
		String[] finalArgs = { "--key=firstvalue", "--key=secondvalue" };
		List<String> finalRaw = Arrays.asList(finalArgs);

		assertMapEquals(params.getNamed(), params2.getNamed());

		params.merge("--key=firstvalue", false);
		params2.mergeIfNotPresent("--key=firstvalue");
		assertMapEquals(params.getNamed(), params2.getNamed());

		params.merge("--key=secondvalue", false);
		params2.mergeIfNotPresent("--key=secondvalue");
		assertMapEquals(params.getNamed(), params2.getNamed());

		assertEquals(params.getUnnamed(), params2.getUnnamed());
		assertEquals(params.getRaw(), params2.getRaw());
		assertArrayEquals(params.getArgs(), params2.getArgs());
	}
}
