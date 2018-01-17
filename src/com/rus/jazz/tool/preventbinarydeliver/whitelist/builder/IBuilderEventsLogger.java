package com.rus.jazz.tool.preventbinarydeliver.whitelist.builder;

/**
 * The interface for the event builder for building the prevent binary white
 * list.
 *
 */
public interface IBuilderEventsLogger {

	/**
	 * Writes the Events of the WhiteListConfigurationBuilderEvents to the jazz
	 * log.
	 * @param events 
	 */
	void writeEventsToJazzLog(final WhiteListConfigurationBuilderEvents events);

}