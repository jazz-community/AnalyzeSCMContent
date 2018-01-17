package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteListConfiguration;

/**
 * This implementation of the IFileChecker checks, if the file has an file
 * ending that is accepted.
 */
public class SimpleFileExtension implements IFileChecker {

	/**
	 * The Pattern of this checker
	 */
	private final transient String pattern;

	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            pattern of the checker
	 */
	public SimpleFileExtension(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return "SimpleFileExtension [pattern=" + pattern + "]";
	}

	@Override
	public boolean checkFile(final AbstractFile file) {
		final String fileName = WhiteListConfiguration
				.normalize(file.getName());
		return fileName.endsWith(pattern);
	}

	@Override
	public String getName() {
		return "SimpleFileExtension";
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
