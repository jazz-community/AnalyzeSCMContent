package com.rus.jazz.tool.analyzescmcontent.analyze.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset.AbstractChangeSetModule;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.component.AbstractComponentModule;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.file.AbstractFileModule;

/**
 * Manage the modules that have to been executed during the analyze process.
 * Contains methods for getting all modules for a certain level. Following modul
 * levels are supported:
 * <ol>
 * <li>COMPONENT</li>
 * <li>CHANGE_SET</li>
 * <li>FILE</li>
 * </ol>
 * For executing a module, the module has to be registered at the module
 * manager.
 * 
 */
public final class ModuleManager {

	private transient final List<AbstractComponentModule> componentModules;
	private transient final List<AbstractChangeSetModule> changeSetsModules;
	private transient final List<AbstractFileModule> fileModules;
	private transient final List<String> order;
	private transient final Map<String, IModule> moduleMap;

	private static ModuleManager instane;

	/**
	 * Instantiate the module manager singleton.
	 */
	public static void instantiate() {
		instane = new ModuleManager();
	}

	/**
	 * Return the instance of the ModuleManager. The Moduelmanager must be
	 * instantiated before.
	 * 
	 * @return module manaager instance
	 */
	public static ModuleManager getInstance() {
		return instane;
	}

	/**
	 * Constructor.
	 */
	private ModuleManager() {
		componentModules = new ArrayList<AbstractComponentModule>();
		changeSetsModules = new ArrayList<AbstractChangeSetModule>();
		fileModules = new ArrayList<AbstractFileModule>();
		order = new ArrayList<String>();
		moduleMap = new HashMap<String, IModule>();
	}

	/**
	 * Add a new module at the module manager.
	 * 
	 * @param module
	 *            Implementation of the IModule interface
	 * @throws AnalyzeException
	 */
	public void add(final IModule module) throws AnalyzeException {

		// verify that module is not already added
		if (order.contains(module.getName())) {
			throw new AnalyzeException("Module is already registered and must not be added again.");
		} else {
			order.add(module.getName());
		}

		switch (module.getLevel()) {
		case COMPONENT:
			componentModules.add((AbstractComponentModule) module);
			break;
		case CHANGE_SET:
			changeSetsModules.add((AbstractChangeSetModule) module);
			break;
		case FILE:
			fileModules.add((AbstractFileModule) module);
			break;
		default:
			throw new AnalyzeException("Undefined module level " + module.getLevel());
		}

		// at last, add module to the map
		moduleMap.put(module.getName(), module);
	}

	/**
	 * Return all modules that has the level FILE
	 * 
	 * @return list of modules
	 */
	public List<AbstractFileModule> getFileModules() {
		return fileModules;
	}

	/**
	 * Return all modules that has the level COMPONENT
	 * 
	 * @return list of modules
	 */
	public List<AbstractComponentModule> getComponentModules() {
		return componentModules;
	}

	/**
	 * Return all modules that has the level CHANGE_SET
	 * 
	 * @return list of modules
	 */
	public List<AbstractChangeSetModule> getChangeSetModules() {
		return changeSetsModules;
	}

	/**
	 * Return the ordered list of modules names. All levels are included in this
	 * list.
	 * 
	 * @return ordered list with module names
	 */
	public List<String> getOrderedModuleList() {
		return order;
	}

	/**
	 * Return the module for a given name
	 * 
	 * @param moduleName
	 *            name of the module
	 * @return module
	 */
	public IModule getModule(final String moduleName) {
		return moduleMap.get(moduleName);
	}
}
