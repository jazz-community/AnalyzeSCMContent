package com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset;

import java.util.Date;

import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeParameter;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;

/**
 * Analyze the ceration date of teh change set.
 */
public class ChangeSetCreationDate extends AbstractChangeSetModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet.creationdate";

	/**
	 * Constructor.
	 */
	public ChangeSetCreationDate() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Change Set Creation Date";
	}

	@Override
	public void execute(final ChangeSetDAO changeSetDAO, final Result result) throws AnalyzeException {
		final Date date = changeSetDAO.getChangeSet().getLastChangeDate();
		result.addResult(SCMAnalyzeParameter.getInstance().getDateFormat().format(date), this);
	}

}
