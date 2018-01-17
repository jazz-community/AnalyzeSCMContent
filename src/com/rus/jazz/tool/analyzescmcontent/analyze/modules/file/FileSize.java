package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;

/**
 * Analyze the size of the file.
 */
public class FileSize extends AbstractFileModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet->file.size";

	/**
	 * Constructor.
	 */
	public FileSize() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "File Size";
	}

	@Override
	public void execute(final VersionableDAO fileDAO, final Result result) throws AnalyzeException {
		final long size = fileDAO.getContent().getSize();
		result.addResult(Long.toString(size), this);
	}

}
