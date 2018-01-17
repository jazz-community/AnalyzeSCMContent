package com.rus.jazz.tool.analyzescmcontent.analyze;

/**
 * The Interface for a analyzer class. An implementation of this interface
 * should realize the analyze of a SCM item. At the moment there are
 * implementation for following item types:
 * <ul>
 * <li>Component</li>
 * <li>Change Set</li>
 * <li>Versionable</li>
 * </ul>
 */
public interface IAnalyzer {

	/**
	 * Execute the analyze modules on a certain level.
	 * 
	 * @param result
	 *            result DAO
	 * @throws AnalyzeException
	 */
	void analyse(final Result result) throws AnalyzeException;

	/**
	 * Return the DAO object.
	 * 
	 * @return DAO Object
	 */
	Object getDAO();

}