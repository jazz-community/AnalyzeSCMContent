package com.rus.jazz.tool.analyzescmcontent.analyze.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.team.filesystem.common.IFileItemHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IChange;
import com.ibm.team.scm.common.IChangeSet;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IVersionableHandle;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeServerConnection;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.FileAnalyzer;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;

/**
 * DAO for a change set. This class is responsible, that every access to the
 * database will only executed once. Every further access will be cashed.
 */
public class ChangeSetDAO {

	private static final Logger LOGGER = LogManager.getLogger(ChangeSetDAO.class.getName());

	private transient final IChangeSetHandle handle;

	private transient IChangeSet changeSet;

	private transient IContributor author;

	private transient final ComponentDAO componentDAO;

	private transient final AnalyzeOutput output;

	private transient List<FileAnalyzer> files;

	/**
	 * Constructor
	 * 
	 * @param handle
	 *            handle of the change set
	 * @param componentDAO
	 *            component DAO
	 * @param output
	 */
	public ChangeSetDAO(final IChangeSetHandle handle, final ComponentDAO componentDAO, final AnalyzeOutput output) {
		this.handle = handle;
		this.componentDAO = componentDAO;
		this.output = output;
	}

	/**
	 * Return the handle of the change set.
	 * 
	 * @return change set handle
	 */
	public IChangeSetHandle getHandle() {
		return handle;
	}

	/**
	 * Return the change set object
	 * 
	 * @return ChangeSet Object
	 * @throws AnalyzeException
	 */
	public IChangeSet getChangeSet() throws AnalyzeException {
		if (changeSet == null) {
			try {
				changeSet = (IChangeSet) SCMAnalyzeServerConnection.getInstance().getItemManager()
						.fetchCompleteItem(handle, IItemManager.DEFAULT, null);
			} catch (TeamRepositoryException exception) {
				throw new AnalyzeException(exception.getMessage(), exception);
			}
		}
		return changeSet;
	}

	/**
	 * Fetch the author contributor item of the change set from the jazz
	 * repository
	 * 
	 * @return contributor object of the change set author
	 * @throws AnalyzeException
	 */
	public IContributor getAuthor() throws AnalyzeException {
		if (author == null) {
			final IContributorHandle authorHandle = changeSet.getAuthor();
			try {
				author = (IContributor) SCMAnalyzeServerConnection.getInstance().getItemManager()
						.fetchCompleteItem(authorHandle, ItemManager.REFRESH, null);
			} catch (TeamRepositoryException exception) {
				throw new AnalyzeException(exception.getMessage(), exception);
			}
		}
		return author;
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
	 * Return the list of files for a change set.
	 * 
	 * @return list of files
	 * @throws AnalyzeException
	 */
	@SuppressWarnings("unchecked")
	public List<FileAnalyzer> getFilesFromChangeSet() throws AnalyzeException {
		if (files == null) {
			final List<IChange> changeList = getChangeSet().changes();

			files = new ArrayList<FileAnalyzer>();
			for (final IChange iChange : changeList) {
				final IVersionableHandle handle = iChange.afterState();
				if (handle instanceof IFileItemHandle) {
					files.add(new FileAnalyzer(handle, this, output));
				}
			}

			LOGGER.info(files.size() + " Files identified in Change Set " + getChangeSet().getComment());
		}

		return files;
	}

}
