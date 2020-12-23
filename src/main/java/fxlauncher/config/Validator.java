package fxlauncher.config;

import static java.util.logging.Logger.getLogger;

import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@FunctionalInterface
/*
 * Functional Interface for validating values passed to LauncherConfig
 * plus default implementations for common cases.
 */
public interface Validator extends Predicate<String>{
	
	static final Logger log = getLogger(Validator.class.getName());

	// OWASP-supplied URL pattern-matching regexp
	static final String URL_REGEX =
            "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
            "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
            "([).!';/?:,][[:blank:]])?$";
 
    static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

	
	public static final Validator DEFAULT = string -> true;
	
	public static final Validator BOOL = string -> {
		log.finer(String.format("validating as boolean: '%s'", string));

		boolean isBool = (string != null);
		isBool &= string.equalsIgnoreCase("false") || string.equalsIgnoreCase("false");
		return isBool;
	};
	
	public static final Validator URL = string -> {
		log.finer(String.format("validating as URL: '%s'", string));

		return URL_PATTERN.matcher(string).matches();
	};
}
