package fxlauncher.tools.io;

import static fxlauncher.tools.io.InputStreamTools.readInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ClasspathResourceFetcherTest {

	@Test
	void fetchClasspathResource() {
		String expected = "I've got a lovely bunch of coconuts. There they are a-standing in a row.";
		String resourceName = "/GenericClasspathResource.txt";

		Optional<InputStream> fetched = new ClasspathResourceFetcher(resourceName).fetch();
		assertTrue(fetched.isPresent());
		assertEquals(expected, readInputStream(fetched.get()));
	}

	@Test
	void classpathResourceNotFound() {
		String resourceName = "/IMadeThisOneUp.txt";

		Optional<InputStream> fetched = new ClasspathResourceFetcher(resourceName).fetch();
		assertFalse(fetched.isPresent());
	}
}
