package com.rus.jazz.tool.analyzescmcontent.analyze.modules.component;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;

/**
 * Count all Change Sets matching the specified time in the component.
 */
public class ComponentChangeSetCount extends AbstractComponentModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component.changeSetCount()";

	/**
	 * Default Constructor
	 */
	public ComponentChangeSetCount() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Number of Change Sets in Component";
	}

	@Override
	public void execute(final ComponentDAO componentDAO, final Result result) throws AnalyzeException {
		result.addResult(Integer.toString(componentDAO.getChangeSetsForComponent().size()), this);
	}

}
