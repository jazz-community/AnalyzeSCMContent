package com.rus.jazz.tool.preventbinarydeliver.whitelist;

import java.util.ArrayList;
import java.util.List;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.checker.IFileChecker;

/**
 * The WhiteList class. It contains a number of user ordered IFileChecker.
 */
public class WhiteList {

	/**
	 * List of user ordered IFileChecker.
	 */
	private transient final List<IFileChecker> fileCheckerList;

	/**
	 * Constructor.
	 */
	public WhiteList() {
		fileCheckerList = new ArrayList<IFileChecker>();
	}

	/**
	 * Add a new reference to the white list. Doubled References will not be
	 * added.
	 * 
	 * @param checker
	 */
	public void addChecker(final IFileChecker checker) {
		fileCheckerList.add(checker);
	}

	/**
	 * Returns the list of checkers in the white list.
	 * 
	 * @return list of checkers
	 */
	public List<IFileChecker> getFileCheckerList() {
		return fileCheckerList;
	}

	/**
	 * Checks if a file type matches one of the file extensions in the white
	 * list.
	 * 
	 * @param file
	 * 
	 * @return true, if the file is permitted, otherwise false.
	 * @throws CheckFileException
	 */
	public boolean isPermitted(final AbstractFile file)
			throws CheckFileException {
		boolean result = false;
		if (file != null) {
			for (final IFileChecker fileChecker : fileCheckerList) {
				if (fileChecker.checkFile(file)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
