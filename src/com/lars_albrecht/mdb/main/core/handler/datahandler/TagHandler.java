/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler.datahandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.Tag;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class TagHandler<E> extends ADataHandler<E> {

	public TagHandler() {
		this.data.put("tags", new ArrayList<FileTag>());
	}

	@Override
	public ArrayList<FileTag> getHandlerDataForFileItem(final FileItem fileItem) {
		final ArrayList<FileTag> resultList = new ArrayList<FileTag>();
		// ArrayList<KeyValue<Key<String>, Value<Object>>>
		ResultSet rs = null;
		final String sql = "SELECT "
				+ " tag.id AS tagId, tag.name AS 'tagName', tag.isuser AS 'tagIsUser', fTag.id AS 'fileTagId', fTag.isuser AS 'fileTagIsUser' "
				+ "FROM " + "	fileInformation as fi " + "LEFT JOIN " + " fileTags as fTag " + "ON " + " fi.id = fTag.file_id "
				+ " LEFT JOIN " + " 	tags AS tag " + "ON " + " 	tag.id = fTag.tag_id " + "WHERE " + "	fi.id = '" + fileItem.getId()
				+ "' ORDER BY tag.name, fTag.isuser ";
		try {
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			rs = DB.query(sql);
			FileTag tempFileTag = null;
			for (; rs.next();) { // for each line

				if ((rs.getInt("fileTagId") > 0) && (rs.getInt("tagId") > 0)) {
					tempFileTag = new FileTag(rs.getInt("fileTagId"), fileItem.getId(), new Tag(rs.getInt("tagId"),
							rs.getString("tagName"), rs.getBoolean("tagIsUser")), rs.getBoolean("fileTagIsUser"));
					resultList.add(tempFileTag);
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
