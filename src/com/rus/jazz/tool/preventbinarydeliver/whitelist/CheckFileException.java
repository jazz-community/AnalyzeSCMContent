package com.rus.jazz.tool.preventbinarydeliver.whitelist;

/**
 * Exception that occurs during file check.
 *
 */
public class CheckFileException extends Exception {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -5348546002846401358L;

	/**
	 * Constructor.
	 */
	public CheckFileException() {
		super();
	}

	/**
	 * Constructor. Will call the constructor of exception with the given
	 * arguments
	 * 
	 * @param message
	 * @param cause
	 */
	public CheckFileException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * * Constructor. Will call the constructor of exception with the given
	 * arguments
	 * 
	 * @param message
	 */
	public CheckFileException(final String message) {
		super(message);
	}

	/**
	 * * Constructor. Will call the constructor of exception with the given
	 * arguments
	 * 
	 * @param cause
	 */
	public CheckFileException(final Throwable cause) {
		super(cause);
	}

}
