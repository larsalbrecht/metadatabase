/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler.datahandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.MediaItem;
import com.lars_albrecht.mdb.main.core.models.persistable.Tag;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class TagHandler<E> extends ADataHandler<E> {

	public TagHandler() {
		this.data.put("tags", new ConcurrentHashMap<FileItem, ArrayList<?>>());
		// this.data.put("tags", new ArrayList<FileTag>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<FileTag> getHandlerDataForFileItem(final FileItem fileItem) {
		final ArrayList<FileItem> fileItems = new ArrayList<FileItem>();
		fileItems.add(fileItem);
		return (ArrayList<FileTag>) this.getHandlerDataForFileItems(fileItems).get(fileItem);
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

		if ((fileItems != null) && (fileItems.size() > 0)) {

			ResultSet rs = null;
			final String sql = "SELECT "
					+ " fi.id AS 'fileId', tag.id AS tagId, tag.name AS 'tagName', tag.isuser AS 'tagIsUser', fTag.id AS 'fileTagId', fTag.isuser AS 'fileTagIsUser' "
					+ "FROM " + "	fileInformation as fi " + "LEFT JOIN " + " fileTags as fTag " + "ON " + " fi.id = fTag.file_id "
					+ " LEFT JOIN " + " 	tags AS tag " + "ON " + " 	tag.id = fTag.tag_id " + "WHERE " + "	fi.id IN ("
					+ Helper.implode(fileIds, ",", "'", "'") + ")" + " ORDER BY tag.name, fTag.isuser ";

			try {
				Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
				rs = DB.query(sql);
				Integer fileId = null;
				FileItem currentFileItem = null;
				for (; rs.next();) { // for each line
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

						((ArrayList<FileTag>) resultMap.get(currentFileItem)).add(new FileTag(rs.getInt("fileTagId"), fileId, new Tag(rs
								.getInt("tagId"), rs.getString("tagName"), rs.getBoolean("tagIsUser")), rs.getBoolean("fileTagIsUser")));
					}

				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}

		}
		return resultMap;
	}

	@Override
	protected void persistData() throws Exception {
		// TODO fill persistData with live
	}

}
