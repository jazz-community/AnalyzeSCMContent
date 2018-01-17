package com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;

/**
 * Counts the number of files in a change set.
 */
public class ChangeSetFileCount extends AbstractChangeSetModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet.fileCount()";

	/**
	 * Default Constructor.
	 */
	public ChangeSetFileCount() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Number of files in Change Set";
	}

	@Override
	public void execute(final ChangeSetDAO changeSetDAO, final Result result) throws AnalyzeException {
		result.addResult(Integer.toString(changeSetDAO.getFilesFromChangeSet().size()), this);
	}

}
