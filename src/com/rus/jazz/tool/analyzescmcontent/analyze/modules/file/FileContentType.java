package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import java.io.ByteArrayOutputStream;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.NonBinaryChecker;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeParameter;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;

/**
 * Analyze, with the help of the white list analyzer if the file is a binary
 * file or not.
 */
public class FileContentType extends AbstractFileModule {

	private static final String NUMBERFILES_PARAM = "FileContentType.NumberBytes";

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet->file.contenttype";

	private transient final int numberBytes;

	/**
	 * Constructor
	 */
	public FileContentType() {
		super();
		if (SCMAnalyzeParameter.getInstance().getConfiguration().containsKey(NUMBERFILES_PARAM)) {
			final String param = SCMAnalyzeParameter.getInstance().getConfiguration().getProperty(NUMBERFILES_PARAM);
			numberBytes = Integer.parseInt(param);
		} else {
			numberBytes = 1000;
		}
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "Content Type";
	}

	@Override
	public void execute(final VersionableDAO fileDAO, final Result result) throws AnalyzeException {
		final ByteArrayOutputStream output = fileDAO.retrieveContent();
		final String isBinary = NonBinaryChecker.isNonBinary(output, numberBytes) ? "Non Binary" : "Binary";
		result.addResult(isBinary, this);
	}

}
