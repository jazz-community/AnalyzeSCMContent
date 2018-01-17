package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file.preventbinaryclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.IBuilderEventsLogger;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.WhiteListConfigurationBuilderEvents;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.WhiteListConfigurationBuilderEvents.Entry;

/**
 * The client implementation for the Build Event logger for the white list
 * configuration of the prevent binary extension.
 */
public class BuilderEventLoggerClientImpl implements IBuilderEventsLogger {

	/**
	 * The logger of the class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(BuilderEventLoggerClientImpl.class.getName());

	/**
	 * Constructor.
	 */
	public BuilderEventLoggerClientImpl() {
		super();
	}

	@Override
	public void writeEventsToJazzLog(final WhiteListConfigurationBuilderEvents events) {
		for (final Entry entry : events.getEventList()) {
			switch (entry.getSeverity()) {
			case ERROR:
				LOGGER.error(entry.getMessage(), entry.getSeverity());
				break;
			case FATAL:
				LOGGER.fatal(entry.getMessage(), entry.getSeverity());
				break;
			case INFO:
				LOGGER.info(entry.getMessage());
				break;
			case WARNING:
				LOGGER.warn(entry.getMessage());
				break;
			default:
				LOGGER.warn("Message with unknown severity " + entry.getSeverity()); // NOPMD
				LOGGER.warn(entry.getMessage());
				break;
			}
		}
	}

}
