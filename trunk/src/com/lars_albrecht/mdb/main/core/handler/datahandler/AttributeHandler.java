/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler.datahandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.FileAttributeList;
import com.lars_albrecht.mdb.main.core.models.KeyValue;
import com.lars_albrecht.mdb.main.core.models.persistable.FileAttributes;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.Key;
import com.lars_albrecht.mdb.main.core.models.persistable.Value;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class AttributeHandler<E> extends ADataHandler<E> {

	public AttributeHandler() {
		this.data.put("keys", new ArrayList<Key<String>>());
		this.data.put("values", new ArrayList<Value<?>>());
		this.data.put("attributes", new ArrayList<FileAttributeList>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<E> getHandlerDataForFileItem(final FileItem fileItem) {
		final ArrayList<FileAttributeList> resultList = new ArrayList<FileAttributeList>();
		// ArrayList<KeyValue<Key<String>, Value<Object>>>
		HashMap<String, Object> tempMapKey = null;
		HashMap<String, Object> tempMapValue = null;
		ResultSet rs = null;
		final String sql = "SELECT "
				+ "	tiKey.id AS 'keyId', tiKey.Key AS 'keyKey', tiKey.infoType AS 'keyInfoType', tiKey.section AS 'keySection', tiKey.editable AS 'keyEditable', tiKey.searchable AS 'keySearchable', tiValue.id as 'valueId', tiValue.value as 'valueValue' "
				+ "FROM " + "	fileInformation as fi " + "LEFT JOIN " + " 	" + new FileAttributes().getDatabaseTable() + " as ti " + "ON "
				+ " 	ti.file_id = fi.id " + " LEFT JOIN " + " 	" + new Key<>().getDatabaseTable() + " AS tiKey " + "ON "
				+ " 	tiKey.id = ti.key_id " + "LEFT JOIN " + "	" + new Value<>().getDatabaseTable() + " AS tiValue " + "ON "
				+ "	tiValue.id = ti.value_id " + "WHERE " + "	fi.id = '" + fileItem.getId() + "' ORDER BY keyInfoType, keySection ";
		try {
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			rs = DB.query(sql);
			final ResultSetMetaData rsmd = rs.getMetaData();
			FileAttributeList tempFileAttributeList = null;
			for (; rs.next();) { // for each line
				KeyValue<String, Object> kv = null;
				tempMapKey = new HashMap<String, Object>();
				tempMapValue = new HashMap<String, Object>();
				String section = null;
				String infoType = null;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) { // for each
																	// column
					final String originalName = Helper.lcfirst(rsmd.getColumnLabel(i).replaceFirst("key", "").replaceFirst("value", ""));
					if (rsmd.getColumnLabel(i).startsWith("key")) {
						if (originalName.equals("section")) {
							section = rs.getString("keySection");
						} else if (originalName.equals("infoType")) {
							infoType = rs.getString("keyInfoType");
						}
						tempMapKey.put(originalName, rs.getObject(i));
					} else if (rsmd.getColumnLabel(i).startsWith("value")) {
						tempMapValue.put(originalName, rs.getObject(i));
					}
				}

				final Key<String> key = (Key<String>) (new Key<String>()).fromHashMap(tempMapKey);
				final Value<Object> value = ((Value<Object>) (new Value<Object>()).fromHashMap(tempMapValue));
				if ((key != null) && (value != null) && (value.getValue() != null)) {
					kv = new KeyValue<String, Object>(key, value);

					int index = -1;
					if ((index = this.indexOfSectionInFileAttributeList(resultList, section, infoType)) > -1) {
						tempFileAttributeList = resultList.get(index);
						tempFileAttributeList.getKeyValues().add(kv);
						resultList.set(index, tempFileAttributeList);
					} else {
						tempFileAttributeList = new FileAttributeList();
						tempFileAttributeList.setSectionName(section);
						tempFileAttributeList.setInfoType(infoType);
						tempFileAttributeList.getKeyValues().add(kv);
						resultList.add(tempFileAttributeList);
					}
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return (ArrayList<E>) resultList;
	}

	/**
	 * Returns the index of a section in a FileAttributeList.
	 * 
	 * @param fileAttribList
	 * @param sectionName
	 * @param infoType
	 * @return int
	 */
	private int indexOfSectionInFileAttributeList(final ArrayList<FileAttributeList> fileAttribList,
			final String sectionName,
			final String infoType) {
		if ((infoType != null) && (sectionName != null)) {
			for (final FileAttributeList fileAttributeListItem : fileAttribList) {
				if ((fileAttributeListItem != null) && (fileAttributeListItem.getSectionName() != null)) {
					if (fileAttributeListItem.getSectionName().equalsIgnoreCase(sectionName)
							&& (fileAttribList.indexOf(fileAttributeListItem) > -1)) {
						final int pos = fileAttribList.indexOf(fileAttributeListItem);
						if ((pos > -1) && (fileAttribList.get(pos) != null) && (fileAttribList.get(pos).getKeyValues() != null)
								&& (fileAttribList.get(pos).getKeyValues().size() > 0)
								&& (fileAttribList.get(pos).getKeyValues().get(0).getKey() != null)
								&& fileAttribList.get(pos).getKeyValues().get(0).getKey().getInfoType().equalsIgnoreCase(infoType)) {
							return pos;
						}
					}
				}
			}
		}

		return -1;
	}
}
