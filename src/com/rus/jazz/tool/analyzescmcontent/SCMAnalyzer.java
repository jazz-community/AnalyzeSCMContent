package com.rus.jazz.tool.analyzescmcontent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IComponentHandle;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.ComponentAnalyzer;
import com.rus.jazz.tool.analyzescmcontent.analyze.IAnalyzer;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleManager;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput.State;

/**
 * This class contains the workflow for the components analyzer. In the analyzed
 * there can be analyze modules registered. Each of this modules will be
 * executed and the result will be added to an csv file.
 */
public class SCMAnalyzer {

	private static final Logger LOGGER = LogManager.getLogger(SCMAnalyzer.class.getName());

	private static final String CONFIG_FILE = "SCMAnalyze.properties";

	/**
	 * Constructor
	 * 
	 * @param user
	 *            user
	 * @param pwd
	 *            password
	 * @param repository
	 *            repository url
	 * @throws AnalyzeException
	 */
	public SCMAnalyzer(final String user, final String pwd, final String repository) throws AnalyzeException {
		try {
			// create the repository connection
			SCMAnalyzeServerConnection.instantiate(user, pwd, repository);

			// Load the configuration of the SCM analyzer
			final Properties configuration = new Properties();
			loadConfiguration(configuration);

			// Instantiate the global instances
			SCMAnalyzeParameter.instantiate(configuration);
			ModuleManager.instantiate();
		} catch (Exception exception) {
			ConsoleOutput.printMessage("Aborting analyze due to an error");
			ConsoleOutput.printException(exception);
		}
	}

	private void loadConfiguration(final Properties configuration) throws AnalyzeException {
		LOGGER.info("Load configuration file");
		try {
			// Open the files
			ConsoleOutput.printMessage("Load the configuration file '" + CONFIG_FILE + "'", State.EXECUTING);
			final File file = new File(CONFIG_FILE);
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			// and load the configuration
			configuration.load(bufferedReader);
			ConsoleOutput.updateMessage("Load the configuration file '" + CONFIG_FILE + "'", State.SUCCESS);
		} catch (FileNotFoundException exception) {
			ConsoleOutput.updateMessage("Load the configuration file '" + CONFIG_FILE + "'", State.ERROR);
			throw new AnalyzeException(exception.getMessage(), exception);
		} catch (IOException exception) {
			ConsoleOutput.updateMessage("Load the configuration file '" + CONFIG_FILE + "'", State.ERROR);
			throw new AnalyzeException(exception.getMessage(), exception);
		}

	}

	/**
	 * Search for all matching components in the repository and analyze them.
	 */
	public void analyze() {
		try {
			// use the defined template
			adoptTemplate(SCMAnalyzeParameter.getInstance().getTemplateFileName());

			// create the output file
			final AnalyzeOutput csv = new AnalyzeOutput();

			// identify all matching components
			ConsoleOutput.printMessage("Identify components that has to been analyzed", State.EXECUTING);
			final List<ComponentAnalyzer> components = findComponentsInRepository(csv);
			ConsoleOutput.updateMessage("Identify components that has to been analyzed", State.SUCCESS);

			// create the result object
			final Result result = new Result();

			int numberAnalyzed = 0;
			for (final IAnalyzer component : components) {
				final String name = ((ComponentDAO) component.getDAO()).getComponent().getName();
				ConsoleOutput.printMessageWithProgressBarAndPercentage("Component \'" + name + "\' analyzed!",
						State.SUCCESS, 0, 100, numberAnalyzed, components.size());

				// analyze
				component.analyse(result);

				numberAnalyzed++;
				ConsoleOutput.updateMessageWithProgressBarAndPercentage("Component \'" + name + "\' analyzed!",
						State.SUCCESS, 100, 100, numberAnalyzed, components.size());
			}

			// close the output file
			csv.close();

			ConsoleOutput.printMessage("Finish SCM Analyze");
		} catch (Exception exception) {
			ConsoleOutput.printMessage("Aborting analyze due to an error");
			ConsoleOutput.printException(exception);
		}
	}

	/**
	 * Add a new analyze module to the analyzer. The analyze module will be
	 * executed for each row of the analyze.
	 * 
	 * @param module
	 *            analyze module
	 * @throws AnalyzeException
	 */
	public void addModule(final IModule module) throws AnalyzeException {
		try {
			ModuleManager.getInstance().add(module);
		} catch (Exception exception) {
			ConsoleOutput.printMessage("Aborting analyze due to an error");
			ConsoleOutput.printException(exception);
		}
	}

	/**
	 * Load and analyze the template file. Create the add the in the template
	 * file defined modules in the defined order to the SCMAnalyzer.
	 * 
	 * @param fileName
	 *            name of the template file
	 * @throws AnalyzeException
	 */
	private void adoptTemplate(final String fileName) throws AnalyzeException {
		LOGGER.info("Read template file " + fileName);
		final TemplateReader reader = new TemplateReader(fileName);
		for (final IModule module : reader) {
			addModule(module);
		}
	}

	private List<ComponentAnalyzer> findComponentsInRepository(final AnalyzeOutput output) throws AnalyzeException {
		// create the query
		LOGGER.info("Search for matching components in repository");
		// TODO: use respository singleton in the ComponentQuery
		final ComponentQuery componentQuery = new ComponentQuery(SCMAnalyzeServerConnection.getInstance());

		// add additional filter for the component query
		if (SCMAnalyzeParameter.getInstance().isRestrictedToProjectAreas()) {
			componentQuery.setProjectAreaNames(SCMAnalyzeParameter.getInstance().getRestrictedToProjectAreas());
		}
		if (SCMAnalyzeParameter.getInstance().isRestrictedToComponents()) {
			componentQuery.setComponentNames(SCMAnalyzeParameter.getInstance().getRestrictedToComponents());
		}

		// and execute it
		List<IComponentHandle> components;
		try {
			components = componentQuery.runQuery();
		} catch (TeamRepositoryException e) {
			throw new AnalyzeException(e.getMessage(), e);
		}

		final List<ComponentAnalyzer> result = new ArrayList<ComponentAnalyzer>();
		for (final IComponentHandle componentHandle : components) {
			result.add(new ComponentAnalyzer(componentHandle, output));
		}

		return result;
	}

}
