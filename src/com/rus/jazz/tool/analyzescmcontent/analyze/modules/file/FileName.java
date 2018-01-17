package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import com.ibm.team.scm.common.IVersionable;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;

/**
 * Fetch the name of the file
 */
public class FileName extends AbstractFileModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet->file.name";

	/**
	 * Constructor.
	 */
	public FileName() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public void execute(final VersionableDAO fileDAO, final Result result) {
		try {
			final IVersionable versionable = fileDAO.getVersionable();
			if (versionable != null) {
				result.addResult(fileDAO.getVersionable().getName(), this);
			}
		} catch (final AnalyzeException e) {
			publishError(result, e);
		}
	}

	@Override
	public String getHeader() {
		return "File Name";
	}
	

}
