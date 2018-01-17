package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;

/**
 * IFileCheckerImplementation that verifies, that the file is smaller than than
 * a certain number. </br> Pattern: <$smaller<{number}>
 */
public class SmallFileChecker implements IFileChecker {

	/**
	 * The Pattern of this checker
	 */
	private final transient String pattern;

	/**
	 * The file size. Files of this size ore smaller are allowed by this
	 * IFileChekcer.
	 */
	private final transient long size;

	/**
	 * List of extensions that are never allowed by this IFileChecker.
	 */
	private final transient Set<String> blacklist;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 *            the pattern of the checker
	 * @param size
	 *            max size that is automatically allowed
	 * @param types
	 *            blacklost of types that are not allowed even if they are
	 *            smaller
	 * @throws ConfigurationException
	 */
	public SmallFileChecker(final String pattern, final long size,
			final String... types) throws ConfigurationException {
		// validate
		if (size <= 0) {
			throw new ConfigurationException(
					"Expected must must be higher then 0");
		}

		this.pattern = pattern;
		blacklist = new TreeSet<String>();
		for (final String type : types) {
			blacklist.add(type.trim());
		}

		this.size = size;
	}

	@Override
	public boolean checkFile(final AbstractFile file) {
		boolean result = false;
		boolean containedOnBlacklist = false;
		final String filename = file.getName();

		for (final String type : blacklist) {
			if (filename.endsWith(type)) {
				containedOnBlacklist = true;
				break;
			}
		}
		if (!containedOnBlacklist) {
			result = file.getFileSize() <= size;
		}

		return result;
	}

	/**
	 * Return an instance of the SmallFileChecker class based on the arguments.
	 * 
	 * @param pattern
	 *            pattern of the checker
	 * @param arguments
	 *            must contains a file size limit. Furthermore an arbitrary
	 *            number of extensions that are never allowed.
	 * @return an instance of the SmallFileChecker
	 * @throws ConfigurationException
	 */
	public static SmallFileChecker getInstance(final String pattern,
			final List<String> arguments) throws ConfigurationException {
		SmallFileChecker result = null;

		if (arguments.size() < 1) { // NOPMD
			throw new ConfigurationException(
					"Incorrect number of arguments in SmallFileChecker pattern. Only one argument allowed");
		} else {
			try {
				final long size = Long.parseLong(arguments.get(0));
				if (arguments.size() > 1) { // NOPMD
					// create and add blacklist
					arguments.remove(0);
					result = new SmallFileChecker(pattern, size,
							arguments.toArray(new String[arguments.size()]));
				} else {
					// create without blacklist
					result = new SmallFileChecker(pattern, size);
				}
			} catch (NumberFormatException e) {
				throw new ConfigurationException(
						"Invalid argument in SmallFileChecker pattern ", e);
			}
		}

		return result;
	}

	@Override
	public String getName() {
		return "SmallFileChecker";
	}

	@Override
	public String toString() {
		return "SmallFileChecker [pattern=" + pattern + ", size=" + size
				+ ", blacklist=" + blacklist + "]";
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
