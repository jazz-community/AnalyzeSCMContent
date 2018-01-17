package com.rus.jazz.tool.analyzescmcontent.output;

/**
 * Class for the console output. The output line on the command line can be
 * updated.
 */
public final class ConsoleOutput { // NOPMD

	/**
	 * State of an analyze step.
	 */
	public enum State {
		/**
		 * Analyze step still executing
		 */
		EXECUTING,
		/**
		 * Analyze step successfully finishes
		 */
		SUCCESS,
		/**
		 * An error occur executing this step
		 */
		ERROR
	}

	private static boolean firstLine = true;

	private static final int MESSAGE_LENTH = 80;
	private static final int STATE_LENTH = 15;
	private static final int PROGRESS_LENGTH = 20;
	private static final int PROGRESS_BAR_SIZE = 12;
	private static final int PERCENTAGE_LENGTH = 20;

	/**
	 * Private constructor.
	 */
	private ConsoleOutput() {
		super();
	}

	/**
	 * Add a new message in a new line on the console.
	 * 
	 * @param message
	 *            message
	 */
	public static void printMessage(final String message) {
		printMessage(message, null, null, null, true);
	}

	/**
	 * Add a new message in a new line on the console.
	 * 
	 * @param message
	 * @param state
	 */
	public static void printMessage(final String message, final State state) {
		printMessage(message, null, state, null, true);
	}

	/**
	 * Update the message on the console.
	 * 
	 * @param message
	 */
	public static void updateMessage(final String message) {
		printMessage(message, null, null, null, false);
	}

	/**
	 * Update the messag eon the console.
	 * 
	 * @param message
	 * @param state
	 */
	public static void updateMessage(final String message, final State state) {
		printMessage(message, null, state, null, false);
	}

	/**
	 * Update the message on the console.
	 * 
	 * @param message
	 * @param state
	 * @param finish
	 * @param total
	 */
	public static void updateMessageWithPercentage(final String message, final State state, final int finish,
			final int total) {
		printMessage(message, null, state, buildPercentageMessage(finish, total), false);
	}

	/**
	 * Update the message on the console.
	 * 
	 * @param message
	 * @param state
	 * @param finish
	 * @param total
	 */
	public static void updateMessageWithProgressBar(final String message, final State state, final int finish,
			final int total) {
		printMessage(message, buildProgressBar(finish, total), state, null, false);
	}

	/**
	 * Update the message on the console.
	 * 
	 * @param message
	 * @param state
	 * @param finishProgress
	 * @param totalProgress
	 * @param finishPercentage
	 * @param totalPercentage
	 */
	public static void printMessageWithProgressBarAndPercentage(final String message, final State state,
			final int finishProgress, final int totalProgress, final int finishPercentage, final int totalPercentage) {
		printMessage(message, buildProgressBar(finishProgress, totalProgress), state,
				buildPercentageMessage(finishPercentage, totalPercentage), true);
	}

	/**
	 * Update the message on the console.
	 * 
	 * @param message
	 * @param state
	 * @param finishProgress
	 * @param totalProgress
	 * @param finishPercentage
	 * @param totalPercentage
	 */
	public static void updateMessageWithProgressBarAndPercentage(final String message, final State state,
			final int finishProgress, final int totalProgress, final int finishPercentage, final int totalPercentage) {
		printMessage(message, buildProgressBar(finishProgress, totalProgress), state,
				buildPercentageMessage(finishPercentage, totalPercentage), false);
	}

	private static void printMessage(final String message, final String process, final State state,
			final String percentage, final boolean newLine) {
		print(buildMessage(message, newLine), MESSAGE_LENTH);
		print((process == null) ? "" : process, PROGRESS_LENGTH);
		print((state == null) ? "" : '[' + state.name() + ']', STATE_LENTH);
		print((percentage == null) ? "" : percentage, PERCENTAGE_LENGTH);
	}

	private static String buildMessage(final String message, final boolean newLine) {
		String result = message;

		// if null then print an empty string
		if (result == null) {
			result = "";
		}

		// Clean the message
		result = result.replace("\n", "");
		result = result.replace("\r", "");

		// new line or at beginning of same line
		if (newLine) {
			if (firstLine) {
				firstLine = false;
			} else {
				result = '\n' + result; // NOPMD
			}
		} else {
			result = '\r' + result; // NOPMD
		}

		return result;
	}

	private static void print(final String message, final int length) {
		final StringBuffer buffer = new StringBuffer();
		buffer.insert(0, message);
		buffer.setLength(length);

		System.out.print(buffer.toString()); // NOPMD
	}

	private static String buildProgressBar(final int finsihed, final int total) {
		final StringBuilder builder = new StringBuilder();
		builder.append('[');

		final int border = (finsihed * PROGRESS_BAR_SIZE) / total;

		for (int i = 0; i < PROGRESS_BAR_SIZE; i++) {
			builder.append(i < border ? '*' : ' ');
		}

		builder.append(']');

		return builder.toString();
	}

	private static String buildPercentageMessage(final int finished, final int total) {
		return "(" + finished + "\\" + total + ")";
	}

	/**
	 * print the stack trace to the console
	 * 
	 * @param exception
	 */
	public static void printException(Exception exception) {
		System.out.print("\n");
		exception.printStackTrace();
	}

}
