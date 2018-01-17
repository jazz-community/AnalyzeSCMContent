package com.rus.jazz.tool.analyzescmcontent.analyze.modules.file;

import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.common.IFileItem;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.CheckFileException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.ConfigurationException;
import com.rus.jazz.tool.preventbinarydeliver.whitelist.WhiteListConfiguration;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeParameter;
import com.rus.jazz.tool.analyzescmcontent.SCMAnalyzeServerConnection;
import com.rus.jazz.tool.analyzescmcontent.analyze.AnalyzeException;
import com.rus.jazz.tool.analyzescmcontent.analyze.Result;
import com.rus.jazz.tool.analyzescmcontent.analyze.dao.VersionableDAO;
import com.rus.jazz.tool.analyzescmcontent.analyze.modules.file.preventbinaryclient.FileCheckItemClientImpl;

/**
 * Verify if this file is restricted by the white-list.
 */
public class FileIsRestricted extends AbstractFileModule {

	/**
	 * The name of the module
	 */
	public static final String MODULE_NAME = "component->changeSet->file.restricted";

	private static final String WHITE_LIST_FILE = "FileIsRestricted.WhiteListFile";

	private transient final WhiteListConfiguration whiteList;

	/**
	 * Constructor.
	 * 
	 * @throws AnalyzeException
	 */
	public FileIsRestricted() throws AnalyzeException {
		super();
		try {
			final String whiteListFile = SCMAnalyzeParameter.getInstance().getConfiguration()
					.getProperty(WHITE_LIST_FILE);
			if (whiteListFile == null) {
				throw new AnalyzeException("Necessary property '" + WHITE_LIST_FILE
						+ "' is not defined in properties file.");
			}

			whiteList = new WhiteListConfiguration(whiteListFile);
			whiteList.loadConfiguration();
		} catch (ConfigurationException exception) {
			throw new AnalyzeException(exception.getMessage(), exception);
		}

	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getHeader() {
		return "File is restricted";
	}

	@Override
	public void execute(final VersionableDAO fileDAO, final Result result) throws AnalyzeException {
		String restricted = "unknown";
		try {
			final FileCheckItemClientImpl fileCheck = new FileCheckItemClientImpl((IFileItem) fileDAO.getVersionable(),
					FileSystemCore.getContentManager(SCMAnalyzeServerConnection.getInstance().getTeamRepository()));
			final String compOwnerName = fileDAO.getComponentDAO().getOwner().getComponentOwnerName();
			final boolean permitted = whiteList.isPermitted(fileCheck, compOwnerName);
			restricted = Boolean.toString(!permitted);

			result.addResult(restricted, this);
		} catch (final CheckFileException exception) {
			throw new AnalyzeException(exception.getMessage(), exception);
		}
	}
}
