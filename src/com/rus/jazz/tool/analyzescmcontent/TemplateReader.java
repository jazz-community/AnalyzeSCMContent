package com.rus.jazz.tool.analyzescmcontent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.IModule;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.ModuleFactory;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput;
import com.rus.jazz.tool.analyzescmcontent.output.ConsoleOutput.State;

/**
 * read and analyze a template file. Creates the in the template file defined
 * modules in the defined order. If the template file contains modules which are
 * not registered in the ModuleFactory an exception will thrown. Lines that
 * start with # will be skipped.
 * 
 * The TemplateReader implements the interface Iterable. Therefore a foreach
 * loop to get the modules in the defined order is possible.
 */
public class TemplateReader implements Iterable<IModule> {

	private transient final List<IModule> modules;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            fileName of the template file
	 * @throws AnalyzeException
	 */
	public TemplateReader(final String fileName) throws AnalyzeException {
		// Instantiate the necessary objects
		modules = new ArrayList<IModule>();

		try {
			// Open the files
			ConsoleOutput.printMessage("Load the template file '" + fileName + "'", State.EXECUTING);
			final File file = new File(fileName);
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			// read the first line
			String line = bufferedReader.readLine();
			while (line != null) {
				line = line.trim();
				// and get the module from the module factory
				if (line.length() > 0 && line.charAt(0) != '#') { // NOPMD
					modules.add(ModuleFactory.getModuleInstance(line));
				}

				// and read the next line
				line = bufferedReader.readLine();
			}

			// and close the reader
			bufferedReader.close();
			ConsoleOutput.updateMessage("Load the template file '" + fileName + "'", State.SUCCESS);
		} catch (FileNotFoundException e) {
			ConsoleOutput.updateMessage("Load the template file '" + fileName + "'", State.ERROR);
			throw new AnalyzeException(e.getMessage(), e);
		} catch (IOException e) {
			ConsoleOutput.updateMessage("Load the template file '" + fileName + "'", State.ERROR);
			throw new AnalyzeException(e.getMessage(), e);
		}
	}

	/**
	 * Return the list with modules of the template file
	 * 
	 * @return list with modules
	 */
	public List<IModule> getModules() {
		return modules;
	}

	@Override
	public Iterator<IModule> iterator() {
		return modules.iterator();
	}

}
