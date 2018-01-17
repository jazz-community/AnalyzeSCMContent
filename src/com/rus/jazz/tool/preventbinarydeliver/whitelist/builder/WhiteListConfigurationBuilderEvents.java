package com.rus.jazz.tool.preventbinarydeliver.whitelist.builder;

import java.util.LinkedList;
import java.util.List;

/**
 * Sores all events that was thrown by the WhiteListConfiguration Builders. Each
 * event has a Severity, a message and optional an Exception.
 */
public class WhiteListConfigurationBuilderEvents {

	/**
	 * List that contains all Events.
	 */
	private final transient List<Entry> eventList;

	/**
	 * Severity of the WhiteListConfigurationBuilderEvents.
	 */
	public enum Severity {
		/**
		 * The info log level
		 */
		INFO,
		/**
		 * The warning log level
		 */
		WARNING,
		/**
		 * the error log level
		 */
		ERROR,
		/**
		 * The fatal log level
		 */
		FATAL
	}

	/**
	 * Constructor.
	 */
	public WhiteListConfigurationBuilderEvents() {
		eventList = new LinkedList<Entry>();
	}

	/**
	 * Add a new message with severity to the events.
	 * 
	 * @param severity
	 * @param message
	 */
	public void add(final Severity severity, final String message) {
		eventList.add(new Entry(severity, message));
	}

	/**
	 * Add a new message wit severity and exception to the events.
	 * 
	 * @param severity
	 * 
	 * @param message
	 * @param exception
	 */
	public void add(final Severity severity, final String message,
			final Exception exception) {
		eventList.add(new Entry(severity, message, exception));
	}

	/**
	 * Return a list that contains all events.
	 * 
	 * @return all stored events
	 */
	public List<Entry> getEventList() {
		return eventList;
	}

	/**
	 * A single entry in the event list.
	 */
	public class Entry {
		/**
		 * The Severity of the entry.
		 */
		private final transient Severity severity;

		/**
		 * The event message for this entry.
		 */
		private final transient String message;

		/**
		 * Optional the exception that is related with this event entry.
		 */
		private transient Exception exception;

		/**
		 * Constructor for the Entry class.
		 * 
		 * @param severity
		 * @param message
		 */
		public Entry(final Severity severity, final String message) {
			this.severity = severity;
			this.message = message;
		}

		/**
		 * Constructor for the Entry class.
		 * 
		 * @param severity
		 * @param message
		 * @param exception
		 */
		public Entry(final Severity severity, final String message,
				final Exception exception) {
			this(severity, message);
			this.exception = exception;
		}

		/**
		 * Return the severity of the entry.
		 * 
		 * @return the severity of the entry
		 */
		public Severity getSeverity() {
			return severity;
		}

		/**
		 * Return the message of the entry.
		 * 
		 * @return the message of teh entry
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * Return the exception of the entry
		 * 
		 * @return the exception of the entry
		 */
		public Exception getException() {
			return exception;
		}
	}

}
