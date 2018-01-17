package com.rus.jazz.tool.analyzescmcontent.analyze;

import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.ChangeSetDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleManager;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.file.AbstractFileModule;
import com.rus.jazz.tool.analyzescmcontent.output.AnalyzeOutput;

/**
 * This class is responsible for analyzing a file. It will execute all modules
 * on file level for this file.
 */
public class FileAnalyzer implements IAnalyzer {

	private transient final VersionableDAO fileDAO;

	private transient final AnalyzeOutput output;

	/**
	 * Constructor
	 * 
	 * @param handle
	 *            IVersionableHandle
	 * @param changeSetDAO
	 *            changeSetDAO
	 * @param output
	 */
	public FileAnalyzer(final IVersionableHandle handle, final ChangeSetDAO changeSetDAO, final AnalyzeOutput output) {
		this.output = output;

		fileDAO = new VersionableDAO(handle, changeSetDAO, changeSetDAO.getComponentDAO());
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

		// if versionable is not a file do nothing, otherwise print
		final IVersionable versionable = fileDAO.getVersionable();
		if (versionable != null && versionable instanceof IFileItem) { // NOPMD
			output.addResult(result);
		}

	}

	/**
	 * Analyze the modules on file level on the file iteself.
	 * 
	 * @param result
	 *            the result dao
	 */
	private void analyseSelf(final Result result) {
		for (final AbstractFileModule module : ModuleManager.getInstance().getFileModules()) {
			try {
				module.execute(fileDAO, result);
			} catch (Exception exception) { //NOPMD
				module.publishError(result, exception);
			}
		}
	}

	@Override
	public Object getDAO() {
		return fileDAO;
	}
}
