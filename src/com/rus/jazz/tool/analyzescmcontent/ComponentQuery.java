package com.rus.jazz.tool.analyzescmcontent;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput.State;
import com.rus.jazz.tools.common.ServerConnection;

/**
 * Create and execute an component query to identify all components that match
 * the search criteria.
 */
public class ComponentQuery {

	/**
	 * The logger of teh class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ComponentQuery.class.getName());

	/**
	 * Search criteria - project area names
	 */
	private transient List<String> projectAreaNames;

	/**
	 * Search criteria - component names
	 */
	private transient List<String> componentNames;

	/**
	 * The RTC server repository connection
	 */
	protected final transient ServerConnection repository;

	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	public ComponentQuery(final ServerConnection repository) {
		super();
		this.repository = repository;
	}

	/**
	 * Run the query for components and returns a list of component handles. If
	 * filter options that has been set will be considered by the query.
	 * 
	 * @return list of components that match the search criteria
	 * @throws TeamRepositoryException
	 */
	public List<IComponentHandle> runQuery() throws TeamRepositoryException {
		final List<IComponentHandle> result = new ArrayList<IComponentHandle>();

		if (componentNames == null || componentNames.isEmpty()) {
			// query for all components
			result.addAll(findComponents());
		} else {
			// query for component by names. In this case a query for each
			// component is necessary
			for (final String componentName : componentNames) {
				result.addAll(findComponentByName(componentName));
			}
		}

		LOGGER.info(result.size() + " Components identified"); // NOPMD

		return result;
	}

	/**
	 * Returns all for the user visible components for the project areas. If the
	 * project area name is null all visible components will be returned.
	 * 
	 * @param projectAreaName
	 * @return
	 * @throws TeamRepositoryException
	 */
	protected List<IComponentHandle> findComponents() throws TeamRepositoryException {
		final IComponentSearchCriteria searchCriteria = IComponentSearchCriteria.FACTORY.newInstance();

		addFilterByOwner(searchCriteria);

		final List<IComponentHandle> compHandleList = repository.getWorkspaceManager().findComponents(searchCriteria,
				Integer.MAX_VALUE, null);
		return compHandleList;
	}

	/**
	 * Add a filter by owner to the component search criteria.
	 * 
	 * @param searchCriteria
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("unchecked")
	private void addFilterByOwner(final IComponentSearchCriteria searchCriteria) throws TeamRepositoryException {
		if (projectAreaNames != null && !projectAreaNames.isEmpty()) {
			final List<IProcessArea> processAreas = new ArrayList<IProcessArea>();

			for (final String projectAreaName : projectAreaNames) {
				ConsoleOutput.updateMessage("Identify components in Project Area \'" + projectAreaName + "\'",
						State.EXECUTING);
				final IProjectArea projectArea = repository.findProcessArea(projectAreaName, null, null);

				if (projectArea == null) {
					ConsoleOutput.updateMessage("Identify components in Project Area \'" + projectAreaName + "\'",
							State.ERROR);
					throw new TeamRepositoryException("Project Area \'" + projectAreaName + "\' not found.");
				}

				processAreas.add(projectArea);
				processAreas.addAll(projectArea.getTeamAreas());
			}
			searchCriteria.getFilterByOwnerOptional().addAll(processAreas);
		}
	}

	/**
	 * Returns the component with the given name. If there are project areas
	 * defined, only in this project areas will be searched.
	 * 
	 * @param projectAreaName
	 * @return
	 * @throws TeamRepositoryException
	 */
	protected List<IComponentHandle> findComponentByName(final String componentName) throws TeamRepositoryException {
		final IComponentSearchCriteria searchCriteria = IComponentSearchCriteria.FACTORY.newInstance();
		addFilterByOwner(searchCriteria);
		searchCriteria.setExactName(componentName);

		final List<IComponentHandle> compHandleList = repository.getWorkspaceManager().findComponents(searchCriteria,
				Integer.MAX_VALUE, null);
		return compHandleList;
	}

	/**
	 * Set the filter option for project areas name. The query will only search
	 * for components in the defined project areas.
	 * 
	 * @param projectAreaNames
	 */
	public void setProjectAreaNames(final List<String> projectAreaNames) {
		this.projectAreaNames = projectAreaNames;
	}

	/**
	 * Set the filter option for component names. Only Components with the given
	 * names will be searched by the query.
	 * 
	 * @param componentNames
	 */
	public void setComponentNames(final List<String> componentNames) {
		this.componentNames = componentNames;
	}
}
