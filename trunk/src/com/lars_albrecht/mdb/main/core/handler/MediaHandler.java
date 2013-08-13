/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileMediaItem;
import com.lars_albrecht.mdb.main.core.models.persistable.MediaItem;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class MediaHandler {

	public static ArrayList<FileMediaItem> getFileMediaItemsForFile(final FileItem fileItem) {
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

	public static ArrayList<MediaItem> getMediaItemsForFile(final FileItem fileItem) {
		final ArrayList<MediaItem> resultList = new ArrayList<MediaItem>();
		if ((fileItem != null) && (fileItem.getId() != null) && (fileItem.getId() > -1)) {

			ResultSet rs = null;
			final String sql = "SELECT mi.id, mi.name, mi.type, mi.uri, mi.options FROM mediaItems AS mi LEFT JOIN fileMedia AS fm ON fm.media_id = mi.id WHERE fm.file_id = '"
					+ fileItem.getId() + "'";
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
		return resultList;
	}

	public static ArrayList<MediaItem> getPersistedMediaItemsForMediaItems(final ArrayList<MediaItem> mediaItems) {
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

	public static void persistFileMediaItems(final FileItem fileItem, final ArrayList<MediaItem> mediaItems) throws Exception {
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

	public static void persistMediaItems(final ArrayList<MediaItem> mediaItems) throws Exception {
		DataHandler.persist(mediaItems, false);
	}
}
