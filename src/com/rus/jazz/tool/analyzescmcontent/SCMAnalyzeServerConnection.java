package com.rus.jazz.tool.analyzescmcontent;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tools.common.ServerConnection;

/**
 * Singleton class for the server RTC server connection.
 * 
 *
 */
public final class SCMAnalyzeServerConnection {

	private static ServerConnection instance;

	/**
	 * Instantiate teh server connection.
	 * 
	 * @param user
	 *            user
	 * @param pwd
	 *            password
	 * @param repository
	 *            repository url
	 * @throws AnalyzeException
	 */
	public static void instantiate(final String user, final String pwd, final String repository) throws AnalyzeException {
		try {
			instance = new ServerConnection(user, pwd, repository, null);
		} catch (TeamRepositoryException exception) {
			throw new AnalyzeException(exception.getMessage(), exception);
		}
	}

	/**
	 * Get the server connection instance.
	 * 
	 * @return server connection.
	 */
	public static ServerConnection getInstance() {
		return instance;
	}
	
	private SCMAnalyzeServerConnection(){
		
	}

}
