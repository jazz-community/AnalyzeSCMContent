package com.rus.jazz.tool.preventbinarydeliver.whitelist;

import java.io.OutputStream;
import java.util.Set;
import java.util.TreeSet;

/**
 * Super class for the representation of an RTC file under source control.
 */
public abstract class AbstractFile {

	/**
	 * The list of white lists in which the file was already searched.
	 */
	private final transient Set<String> alreadySearchedIn;

	/**
	 * Constructor
	 */
	public AbstractFile() {
		super();
		alreadySearchedIn = new TreeSet<String>();
	}

	/**
	 * Checks, if the file was already searched in the reference.
	 * 
	 * @param reference
	 * @return true if this reference was already searched, otherwiese false
	 */
	public boolean alreadySerachedIn(final String reference) {
		return alreadySearchedIn.contains(reference);
	}

	/**
	 * Add a new reference that was already searched.
	 * 
	 * @param reference
	 */
	public void addReference(final String reference) {
		alreadySearchedIn.add(reference);
	}

	/**
	 * Return the name of the file.
	 * 
	 * @return name of the file.
	 */
	public abstract String getName();

	/**
	 * Returns the content of the file under jazz source control as byte stream.
	 * 
	 * @param outputStream
	 * 
	 * @throws CheckFileException
	 */
	public abstract void getContent(final OutputStream outputStream)
			throws CheckFileException;

	/**
	 * Returns the file size of the file under version control.
	 * 
	 * @return the length in byte of the file
	 */
	public abstract long getFileSize();
}