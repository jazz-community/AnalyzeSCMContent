package com.rus.jazz.tool.analyzescmcontent.analyze.dao;

import java.io.ByteArrayOutputStream;

import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeServerConnection;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;

/**
 * DAO for a Versionable. This class is responsible, that every access to the
 * database will only executed once. Every further access will be cashed.
 */
public class VersionableDAO {

	private transient final IVersionableHandle handle;

	private transient IVersionable versionable;

	private final transient ComponentDAO componentDAO;

	private final transient ChangeSetDAO changeSetDAO;

	/**
	 * Constructor.
	 * 
	 * @param handle
	 *            handle of the versionable
	 * @param changeSetDAO
	 *            change set DAO
	 * @param componentDAO
	 *            component DAP
	 */
	public VersionableDAO(final IVersionableHandle handle, final ChangeSetDAO changeSetDAO,
			final ComponentDAO componentDAO) {
		this.handle = handle;
		this.componentDAO = componentDAO;
		this.changeSetDAO = changeSetDAO;
	}

	/**
	 * Get the versionable object
	 * 
	 * @return versionable object or null if not exists
	 * @throws AnalyzeException
	 */
	public IVersionable getVersionable() throws AnalyzeException {
		if (versionable == null) {
			try {
				versionable = SCMAnalyzeServerConnection.getInstance().getWorkspaceManager().versionableManager().fetchCompleteState(handle, null);
			} catch (TeamRepositoryException e) {
				throw new AnalyzeException(e.getMessage(), e);
			}
		}

		return versionable;
	}

	/**
	 * If the versionable is a file it returns the content, null otherwise.
	 * 
	 * @return content of file or null
	 * @throws AnalyzeException
	 */
	public IFileContent getContent() throws AnalyzeException {
		IFileContent result = null;
		final IVersionable versionable = getVersionable();
		if (versionable instanceof IFileItem) {
			final IFileItem file = (IFileItem) versionable;
			result = file.getContent();
		}

		return result;
	}

	/**
	 * Load the content from the repository. This method not cash the content
	 * because it can need a lot of memory and it is not shure that the content
	 * is needed twice.
	 * 
	 * @return content fo the file
	 * @throws AnalyzeException
	 */
	public ByteArrayOutputStream retrieveContent() throws AnalyzeException {
		try {
			final IFileItem fileItem = (IFileItem) versionable;
			final IFileContentManager fileContentMgr = FileSystemCore.getContentManager(SCMAnalyzeServerConnection.getInstance().getTeamRepository());
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			fileContentMgr.retrieveContent(fileItem, getContent(), output, null);

			return output;
		} catch (TeamRepositoryException exception) {
			throw new AnalyzeException(exception.getMessage(), exception);
		}
	}

	/**
	 * Return the component DAO
	 * 
	 * @return componentDAO
	 */
	public ComponentDAO getComponentDAO() {
		return componentDAO;
	}

	/**
	 * Return the component DAO
	 * 
	 * @return componentDAO
	 */
	public ChangeSetDAO getChangeSetDAO() {
		return changeSetDAO;
	}

}
