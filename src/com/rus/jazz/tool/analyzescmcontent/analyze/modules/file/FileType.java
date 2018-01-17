package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;

/**
 * Analyze the file type based on the ending of the file.
 */
public class FileType extends AbstractFileModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet->file.type";

	/**
	 * Constructor.
	 */
	public FileType() {
		super();
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "File type";
	}

	@Override
	public void execute(final VersionableDAO fileDAO, final Result result) throws AnalyzeException {
		final String fileName = fileDAO.getVersionable().getName();
		final String filetype = extractFileType(fileName);
		result.addResult(filetype, this);
	}

	/**
	 * Search for the last point and return the characters after the last point.
	 * If file name not contains a point, the string 'unknown' will be returned.
	 * 
	 * @param fileName
	 *            Name of the file
	 * @return file type ore unknown
	 */
	private String extractFileType(final String fileName) {
		String result = "unknown";

		final int pos = fileName.lastIndexOf('.');
		if (pos >= 0) {
			result = normalizeCSVEntry(fileName.substring(pos));
		}
		return result;
	}

	/**
	 * Do some changes so that the output will be valid for csv files that could
	 * be opened by Excel.
	 * 
	 * @param string
	 * @return
	 */
	private String normalizeCSVEntry(final String string) {
		String result = string.replace(";", "");

		if (result.startsWith("1E")) {
			result = "'" + result; // NOPMD
		}

		return result;
	}

}
