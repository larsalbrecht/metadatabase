/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;

/**
 * @author lalbrecht
 * 
 */
public abstract class ADataHandler<E> {

	private static ArrayList<ADataHandler<?>>	dataHandlers	= new ArrayList<ADataHandler<?>>();

	public static final boolean addDataHandler(final ADataHandler<?> dataHandler) {
		return ADataHandler.dataHandlers.add(dataHandler);
	}

	public static final void clearAllDataHandler() {
		for (final ADataHandler<?> dataHandler : ADataHandler.dataHandlers) {
			dataHandler.clearData();
		}
	}

	public static final ADataHandler<?> getDataHandler(final Class<?> clazz) {
		if (clazz.getSuperclass() == ADataHandler.class) {
			for (final ADataHandler<?> dataHandler : ADataHandler.dataHandlers) {
				if (dataHandler.getClass() == clazz) {
					return dataHandler;
				}
			}
		}
		return null;
	}

	public static final ArrayList<ADataHandler<?>> getDataHandlers() {
		return ADataHandler.dataHandlers;
	}

	/**
	 * Returns an ArrayList with handlerInformation from the fileItem.
	 * 
	 * @param fileItem
	 * @param clazz
	 * @return ArrayList<?>
	 */
	public static final ArrayList<?> getHandlerDataFromFileItem(final FileItem fileItem, final Class<?> clazz) {
		if ((clazz.getSuperclass() == ADataHandler.class) && (fileItem != null) && (fileItem.getDataStore() != null)
				&& fileItem.getDataStore().containsKey(clazz.getCanonicalName())) {
			return fileItem.getDataStore().get(clazz.getCanonicalName());
		}
		return null;
	}

	/**
	 * Returns true|false if the specific item has a storage key stored or not.
	 * 
	 * @param fileItem
	 * @param clazz
	 * @return
	 */
	public static final boolean hasFileItemHandlerData(final FileItem fileItem, final Class<?> clazz) {
		if ((clazz.getSuperclass() == ADataHandler.class) && (fileItem != null) && (fileItem.getDataStore() != null)
				&& fileItem.getDataStore().containsKey(clazz.getCanonicalName())) {
			return true;
		}
		return false;
	}

	public static final void persistAllDataHandler() throws Exception {
		for (final ADataHandler<?> dataHandler : ADataHandler.dataHandlers) {
			dataHandler.persistData();
		}
	}

	public static final boolean removeDataHandler(final ADataHandler<?> dataHandler) {
		return ADataHandler.dataHandlers.remove(dataHandler);
	}

	protected ConcurrentHashMap<String, ConcurrentHashMap<FileItem, ArrayList<?>>>	data	= null;

	public ADataHandler() {
		this.data = new ConcurrentHashMap<String, ConcurrentHashMap<FileItem, ArrayList<?>>>();
	}

	@SuppressWarnings("unchecked")
	public final void addData(final String dataKey, final FileItem fileItem, final ArrayList<IPersistable> dataList) {
		if ((dataKey != null) && (fileItem != null) && (fileItem.getId() != null) && (this.data != null)) {

			if (!this.data.containsKey(dataKey) || (this.data.containsKey(dataKey) && (this.data.get(dataKey) == null))) {
				this.data.put(dataKey, new ConcurrentHashMap<FileItem, ArrayList<?>>());
			}
			if (this.data.get(dataKey).containsKey(fileItem)) {
				dataList.addAll((Collection<? extends IPersistable>) this.data.get(dataKey).get(fileItem));
			}
			this.data.get(dataKey).put(fileItem, dataList);
		}
	}

	public final void addData(final String dataKey, final FileItem fileItem, final IPersistable data) {
		if ((dataKey != null) && (fileItem != null) && (fileItem.getId() != null) && (data != null)) {
			final ArrayList<IPersistable> tempList = new ArrayList<IPersistable>();
			tempList.add(data);
			this.addData(dataKey, fileItem, tempList);
		}
	}

	/**
	 * Clears all data from this Handler.
	 */
	private void clearData() {
		this.data.clear();
	}

	/**
	 * Returns all data that are saved in database for a specific dataKey. If
	 * fileItem is null, all items will be returned. If fileItem is NOT null,
	 * the Map only contains a single FileItem with all values.
	 * 
	 * @param dataKey
	 * @param fileItem
	 * @return
	 */
	public final ConcurrentHashMap<FileItem, ArrayList<?>> getData(final String dataKey, final FileItem fileItem) {
		if (this.data.get(dataKey) != null) {
			if (fileItem != null) {
				final ArrayList<?> tempArrayList = this.data.get(dataKey).get(fileItem);
				final ConcurrentHashMap<FileItem, ArrayList<?>> tempMap = new ConcurrentHashMap<FileItem, ArrayList<?>>();
				tempMap.put(fileItem, tempArrayList);
				return tempMap;
			} else {
				return this.data.get(dataKey);
			}
		}
		return null;
	}

	public abstract ArrayList<?> getHandlerDataForFileItem(final FileItem fileItem);

	@SuppressWarnings("unchecked")
	public final ArrayList<E> getHandlerDataFromFileItem(final FileItem fileItem) {

		if ((fileItem != null) && (fileItem.getDataStore() != null)
				&& fileItem.getDataStore().containsKey(this.getClass().getCanonicalName())) {
			return (ArrayList<E>) fileItem.getDataStore().get(this.getClass().getCanonicalName());
		}
		return null;
	}

	protected abstract void persistData() throws Exception;

	/**
	 * Sets the data to the specific fileItem in the DataStore.
	 * 
	 * @param fileItem
	 * @param handlerData
	 * @return FileItem
	 */
	public final FileItem setHandlerDataToFileItem(final FileItem fileItem, final ArrayList<?> handlerData) {
		if ((fileItem != null) && (fileItem.getDataStore() != null)) {
			fileItem.getDataStore().put(this.getClass().getCanonicalName(), handlerData);
		}
		return fileItem;
	}

}
