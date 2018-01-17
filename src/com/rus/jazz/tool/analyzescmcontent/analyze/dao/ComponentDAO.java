package com.rus.jazz.tool.analyzescmcontent.analyze.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.common.IAuditable;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.common.IChangeSet;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IChangeSetSearchCriteria;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeParameter;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeServerConnection;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.ChangeSetAnalyzer;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;

/**
 * DAO for a component. This class is responsible, that every access to the
 * database will only executed once. Every further access will be cashed.
 */
public class ComponentDAO {

	private static final Logger LOGGER = LogManager.getLogger(ComponentDAO.class.getName());

	private transient IComponent component;

	private transient final IComponentHandle componentHandle;

	private transient ComponentOwnerDAO componentOwner;

	private transient final AnalyzeOutput output;

	private transient List<ChangeSetAnalyzer> changeSets;

	/**
	 * Constructor.
	 * 
	 * @param componentHandle
	 *            handle of the component
	 * @param output
	 */
	public ComponentDAO(final IComponentHandle componentHandle, final AnalyzeOutput output) {
		this.componentHandle = componentHandle;
		this.output = output;
	}

	/**
	 * Return the component object.
	 * 
	 * @return component object
	 * @throws AnalyzeException
	 */
	public IComponent getComponent() throws AnalyzeException {
		if (component == null) {
			try {
				component = (IComponent) SCMAnalyzeServerConnection.getInstance().getItemManager()
						.fetchCompleteItem(componentHandle, ItemManager.REFRESH, null);
			} catch (TeamRepositoryException exception) {
				throw new AnalyzeException(exception.getMessage(), exception);
			}
		}

		return component;
	}

	/**
	 * Returns the component owner DAO of teh component.
	 * 
	 * @return componentDAO
	 * @throws AnalyzeException
	 */
	public ComponentOwnerDAO getOwner() throws AnalyzeException {
		if (componentOwner == null) {
			try {
				List<IAuditableHandle> ownerHandles;
				final List<IComponentHandle> compHandles = new ArrayList<IComponentHandle>();
				compHandles.add(componentHandle);

				ownerHandles = SCMAnalyzeServerConnection.getInstance().getWorkspaceManager().findOwnersForComponents( // NOPMD
						compHandles, null);

				if (ownerHandles.size() != 1) { // NOPMD
					// this method only search for one component. Therefore in
					// the
					// result list also only one is possible
					throw new UnsupportedOperationException("Only one owner is expected for the component "
							+ getComponent().getName());
				}

				// and load the first (and only) owner
				final IAuditableHandle auditibleHandle = ownerHandles.get(0);
				final IAuditable auditable = (IAuditable) SCMAnalyzeServerConnection.getInstance().getItemManager()
						.fetchCompleteItem(auditibleHandle, IItemManager.DEFAULT, null);

				Object owner = SCMAnalyzeServerConnection.getInstance().getItemManager()
						.fetchCompleteItem(auditable.getItemHandle(), IItemManager.DEFAULT, null);

				// Return the Project Area if component owner is instance of
				// team
				// area
				if (owner instanceof ITeamArea) {
					final ITeamArea teamArea = (ITeamArea) owner;
					owner = SCMAnalyzeServerConnection.getInstance().getItemManager()
							.fetchCompleteItem(teamArea.getProjectArea(), ItemManager.REFRESH, null);
				}

				componentOwner = new ComponentOwnerDAO(owner);

			} catch (TeamRepositoryException exception) {
				throw new AnalyzeException(exception.getMessage(), exception);
			}
		}
		return componentOwner;
	}

	/**
	 * DAO object that contains the component owner. A component owner can
	 * either be a project area or a user.
	 */
	public static class ComponentOwnerDAO {

		private transient final Object compOwnerObject;

		/**
		 * Constructor
		 * 
		 * @param compOwnerObject
		 *            owner object
		 */
		public ComponentOwnerDAO(final Object compOwnerObject) {
			this.compOwnerObject = compOwnerObject;
		}

		/**
		 * Get the name from a component owner. A component owner can be a
		 * process area or a contributor.
		 * 
		 * @return the name of the component owner
		 */
		public String getComponentOwnerName() {
			String result = null;

			if (compOwnerObject instanceof IProcessArea) {
				final IProcessArea processArea = (IProcessArea) compOwnerObject;
				result = processArea.getName();
			} else if (compOwnerObject instanceof IContributor) {
				final IContributor contributor = (IContributor) compOwnerObject;
				result = contributor.getName();
			}

			return result;
		}
	}

	/**
	 * Return all change sets for this component that area that match the
	 * criterias.
	 * 
	 * @return list of ChangeSetAnalyzer objects
	 * @throws AnalyzeException
	 */
	public List<ChangeSetAnalyzer> getChangeSetsForComponent() throws AnalyzeException {
		if (changeSets == null) {
			// Search for the change sets
			final IChangeSetSearchCriteria searchCriteria = IChangeSetSearchCriteria.FACTORY.newInstance();
			searchCriteria.setComponent(getComponent());
			searchCriteria.setModifiedAfter(SCMAnalyzeParameter.getInstance().getModifiedAfter());
			searchCriteria.setModifiedBefore(SCMAnalyzeParameter.getInstance().getModifiedBefore());
			List<IChangeSetHandle> handles;
			try {
				handles = findChangeSets(searchCriteria, Integer.MAX_VALUE);
			} catch (TeamRepositoryException e) {
				throw new AnalyzeException(e.getMessage(), e);
			}

			changeSets = new ArrayList<ChangeSetAnalyzer>();
			for (final IChangeSetHandle handle : handles) {
				ChangeSetAnalyzer changeSet = new ChangeSetAnalyzer(handle, this, output);
				ChangeSetDAO dao = (ChangeSetDAO) changeSet.getDAO();
				if (dao.getChangeSet().isComplete()) {
					changeSets.add(changeSet);
				}
			}

			LOGGER.info(changeSets.size() + " change sets in component " + getComponent().getName() + " identified");
		}

		return changeSets;
	}

	/**
	 * Find all change sets for a given search criteria. It is necessary to
	 * split the query because one execution of a change set query can only
	 * return 256 change sets. Therefore the query is executed more often with
	 * different start dates, until no further change sets can be wound for the
	 * given criteria.
	 * 
	 * @param searchCriteria
	 * @param maxResults
	 * @param repository
	 * @return
	 * @throws TeamRepositoryException
	 */
	private List<IChangeSetHandle> findChangeSets(final IChangeSetSearchCriteria searchCriteria, final int maxResults)
			throws TeamRepositoryException {
		int toFetch = maxResults;

		// Load necessary Services
		final IWorkspaceManager workspacemanager = SCMAnalyzeServerConnection.getInstance().getWorkspaceManager();

		final List<IChangeSetHandle> result = new ArrayList<IChangeSetHandle>();

		final IChangeSetSearchCriteria pagingCriteria = (IChangeSetSearchCriteria) EcoreUtil
				.copy((EObject) searchCriteria);

		List<IChangeSetHandle> findResult = workspacemanager.findChangeSets(pagingCriteria, toFetch, null);
		result.addAll(findResult);

		while (toFetch > IWorkspaceManager.MAX_QUERY_SIZE && findResult.size() == IWorkspaceManager.MAX_QUERY_SIZE) {
			toFetch = toFetch - findResult.size();
			final IChangeSet lastChangeSet = (IChangeSet) SCMAnalyzeServerConnection.getInstance().getItemManager()
					.fetchCompleteItem(result.get(result.size() - 1), IItemManager.DEFAULT, null);
			// update the modified before/after to get the remaining change sets
			if (pagingCriteria.isOldestFirst()) {
				pagingCriteria.setModifiedAfter(new Timestamp(lastChangeSet.getLastChangeDate().getTime()));
			} else {
				pagingCriteria.setModifiedBefore(new Timestamp(lastChangeSet.getLastChangeDate().getTime()));
			}

			findResult = workspacemanager.findChangeSets(pagingCriteria, toFetch, null);
			result.addAll(findResult);
		}

		return result;
	}

}
