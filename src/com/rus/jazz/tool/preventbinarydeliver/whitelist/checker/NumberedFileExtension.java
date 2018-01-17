package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import java.util.List;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteListConfiguration;

/**
 * This implementation of the IFileChecker Interface proves if the file has an
 * defined file extension. This extension can contain non numeric part that has
 * been specified with <d> in the file ending.
 */
public class NumberedFileExtension implements IFileChecker {

	/**
	 * The Pattern of this checker
	 */
	private final transient String pattern;

	/**
	 * Pattern for the numeric part in the Checker arguments
	 */
	private static final String NUMERIC_PATTERN = "<d>";

	/**
	 * The part of the file Ending before the numeric pattern.
	 */
	private transient final String fileEndingBefore;

	/**
	 * The part of the file Ending after the numeric pattern.
	 */
	private transient final String fileEndingAfter;

	/**
	 * The pattern for the NumberedFileExtension
	 */
	private transient final String numberdFileExtension;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 *            pattern of the checker
	 * @param numberdFileExtension
	 *            numbered file extension
	 * @throws ConfigurationException
	 */
	public NumberedFileExtension(final String pattern,
			final String numberdFileExtension) throws ConfigurationException {
		this.pattern = pattern;
		this.numberdFileExtension = numberdFileExtension;

		// validate
		if (numberdFileExtension == null) {
			throw new ConfigurationException("file ending must not be null");
		} else if (numberdFileExtension.isEmpty()) {
			throw new ConfigurationException("file ending must not be empty");
		} else if (numberdFileExtension.trim().equals(NUMERIC_PATTERN)) {
			throw new ConfigurationException(
					"file ending must not only contain numeric pattern");
		} else if (!containsOnlyOneNumericPattern(numberdFileExtension)) {
			throw new ConfigurationException(
					"file ending must not only contain numeric pattern");
		}

		final String trimmed = numberdFileExtension.trim();
		final int index = trimmed.indexOf(NUMERIC_PATTERN);
		final String[] fragments = trimmed.split(NUMERIC_PATTERN);
		if (index == trimmed.length() - NUMERIC_PATTERN.length()) {
			// ... numeric pattern was at the end
			fileEndingBefore = fragments[0];
			fileEndingAfter = "";
		} else {
			fileEndingBefore = fragments[0];
			fileEndingAfter = fragments[1];
		}
	}

	private boolean containsOnlyOneNumericPattern(final String fileEnding) {
		boolean result = false;
		final int index = fileEnding.lastIndexOf(NUMERIC_PATTERN);
		if (index >= 0) {
			final String temp = fileEnding.substring(0, index);
			result = !temp.contains(NUMERIC_PATTERN); // NOPMD
		}

		return result;
	}

	private String removeNumberedEnding(final String filename) {
		int lastNunNumberedSign = filename.length() - 1;

		while (lastNunNumberedSign > 0) {
			final char sign = filename.charAt(lastNunNumberedSign);
			if (sign >= '0' && sign <= '9') {
				lastNunNumberedSign--;
			} else {
				break;
			}
		}

		return filename.substring(0, lastNunNumberedSign + 1);
	}

	@Override
	public boolean checkFile(final AbstractFile file) {
		boolean result = false;
		String fileName = WhiteListConfiguration.normalize(file.getName());

		if (fileName.endsWith(fileEndingAfter)) {
			fileName = fileName.substring(0, fileName.length()
					- fileEndingAfter.length());
			fileName = removeNumberedEnding(fileName);

			result = fileName.endsWith(fileEndingBefore);
		}

		return result;
	}

	/**
	 * Verify the arguments and returns an Instance of the NumberedFileExtension
	 * class. If the verification fails it will throw an ConfigurationException.
	 * 
	 * @param pattern
	 *            pattern of the checker
	 * @param arguments
	 *            arguments for the checker
	 * @return an instance of the NumberedFileExtension
	 * @throws ConfigurationException
	 */
	public static NumberedFileExtension getInstance(final String pattern,
			final List<String> arguments) throws ConfigurationException {
		NumberedFileExtension result = null;
		if (arguments.size() != 1) { // NOPMD
			throw new ConfigurationException(
					"Incorrect number of arguments in pattern of the NumberedFileExtension Checker. Only one argument allowed");
		} else {
			result = new NumberedFileExtension(pattern, arguments.get(0));
		}

		return result;
	}

	@Override
	public String toString() {
		return "NumberedFileExtension [pattern=" + pattern
				+ ", fileEndingBefore=" + fileEndingBefore
				+ ", fileEndingAfter=" + fileEndingAfter
				+ ", numberdFileExtension=" + numberdFileExtension + "]";
	}

	@Override
	public String getName() {
		return "NumberedFileExtensionChecker";
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
