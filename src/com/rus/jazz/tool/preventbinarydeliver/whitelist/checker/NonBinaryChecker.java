package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.CheckFileException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;

/**
 * Implementation of the IFileChecker Interface that verifies, if the file is a
 * non binary file. Therefore it will check the first "number of bytes" if
 * mainly typical signs for text or development will be used (characters,
 * numbers, brackets ...)
 * 
 * @author wasser_m
 *
 */
public class NonBinaryChecker implements IFileChecker {

	/**
	 * The Pattern of this checker
	 */
	private final transient String pattern;

	/**
	 * Defines how much number of bytes will be checked.
	 */
	private final transient int numberBytes;

	/**
	 * The blacklist. Files with this extensions are not allowed even if they ar
	 * non binary.
	 */
	private final transient Set<String> blacklist;

	/**
	 * Non Binary signs that are not alphanumeric
	 */
	private static final List<Byte> NON_BINRAY = Arrays.asList(new Byte[] { 0,
			' ', '!', '"', (byte) '~', '$', '%', '&', '/', '(', ')', '=', '?',
			'+', '*', '-', '_', '.', ',', ';', ':', '@', '"', '\'', '#', '{',
			'[', ']', '}', '\\', '<', '>', '\t', '\n', '\r' });

	/**
	 * Defines the percents of non binary signs that are necessary to allo this
	 * file
	 */
	private static final float LIMIT = 0.9f;

	/**
	 * Define, how much arguments this checker at least needs.
	 */
	private static final int MIN_NUM_ARGUMENTS = 1;

	/**
	 * Constructor of the NonBinaryChecker class.
	 * 
	 * @param pattern
	 *            pattern of the NonBinaryChecker
	 * @param numberBytes
	 *            defines how much bytes of the file will be checked. Must be
	 *            higher than 0.
	 * @param blacklist
	 *            define a list of file extensions that will be denied weather
	 *            there are binary files or not.
	 * @throws ConfigurationException
	 */
	public NonBinaryChecker(final String pattern, final int numberBytes,
			final String... blacklist) throws ConfigurationException {
		// Verify the arguments
		if (numberBytes <= 0) {
			throw new ConfigurationException(
					"Number Bytes must higher than 0 in the NonBinaryChecker");
		} else if (blacklist == null) {
			throw new ConfigurationException(
					"Blacklist must not be null in the NonBinaryChecker");
		}

		this.pattern = pattern;
		this.numberBytes = numberBytes;
		this.blacklist = new TreeSet<String>();
		for (final String extension : blacklist) {
			final String trimmed = extension.trim();
			if (trimmed.isEmpty()) {
				throw new ConfigurationException(
						"Empty entry in blacklist in the NonBinaryChecker");
			} else {
				this.blacklist.add(trimmed);
			}
		}

	}

	/**
	 * Checks weather the file contains mostly of binary characters or not. Only
	 * the first numberOfBytes characters will be checked.
	 */
	@Override
	public boolean checkFile(final AbstractFile file) throws CheckFileException {

		try {
			boolean result = true;
			final String filename = file.getName();
			// check first, if file is on blacklist
			for (final String extension : blacklist) {
				if (filename.endsWith(extension)) {
					result = false;
				}
			}
			/**
			 * Only if file extension was not on blacklist
			 */
			if (result) {
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				long start = System.currentTimeMillis();
				file.getContent(out);
				long finished = System.currentTimeMillis();
				System.out.println("Runtime for file.getContent(out) = "
						+ (finished - start) + " ms. for " + out.size()
						+ " bytes in output stream");

				result = isNonBinary(out, numberBytes);
			}
			return result;
		} catch (Exception e) { // NOPMD
			throw new CheckFileException(e.getMessage(), e);
		}
	}

	/**
	 * Verify if an ByteArrayOutputStream contains not a binary file by
	 * approximation.
	 * 
	 * @param outputStream
	 * @param numberBytes
	 * @return true, if the file is identified as non-binary, otherwise false
	 */
	public static boolean isNonBinary(final ByteArrayOutputStream outputStream,
			final int numberBytes) {
		boolean result;
		final byte[] content = outputStream.toByteArray();

		long nonBinaryChars = 0;

		/**
		 * Analyse numberOfBytes bytes or until the length of the content.
		 */
		final int max = content.length < numberBytes ? content.length
				: numberBytes;

		for (int i = 0; i < max; i++) {
			final byte sign = content[i];

			if (Character.isLetterOrDigit(sign)) {
				nonBinaryChars++;
			} else if (NON_BINRAY.contains(sign)) {
				nonBinaryChars++;
			}
		}

		final float factor = (float) nonBinaryChars / max;
		if (content.length > 0) {
			result = factor > LIMIT;
		} else {
			result = true; // if there is no content it can't be binary
		}
		return result;
	}

	/**
	 * Create an instance of the NonBinaryChecker class with the given
	 * arguments. When the arguments are not valid an ConfigurationException
	 * will be thrown.
	 * 
	 * @param pattern
	 * @param arguments
	 * @return an instance of the NonBinaryChecker class
	 * @throws ConfigurationException
	 */
	public static NonBinaryChecker getInstance(final String pattern,
			final List<String> arguments) throws ConfigurationException {
		NonBinaryChecker result = null;

		if (arguments.size() < MIN_NUM_ARGUMENTS) {
			throw new ConfigurationException(
					"Incorrect number of arguments in SmallFileChecker pattern. At least one argument allowed");
		} else {
			try {
				final int numberBytes = Integer.parseInt(arguments.get(0));
				if (arguments.size() > MIN_NUM_ARGUMENTS) {
					// create and add blacklist
					arguments.remove(0);
					result = new NonBinaryChecker(pattern, numberBytes,
							arguments.toArray(new String[arguments.size()]));
				} else {
					// create without blacklist
					result = new NonBinaryChecker(pattern, numberBytes);
				}
			} catch (final NumberFormatException e) {
				throw new ConfigurationException(
						"Invalid argument in SmallFileChecker pattern "
								+ arguments.get(0), e);
			}
		}

		return result;
	}

	@Override
	public String getName() { // NOMPD
		return "NonBinaryChecker";
	}

	@Override
	public String toString() {
		return "NonBinaryChecker [pattern=" + pattern + ", numberBytes="
				+ numberBytes + ", blacklist=" + blacklist + "]";
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
