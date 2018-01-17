package com.rus.jazz.tool.analyzescmcontent.analyze;

import java.util.HashMap;
import java.util.Map;

import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;

/**
 * Store the results of the analyze for the modules. Each module can only story
 * one result. If the module store the second result the last will be
 * overwritten.
 * 
 * @author wasser_m
 *
 */
public class Result {

	private transient Map<String, String> results; //NOPMD

	/**
	 * Constructor.
	 */
	public Result() {
		results = new HashMap<String, String>();
	}

	/**
	 * Add a new result for a module. Each module can only story one result. If
	 * the method is called more oftern for the same module, only the last
	 * result will be stored.
	 * 
	 * @param result
	 * @param module
	 */
	public void addResult(final String result, final IModule module) {
		results.put(module.getName(), result);
	}

	/**
	 * Return the result from a module. For identifying the module the name has
	 * to be specified.
	 * 
	 * @param moduleName
	 *            name of the module
	 * @return last result od the module
	 */
	public String getResultByName(final String moduleName) {
		return results.get(moduleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return results.toString();
	}

}
