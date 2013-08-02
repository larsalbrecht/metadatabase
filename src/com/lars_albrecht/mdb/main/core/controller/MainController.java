/**
 * 
 */
package com.lars_albrecht.mdb.main.core.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.FileFinder;
import com.lars_albrecht.general.utilities.PropertiesExNotInitilizedException;
import com.lars_albrecht.mdb.main.core.collector.event.CollectorEvent;
import com.lars_albrecht.mdb.main.core.collector.event.ICollectorListener;
import com.lars_albrecht.mdb.main.core.finder.event.FinderEvent;
import com.lars_albrecht.mdb.main.core.finder.event.IFinderListener;
import com.lars_albrecht.mdb.main.core.handler.ConfigurationHandler;
import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.handler.ObjectHandler;
import com.lars_albrecht.mdb.main.core.models.FileItem;

/**
 * @author lalbrecht
 * 
 */
public class MainController implements IFinderListener, ICollectorListener {

	private FinderController					fController		= null;
	private TypeController						tController		= null;
	private CollectorController					cController		= null;
	private InterfaceController					iController		= null;
	private ExportController					eController		= null;
	private DataHandler							dataHandler		= null;
	private ConfigurationHandler				configHandler	= null;

	private ConcurrentHashMap<String, Object>	globalVars		= null;

	public MainController() {
		this.init();
	}

	@Override
	public void finderAddFinish(final FinderEvent e) {
		Debug.log(Debug.LEVEL_INFO, "Found " + e.getFiles().size() + " files. Type them and start to collect.");
		this.getDataHandler().reloadData(DataHandler.RELOAD_FILEITEMS);
		final ArrayList<FileItem> typedFilesList = new ArrayList<FileItem>();

		// type files
		for (final FileItem fileItem : this.startTyper(ObjectHandler.fileListToFileItemList(e.getFiles()))) {
			typedFilesList.add(fileItem);
		}

		// insert to database
		try {
			this.persistFileItems(typedFilesList);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		this.getDataHandler().reloadData(DataHandler.RELOAD_FILEITEMS);

		Debug.log(Debug.LEVEL_INFO, "Probably collect for: " + this.getDataHandler().getFileItems().size());
		// filter filled database data to reduce runtime
		this.startCollect(this.getDataHandler().getFileItems());
	}

	/**
	 * TODO refactor
	 * 
	 * @param files
	 * @throws Exception
	 */
	private void persistFileItems(final ArrayList<FileItem> files) throws Exception {
		final ArrayList<FileItem> missingFilesList = new ArrayList<FileItem>();
		ArrayList<FileItem> newFilesList = new ArrayList<FileItem>();
		final ArrayList<FileItem> movedFilesList = new ArrayList<FileItem>();

		for (final FileItem fileItem : this.dataHandler.getFileItems()) {
			if (fileItem != null && fileItem.getFullpath() != null && !new File(fileItem.getFullpath()).exists()) {
				missingFilesList.add(fileItem);
				Debug.log(Debug.LEVEL_TRACE, "FOUND MISSING ITEM " + fileItem.getName());
			}
		}

		boolean found = false;
		for (final FileItem fileItem : files) {
			found = false;
			if (!this.dataHandler.getFileItems().contains(fileItem)) {
				for (final FileItem missingFileItem : missingFilesList) {
					if (this.isSameFile(fileItem, missingFileItem)) {
						fileItem.setId(missingFileItem.getId());
						movedFilesList.add(fileItem);
						Debug.log(Debug.LEVEL_TRACE, "FOUND MOVED ITEM " + fileItem.getName());
						found = true;
						break;
					}
				}
				if (!found) {
					Debug.log(Debug.LEVEL_TRACE, "NEW FILE ITEM " + fileItem.getName());
					newFilesList.add(fileItem);
				}
			} else {
				final FileItem currentFileItem = this.dataHandler.getFileItems().get(this.dataHandler.getFileItems().indexOf(fileItem));
				if (currentFileItem.getStatus() == 1) {
					Debug.log(Debug.LEVEL_TRACE, "REFOUND MISSING ITEM " + fileItem.getName());
					this.getDataHandler().updateStatusOfFileItem(currentFileItem.getId(), DataHandler.FILEITEMSTATUS_NORMAL);
				}
			}
		}

		// save new files to load them in the collector
		this.getDataHandler().getNewFileItems().clear();
		if (newFilesList != null && newFilesList.size() > 0) {
			newFilesList = this.getFilesWithHash(newFilesList);
			this.getDataHandler().getNewFileItems().addAll(newFilesList);
		}

		// persist new files
		this.getDataHandler().persist(newFilesList, false);

		// persist moved files
		this.getDataHandler().persist(movedFilesList, true);

		// update old files that are missing
		// TODO create function
		for (final FileItem fileItem : missingFilesList) {
			this.getDataHandler().updateStatusOfFileItem(fileItem.getId(), DataHandler.FILEITEMSTATUS_MISSING);
		}
	}

	/**
	 * Compares to fileItems with name and size. If this are equals on both
	 * files, the file is the same.
	 * 
	 * @param fileA
	 * @param fileB
	 * @return boolean
	 */
	private boolean isSameFile(final FileItem fileA, final FileItem fileB) {
		if (!fileA.getName().equalsIgnoreCase(fileB.getName())) {
			return false;
		}
		if (!fileA.getSize().equals(fileB.getSize())) {
			return false;
		}

		return true;
	}

	/**
	 * Currently not really used.
	 * 
	 * TODO create a file-hash and save it in FileItem. TODO find better method.
	 * MD5 and CRC32 too slow for many (big) files. TODO CRC32 over only the
	 * first (512) bytes?
	 * 
	 * 
	 * @param files
	 * @return ArrayList<FileItem>
	 */
	private ArrayList<FileItem> getFilesWithHash(final ArrayList<FileItem> files) {
		// METHOD 1 CRC32
		// for (final FileItem fileItem : tempList) {
		// try {
		// Debug.startTimer("Hash " + fileItem.getName());
		// fileItem.setFilehash(MD5Checksum.getCRC32Checksum(fileItem.getFullpath()));
		// Debug.stopTimer("Hash " + fileItem.getName());
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
		// }
		// METHOD 2 MD5
		// for (final FileItem fileItem : tempList) {
		// try {
		//
		// //
		// fileItem.setFilehash(MD5Checksum.getMD5Checksum(fileItem.getFullpath()));
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
		// }
		return files;
	}

	@Override
	public void finderAfterAdd(final FinderEvent e) {
	}

	@Override
	public void finderFoundDir(final FinderEvent e) {
	}

	@Override
	public void finderFoundFile(final FinderEvent e) {
	}

	@Override
	public void finderPreAdd(final FinderEvent e) {
	}

	/**
	 * @return the cController
	 */
	public CollectorController getcController() {
		return this.cController;
	}

	/**
	 * @return the dataHandler
	 */
	public DataHandler getDataHandler() {
		return this.dataHandler;
	}

	/**
	 * @return the configHandler
	 */
	public final ConfigurationHandler getConfigHandler() {
		return this.configHandler;
	}

	/**
	 * @return the fController
	 */
	public FinderController getfController() {
		return this.fController;
	}

	/**
	 * @return the tController
	 */
	public TypeController gettController() {
		return this.tController;
	}

	/**
	 * @return the iController
	 */
	public final InterfaceController getiController() {
		return this.iController;
	}

	/**
	 * @return the eController
	 */
	public final ExportController geteController() {
		return this.eController;
	}

	/**
	 * @return the globalVars
	 */
	public ConcurrentHashMap<String, Object> getGlobalVars() {
		return this.globalVars;
	}

	private void init() {
		Thread.setDefaultUncaughtExceptionHandler(new Debug());

		// specify folders to search for files
		FileFinder.getInstance().addToPathList(Paths.get("").toAbsolutePath().toString(), -1);
		FileFinder.getInstance().addToPathList(new File("."), -1);
		FileFinder.getInstance().addToPathList(new File("web"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/pages"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/css"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/css/vader"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/css/vader/images"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/js"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/vendor"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/img"), -1);
		FileFinder.getInstance().addToPathList(new File("trunk/web/img/plugins/fancybox"), -1);

		this.dataHandler = new DataHandler(this);
		this.globalVars = new ConcurrentHashMap<String, Object>();

		try {
			this.configHandler = new ConfigurationHandler();
		} catch (final PropertiesExNotInitilizedException e) {
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final ArrayList<?> tempList = ObjectHandler.castStringListToFileList(this.configHandler.getConfigOptionModuleFinderPath());
		this.globalVars.put("searchPathList", tempList);
	}

	private ArrayList<FileItem> startTyper(final ArrayList<FileItem> fileItemList) {
		return this.tController.findOutType(fileItemList);
	}

	public void run() {
		this.startInterfaces();
		this.startSearch();
	}

	private void startCollect(final ArrayList<FileItem> fileItemList) {
		try {
			this.cController.run(fileItemList);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void startInterfaces() {
		try {
			this.iController.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void startSearch() {
		try {
			this.fController.run(((ArrayList<File>) this.globalVars.get("searchPathList")));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void collectorsEndAll(final CollectorEvent e) {
		Debug.log(Debug.LEVEL_INFO, "All collectors ended");
		Debug.log(Debug.LEVEL_DEBUG, "Times: ");
		for (final String string : Debug.getFormattedTimes()) {
			Debug.log(Debug.LEVEL_DEBUG, string);
		}
		Debug.saveLogToFileForLevel(Debug.LEVEL_ALL);
	}

	@Override
	public void collectorsEndSingle(final CollectorEvent e) {
		// OptionsHandler.setOption("collectorEndRunLast" +
		// Helper.ucfirst(e.getCollectorName()), new
		// Timestamp(System.currentTimeMillis()));
		Debug.log(Debug.LEVEL_INFO, "Collector " + e.getCollectorName() + " ends");
	}

	public void exitProgram() {
		Debug.log(Debug.LEVEL_INFO, "Program will shut down");
		System.exit(-1);
	}

	/**
	 * @param fController
	 *            the fController to set
	 */
	public final void setfController(final FinderController fController) {
		this.fController = fController;
	}

	/**
	 * @param tController
	 *            the tController to set
	 */
	public final void settController(final TypeController tController) {
		this.tController = tController;
	}

	/**
	 * @param cController
	 *            the cController to set
	 */
	public final void setcController(final CollectorController cController) {
		this.cController = cController;
	}

	/**
	 * @param iController
	 *            the iController to set
	 */
	public final void setiController(final InterfaceController iController) {
		this.iController = iController;
	}

	/**
	 * @param eController
	 *            the eController to set
	 */
	public final void seteController(final ExportController eController) {
		this.eController = eController;
	}

	/**
	 * @param dataHandler
	 *            the dataHandler to set
	 */
	public final void setDataHandler(final DataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	/**
	 * @param configHandler
	 *            the configHandler to set
	 */
	public final void setConfigHandler(final ConfigurationHandler configHandler) {
		this.configHandler = configHandler;
	}

	/**
	 * @param globalVars
	 *            the globalVars to set
	 */
	public final void setGlobalVars(final ConcurrentHashMap<String, Object> globalVars) {
		this.globalVars = globalVars;
	}

}
