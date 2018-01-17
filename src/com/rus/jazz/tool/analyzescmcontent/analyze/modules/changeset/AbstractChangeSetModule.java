package com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;

/**
 * Abstract super class of all Change Set analyze modules.
 */
public abstract class AbstractChangeSetModule implements IModule {
	
	/**
	 * The logger of the class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AbstractChangeSetModule.class.getName());

	/**
	 * Constructor.
	 */
	public AbstractChangeSetModule() {
		super();
	}

	@Override
	final public Level getLevel() {
		return Level.CHANGE_SET;
	}

	/**
	 * Execute the analyze of the change set
	 * 
	 * @param changeSetDAO
	 *            the analyzed change set
	 * @param result
	 *            the result DAO
	 * @throws AnalyzeException 
	 */
	abstract public void execute(final ChangeSetDAO changeSetDAO, final Result result) throws AnalyzeException;
	
	/**
	 * Publish an error message ot the result and the log.
	 * 
	 * @param result
	 * @param exception
	 */
	public void publishError(final Result result, final Exception exception) {
		LOGGER.error("Error in analyze module " + getName() , exception);
		result.addResult(ERROR, this);
	}

}
