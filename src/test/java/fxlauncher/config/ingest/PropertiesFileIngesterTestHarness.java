package fxlauncher.config.ingest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import fxlauncher.tools.io.ClasspathResourceFetcher;

public abstract class PropertiesFileIngesterTestHarness extends ConfigIngesterTestHarness {

    protected String resourceName = null;

    @InjectMocks
    protected PropertiesFileIngester ingester;

    @Mock
    protected Function<String, ClasspathResourceFetcher> fetcherFactory;

    @Mock
    protected Supplier<String> resourceNameSupplier;
    protected ClasspathResourceFetcher observableFetcher;

    protected boolean fileWasFound = false;

    protected void setResourceName(String resourceName) {
	log.info("setting resource name to: " + resourceName);
	this.resourceName = resourceName;

	// provides a mechanism for swapping in non-default resource names for
	// appropriate tests
	when(resourceNameSupplier.get()).thenAnswer(invocation -> resourceName);
	initFetcherFactory();
    }

    private void initFetcherFactory() {
	observableFetcher = spy(new ClasspathResourceFetcher(resourceNameSupplier.get()));
	// make mocked fetcherFactory() return a fetcher for the current
	// resourceName.
	// Capture the fetcher in a variable so calls against it can be verified
	when(fetcherFactory.apply(anyString())).thenAnswer(invocation -> {
	    log.info("returning ClasspathResourceFetcher spy from call to fetcherFactory.apply()");
	    return observableFetcher;
	});

	lenient().when(observableFetcher.fetch()).thenAnswer(invocation -> {
	    @SuppressWarnings("unchecked")
	    Optional<InputStream> result = (Optional<InputStream>) invocation.callRealMethod();
	    fileWasFound = result.isPresent();
	    return result;
	});
    }

    public void verifyHappyPath() {
	verify(fetcherFactory, times(1)).apply(resourceName);
	verify(observableFetcher, times(1)).fetch();
	verifyExpectedIngestOpCalls();
    }

    public void verifyNotFoundPath() {
	verify(fetcherFactory, times(1)).apply(resourceName);
	verify(observableFetcher, times(1)).fetch();
	verifyNoIngestOpCalls();
    }

}
