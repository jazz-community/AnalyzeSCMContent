package com.rus.jazz.tool.preventbinarydeliver.whitelist.checker;

import java.util.Map;

import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.CheckFileException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteList;

/**
 * This class is an implementation of the IFileChecker interface. It contains a
 * reference to another white list containing in a white list map. If this
 * checker is called to check a file the call is forwarded to the referenced
 * white list.
 */
public class Reference implements IFileChecker {

	/**
	 * The Pattern of this checker
	 */
	private final transient String pattern;

	/**
	 * The name of the white list that is referenced.
	 */
	private transient final String referenceName;

	/**
	 * The white list map.
	 */
	private transient final Map<String, WhiteList> whiteListMap;

	/**
	 * Default Constructor.
	 * 
	 * @param pattern
	 *            pattern of the checker
	 * @param referenceName
	 *            name of the referenced white list
	 * @param whiteListMap
	 *            white list map
	 */
	public Reference(final String pattern, final String referenceName,
			final Map<String, WhiteList> whiteListMap) {
		this.pattern = pattern;
		this.referenceName = referenceName;
		this.whiteListMap = whiteListMap;
	}

	/**
	 * Return the name of the referenced white list.
	 * 
	 * @return name of the referenced white list.
	 */
	public String getReferenceName() {
		return referenceName;
	}

	@Override
	public boolean checkFile(final AbstractFile file) throws CheckFileException {
		try {
			boolean result = false;
			if (!file.alreadySerachedIn(referenceName)) {
				final WhiteList whiteList = whiteListMap.get(referenceName);
				file.addReference(referenceName);
				result = whiteList.isPermitted(file);
			}
			return result;
		} catch (CheckFileException e) {
			throw new CheckFileException(e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "Reference [pattern=" + pattern + ", referenceName="
				+ referenceName + "]";
	}

	@Override
	public String getName() {
		return "Reference";
	}

	@Override
	public String getPattern() {
		return pattern;
	}
}
