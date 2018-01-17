package com.rus.jazz.tool.analyzescmcontent.analyze;

import java.util.List;

import com.ibm.team.scm.common.IComponentHandle;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleManager;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.component.AbstractComponentModule;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput.State;

/**
 * This class is responsible for analyzing a single component. It will execute
 * all modules on change set level for this change set. If there are rules for
 * analyzing change sets or even files. every change set that match the criteria
 * will also be analyzed.
 */
public class ComponentAnalyzer implements IAnalyzer {

	private transient final ComponentDAO componentDAO;

	private transient final AnalyzeOutput output;

	/**
	 * Constructor.
	 * 
	 * @param componentHandle
	 *            analyzed component
	 * @param output
	 */
	public ComponentAnalyzer(final IComponentHandle componentHandle, final AnalyzeOutput output) {
		this.output = output;

		componentDAO = new ComponentDAO(componentHandle, output);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rus.jazz.tool.analyze.components.analyze.IAnalyzer#analyse(com.rus
	 * .jazz.tool.analyze.components.analyze.Result)
	 */
	@Override
	public void analyse(final Result result) throws AnalyzeException {
		// any case analyze the component itself
		analyseSelf(result);

		// if there are modules on change set or file level
		if (hasChangeSetOrFileModules()) {

			// analyze all change sets
			final List<ChangeSetAnalyzer> changeSets = componentDAO.getChangeSetsForComponent();
			int finished = 0;
			for (final ChangeSetAnalyzer analyzer : changeSets) {
				final String comment = ((ChangeSetDAO) analyzer.getDAO()).getChangeSet().getComment();
				ConsoleOutput.updateMessageWithProgressBar("Analyze ChangeSet \'" + comment + "\'", State.EXECUTING,
						finished, changeSets.size());

				// analyze
				analyzer.analyse(result);

				finished++;
			}
		} else {
			output.addResult(result);
		}
	}

	private boolean hasChangeSetOrFileModules() {
		return !ModuleManager.getInstance().getChangeSetModules().isEmpty()
				|| !ModuleManager.getInstance().getFileModules().isEmpty();
	}

	/**
	 * Execute the component analyze modules for the component itself.
	 * 
	 * @param result
	 */
	private void analyseSelf(final Result result) {
		for (final AbstractComponentModule componentModule : ModuleManager.getInstance().getComponentModules()) {
			try {
				componentModule.execute(componentDAO, result);
			} catch (Exception exception) { //NOPMD
				componentModule.publishError(result, exception);
			}
		}
	}

	@Override
	public Object getDAO() {
		return componentDAO;
	}

}
