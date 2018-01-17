package com.rus.jazz.tool.analyzescmcontent.analyze;

import java.util.List;

import com.ibm.team.scm.common.IChangeSetHandle;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ComponentDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleManager;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset.AbstractChangeSetModule;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;

/**
 * This class is responsible for analyzing a single change sets. If there are
 * further modules on file level each file of the change set will be analyzed.
 */
public class ChangeSetAnalyzer implements IAnalyzer {

	

	private transient final ChangeSetDAO changeSetDAO;

	private transient final AnalyzeOutput output;

	/**
	 * Constructor.
	 * 
	 * @param handle
	 *            ChangeSet handle
	 * @param componentDAO
	 * @param output
	 */
	public ChangeSetAnalyzer(final IChangeSetHandle handle, final ComponentDAO componentDAO,
			final AnalyzeOutput output) {
		this.output = output;

		changeSetDAO = new ChangeSetDAO(handle, componentDAO, output);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rus.jazz.tool.analyze.components.analyze.IAnalyzer#analyse(com.rus
	 * .jazz.tool.analyze.components.analyze.Result)
	 */
	public void analyse(final Result result) throws AnalyzeException {
		// any case analyze the component itself
		analyseSelf(result);

		// if there are modules on file level
		if (!ModuleManager.getInstance().getFileModules().isEmpty()) { // NOPMD
			final List<FileAnalyzer> files = changeSetDAO.getFilesFromChangeSet();

			for (final FileAnalyzer file : files) {
				file.analyse(result);
			}
		} else {
			output.addResult(result);
		}
	}



	/**
	 * Execute all modules on change set level on the change set itself.
	 * 
	 * @param result
	 *            the result dao
	 * @throws AnalyzeException
	 */
	private void analyseSelf(final Result result) {
		for (final AbstractChangeSetModule module : ModuleManager.getInstance().getChangeSetModules()) {
			try {
				module.execute(changeSetDAO, result);
			} catch (Exception exception) { //NOPMD
				module.publishError(result, exception);
			}
		}
	}

	@Override
	public Object getDAO() {
		return changeSetDAO;
	}
}
