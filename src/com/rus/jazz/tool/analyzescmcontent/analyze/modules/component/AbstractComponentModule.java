package com.rus.jazz.tool.analyzescmcontent.analyze.modules.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;

/**
 * Abstract super class of all component analyze modules.
 */
public abstract class AbstractComponentModule implements IModule {

	/**
	 * The logger of the class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AbstractComponentModule.class.getName());

	/**
	 * Constructor.
	 */
	public AbstractComponentModule() {
		super();
	}

	@Override
	final public Level getLevel() {
		return Level.COMPONENT;
	}

	/**
	 * Execute the analyze of the change set
	 * 
	 * @param componentDAO
	 *            the analyzed component
	 * @param result
	 *            the result DAO
	 * @throws AnalyzeException
	 */
	abstract public void execute(final ComponentDAO componentDAO, final Result result) throws AnalyzeException;

	/**
	 * Publish an error message of the result and the log.
	 * 
	 * @param result
	 * @param exception
	 */
	public void publishError(final Result result, final Exception exception) {
		LOGGER.error("Error in analyze module " + getName(), exception);
		result.addResult(ERROR, this);
	}

}
