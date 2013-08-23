/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler.datahandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileMediaItem;
import com.lars_albrecht.mdb.main.core.models.persistable.MediaItem;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class MediaHandler<E> extends ADataHandler<E> {

	/**
	 * Persist FileMediaItems.
	 * 
	 * @param fileItem
	 * @param mediaItems
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void createAndPersistFileMediaItems(final FileItem fileItem, final ArrayList<MediaItem> mediaItems) throws Exception {
		if ((fileItem != null) && (fileItem.getId() != null) && (fileItem.getId() > -1) && (mediaItems != null) && (mediaItems.size() > 0)) {
			final ArrayList<FileMediaItem> tempFileMediaItems = new ArrayList<FileMediaItem>();
			for (final MediaItem mediaItem : mediaItems) {
				if ((mediaItem != null) && (mediaItem.getId() != null)) {
					tempFileMediaItems.add(new FileMediaItem(fileItem.getId(), mediaItem.getId()));
				}
			}
			if ((tempFileMediaItems != null) && (tempFileMediaItems.size() > 0)) {
				DataHandler.persist(tempFileMediaItems, false);
			}
		}
	}

	/**
	 * Returns a list of FileMediaItems for a single fileItem.
	 * 
	 * @param fileItem
	 * @return ArrayList<FileMediaItem>
	 */
	@SuppressWarnings("unused")
	private static ArrayList<FileMediaItem> getFileMediaItemsForFile(final FileItem fileItem) {
		final ArrayList<FileMediaItem> resultList = new ArrayList<FileMediaItem>();
		if ((fileItem != null) && (fileItem.getId() != null) && (fileItem.getId() > -1)) {

			ResultSet rs = null;
			final String sql = "SELECT fm.id, fm.file_id, fm.media_id FROM fileMedia AS fm WHERE fm.file_id = '" + fileItem.getId() + "'";
			try {
				Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
				rs = DB.query(sql);
				HashMap<String, Object> tempMap = null;
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) { // for each line
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					resultList.add((FileMediaItem) new FileMediaItem().fromHashMap(tempMap));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}

		}
		return resultList;
	}

	/**
	 * Returns a list of persisted MediaItems for a list of non-persisted
	 * MediaItems.
	 * 
	 * @param mediaItems
	 * @return
	 */
	private static ArrayList<MediaItem> getPersistedMediaItemsForMediaItems(final ArrayList<MediaItem> mediaItems) {
		final ArrayList<MediaItem> resultList = new ArrayList<MediaItem>();
		if ((mediaItems != null) && (mediaItems.size() > 0)) {
			final ArrayList<String> mediaNames = new ArrayList<String>();
			for (final MediaItem mediaItem : mediaItems) {
				if (mediaItem.getName() != null) {
					mediaNames.add(mediaItem.getName());
				}
			}
			if ((mediaNames != null) && (mediaNames.size() > 0)) {
				ResultSet rs = null;
				final String sql = "SELECT mi.id, mi.name, mi.type, mi.uri, mi.options FROM mediaItems AS mi WHERE mi.name IN ("
						+ Helper.implode(mediaNames, ", ", "'", "'") + ")";

				try {
					Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
					rs = DB.query(sql);
					HashMap<String, Object> tempMap = null;
					final ResultSetMetaData rsmd = rs.getMetaData();
					for (; rs.next();) { // for each line
						tempMap = new HashMap<String, Object>();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
						}
						resultList.add((MediaItem) new MediaItem().fromHashMap(tempMap));
					}
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return resultList;
	}

	private static void persistFileMediaItems(final ArrayList<FileMediaItem> fileMediaItems) throws Exception {
		DataHandler.persist(fileMediaItems, false);
	}

	private static void persistMediaItems(final ArrayList<MediaItem> mediaItems) throws Exception {
		DataHandler.persist(mediaItems, false);
	}

	public MediaHandler() {
		this.data.put("fileMediaItems", new ConcurrentHashMap<FileItem, ArrayList<?>>());
		this.data.put("mediaItems", new ConcurrentHashMap<FileItem, ArrayList<?>>());

		// this.data.put("fileMediaItems", new ArrayList<FileMediaItem>());
		// this.data.put("mediaItems", new ArrayList<MediaItem>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<MediaItem> getHandlerDataForFileItem(final FileItem fileItem) {
		final ArrayList<FileItem> fileItems = new ArrayList<FileItem>();
		fileItems.add(fileItem);
		return (ArrayList<MediaItem>) this.getHandlerDataForFileItems(fileItems).get(fileItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ConcurrentHashMap<FileItem, ArrayList<?>> getHandlerDataForFileItems(final ArrayList<FileItem> fileItems) {
		final ConcurrentHashMap<FileItem, ArrayList<?>> resultMap = new ConcurrentHashMap<FileItem, ArrayList<?>>();

		final String[] fileIds = new String[fileItems.size()];

		int idCounter = 0;
		for (final FileItem fileItem : fileItems) {
			fileIds[idCounter] = fileItem.getId().toString();
			idCounter++;
		}

		if ((fileItems != null) && fileItems.size() > 0) {

			ResultSet rs = null;
			final String sql = "SELECT fm.file_id AS 'fileId', mi.id, mi.name, mi.type, mi.uri, mi.options FROM mediaItems AS mi LEFT JOIN fileMedia AS fm ON fm.media_id = mi.id WHERE fm.file_id IN ("
					+ Helper.implode(fileIds, ",", "'", "'") + ")";
			try {
				Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
				rs = DB.query(sql);
				HashMap<String, Object> tempMap = null;
				final ResultSetMetaData rsmd = rs.getMetaData();
				Integer fileId = null;
				FileItem currentFileItem = null;
				for (; rs.next();) { // for each line
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					fileId = rs.getInt("fileId");

					// get fileitem
					for (final FileItem fileItem : fileItems) {
						if (fileItem.getId().equals(fileId)) {
							currentFileItem = fileItem;
							break;
						}
					}

					if (currentFileItem != null) {
						if (!resultMap.containsKey(currentFileItem)) {
							resultMap.put(currentFileItem, new ArrayList<MediaItem>());
						}

						((ArrayList<MediaItem>) resultMap.get(currentFileItem)).add((MediaItem) new MediaItem().fromHashMap(tempMap));
					}

				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}

		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void persistData() throws Exception {
		if (this.data.containsKey("mediaItems") && (this.data.get("mediaItems") != null)) {
			// create full list of media items
			ArrayList<MediaItem> tempMediaItems = new ArrayList<MediaItem>();
			for (final Map.Entry<FileItem, ArrayList<?>> entry : this.data.get("mediaItems").entrySet()) {
				if (entry.getValue() != null) {
					tempMediaItems.addAll((Collection<? extends MediaItem>) entry.getValue());
				}
			}
			// persist media items...
			MediaHandler.persistMediaItems(tempMediaItems);
			// get media items with id
			tempMediaItems = MediaHandler.getPersistedMediaItemsForMediaItems(tempMediaItems);

			// create full list of FileMediaItems
			final ArrayList<FileMediaItem> tempFileMediaItems = new ArrayList<FileMediaItem>();
			for (final Map.Entry<FileItem, ArrayList<?>> entry : this.data.get("mediaItems").entrySet()) {
				if ((entry != null) && (entry.getKey() != null) && (entry.getKey().getId() != null) && (entry.getValue() != null)) {
					for (final MediaItem mediaItem : (ArrayList<MediaItem>) entry.getValue()) {
						if (tempMediaItems.contains(mediaItem)) {
							tempFileMediaItems.add(new FileMediaItem(entry.getKey().getId(), tempMediaItems.get(
									tempMediaItems.indexOf(mediaItem)).getId()));
						}
					}
				}
			}

			// persist full list of FileMediaItems
			if ((tempFileMediaItems != null) && (tempFileMediaItems.size() > 0)) {
				MediaHandler.persistFileMediaItems(tempFileMediaItems);
			}

			this.data.get("mediaItems").clear();
		}
	}
}
