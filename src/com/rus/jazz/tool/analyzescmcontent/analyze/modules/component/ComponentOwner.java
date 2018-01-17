package com.rus.jazz.tool.analyzescmcontent.analyze.modules.component;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;

/**
 * Component module that extracts the owner of the component. If the owner of a
 * component is a team area the project area will be evaluated.
 */
public class ComponentOwner extends AbstractComponentModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component.owner";

	/**
	 * Constructor.
	 */
	public ComponentOwner() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Component Owner";
	}

	@Override
	public void execute(final ComponentDAO componentDAO, final Result result) throws AnalyzeException {
		result.addResult(componentDAO.getOwner().getComponentOwnerName(), this);
	}

}
