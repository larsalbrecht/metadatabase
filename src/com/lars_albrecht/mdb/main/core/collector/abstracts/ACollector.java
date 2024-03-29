/**
 * 
 */
package com.lars_albrecht.mdb.main.core.collector.abstracts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.collector.event.CollectorEvent;
import com.lars_albrecht.mdb.main.core.collector.event.CollectorEventMulticaster;
import com.lars_albrecht.mdb.main.core.controller.CollectorController;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.controller.TypeController;
import com.lars_albrecht.mdb.main.core.controller.interfaces.IController;
import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.handler.OptionsHandler;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.FileAttributeList;
import com.lars_albrecht.mdb.main.core.models.KeyValue;
import com.lars_albrecht.mdb.main.core.models.persistable.FileAttributes;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.Key;
import com.lars_albrecht.mdb.main.core.models.persistable.Value;

/**
 * @author lalbrecht
 * 
 */
public abstract class ACollector implements Runnable {

	protected MainController											mainController			= null;
	protected IController												controller				= null;
	private ArrayList<FileItem>											fileItems				= null;
	private ArrayList<Key<String>>										keysToAdd				= null;
	private ArrayList<Value<?>>											valuesToAdd				= null;
	private ConcurrentHashMap<FileItem, ArrayList<FileAttributeList>>	fileAttributeListToAdd	= null;
	private ArrayList<FileAttributes>									fileAttributesToAdd		= null;
	private CollectorEventMulticaster									collectorMulticaster	= null;
	private ArrayList<String>											types					= null;

	private static final Object											lockObject				= new Object();

	/**
	 * Default constructor.
	 * 
	 */
	public ACollector() {
		this.keysToAdd = new ArrayList<Key<String>>();
		this.valuesToAdd = new ArrayList<Value<?>>();
		this.fileAttributesToAdd = new ArrayList<FileAttributes>();
		this.types = new ArrayList<String>();
	}

	protected void addType(final String type) {
		if (type != null) {
			TypeController.addType(type);
			this.types.add(type);
		} else {
			throw new NullPointerException("Type was null");
		}
	}

	/**
	 * Start the collect method.
	 */
	public abstract void doCollect();

	/**
	 * Returns a list of file attributes to add to the database.
	 * 
	 * @return ConcurrentHashMap<FileItem, ArrayList<FileAttributeList>>
	 */
	public abstract ConcurrentHashMap<FileItem, ArrayList<FileAttributeList>> getFileAttributeListToAdd();

	/**
	 * Returns an id of a given fileItem.
	 * 
	 * @param fileItem
	 * @return int
	 * @throws Exception
	 */
	private int getFileItemId(final FileItem fileItem) throws Exception {
		final ArrayList<FileItem> fileItems = this.mainController.getDataHandler().getFileItems();
		int pos = -1;
		if ((pos = fileItems.indexOf(fileItem)) > -1) {
			return fileItems.get(pos).getId();
		} else {
			/*
			 * Should not be called.
			 */
			Debug.log(Debug.LEVEL_FATAL,
					"File item not found in DataHandler-FileItem-List: " + fileItem.getId() + " - " + fileItem.getFullpath());
			throw new Exception("File item not found in DataHandler-FileItem-List: " + fileItem.getId() + " - " + fileItem.getFullpath());
		}

	}

	final protected ArrayList<FileItem> getFileItems() {
		return this.fileItems;
	}

	/**
	 * Returns the name of the collector.
	 * 
	 * @return String
	 */
	public abstract String getInfoType();

	/**
	 * Returns the keys to add.
	 * 
	 * @return ArrayList<Key<String>>
	 */
	public abstract ArrayList<Key<String>> getKeysToAdd();

	/**
	 * @return the types
	 */
	public final ArrayList<String> getTypes() {
		return this.types;
	}

	/**
	 * Returns the values to add.
	 * 
	 * @return ArrayList<Value<?>>
	 */
	public abstract ArrayList<Value<?>> getValuesToAdd();

	private void persist() {
		Debug.log(Debug.LEVEL_DEBUG, "persist now (" + this.getInfoType() + ")");
		this.persistKeys();
		this.persistValues();
		this.persistAttributes();
		Debug.log(Debug.LEVEL_DEBUG, "end persist (" + this.getInfoType() + ")");
	}

	/**
	 * Persist attributes (fileAttributes). Take this.fileAttributeListToAdd
	 * which contains the fileItem and an ArrayList of FileAttributeList
	 * (ArrayList<FileAttributeList>). To persist the fileAttributes, a new
	 * method called "persistFileAttributes" was created. In this method here, a
	 * method called "prepareAttributes" was created to add specific items to a
	 * general list.
	 * 
	 */
	private void persistAttributes() {
		int fileItemId = -1;
		if ((this.fileAttributeListToAdd != null) && (this.fileAttributeListToAdd.size() > 0)) {
			for (final Map.Entry<FileItem, ArrayList<FileAttributeList>> entry : this.fileAttributeListToAdd.entrySet()) {
				if ((entry.getValue() != null) && (entry.getValue().size() > 0)) {
					try {
						if (((fileItemId = this.getFileItemId(entry.getKey())) > -1)) {
							this.transformToFileAttributes(fileItemId, entry.getValue());
							// if this is commented, the file-timestamp will be
							// not updated
							this.mainController.getDataHandler().updateUpdateTSForFileItem(fileItemId);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				} else {
					this.mainController.getDataHandler().setNoInformationFoundFlag(entry.getKey(), this.getInfoType());
				}
			}
		}
		this.mainController.getDataHandler().reloadData(DataHandler.RELOAD_NOINFOFILEITEMS);
		this.persistFileAttributes();
	}

	/**
	 * Persist fileAttributes.
	 */
	private void persistFileAttributes() {
		try {
			this.mainController.getDataHandler();
			DataHandler.persist(this.fileAttributesToAdd, false);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void persistKeys() {
		try {
			this.mainController.getDataHandler();
			DataHandler.persist(this.keysToAdd, false);
			this.mainController.getDataHandler().reloadData(DataHandler.RELOAD_KEYS);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void persistValues() {
		try {
			this.mainController.getDataHandler();
			DataHandler.persist(this.valuesToAdd, false);
			this.mainController.getDataHandler().reloadData(DataHandler.RELOAD_VALUES);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Let only items in fileItems-list that are not collected already. Use the
	 * updateTS and the options (collectorEndRunLast) for this feature.
	 * 
	 * @param fileItems
	 * @param collectorName
	 * @return ArrayList<FileItem>
	 */
	private ArrayList<FileItem> prepareFileItems(final ArrayList<FileItem> fileItems, final String collectorName) {
		final ArrayList<FileItem> tempList = new ArrayList<FileItem>();
		if ((fileItems != null) && (fileItems.size() > 0)) {
			Debug.log(Debug.LEVEL_TRACE, Arrays.deepToString(fileItems.toArray()));
			final ArrayList<FileItem> noInformationList = this.mainController.getDataHandler().getNoInfoFileItems(this.getInfoType())
					.get(this.getInfoType());
			final Object lastRunObj = OptionsHandler.getOption("collectorEndRunLast" + Helper.ucfirst(collectorName));
			// TODO fix uncaughtException MSG: UncaughtException thrown
			// (java.sql.Timestamp cannot be cast to java.lang.Long -
			// java.lang.ClassCastException: java.sql.Timestamp cannot be cast
			// to
			// java.lang.Long) in Thread TheTVDB (264)
			// java.lang.ClassCastException:
			// java.sql.Timestamp cannot be cast to java.lang.Long

			// if lastRun = NULL, the collector collects for all files
			final Long lastRun = (lastRunObj == null ? null : (lastRunObj instanceof String ? Long.parseLong((String) lastRunObj)
					: (lastRunObj instanceof Timestamp ? ((Timestamp) lastRunObj).getTime() : (Long) lastRunObj)));
			for (int i = 0; i < fileItems.size(); i++) {
				// item for this collector?
				if ((fileItems.get(i) != null) && this.types.contains(fileItems.get(i).getFiletype())) {
					// runned before?
					if (lastRun == null) {
						// no, never runned
						if (i == 0) {
							// log only once
							Debug.log(Debug.LEVEL_TRACE, "Collector never runned before: " + Helper.ucfirst(collectorName));
						}
						tempList.add(fileItems.get(i));
					} else {
						// yes, runned before

						boolean noInfo = false;

						// no information found
						noInfo = this.mainController.getDataHandler().getNoInfoFileItems(this.getInfoType()).contains(fileItems.get(i));
						// new files
						noInfo = noInfo ? noInfo : this.mainController.getDataHandler().getNewFileItems().contains(fileItems.get(i));
						// reload files
						noInfo = noInfo ? noInfo : (fileItems.get(i).getStatus() == DataHandler.FILEITEMSTATUS_RELOAD ? true : false);
						if (!noInfo) {
							Debug.log(Debug.LEVEL_DEBUG, "Element collected already: " + fileItems.get(i));
						} else {
							Debug.log(Debug.LEVEL_TRACE, "Element NOT collected: " + fileItems.get(i));
							tempList.add(fileItems.get(i));
						}
					}
				}
			}
			if ((noInformationList != null) && (noInformationList.size() > 0)) {
				tempList.addAll(noInformationList);
			}

			this.mainController.getDataHandler().clearNoInfoFileItems(this.getInfoType());
		}
		return tempList;
	}

	/**
	 * Prepare keyList and valueList for persist. e.g. delete duplicated
	 * entries.
	 */
	private void preparePersist() {
		this.keysToAdd = Helper.uniqueList(this.keysToAdd);
		this.valuesToAdd = Helper.uniqueList(this.valuesToAdd);
	}

	@Override
	public final void run() {
		if (this.controller instanceof CollectorController) {
			this.collectorMulticaster = ((CollectorController) this.controller).getCollectorMulticaster();
		}
		OptionsHandler.setOption("collectorStartRunLast" + Helper.ucfirst(this.getInfoType()), new Timestamp(System.currentTimeMillis()));
		Debug.startTimer("Collector collect time: " + this.getInfoType());
		this.fileItems = this.prepareFileItems(this.fileItems, this.getInfoType());
		Debug.log(Debug.LEVEL_INFO, "Really collect for: " + this.fileItems.size() + " in " + this.getInfoType());
		ADataHandler.clearAllDataHandler();
		this.doCollect();
		Debug.stopTimer("Collector collect time: " + this.getInfoType());
		this.keysToAdd = this.getKeysToAdd();
		this.valuesToAdd = this.getValuesToAdd();
		this.fileAttributeListToAdd = this.getFileAttributeListToAdd();
		this.preparePersist();
		Debug.startTimer("Collector wait for persist time: " + this.getInfoType());
		synchronized (ACollector.lockObject) {
			Debug.stopTimer("Collector wait for persist time: " + this.getInfoType());
			Debug.startTimer("Collector persist time: " + this.getInfoType());
			this.persist();

			try {
				ADataHandler.persistAllDataHandler();
			} catch (final Exception e) {
				e.printStackTrace();
			}
			ACollector.lockObject.notify();
			Debug.stopTimer("Collector persist time: " + this.getInfoType());
		}
		OptionsHandler.setOption("collectorEndRunLast" + Helper.ucfirst(this.getInfoType()),
				(new Timestamp(System.currentTimeMillis()).getTime() / 1000));
		if (this.controller != null) {
			this.controller.getThreadList().remove(Thread.currentThread());
		}
		this.collectorMulticaster.collectorsEndSingle((new CollectorEvent(this, CollectorEvent.COLLECTOR_ENDSINGLE_COLLECTOR, this
				.getInfoType())));
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public final void setController(final IController controller) {
		this.controller = controller;
	}

	public final void setFileItems(final ArrayList<FileItem> fileItems) {
		this.fileItems = fileItems;
	}

	/**
	 * @param mainController
	 *            the mainController to set
	 */
	public final void setMainController(final MainController mainController) {
		this.mainController = mainController;
	}

	/**
	 * 
	 * @param fileItemId
	 * @param fileAttributeListList
	 * @throws Exception
	 */
	private void transformToFileAttributes(final int fileItemId, final ArrayList<FileAttributeList> fileAttributeListList) throws Exception {
		if (fileAttributeListList.size() > 0) {
			FileAttributes tempTypeInfo = null;
			final ArrayList<Key<?>> keys = this.mainController.getDataHandler().getKeys();
			final ArrayList<Value<?>> values = this.mainController.getDataHandler().getValues();
			final ArrayList<FileAttributes> typeInfo = this.mainController.getDataHandler().getFileAttributes();
			for (final FileAttributeList fileAttributes : fileAttributeListList) {
				for (final KeyValue<String, Object> keyValue : fileAttributes.getKeyValues()) {
					int keyPos = -1;
					int keyId = -1;
					if ((keyPos = keys.indexOf(keyValue.getKey())) > -1) {
						keyId = keys.get(keyPos).getId();
					}

					int valuePos = -1;
					int valueId = -1;
					if ((valuePos = values.indexOf(keyValue.getValue())) > -1) {
						valueId = values.get(valuePos).getId();
					}

					tempTypeInfo = new FileAttributes(fileItemId, keyId, valueId);

					if ((fileItemId > -1) && (keyId > -1) && (valueId > -1) && (tempTypeInfo != null) && !typeInfo.contains(tempTypeInfo)) {
						this.fileAttributesToAdd.add(tempTypeInfo);
					}
				}
			}
		}
	}

}
