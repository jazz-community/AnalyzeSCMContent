package com.rus.jazz.tool.analyzescmcontent;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;

/**
 * The parameter for the analyze SCM execution.
 */
public final class SCMAnalyzeParameter {

	private static final String DATE_PATTERN = "dateFormatPattern";

	private static final String ANALYZE_PERIOD = "defaultAnalyzePeriod";

	private static final String TEMPLATE_FILE = "defaultTemplateFile";

	private static final String OUTPUT_FILE = "defaultOutputFile";

	/**
	 * Date format
	 */
	private transient final DateFormat format;

	/**
	 * List of project area to whom the execution is restricted
	 */
	private transient final List<String> projectAreas;

	/**
	 * List of components to whom the execution is restricted
	 */
	private transient final List<String> components;

	/**
	 * Restrict to change sets that are newer than...
	 */
	private transient Timestamp modifiedAfter;

	/**
	 * Restrict to change sets that are older than...
	 */
	private transient Timestamp modifiedBefore;

	/**
	 * The name of the csv output file name.
	 */
	private transient String csvFileName;

	/**
	 * The configuration for the SCM analyze.
	 */
	private transient final Properties configuration;

	/**
	 * The file name of the template file.
	 */
	private transient String templateFileName;

	private static SCMAnalyzeParameter instance;

	/**
	 * Constructor.
	 * 
	 * @param repository
	 *            the repository connection
	 * @param configuration
	 *            the configuration of the SCM analyze
	 */
	private SCMAnalyzeParameter(final Properties configuration) {
		this.configuration = configuration;
		projectAreas = new ArrayList<String>();
		components = new ArrayList<String>();
		format = new SimpleDateFormat(configuration.getProperty(DATE_PATTERN), Locale.getDefault());
	}

	/**
	 * Create the instance of the SCMAnalyzeParameter
	 * @param configuration
	 *            configuration parameter
	 */
	public static void instantiate(final Properties configuration) {
		instance = new SCMAnalyzeParameter(configuration);
	}

	/**
	 * Returns the instance of the SCM analyze parameters. The instance must be
	 * instantiate before.
	 * 
	 * @return SCMAnalyzeParameter instance
	 */
	public static SCMAnalyzeParameter getInstance() {
		return instance;
	}

	/**
	 * Restrict the analyze SCM execution to the defined project areas.
	 * 
	 * @param componentNames
	 *            names of the components
	 * @throws AnalyzeException
	 */
	public void setRestrictToComponents(final String... componentNames) throws AnalyzeException {
		final IComponentSearchCriteria searchCriteria = IComponentSearchCriteria.FACTORY.newInstance();

		for (final String componentName : componentNames) {
			// validate if component exists

			List<IComponentHandle> compHandleList;
			try {
				searchCriteria.setExactName(componentName);
				compHandleList = SCMAnalyzeServerConnection.getInstance().getWorkspaceManager().findComponents(searchCriteria, Integer.MAX_VALUE,
						null);
			} catch (TeamRepositoryException e) {
				throw new AnalyzeException("Unable to find component " + componentName + " from the repository.", e);
			}

			if (compHandleList.size() == 1) { // NOPMD
				components.add(componentName);
			} else {
				throw new AnalyzeException("Unable to find component " + componentName + " from the repository.");
			}
		}
	}

	/**
	 * Restrict the analyze SCM execution to the defined project areas.
	 * 
	 * @param projectAreaNames
	 *            name of the project areas
	 * @throws AnalyzeException
	 */
	public void setRestrictToProjectAreas(final String... projectAreaNames) throws AnalyzeException {
		for (final String projectAreaName : projectAreaNames) {
			try {
				// Validate if the project area exists in the Repository
				SCMAnalyzeServerConnection.getInstance().findProcessArea(projectAreaName, null, null);
				projectAreas.add(projectAreaName);
			} catch (TeamRepositoryException exception) {
				throw new AnalyzeException("Unable to load the project area " + projectAreaName
						+ " from the repository.", exception);
			}
		}
	}

	/**
	 * Return, if the execution is restricted to defined components or not.
	 * 
	 * @return true, if the analyze is restricted to defined components, false
	 *         otherwise
	 */
	public boolean isRestrictedToComponents() {
		return !components.isEmpty();
	}

	/**
	 * Return, if the execution is restricted to defined project areas or not.
	 * 
	 * @return true, if the analyze is restricted to defined project areas,
	 *         false otherwise
	 */
	public boolean isRestrictedToProjectAreas() {
		return !projectAreas.isEmpty();
	}

	/**
	 * Returns the list of project areas names to whom the execution is
	 * restricted.
	 * 
	 * @return list of project areas names
	 */
	public List<String> getRestrictedToProjectAreas() {
		return projectAreas;
	}

	/**
	 * Returns the list of component names to whom the execution is restricted.
	 * 
	 * @return list of component names
	 */
	public List<String> getRestrictedToComponents() {
		return components;
	}

	/**
	 * Parse and set the modified after date.
	 * 
	 * @param dateString
	 *            date string
	 * @throws AnalyzeException
	 */
	public void setModifiedAfter(final String dateString) throws AnalyzeException {
		try {
			final Date date = format.parse(dateString);
			modifiedAfter = new Timestamp(date.getTime());
		} catch (ParseException exception) {
			throw new AnalyzeException("Unable to parse date string " + dateString, exception);
		}
	}

	/**
	 * Get the date modified after restriction for the change set search
	 * 
	 * @return from timestamp
	 */
	public Timestamp getModifiedAfter() {
		Timestamp result = null;

		if (modifiedAfter != null) { // NOPMD
			result = new Timestamp(modifiedAfter.getTime());
		} else {
			// read the default period from the config file
			final int period = Integer.parseInt(configuration.getProperty(ANALYZE_PERIOD));

			// and calculate the modified after date
			final Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(getModifiedBefore().getTime());
			cal.add(Calendar.DAY_OF_YEAR, -period);
			result = new Timestamp(cal.getTimeInMillis());
		}

		return result;
	}

	/**
	 * Get the date modified before restriction for the change set search
	 * 
	 * @return from timestamp
	 */
	public Timestamp getModifiedBefore() {
		Timestamp result = null;

		if (modifiedBefore != null) { // NOPMD
			result = new Timestamp(modifiedBefore.getTime());
		} else {
			result = new Timestamp(System.currentTimeMillis());
		}

		return result;
	}

	/**
	 * Parse and set the modified before date.
	 * 
	 * @param dateString
	 *            date string
	 * @throws AnalyzeException
	 */
	public void setModifiedBefore(final String dateString) throws AnalyzeException {
		try {
			final Date date = format.parse(dateString);
			modifiedBefore = new Timestamp(date.getTime());
		} catch (ParseException exception) {
			throw new AnalyzeException("Unable to parse date string " + dateString, exception);
		}
	}

	/**
	 * Return the name of the output CSV file. If no file was specified the
	 * deault file name from the configuration will be used.
	 * 
	 * @return file name
	 */
	public String getCSVFileName() {
		String result = csvFileName;

		if (result == null) {
			result = configuration.getProperty(OUTPUT_FILE);
		}

		return result;
	}

	/**
	 * Set the name of the output CSV file
	 * 
	 * @param csvFileName
	 *            name of the csv output file
	 */
	public void setCSVFileName(final String csvFileName) {
		this.csvFileName = csvFileName;
	}

	/**
	 * Set the file name of the template file.
	 * 
	 * @param templateFileName
	 *            file name.
	 */
	public void setTemplateFile(final String templateFileName) {
		this.templateFileName = templateFileName;
	}

	/**
	 * Return the name of the template file. If the file name was not yet
	 * specified, the default template file name from teh configuration file
	 * will be returned.
	 * 
	 * @return file name
	 */
	public String getTemplateFileName() {
		String result = templateFileName;

		if (result == null) {
			result = configuration.getProperty(TEMPLATE_FILE);
		}

		return result;
	}

	/**
	 * Return the configured date format.
	 * 
	 * @return dateFormat
	 */
	public DateFormat getDateFormat() {
		return format;
	}

	/**
	 * Return the properties with the configuration.
	 * 
	 * @return properties
	 */
	public Properties getConfiguration() {
		return configuration;
	}

}
