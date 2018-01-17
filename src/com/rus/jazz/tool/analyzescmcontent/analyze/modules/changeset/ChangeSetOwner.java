package com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;

/**
 * Analyze the owner of a change set.
 */
public class ChangeSetOwner extends AbstractChangeSetModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeset.owner";

	/**
	 * Constructor.
	 */
	public ChangeSetOwner() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Change Set Owner";
	}

	@Override
	public void execute(final ChangeSetDAO changeSetDAO, final Result result) throws AnalyzeException {
		result.addResult(changeSetDAO.getAuthor().getName(), this);
	}

}
