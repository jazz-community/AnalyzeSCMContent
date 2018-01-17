package com.rus.jazz.tool.preventbinarydeliver.whitelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.WhiteListConfigurationBuilder;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.builder.WhiteListConfigurationBuilderEvents;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.IFileChecker;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.Reference;

/**
 * The WhiteListConfiguration class for the PreventBinaryDeliver. It contains a
 * map of WhiteList objects. The key of each white list should be a ProjectArea
 * name or a reference name.
 */
public class WhiteListConfiguration {

	/**
	 * The key of the global white list. Each WhiteListConfiguration must
	 * contain a WhiteList with the global key.
	 */
	public static final String GLOBAL_KEY = "global";

	/**
	 * Maps of WhiteListe. The keys are the project Area names or the reference
	 * names that can be used by Reference File Checker.
	 */
	private final transient Map<String, WhiteList> whiteListMap;

	/**
	 * Builder for the configuration file. Load and parse the configuration and
	 * return the WhiteListMap.
	 */
	private final transient WhiteListConfigurationBuilder builder;

	/**
	 * Constructor - Initialise the WhiteListConfigurationBuilder but do not
	 * load the white list. Loading is excluded so that the Constructor will not
	 * fail if there is an IO Error on reading.
	 * 
	 * @param propertyFileName
	 *            filename of the property file
	 * @throws ConfigurationException
	 */
	public WhiteListConfiguration(final String propertyFileName)
			throws ConfigurationException {
		whiteListMap = new HashMap<String, WhiteList>();
		builder = new WhiteListConfigurationBuilder(propertyFileName);
	}

	/**
	 * Verify, if the file satisfy the permitted pattern (white list).
	 * 
	 * @param file
	 * @param projectAreaName
	 * 
	 * @return true if the file is permitted, otherwise false
	 * @throws CheckFileException
	 */
	public boolean isPermitted(final AbstractFile file,
			final String projectAreaName) throws CheckFileException {
		final WhiteList whiteList = getWhitelistForKey(projectAreaName);

		return whiteList.isPermitted(file);
	}

	/**
	 * Returns the white list for a project area
	 * 
	 * @param key
	 *            name of the project area
	 * @return White List for the project area
	 */
	public WhiteList getWhitelistForKey(final String key) {
		final String normalizedKey = normalize(key);

		final WhiteList whiteList = whiteListMap.containsKey(normalizedKey) ? whiteListMap
				.get(normalizedKey) : whiteListMap.get(GLOBAL_KEY);
		return whiteList;
	}

	/**
	 * Returns the white list for a project area
	 * 
	 * @param key
	 *            name of the project area
	 * @return White List for the project area
	 */
	public List<IFileChecker> getRulesForKey(final String key) {
		return getWhitelistForKey(key).getFileCheckerList();
	}

	/**
	 * Returns all rules that are configured for this key. If it is a reference
	 * search also in the references key.
	 * 
	 * @param key
	 *            name of the project area
	 * @return White List for the project area
	 */
	public List<IFileChecker> getRulesForKeyIncludingRef(final String key) {
		final List<IFileChecker> result = new ArrayList<IFileChecker>();
		final List<String> alreadySearchedRefs = new ArrayList<String>();

		// He 2016-11-03: To avoid double global extensions, if key is empty (i.e. only global)
		if (key.equalsIgnoreCase(""))
			alreadySearchedRefs.add(GLOBAL_KEY);

		for (IFileChecker checker : getRulesForKey(key)) {
			// if checker is a reference add all rules
			if (checker instanceof Reference) {
				final String referenceName = ((Reference) checker)
						.getReferenceName();
				if (!alreadySearchedRefs.contains(referenceName)) {
					result.addAll(getRulesForKeyIncludingRef(referenceName,
							alreadySearchedRefs));
				}
			} else {
				result.add(checker);
			}
		}
		return result;
	}

	/**
	 * Returns all rules that are configured for this key. If it is a reference
	 * search also in the references key, if it is not yet in the List
	 * alreadySearchedRefs
	 * 
	 * @param key
	 *            key of the whitelit
	 * @param alreadySearchedRefs
	 *            list with already searched references
	 * @return White List for the project area
	 */
	private List<IFileChecker> getRulesForKeyIncludingRef(final String key,
			final List<String> alreadySearchedRefs) {
		final List<IFileChecker> result = new ArrayList<IFileChecker>();

		if (!alreadySearchedRefs.contains(key)) {
			alreadySearchedRefs.add(key);
			for (IFileChecker checker : getRulesForKey(key)) {
				// if checker is a reference add all rules
				if (checker instanceof Reference) {
					final String referenceName = ((Reference) checker)
							.getReferenceName();
					result.addAll(getRulesForKeyIncludingRef(referenceName,
							alreadySearchedRefs));
				} else {
					result.add(checker);
				}
			}
		}
		return result;
	}

	/**
	 * Remove leading and anding spaces and change the string to lower case.
	 * 
	 * @param string
	 * @return the normalised string
	 */
	public static String normalize(final String string) {
		return string == null ? string : string.trim().toLowerCase();
	}

	/**
	 * Checks, if the configuration file was modified since the last loading of
	 * the configuration.
	 * 
	 * @return true, if the file was modified since teh last load, otherwiese
	 *         false
	 */
	public boolean isModified() {
		return builder.isModified();
	}

	/**
	 * Read and parse the configuration from the configuration file.
	 * 
	 * @return All events that occur during the loading of the configuration
	 * 
	 * @throws ConfigurationException
	 */
	public final WhiteListConfigurationBuilderEvents loadConfiguration()
			throws ConfigurationException {
		return builder.loadConfiguration(whiteListMap);
	}

	/**
	 * Returns true, when the white list is not yet loaded.
	 * 
	 * @return true, if the file was not yet loaded or if there was an error
	 *         during loading, otherwise false
	 */
	public boolean notLoaded() {
		return whiteListMap.isEmpty();
	}
}
