/**
 * 
 */
package com.lars_albrecht.mdb.core.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.core.controller.MainController;
import com.lars_albrecht.mdb.core.models.FileAttributeList;
import com.lars_albrecht.mdb.core.models.FileItem;
import com.lars_albrecht.mdb.core.models.IPersistable;
import com.lars_albrecht.mdb.core.models.Key;
import com.lars_albrecht.mdb.core.models.KeyValue;
import com.lars_albrecht.mdb.core.models.TypeInformation;
import com.lars_albrecht.mdb.core.models.Value;
import com.lars_albrecht.mdb.database.DB;

/**
 * @author lalbrecht
 * 
 * 
 *         TODO To support multiple databases, use a base class and create a
 *         class for each database type?
 * 
 *         TODO Build singleton
 * 
 */
public class DataHandler {

	private ArrayList<Key<?>>			keys				= null;
	private ArrayList<Value<?>>			values				= null;
	private ArrayList<FileItem>			fileItems			= null;
	private ArrayList<TypeInformation>	typeInformation		= null;

	public static final int				RELOAD_ALL			= 0;
	public static final int				RELOAD_KEYS			= 1;
	public static final int				RELOAD_VALUES		= 2;
	public static final int				RELOAD_TYPEINFO		= 3;
	public static final int				RELOAD_FILEITEMS	= 4;

	public DataHandler(final MainController mainController) {
		this.reloadData(DataHandler.RELOAD_ALL);
	}

	/**
	 * Currently unused.
	 * 
	 * @param fullpath
	 * @return boolean
	 */
	@Deprecated
	public boolean fileItemFullpathPersisted(final String fullpath) {
		boolean result = false;
		ResultSet rs = null;
		if (!this.fileItems.contains(new FileItem(fullpath))) {
			final String sql = "SELECT fi.id FROM fileInformation AS fi WHERE fi.fullpath = " + fullpath + " LIMIT 1";
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				if (rs.next()) {
					result = true;
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public ArrayList<String> findAllValuesForKeyWithValuePart(final String key, final String valuePart) {
		final ArrayList<String> resultList = new ArrayList<String>();

		if (!key.equalsIgnoreCase("")) {
			ResultSet rs = null;

			final String sql = "SELECT DISTINCT value.value AS value FROM typeInformation_key AS key "
					+ "LEFT JOIN typeInformation AS ti ON ti.key_id = key.id "
					+ "LEFT JOIN typeInformation_value AS value ON value.id = ti.value_id " + "WHERE key.key = ? AND value LIKE ?";

			final ConcurrentHashMap<Integer, Object> values = new ConcurrentHashMap<Integer, Object>();
			values.put(1, key);
			values.put(2, valuePart + "%");
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.queryPS(sql, values);
				for (; rs.next();) {
					resultList.add(rs.getString("value"));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		return resultList;
	}

	public ArrayList<String> findAllValuesForKey(final String key) {
		final ArrayList<String> resultList = new ArrayList<String>();

		if (!key.equalsIgnoreCase("")) {
			ResultSet rs = null;

			final String sql = "SELECT DISTINCT value.value AS value FROM typeInformation_key AS key "
					+ "LEFT JOIN typeInformation AS ti ON ti.key_id = key.id "
					+ "LEFT JOIN typeInformation_value AS value ON value.id = ti.value_id " + "WHERE key.key = ?";

			final ConcurrentHashMap<Integer, Object> values = new ConcurrentHashMap<Integer, Object>();
			values.put(1, key);
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.queryPS(sql, values);
				for (; rs.next();) {
					resultList.add(rs.getString("value"));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		return resultList;
	}

	/**
	 * Finds all items of the given IPersistable object in database and return a
	 * list of them.
	 * 
	 * @param object
	 * @param limit
	 * @param order
	 * @return ArrayList<Object>
	 */
	public ArrayList<Object> findAll(final IPersistable object, final Integer limit, final String order) {
		HashMap<String, Object> tempMap = null;
		final ArrayList<Object> resultList = new ArrayList<Object>();
		if (object != null) {
			String where = "";
			String limitStr = "";
			ResultSet rs = null;
			if (object.getId() != null) {
				where = " WHERE id=" + object.getId();
			}
			if ((limit != null) && (limit > 0)) {
				limitStr = " LIMIT " + limit;
			}

			final String sql = "SELECT * FROM " + object.getDatabaseTable() + where + (order != null ? order : "") + limitStr;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) {
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					resultList.add(object.fromHashMap(tempMap));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return resultList;
	}

	public ArrayList<FileItem> findAllByFileItemValue(final String fileItemValue) {
		final FileItem fileItem = new FileItem();
		HashMap<String, Object> tempMap = null;
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();
		if (fileItem != null) {
			String where = "";
			ResultSet rs = null;
			where = " WHERE ((name || fullpath || dir || size || ext ) LIKE '%" + fileItemValue + "%')";

			final String sql = "SELECT * FROM " + fileItem.getDatabaseTable() + where;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) {
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					resultList.add((FileItem) fileItem.fromHashMap(tempMap));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return resultList;
	}

	public void updateUpdateTSForFileItem(final Integer id) {
		if (id != null && id > 0) {
			final String sql = "UPDATE fileInformation SET updateTS = (datetime('now','localtime')) WHERE id = ?";
			final ConcurrentHashMap<Integer, Object> values = new ConcurrentHashMap<Integer, Object>();
			values.put(1, id);
			try {
				DB.updatePS(sql, values);
			} catch (final SQLException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns a list of FileItems which are searched by the search term
	 * searchStr.
	 * 
	 * @param searchStr
	 *            String
	 * @return ArrayList<FileItem>
	 */
	public ArrayList<FileItem> findAllFileItemForStringInAll(final String searchStr) {
		ArrayList<FileItem> tempList = null;
		tempList = this.findAllByFileItemValue(searchStr);

		final FileItem fileItem = new FileItem();
		HashMap<String, Object> tempMap = null;
		if (fileItem != null) {
			String where = "";
			ResultSet rs = null;
			where = " WHERE (tiValue.value LIKE '%" + searchStr + "%')";

			String searchResultOrderOption = (String) OptionsHandler.getOption("searchResultOrder");
			if (searchResultOrderOption == null) {
				searchResultOrderOption = "fileInformation.name";
				OptionsHandler.setOption("searchResultOrder", searchResultOrderOption);
			}

			final String order = " ORDER BY '" + searchResultOrderOption + "'";

			final String sql = "SELECT fi.* FROM '" + fileItem.getDatabaseTable() + "' AS fi LEFT JOIN " + " 	typeInformation as ti "
					+ "ON " + " 	ti.file_id = fi.id " + " LEFT JOIN " + " 	typeInformation_key AS tiKey " + "ON "
					+ " 	tiKey.id = ti.key_id " + "LEFT JOIN " + "	typeInformation_value AS tiValue " + "ON "
					+ "	tiValue.id = ti.value_id " + where + order;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) {
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					tempList.add((FileItem) fileItem.fromHashMap(tempMap));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return tempList;
	}

	/**
	 * Returns a list of FileItems which are searched by the search term
	 * searchStr.
	 * 
	 * @param key
	 * @param value
	 * @return ArrayList<Object>
	 */
	public ArrayList<FileItem> findAllFileItemForStringInAttributesByKeyValue(final String key, final String value) {
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();

		if (key != null && value != null) {
			final FileItem fileItem = new FileItem();
			HashMap<String, Object> tempMap = null;
			if (fileItem != null) {
				String where = "";
				ResultSet rs = null;
				where = " WHERE (tiValue.value LIKE '%" + value + "%' AND tiKey.key LIKE '%" + key + "%' )";

				String searchResultOrderOption = (String) OptionsHandler.getOption("searchResultOrder");
				if (searchResultOrderOption == null) {
					searchResultOrderOption = "fileInformation.name";
					OptionsHandler.setOption("searchResultOrder", searchResultOrderOption);
				}

				final String order = " ORDER BY '" + searchResultOrderOption + "'";

				final String sql = "SELECT fi.* FROM '" + fileItem.getDatabaseTable() + "' AS fi LEFT JOIN " + " 	typeInformation as ti "
						+ "ON " + " 	ti.file_id = fi.id " + " LEFT JOIN " + " 	typeInformation_key AS tiKey " + "ON "
						+ " 	tiKey.id = ti.key_id " + "LEFT JOIN " + "	typeInformation_value AS tiValue " + "ON "
						+ "	tiValue.id = ti.value_id " + where + order;
				Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
				try {
					rs = DB.query(sql);
					final ResultSetMetaData rsmd = rs.getMetaData();
					for (; rs.next();) {
						tempMap = new HashMap<String, Object>();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
						}
						resultList.add((FileItem) fileItem.fromHashMap(tempMap));
					}
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultList;
	}

	/**
	 * Finds all information for a fileId and returns a FileItem.
	 * 
	 * @param fileId
	 * @return FileItem
	 */
	public FileItem findAllInfoForAllByFileId(final Integer fileId) {
		HashMap<String, Object> tempMap = null;
		FileItem resultItem = new FileItem();
		if (fileId != null) {
			String where = "";
			ResultSet rs = null;
			where = " WHERE id = '" + fileId + "'";

			final String sql = "SELECT * FROM " + resultItem.getDatabaseTable() + where;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) {
					tempMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					resultItem = (FileItem) resultItem.fromHashMap(tempMap);
					if (resultItem.getId() != null) {
						final ArrayList<FileAttributeList> attribList = this.findAllInfoForId(fileId);
						if ((attribList != null) && (attribList.size() > 0)) {
							resultItem.getAttributes().addAll(attribList);
						}
					}
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return resultItem;
	}

	public ArrayList<FileItem> findAllWithNoInfosForType(final String type) {
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();

		return resultList;
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public ArrayList<FileAttributeList> findAllInfoForId(final Integer id) {
		final ArrayList<FileAttributeList> resultList = new ArrayList<FileAttributeList>();
		// ArrayList<KeyValue<Key<String>, Value<Object>>>
		HashMap<String, Object> tempMapKey = null;
		HashMap<String, Object> tempMapValue = null;
		ResultSet rs = null;
		final String sql = "SELECT "
				+ "	tiKey.id AS 'keyId', tiKey.Key AS 'keyKey', tiKey.infoType AS 'keyInfoType', tiKey.section AS 'keySection', tiKey.editable AS 'keyEditable', tiKey.searchable AS 'keySearchable', tiValue.id as 'valueId', tiValue.value as 'valueValue' "
				+ "FROM " + "	fileInformation as fi " + "LEFT JOIN " + " 	typeInformation as ti " + "ON " + " 	ti.file_id = fi.id "
				+ " LEFT JOIN " + " 	typeInformation_key AS tiKey " + "ON " + " 	tiKey.id = ti.key_id " + "LEFT JOIN "
				+ "	typeInformation_value AS tiValue " + "ON " + "	tiValue.id = ti.value_id " + "WHERE " + "	fi.id = '" + id
				+ "' ORDER BY keyInfoType, keySection ";
		try {
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			rs = DB.query(sql);
			final ResultSetMetaData rsmd = rs.getMetaData();
			FileAttributeList tempFileAttributeList = null;
			for (; rs.next();) { // for each line
				KeyValue kv = null;
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
						tempFileAttributeList.getKeyValues().add(kv);
						resultList.add(tempFileAttributeList);
					}
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		// System.exit(-1);
		return resultList;
	}

	/**
	 * @return the fileItems
	 */
	public ArrayList<FileItem> getFileItems() {
		if (this.fileItems == null) {
			this.loadFileItems();
		}
		return this.fileItems;
	}

	public ConcurrentHashMap<String, Object> getInfoFromDatabase() {
		final ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();

		resultMap.put("fileCount", this.getRowCount(new FileItem()));
		resultMap.put("keyCount", this.getRowCount(new Key<Object>()));
		resultMap.put("valueCount", this.getRowCount(new Value<Object>()));
		resultMap.put("filetypes", this.getFiletypesFromDatabase());
		resultMap.put("filesWithFiletype", this.getFilesWithFiletypeFromDatabase());

		return resultMap;
	}

	public ConcurrentHashMap<String, Integer> getFilesWithFiletypeFromDatabase() {

		final ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<String, Integer>();
		ResultSet rs = null;
		final String sql = "SELECT fi.filetype AS filetype, COUNT(fi.filetype) AS countOfFiles FROM " + new FileItem().getDatabaseTable()
				+ " AS fi GROUP BY filetype";

		try {
			rs = DB.query(sql);
			while (rs.next()) {
				result.put(rs.getString("filetype"), rs.getInt("countOfFiles"));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<String> getFiletypesFromDatabase() {
		final ArrayList<String> result = new ArrayList<String>();
		ResultSet rs = null;
		final String sql = "SELECT fi.filetype AS filetype FROM " + new FileItem().getDatabaseTable() + " AS fi GROUP BY filetype";

		try {
			rs = DB.query(sql);
			while (rs.next()) {
				result.add(rs.getString("filetype"));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @return the keys
	 */
	public ArrayList<Key<?>> getKeys() {
		if (this.keys == null) {
			this.loadKeys();
		}
		return this.keys;
	}

	public Integer getRowCount(final IPersistable object) {
		Integer result = null;
		ResultSet rs = null;
		final String sql = "SELECT COUNT(id) AS count FROM " + object.getDatabaseTable();

		try {
			rs = DB.query(sql);
			if (rs.next()) {
				result = rs.getInt("count");
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @return the typeInformation
	 */
	public ArrayList<TypeInformation> getTypeInformation() {
		return this.typeInformation;
	}

	/**
	 * @return the values
	 */
	public ArrayList<Value<?>> getValues() {
		return this.values;
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

	/**
	 * Returns the position of a key in this.keys.
	 * 
	 * @param searchKey
	 * @return Integer
	 * @deprecated
	 */
	@Deprecated
	public Integer keyPos(final Key<?> searchKey) {
		for (final Key<?> key : this.getKeys()) {
			if ((key.getKey() == searchKey.getKey()) && (key.getSection() == searchKey.getSection())
					&& (key.getInfoType() == searchKey.getInfoType())) {
				return key.getId();
			}
		}
		return null;
	}

	/**
	 * Reload the fileitems.
	 * 
	 * @param withArguments
	 * @return
	 */
	private DataHandler loadFileItems() {
		this.fileItems = ObjectHandler.castObjectListToFileItemList(this.findAll(new FileItem(), null, null));
		return this;
	}

	/**
	 * Reload the keys.
	 * 
	 * @return
	 */
	private DataHandler loadKeys() {
		this.keys = ObjectHandler.castObjectListToKeyList(this.findAll(new Key<String>(), null, null));
		return this;
	}

	/**
	 * Reload the typeInformation.
	 * 
	 * @return
	 */
	private DataHandler loadTypeInformation() {
		this.typeInformation = ObjectHandler.castObjectListToTypeInformationList(this.findAll(new TypeInformation(), null, null));
		return this;
	}

	/**
	 * Reload the values.
	 * 
	 * @return
	 */
	private DataHandler loadValues() {
		this.values = ObjectHandler.castObjectListToValueList(this.findAll(new Value<Object>(), null, null));
		return this;
	}

	/**
	 * Persist a list of IPersistable's into the database using the IPersistable
	 * interface. Insert not more than 500 items at time, because SQLite has a
	 * limit of 500 (http://sqlite.org/limits.html @ point 7).
	 * 
	 * @see "http://sqlite.org/limits.html"
	 * 
	 * @param objects
	 * @throws Exception
	 */
	public void persist(final ArrayList<?> objects) throws Exception {
		if ((objects != null) && (objects.size() > 0)) {

			final IPersistable tempPersistable = (IPersistable) objects.get(0);
			final String insertStr = "INSERT OR IGNORE INTO '"
					+ tempPersistable.getDatabaseTable()
					+ "' ("
					+ Helper.implode(tempPersistable.toHashMap().keySet(), ",", "" + (DB.useQuotesForFields ? "'" : "") + "", ""
							+ (DB.useQuotesForFields ? "'" : "") + "") + ")";

			final LinkedHashMap<Integer, Object> insertValues = new LinkedHashMap<Integer, Object>();
			boolean isFirst = true;
			Map.Entry<String, LinkedHashMap<Integer, Object>> insertItem = null;
			String sql = insertStr;

			// max count for sqlite inserts
			final int maxObjectCount = 500;
			int objectItemCount = 0;
			// max count for sqlite items
			final int maxVariables = 500;
			int variablesCount = 0;

			// contains count of items which have are null, key = null and where
			// key = ""
			int missedItems = 0;
			for (final Object object : objects) {
				insertItem = this.generateSQLiteMultiInsertItem((IPersistable) object, isFirst,
						insertValues.size() > 0 ? insertValues.size() + 1 : 1);
				if (insertItem != null && insertItem.getKey() != null && !insertItem.getKey().equalsIgnoreCase("")) {
					if (objectItemCount == maxObjectCount || (variablesCount + insertItem.getValue().size()) >= maxVariables) {
						DB.updatePS(sql, insertValues);
						objectItemCount = 0;
						variablesCount = 0;
						sql = insertStr;
						insertValues.clear();
						isFirst = true;
					} else {
						objectItemCount++;
						variablesCount += insertItem.getValue().size();
						sql += insertItem.getKey();
						insertValues.putAll(insertItem.getValue());
						isFirst = false;
					}
					if (objects.indexOf(object) == (objects.size() - 1 + missedItems)) {
						DB.updatePS(sql, insertValues);
					}

				} else {
					missedItems++;
				}
			}

		}
	}

	public ConcurrentHashMap<String, ArrayList<FileItem>> getAllFileItemsWithNoCollectorinfo() {
		final ConcurrentHashMap<String, ArrayList<FileItem>> resultList = new ConcurrentHashMap<String, ArrayList<FileItem>>();
		final FileItem fileItem = new FileItem();
		HashMap<String, Object> tempMap = null;
		if (fileItem != null) {
			// TODO move to helper function "getSearchresultOrder" or something
			// like that
			String searchResultOrderOption = (String) OptionsHandler.getOption("searchResultOrder");
			if (searchResultOrderOption == null) {
				searchResultOrderOption = "fileInformation.name";
				OptionsHandler.setOption("searchResultOrder", searchResultOrderOption);
			}

			final String order = " ORDER BY '" + searchResultOrderOption + "'";

			String where = "";
			ResultSet rs = null;
			where = " WHERE key = 'noinformation'";
			final String sql = "SELECT ci.collectorName AS collectorName, fi.* FROM " + fileItem.getDatabaseTable()
					+ " AS fi INNER JOIN collectorInformation AS ci ON fi.id = ci.file_id " + where + order;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + sql);
			try {
				rs = DB.query(sql);
				final ResultSetMetaData rsmd = rs.getMetaData();
				for (; rs.next();) {
					// TODO FIX THE SAME ITEMS LIST
					tempMap = new HashMap<String, Object>();
					for (int i = 2; i <= rsmd.getColumnCount(); i++) {
						tempMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					if (!resultList.containsKey(rs.getString("collectorName"))) {
						resultList.put(rs.getString("collectorName"), new ArrayList<FileItem>());
					}
					resultList.get(rs.getString("collectorName")).add((FileItem) fileItem.fromHashMap(tempMap));
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}

		return resultList;
	}

	/**
	 * Returns a Map.Entry<String, LinkedHashMap<Integer, Object>> with the sql
	 * part as Key and the value-set as value.
	 * 
	 * @param object
	 * @param isFirst
	 * @param valueStartIndex
	 * @return Map.Entry<String, LinkedHashMap<Integer, Object>>
	 * @throws Exception
	 */
	private Map.Entry<String, LinkedHashMap<Integer, Object>> generateSQLiteMultiInsertItem(final IPersistable object,
			final boolean isFirst,
			final int valueStartIndex) throws Exception {
		Map.Entry<String, LinkedHashMap<Integer, Object>> resultEntry = null;
		final LinkedHashMap<Integer, Object> resultValues = new LinkedHashMap<Integer, Object>();

		String valueStr = null;

		final HashMap<String, Object> tempObject = object.toHashMap();
		int i = valueStartIndex > 0 ? valueStartIndex : 1;
		valueStr = isFirst ? " SELECT " : " UNION ALL SELECT ";
		for (final Map.Entry<String, Object> entry : tempObject.entrySet()) {

			Object x = null;
			if (entry.getValue() == null) {
				x = "";
			} else {
				x = entry.getValue();
			}
			resultValues.put(i, x);

			if (i != valueStartIndex) {
				valueStr += ",";
			}
			valueStr += "?";

			i++;
		}

		if (valueStr != null && resultValues.size() > 0) {
			resultEntry = new AbstractMap.SimpleEntry<String, LinkedHashMap<Integer, Object>>(valueStr, resultValues);
		}
		return resultEntry;
	}

	/**
	 * Reload the data from the database to the current object stack to handle
	 * the objects fast and to reduce database selects.
	 * 
	 * @param reloadType
	 */
	public void reloadData(final int reloadType) {
		Debug.startTimer("DataHandler reloadData time");
		this.loadKeys();
		this.loadValues();
		this.loadTypeInformation();
		this.loadFileItems();
		Debug.stopTimer("DataHandler reloadData time");
	}

	/**
	 * Search key in this.keys. This method ignore the infoType/section
	 * parameters.
	 * 
	 * @param key
	 * @return boolean
	 */
	public boolean isKeyInKeyList(final String key) {
		for (final Key<?> thisKey : this.keys) {
			if (((String) thisKey.getKey()).equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

}