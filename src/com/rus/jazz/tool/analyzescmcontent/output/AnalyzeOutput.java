package com.rus.jazz.tool.analyzescmcontent.output;

import java.io.PrintWriter;
import java.util.List;

import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeParameter;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleManager;

/**
 * This class is responsible for writing the results to a csv file.
 */
public class AnalyzeOutput {

	private final transient PrintWriter csv;

	/**
	 * Constructor. Create a new file and write the header to this file.
	 * 
	 * @throws AnalyzeException
	 */
	public AnalyzeOutput()
			throws AnalyzeException {
		try {
			csv = new PrintWriter(SCMAnalyzeParameter.getInstance().getCSVFileName(), "UTF-8");
			final List<String> order = ModuleManager.getInstance().getOrderedModuleList();

			for (final String moduleName : order) {
				csv.print(ModuleManager.getInstance().getModule(moduleName).getHeader());
				csv.print(';');
			}
			
			csv.print('\n');
		} catch (Exception exception) { //NOPMD
			throw new AnalyzeException("Error while creating the CSV output file ", exception);
		}
	}

	/**
	 * Close the connection to the csv file.
	 */
	public void close() {
		csv.close();
	}

	/**
	 * Add a new line to the result output file
	 * 
	 * @param result
	 *            the result DAO
	 */
	public void addResult(final Result result) {
		final List<String> order = ModuleManager.getInstance().getOrderedModuleList();

		for (final String moduleName : order) {
			csv.print(result.getResultByName(moduleName));
			csv.print(';');
		}
		
		csv.print('\n');
	}

}
