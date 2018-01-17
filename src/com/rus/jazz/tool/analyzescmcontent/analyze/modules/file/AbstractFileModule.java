package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;

/**
 * Abstract super class of all file analyze modules.
 */
public abstract class AbstractFileModule implements IModule {
	
	/**
	 * The logger of the class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AbstractFileModule.class.getName());

	/**
	 * Constructor.
	 */
	public AbstractFileModule() {
		super();
	}

	@Override
	final public Level getLevel() {
		return Level.FILE;
	}

	/**
	 * Execute the analyze of the change set
	 * 
	 * @param fileDAO
	 *            the analyzed file
	 * @param result
	 *            the result DAO
	 * @throws AnalyzeException 
	 */
	abstract public void execute(final VersionableDAO fileDAO, final Result result) throws AnalyzeException;
	
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
