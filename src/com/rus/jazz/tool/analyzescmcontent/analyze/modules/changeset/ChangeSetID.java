package com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset;

import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;

/**
 * Fetch the ID of the change set.
 */
public class ChangeSetID extends AbstractChangeSetModule {
	
	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet.id";
	
	/**
	 * Constructor.
	 */
	public ChangeSetID() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public void execute(final ChangeSetDAO changeSetDao, final Result result) {
		result.addResult(changeSetDao.getHandle().getItemId().getUuidValue(), this);
	}

	@Override
	public String getHeader() {
		return "ChangeSet ID";
	}

}
