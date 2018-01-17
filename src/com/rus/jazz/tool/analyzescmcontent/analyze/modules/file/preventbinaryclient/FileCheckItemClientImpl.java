package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file.preventbinaryclient;

import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.AbstractFile;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.CheckFileException;

/**
 * Client implementation of the AbstractFile class.
 */
public class FileCheckItemClientImpl extends AbstractFile {
	
	/**
	 * The logger of the class. 
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileCheckItemClientImpl.class.getName());

	/**
	 * The RTC file item.
	 */
	private final transient IFileItem item;

	/**
	 * The file content manager
	 */
	private final transient IFileContentManager fileContentMgr;

	/**
	 * Constrcutor.
	 * 
	 * @param item
	 * @param fileContentMgr 
	 */
	public FileCheckItemClientImpl(final IFileItem item, final IFileContentManager fileContentMgr) {
		super();
		this.item = item;
		this.fileContentMgr = fileContentMgr;
	}

	@Override
	public String getName() {
		return item.getName();
	}

	@Override
	public void getContent(final OutputStream outputStream) throws CheckFileException {
		try {
			fileContentMgr.retrieveContent(item, item.getContent(), outputStream, null);
		} catch (TeamRepositoryException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public long getFileSize() {
		return item.getContent().getSize();
	}

}
