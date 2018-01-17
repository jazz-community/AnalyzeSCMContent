package com.rus.jazz.tool.analyzescmcontent;

import org.apache.commons.cli.OptionBuilder;

import com.rus.jazz.tools.common.cli.AbstractALMToolCLI;

/**
 * The main class for calling the SCM analyze from the command line.
 */
public class SCMAnalyzeCLI extends AbstractALMToolCLI {

	private static final String TEMPL_FILE_NAME = "templateFileName";

	private static final String CSV_FILE_NAME = "csvFileName";

	private static final String MODIFIED_BEFORE = "modifiedBefore";

	private static final String MODIFIED_AFTER = "modifiedAfter";

	private static final String COMPONENTS = "components";

	private static final String PROJECT_AREAS = "projectAreas";

	private transient String[] projectAreas;
	private transient String[] components;
	private transient String modifiedAfter;
	private transient String modifiedBefore;
	private transient String csvFileName;
	private transient String templateFileName;

	@SuppressWarnings("static-access")
	protected SCMAnalyzeCLI(final String commandName) {
		super(commandName);
		options.addOption(OptionBuilder.hasArgs().withArgName("project Areas")
				.withDescription("Only component in the defined project areas will be analyzed.").create(PROJECT_AREAS));
		options.addOption(OptionBuilder.hasArgs().withArgName("components")
				.withDescription("Only the specified components will be analyzed.").create(COMPONENTS));
		options.addOption(OptionBuilder
				.hasArg()
				.withArgName("modified after")
				.withDescription(
						"Only change sets which are modified after the specified date will be analyzed. If not specified the date will be calculated based on the default configuration in the SCMAnalyze.properties.")
				.create(MODIFIED_AFTER));
		options.addOption(OptionBuilder.hasArg().withArgName("modified before")
				.withDescription("Only change sets which are modified before the specified date will be analyzed. If not specified the modified before attribute will be set to 'now'.")
				.create(MODIFIED_BEFORE));
		options.addOption(OptionBuilder
				.hasArg()
				.withArgName(CSV_FILE_NAME)
				.withDescription(
						"The file name of the output csv file. If not specified, the file name defined in the SCMAnalyze.properties will be used.")
				.create(CSV_FILE_NAME));
		options.addOption(OptionBuilder
				.hasArg()
				.withArgName(TEMPL_FILE_NAME)
				.withDescription(
						"The file name of the template file. In the template file you define the analyze modules and their order for the SCM analyze. If not specified, the file name defined in the SCMAnalyze.properties will be used.")
				.create(TEMPL_FILE_NAME));
	}

	@Override
	protected void parseAdditionalArguments() throws Exception { //NOPMD
		projectAreas = cmd.getOptionValues(PROJECT_AREAS);
		components = cmd.getOptionValues(COMPONENTS);
		modifiedAfter = cmd.getOptionValue(MODIFIED_AFTER);
		modifiedBefore = cmd.getOptionValue(MODIFIED_BEFORE);
		csvFileName = cmd.getOptionValue(CSV_FILE_NAME);
		templateFileName = cmd.getOptionValue(TEMPL_FILE_NAME);
	}

	@Override
	protected void execute() {

		try {
			final SCMAnalyzer analyzer = new SCMAnalyzer(user, pwd, repository);

			if (cmd.hasOption(PROJECT_AREAS)) {
				SCMAnalyzeParameter.getInstance().setRestrictToProjectAreas(projectAreas);
			}
			if (cmd.hasOption(COMPONENTS)) {
				SCMAnalyzeParameter.getInstance().setRestrictToComponents(components);
			}
			if (cmd.hasOption(MODIFIED_AFTER)) {
				SCMAnalyzeParameter.getInstance().setModifiedAfter(modifiedAfter);
			}
			if (cmd.hasOption(MODIFIED_BEFORE)) {
				SCMAnalyzeParameter.getInstance().setModifiedBefore(modifiedBefore);
			}
			if (cmd.hasOption(CSV_FILE_NAME)) {
				SCMAnalyzeParameter.getInstance().setCSVFileName(csvFileName);
			}
			if (cmd.hasOption(TEMPL_FILE_NAME)) {
				SCMAnalyzeParameter.getInstance().setTemplateFile(templateFileName);
			}
			analyzer.analyze();
		} catch (Exception e) { // NOPMD
			e.printStackTrace(); // NOPMD
		}
	}

	/**
	 * Main method called by the system.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String... args) {
		final SCMAnalyzeCLI cli = new SCMAnalyzeCLI("SCMAnalyze");
		cli.processCLI(args);
	}

}
