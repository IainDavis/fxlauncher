package fxlauncher.config.ingest;

import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import fxlauncher.config.LauncherConfig;
import fxlauncher.config.LauncherOption;

/**
 * Concrete implemention of {@link ConfigurationIngester} that deduces
 * {@link LauncherConfig} settings from command-line arguments (or any other
 * String array);
 *
 * @author idavis1
 *
 */
public class ArgsIngester extends ConfigurationIngester {

    private static final Logger log = getLogger(ArgsIngester.class.getName());

    private Supplier<String[]> argsSupplier = () -> new String[0];

    public ArgsIngester(String... args) {
	super();
	this.argsSupplier = () -> args;
    }

    public ArgsIngester(Supplier<String[]> argsSupplier) {
	super();
	this.argsSupplier = argsSupplier;
    }

    // -- invoked by superclass when ingest() is called.
    @Override
    protected List<String> _ingest() {
	log.info(BEGIN_MSG.apply(argsSupplier.get()));
	List<String> leftovers = Stream.of(argsSupplier.get()).filter(this::matchAndExtract).collect(toList());
	return leftovers;
    }

    private boolean matchAndExtract(String arg) {
	for (LauncherOption opt : LauncherOption.getValueSet()) {
	    Matcher matcher = opt.getMatcher(arg);
	    if (matcher.matches()) {
		log.finer(MATCHED_OPT_MSG.apply(arg, opt.toString()));
		String value = matcher.groupCount() == 0 ? null : matcher.group(1);
		ingestOp.accept(opt, value);
		return false;
	    } else {
		log.finer(UNMATCHED_OPT_MSG.apply(arg));
		continue;
	    }
	}
	return true;
    }

    private static final Function<String[], String> BEGIN_MSG = args -> String
	    .format("Ingesting command-line arguments: %s", Stream.of(args).collect(joining(",", "[", "]")));
    private static final BinaryOperator<String> MATCHED_OPT_MSG = (arg, opt) -> String
	    .format("Matched argument '%s' with LauncherOption '%s'", arg, opt);
    private static final UnaryOperator<String> UNMATCHED_OPT_MSG = (arg) -> String
	    .format("No matching LauncherOption found for argument: '%s'. Sending to downstream application", arg);
}
