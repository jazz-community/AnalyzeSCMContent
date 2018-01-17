package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteList;

/**
 * This Factory class returns instances of the IFileChecker interface. It will
 * analyse the pattern and identify the concrete implementation of the interface
 * and return an instance of the implementation.
 */
public final class FileCheckerFactory {

	/**
	 * Numbered pattern: <br>
	 * Must have one argument with the numbered pattern <br>
	 * <$numbered{Makefile<d>}>
	 */
	private static final String NUMBERED_PATTERN = "<$numbered";

	/**
	 * Smaller than pattern <br>
	 * Must have an integer argument Pattern and optional a list of extenions
	 * that are not allowed even if they are smaller: <br>
	 * <$smaller{number; ext1; ext2}>
	 */
	private static final String SMALLER_PATTERN = "<$smaller";

	/**
	 * Smaller than pattern <br>
	 * Must have an integer argument Pattern and optional a list of extensions
	 * that not allowed, even when they are nonBinary: <br>
	 * <$nonbinary{number; ext1; ext2}>
	 */
	private static final String NONBINARY_PATTERN = "<$nonbinary";

	/**
	 * Pattern of a reference <br>
	 * <reference>
	 */
	private static final String REFERENCE_PATTERN = "<";

	/**
	 * 
	 * @param pattern
	 *            pattern of the configuration
	 * @return IFileChecker instance
	 * @throws TeamRepositoryException
	 */

	/**
	 * From this class no object is needed. Therefore it has a private
	 * constructor.
	 */
	private FileCheckerFactory() {

	}

	/**
	 * Return an instance of a IFileChecker class that belongs to the pattern.
	 * If no pattern match the SimpleFileExtension Implementation will be
	 * returned.
	 * 
	 * @param pattern
	 *            pattern of the configuration
	 * @param whiteListMap
	 *            Necessary for references pattern
	 * @param keySet
	 *            Necessary for references pattern
	 * @return an implementation of the IFileChecker 
	 * @throws ConfigurationException
	 */
	public static IFileChecker getInstance(final String pattern,
			final Map<String, WhiteList> whiteListMap,
			@SuppressWarnings("rawtypes") final Set keySet)
			throws ConfigurationException {

		// validate the arguments
		if (pattern == null || pattern.isEmpty()) {
			throw new ConfigurationException("Invalid pattern " + pattern);
		} else if (whiteListMap == null) {
			throw new ConfigurationException("WhiteListMap must not be null");
		} else if (keySet == null) {
			throw new ConfigurationException("WhiteListMap must not be null");
		}

		IFileChecker result = null;

		if (pattern.startsWith("<$")) {
			result = getCheckerForPattern(pattern);
		} else if (pattern.startsWith(REFERENCE_PATTERN)) {
			result = getCheckerForReference(pattern, whiteListMap, keySet);
		} else {
			if (pattern.charAt(0)=='<' || pattern.endsWith(">")) {
				throw new ConfigurationException("Unknown pattern " + pattern);
			}
			result = new SimpleFileExtension(pattern);
		}

		return result;

	}

	@SuppressWarnings("rawtypes")
	private static IFileChecker getCheckerForReference(final String pattern,
			final Map<String, WhiteList> whiteListMap, final Set keySet)
			throws ConfigurationException {

		// validate
		if (!(pattern.charAt(0)=='<' && pattern.endsWith(">"))) { 
			throw new ConfigurationException("Invalid reference " + pattern);
		}

		IFileChecker result = null;
		String reference = pattern.replace("<", "");
		reference = reference.replace(">", "");

		if (keySet.contains(reference)) {
			result = new Reference(pattern, reference, whiteListMap);
		} else {
			throw new ConfigurationException("Pattern " + pattern
					+ " must be contained in the key Set (context sensitiv");
		}
		return result;
	}

	private static IFileChecker getCheckerForPattern(final String pattern)
			throws ConfigurationException {
		IFileChecker result = null;

		validatePattern(pattern);
		final List<String> arguments = getArguments(pattern);

		if (pattern.startsWith(NUMBERED_PATTERN)) {
			result = NumberedFileExtension.getInstance(pattern, arguments);
		} else if (pattern.startsWith(SMALLER_PATTERN)) {
			result = SmallFileChecker.getInstance(pattern,arguments);
		} else if (pattern.startsWith(NONBINARY_PATTERN)) {
			result = NonBinaryChecker.getInstance(pattern,arguments);
		} else {
			throw new ConfigurationException("Unknown pattern " + pattern);
		}
		return result;
	}

	private static void validatePattern(final String pattern)
			throws ConfigurationException {
		if (!(pattern.charAt(0)=='<' && pattern.endsWith(">"))) { 
			throw new ConfigurationException("Invalid pattern " + pattern);
		}
	}

	private static List<String> getArguments(final String pattern)
			throws ConfigurationException {

		// check for validity
		if (!(occurOnlyOnce(pattern, '{') && occurOnlyOnce(pattern, '}'))) {
			throw new ConfigurationException("Incorrect pattern " + pattern
					+ " in configuration");
		}

		final List<String> result = new ArrayList<String>();

		final int start = pattern.indexOf('{');
		final int end = pattern.indexOf('}');

		final String arguments = pattern.substring(start + 1, end);
		final String[] fragments = arguments.split(";");

		// Add all fragments that are not empty or contains only from
		// whitespaces
		for (final String string : fragments) {
			final String trimmed = string.trim();
			if (!trimmed.isEmpty()) {
				result.add(trimmed);
			}
		}

		return result;
	}

	private static boolean occurOnlyOnce(final String pattern, final char symbol) {
		return pattern.contains(Character.toString(symbol))
				&& pattern.indexOf(symbol) == pattern.lastIndexOf(symbol);
	}

}
