package com.rus.jazz.tool.analyzescmcontent.analyze.modules;

/**
 * The interface of the analyze modules. Each modules muss have an level and an
 * name.
 * 
 * @author wasser_m
 *
 */
public interface IModule {

	/**
	 * Message in output file if error occur during analyze.
	 */
	String ERROR = "Error";

	/**
	 * Level of the analyze module. Define for which item type it will executed.
	 */
	enum Level {
		COMPONENT, CHANGE_SET, FILE
	}

	/**
	 * Return the level of the analyze module. define on which item type the
	 * module will be executed.
	 * 
	 * @return Level of module
	 */
	Level getLevel();

	/**
	 * Return the name of the module.
	 * 
	 * @return name of the module
	 */
	String getName();

	/**
	 * Returns the header of the module that will be used for the csv file
	 * 
	 * @return header string
	 */
	String getHeader();

}
