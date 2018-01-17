package com.rus.jazz.tool.preventbinarydeliver.whitelist.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteList;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteListConfiguration;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.WhiteListConfigurationBuilderEvents.Severity;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.FileCheckerFactory;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.IFileChecker;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.Reference;

/**
 * Load the configuration from a property file and return a
 * WhiteListConfigurationBuilderResult.
 */
public class WhiteListConfigurationBuilder {

	/**
	 * Name of the property file that contains the configuration.
	 */
	private final transient String propertyFileName;

	/**
	 * Last modified time for the property file.
	 */
	private transient long lastModified;

	/**
	 * Constructor. Instantiate all necessary objects.
	 * 
	 * @param propertyFileName
	 */
	public WhiteListConfigurationBuilder(final String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

	/**
	 * Read and parse the configuration from the configuration file.
	 * 
	 * @param whiteListMap
	 * 
	 * @return return the events that occur during the build
	 * @throws ConfigurationException
	 */
	public WhiteListConfigurationBuilderEvents loadConfiguration(
			final Map<String, WhiteList> whiteListMap)
			throws ConfigurationException {
		final WhiteListConfigurationBuilderEvents events = new WhiteListConfigurationBuilderEvents();

		// Store the time stamp
		final File file = new File(propertyFileName);
		lastModified = file.lastModified();

		try {
			// load property file
			final Properties properties = new Properties();
			final FileInputStream fis = new FileInputStream(propertyFileName);
			properties.load(fis);
			fis.close();

			// Validation, must contain the key global
			if (!properties.containsKey(WhiteListConfiguration.GLOBAL_KEY)) {
				events.add(Severity.FATAL,
						"Configuration file must contain the key + WhiteListConfiguration.GLOBAL_KEY");
				throw new ConfigurationException(
						"Property file must contain the key "
								+ WhiteListConfiguration.GLOBAL_KEY);
			}

			// reset the white list
			whiteListMap.clear();

			// prepare the reference set
			final Set<String> references = getReferencesOfConfiguration(properties);

			// Create the white lists with the allowed file types
			for (final Object keyObject : properties.keySet()) {
				final String key = (String) keyObject;
				final String referenceKey = WhiteListConfiguration
						.normalize(key);
				final WhiteList whiteList = createWhiteListForKey(whiteListMap,
						properties, references, key, referenceKey, events);

				whiteListMap.put(referenceKey, whiteList);
			}

		} catch (final FileNotFoundException e) {
			throw new ConfigurationException(
					"Error by reading the config file", e);
		} catch (final IOException e) {
			throw new ConfigurationException(
					"Error by reading the config file", e);
		}
		return events;
	}

	/**
	 * Create a white list for a key in the configuration file.
	 * 
	 * @param whiteListMap
	 * @param properties
	 * @param references
	 * @param key
	 *            necessary only for accessing the property file
	 * @param referenceKey
	 * @param events
	 * @return
	 */
	private WhiteList createWhiteListForKey(
			final Map<String, WhiteList> whiteListMap,
			final Properties properties, final Set<String> references,
			final String key, final String referenceKey,
			final WhiteListConfigurationBuilderEvents events) {
		final WhiteList whiteList = new WhiteList();

		/*
		 * For each white list (except the global list itself) the first checker
		 * is a reference to the global list
		 */
		if (!referenceKey.equals(WhiteListConfiguration.GLOBAL_KEY)) {
			whiteList.addChecker(new Reference("<$global>", 
					WhiteListConfiguration.GLOBAL_KEY, whiteListMap));
			events.add(Severity.INFO, "Key "
					+ WhiteListConfiguration.GLOBAL_KEY
					+ " added to WhiteList " + referenceKey);
		}

		/*
		 * read and parse the line form the configuration. The IFileCheckers are
		 * comma separated in the configuration file (if the IFileChecker itself
		 * has arguments they are semicolon separated)
		 */
		final String line = properties.getProperty(key);
		for (final String pattern : line.split(",")) {

			// remove whitespace from the single value
			final String normalizedPattern = WhiteListConfiguration
					.normalize(pattern);

			// continue if it is an empty patter
			if (normalizedPattern.isEmpty()) {
				continue;
			}

			// get the IFileChecker
			IFileChecker checker;
			try {
				checker = FileCheckerFactory.getInstance(normalizedPattern,
						whiteListMap, references);
				whiteList.addChecker(checker);
				events.add(Severity.INFO, "FileCheacker " + checker.getName()
						+ " added to WhiteList " + referenceKey + "["
						+ normalizedPattern + "]");
			} catch (ConfigurationException e) {
				events.add(Severity.ERROR, "Fail to parse the File Checker "
						+ normalizedPattern + " in  WhiteList " + referenceKey,
						e);
			}

		}
		return whiteList;
	}

	/**
	 * Returns (and normalize) all references containing in the configuration
	 * file.
	 * 
	 * @param properties
	 * @return
	 */
	private Set<String> getReferencesOfConfiguration(final Properties properties) {
		final Set<String> references = new TreeSet<String>();
		for (final Object keyObject : properties.keySet()) {
			references
					.add(WhiteListConfiguration.normalize((String) keyObject));
		}
		return references;
	}

	/**
	 * Checks, if the configuration file was modified since the last loading of
	 * the configuration.
	 * 
	 * @return true, if the file was modified since the last load, otherwiese
	 *         false
	 */
	public boolean isModified() {
		final File file = new File(propertyFileName);
		return file.lastModified() > lastModified;
	}

}
