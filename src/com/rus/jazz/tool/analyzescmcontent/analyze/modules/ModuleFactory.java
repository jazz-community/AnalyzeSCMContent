package com.rus.jazz.tool.analyzescmcontent.analyze.modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;

/**
 * Factory class for the modules. Returns modules for a module name.
 */
public final class ModuleFactory {

	private static final Logger LOGGER = LogManager.getLogger(ModuleFactory.class.getName());

	private static final String CHANGESET_MODULES = "com.rus.jazz.tool.analyzescmcontent.analyze.modules.changeset";
	private static final String COMPONENT_MODULES = "com.rus.jazz.tool.analyzescmcontent.analyze.modules.component";
	private static final String FILE_MODULES = "com.rus.jazz.tool.analyzescmcontent.analyze.modules.file";
	private static Map<String, IModule> modules;

	private static void initialize() throws AnalyzeException {
		modules = new HashMap<String, IModule>();

		/*
		 * Search all implementaion of the interface IModules in teh predefined
		 * packages
		 */
		final Set<Class<? extends IModule>> classes = new HashSet<Class<? extends IModule>>();
		classes.addAll(new Reflections(COMPONENT_MODULES).getSubTypesOf(IModule.class));
		classes.addAll(new Reflections(CHANGESET_MODULES).getSubTypesOf(IModule.class));
		classes.addAll(new Reflections(FILE_MODULES).getSubTypesOf(IModule.class));

		// and instantiate objects from them
		for (final Class<? extends IModule> moduleClass : classes) {
			try {
				String moduleName;
				moduleName = (String) moduleClass.getField("MODULE_NAME").get(null);
				final Constructor<? extends IModule> constructor = moduleClass.getConstructor();
				modules.put(moduleName, constructor.newInstance());
			// last change from Martin (replace error log with exception) always throws exception
			//   cause the AbstractXXX classes don't have the field MODULE_NAME
			// -> Not having a MODULE_NAME must be somehow acceptable
			} catch (NoSuchFieldException exception ) {
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException
					| InstantiationException | InvocationTargetException | NoSuchMethodException exception) {
				throw new AnalyzeException("Error during initializing the ModuleFactory", exception);
			}
		}
	}

	/**
	 * Utility class. Should not be instantiated.
	 */
	private ModuleFactory() {

	}

	/**
	 * Returns an instance of the given module.
	 * 
	 * @param moduleName
	 *            name of the module
	 * @return module
	 * @throws AnalyzeException
	 */
	public static IModule getModuleInstance(final String moduleName) throws AnalyzeException {

		if (modules == null) {
			initialize();
		}

		final IModule module = modules.get(moduleName);

		if (module == null) {
			throw new AnalyzeException("Module " + moduleName + " not defined!");
		}

		return module;
	}
}
