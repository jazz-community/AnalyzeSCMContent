package com.rus.jazz.tool.preventbinarydeliver.whitelist;

/**
 * Exception class for configuration exceptions of the
 * "prevent binary precondition".
 */
public class ConfigurationException extends Exception {

	/**
	 * Generated UUID.
	 */
	private static final long serialVersionUID = -4137026498195568833L;

	/**
	 * Constructor. Calls the corresponding constructor of the superclass
	 * Exception.
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * Constructor. Calls the corresponding constructor of the superclass
	 * Exception.
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ConfigurationException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Constructor. Calls the corresponding constructor of the superclass
	 * Exception.
	 * 
	 * @param message
	 * @param cause
	 */
	public ConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor. Calls the corresponding constructor of the superclass
	 * Exception.
	 * 
	 * @param message
	 */
	public ConfigurationException(final String message) {
		super(message);
	}

	/**
	 * Constructor. Calls the corresponding constructor of the superclass
	 * Exception.
	 * 
	 * @param cause
	 */
	public ConfigurationException(final Throwable cause) {
		super(cause);
	}

}
