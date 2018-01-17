package com.rus.jazz.tool.analyzescmcontent.analyze;

/**
 * The Exception class for the analyze tool.
 * 
 */
public class AnalyzeException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public AnalyzeException() {
		super();
	}

	/**
	 * Constrcutor.
	 * 
	 * @param message
	 * @param cause
	 */
	public AnalyzeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public AnalyzeException(final String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public AnalyzeException(final Throwable cause) {
		super(cause);
	}

}
