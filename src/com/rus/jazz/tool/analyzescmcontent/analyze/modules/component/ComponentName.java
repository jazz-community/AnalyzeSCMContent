package com.rus.jazz.tool.analyzescmcontent.analyze.modules.component;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;

/**
 * Fetch the name of the component.
 */
public class ComponentName extends AbstractComponentModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component.name";

	/**
	 * Constructor.
	 */
	public ComponentName() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public void execute(final ComponentDAO componentDAO, final Result result) throws AnalyzeException {
			result.addResult(componentDAO.getComponent().getName(), this);
	}

	@Override
	public String getHeader() {
		return "Component Name";
	}
}
