package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.CheckFileException;

/**
 * Interface for the IFile Checker. For the PreventBinaryDelivery Extension
 * there can be different checker defined, that can be configured and used by
 * the extension. Every Checker Class must implement this interface.
 * 
 * @author wasser_m
 *
 */
public interface IFileChecker {

	/**
	 * Check a file, weather it should be prevented or not.
	 * 
	 * @param file
	 * @return true, if the file is allowed by the IFileChecker, otherwise false
	 * @throws CheckFileException
	 */
	boolean checkFile(final AbstractFile file) throws CheckFileException;

	/**
	 * Return the name of the IFileChecker implementation.
	 * 
	 * @return the name of the iFileChecker Implementation class
	 */
	String getName();
	
	/**
	 * Return the pattern of the white list configuration
	 * @return pattern of the white list configuration
	 */
	String getPattern();

}
