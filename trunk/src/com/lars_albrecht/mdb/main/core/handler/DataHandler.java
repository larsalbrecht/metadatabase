/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;
import com.lars_albrecht.mdb.main.core.models.persistable.FileAttributes;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.Key;
import com.lars_albrecht.mdb.main.core.models.persistable.Tag;
import com.lars_albrecht.mdb.main.core.models.persistable.Value;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 * 
 *         TODO To support multiple databases, use a base class and create a
 *         class for each database type?
 * 
 *         TODO Build singleton?
 * 
 */
public class DataHandler {

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
	private static Map.Entry<String, LinkedHashMap<Integer, Object>> generateSQLiteMultiInsertItem(final IPersistable object,
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

		if ((valueStr != null) && (resultValues.size() > 0)) {
			resultEntry = new AbstractMap.SimpleEntry<String, LinkedHashMap<Integer, Object>>(valueStr, resultValues);
		}
		return resultEntry;
	}

	private ArrayList<Key<?>>								keys					= null;
	private ArrayList<Value<?>>								values					= null;
	private ArrayList<FileItem>								fileItems				= null;
	private ArrayList<FileAttributes>						fileAttributes			= null;
	private ArrayList<Tag>									tags					= null;
	private ArrayList<FileTag>								fileTags				= null;
	private ConcurrentHashMap<String, ArrayList<FileItem>>	noInfoFileItems			= null;
	private ArrayList<FileItem>								missingFileItems		= null;

	private ArrayList<FileItem>								newFileItems			= null;
	public static final int									RELOAD_ALL				= 0;
	public static final int									RELOAD_KEYS				= 1;
	public static final int									RELOAD_VALUES			= 2;
	public static final int									RELOAD_FILEATTRIBUTES	= 3;
	public static final int									RELOAD_FILEITEMS		= 4;
	public static final int									RELOAD_NOINFOFILEITEMS	= 5;
	public static final int									RELOAD_MISSINGFILEITEMS	= 6;
	public static final int									RELOAD_TAGS				= 7;

	public static final int									RELOAD_FILETAGS			= 8;
	public static final int									FILEITEMSTATUS_NORMAL	= 0;

	public static final int									FILEITEMSTATUS_MISSING	= 1;

	/**
	 * Persist a list of IPersistable's into the database using the IPersistable
	 * interface. Insert not more than 500 items at time, because SQLite has a
	 * limit of 500 (http://sqlite.org/limits.html @ point 7).
	 * 
	 * Use "doReplace" to replace (REPLACE INTO) an existing item instead of
	 * insert (INSERT OR IGNORE) the item.
	 * 
	 * @see "http://sqlite.org/limits.html"
	 * 
	 * @param objects
	 * @param doReplace
	 * @throws Exception
	 */
	public static void persist(final ArrayList<?> objects, final boolean doReplace) throws Exception {
		if ((objects != null) && (objects.size() > 0)) {

			final IPersistable tempPersistable = (IPersistable) objects.get(0);
			String insertReplaceStr = null;

			insertReplaceStr = (doReplace ? "REPLACE INTO " : "INSERT OR IGNORE INTO ");
			insertReplaceStr += " '"
					+ tempPersistable.getDatabaseTable()
					+ "' ("
					+ Helper.implode(tempPersistable.toHashMap().keySet(), ",", "" + (DB.useQuotesForFields ? "'" : "") + "", ""
							+ (DB.useQuotesForFields ? "'" : "") + "") + ")";

			final LinkedHashMap<Integer, Object> insertValues = new LinkedHashMap<Integer, Object>();
			boolean isFirst = true;
			Map.Entry<String, LinkedHashMap<Integer, Object>> insertItem = null;
			String sql = insertReplaceStr;

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
				insertItem = DataHandler.generateSQLiteMultiInsertItem((IPersistable) object, isFirst,
						insertValues.size() > 0 ? insertValues.size() + 1 : 1);
				if ((insertItem != null) && (insertItem.getKey() != null) && !insertItem.getKey().equalsIgnoreCase("")) {
					if ((objectItemCount == maxObjectCount) || ((variablesCount + insertItem.getValue().size()) >= maxVariables)) {
						DB.updatePS(sql, insertValues);
						objectItemCount = 0;
						variablesCount = 0;
						sql = insertReplaceStr;
						insertValues.clear();
						isFirst = true;
					} else {
						objectItemCount++;
						variablesCount += insertItem.getValue().size();
						sql += insertItem.getKey();
						insertValues.putAll(insertItem.getValue());
						isFirst = false;
					}
					if (objects.indexOf(object) == ((objects.size() - 1) + missedItems)) {
						DB.updatePS(sql, insertValues);
					}

				} else {
					missedItems++;
				}
			}

		}
	}

	public DataHandler(final MainController mainController) {
		this.newFileItems = new ArrayList<FileItem>();
		this.reloadData(DataHandler.RELOAD_ALL);
	}

	public Integer addFileTag(final FileTag fileTag) throws Exception {
		this.reloadData(DataHandler.RELOAD_FILETAGS);
		if ((fileTag != null) && (fileTag.getTag() != null) && (fileTag.getFileId() != null)) {
			if (!this.getFileTags().contains(fileTag)) {
				if (!this.getTags().contains(fileTag.getTag())) {
					this.addTag(fileTag.getTag());
				}
				fileTag.setTag(this.tags.get(this.tags.indexOf(fileTag.getTag())));
				this.persist(fileTag, false);
				this.reloadData(DataHandler.RELOAD_FILETAGS);
			}
			return this.fileTags.get(this.fileTags.indexOf(fileTag)).getId();
		}
		return null;

	}

	public Integer addTag(final Tag tag) throws Exception {
		if (tag != null) {
			if (!this.getTags().contains(tag)) {
				this.persist(tag, false);
				this.reloadData(DataHandler.RELOAD_TAGS);
			}
			return this.tags.get(this.tags.indexOf(tag)).getId();
		}
		return null;
	}

	public void clearNoInfoFileItems(final String infoType) {
		final String sql = "DELETE FROM collectorInformation WHERE key = 'noinformation' AND collectorName = '" + infoType + "'";

		try {
			DB.update(sql);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
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
			where = " WHERE ((tiValue.value LIKE '%" + searchStr + "%') OR (t.name LIKE '%" + searchStr + "%'))";

			String searchResultOrderOption = (String) OptionsHandler.getOption("searchResultOrder");
			if (searchResultOrderOption == null) {
				searchResultOrderOption = "fileInformation.name";
				OptionsHandler.setOption("searchResultOrder", searchResultOrderOption);
			}

			final String order = " ORDER BY '" + searchResultOrderOption + "'";

			final String sql = "SELECT fi.* FROM '" + fileItem.getDatabaseTable() + "' AS fi LEFT JOIN " + " "
					+ new FileAttributes().getDatabaseTable() + " as ti " + "ON " + " 	ti.file_id = fi.id " + " LEFT JOIN " + " 	"
					+ new Key<>().getDatabaseTable() + " AS tiKey " + "ON " + " 	tiKey.id = ti.key_id " + "LEFT JOIN " + "	"
					+ new Value<>().getDatabaseTable() + " AS tiValue " + "ON " + "	tiValue.id = ti.value_id "
					+ "LEFT JOIN fileTags AS ft ON fi.id = ft.file_id " + "LEFT JOIN tags AS t ON ft.tag_id = t.id " + where + order;
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

		if ((key != null) && (value != null)) {
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

				final String sql = "SELECT fi.* FROM '" + fileItem.getDatabaseTable() + "' AS fi LEFT JOIN " + " 	"
						+ new FileAttributes().getDatabaseTable() + " as ti " + "ON " + " 	ti.file_id = fi.id " + " LEFT JOIN " + " 	"
						+ new Key<>().getDatabaseTable() + " AS tiKey " + "ON " + " 	tiKey.id = ti.key_id " + "LEFT JOIN " + "	"
						+ new Value<>().getDatabaseTable() + " AS tiValue " + "ON " + "	tiValue.id = ti.value_id " + where + order;
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

					// load data from handlers
					if (resultItem.getId() != null) {
						for (final ADataHandler<?> dataHandler : ADataHandler.getDataHandlers()) {
							dataHandler.setHandlerDataToFileItem(resultItem, dataHandler.getHandlerDataForFileItem(resultItem));
						}
					}
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return resultItem;
	}

	public ArrayList<String> findAllValuesForKey(final String key) {
		final ArrayList<String> resultList = new ArrayList<String>();

		if (!key.equalsIgnoreCase("")) {
			ResultSet rs = null;

			final String sql = "SELECT DISTINCT value.value AS value FROM " + new Key<>().getDatabaseTable() + " AS key " + "LEFT JOIN "
					+ new FileAttributes().getDatabaseTable() + " AS ti ON ti.key_id = key.id " + "LEFT JOIN "
					+ new Value<>().getDatabaseTable() + " AS value ON value.id = ti.value_id " + "WHERE key.key = ?";

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

	public ArrayList<String> findAllValuesForKeyWithValuePart(final String key, final String valuePart) {
		final ArrayList<String> resultList = new ArrayList<String>();

		if (!key.equalsIgnoreCase("")) {
			ResultSet rs = null;

			final String sql = "SELECT DISTINCT value.value AS value FROM attributes_key AS key "
					+ "LEFT JOIN fileAttributes AS ti ON ti.key_id = key.id "
					+ "LEFT JOIN attributes_value AS value ON value.id = ti.value_id " + "WHERE key.key = ? AND value LIKE ?";

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

	private ConcurrentHashMap<String, ArrayList<FileItem>> getAllFileItemsWithNoCollectorinfo() {
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

	private ArrayList<FileItem> getAllMissingFileItems() {
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();
		HashMap<String, Object> tempMap = null;
		final FileItem fileItem = new FileItem();
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
			where = " WHERE status = '1'";
			final String sql = "SELECT * FROM " + fileItem.getDatabaseTable() + where + order;
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

	public ConcurrentHashMap<String, Integer> getAssociatedFileTags() {
		final ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<String, Integer>();
		ResultSet rs = null;

		final String sql = "SELECT tags.name AS tagName, COUNT(ft.id) AS fileCount FROM tags AS tags LEFT JOIN fileTags AS ft ON ft.tag_id = tags.id GROUP BY tags.name";
		try {
			rs = DB.query(sql);
			while (rs.next()) {
				result.put(rs.getString("filetype"), rs.getInt("fileCount"));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @return the fileAttributes
	 */
	public ArrayList<FileAttributes> getFileAttributes() {
		return this.fileAttributes;
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

	public ArrayList<FileItem> getFileItemsForPaging(final int startIndex, final int maxElems, final String orderBy) {
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();
		HashMap<String, Object> tempMap = null;
		final FileItem fileItem = new FileItem();
		if (fileItem != null) {
			final String order = (orderBy == null ? "" : " ORDER BY " + orderBy);
			final String limit = (maxElems > 0 ? " LIMIT " + startIndex + ", " + maxElems : "");
			ResultSet rs = null;
			final String sql = "SELECT * FROM " + fileItem.getDatabaseTable() + order + limit;
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

	/**
	 * @return the fileTags
	 */
	public final ArrayList<FileTag> getFileTags() {
		if (this.fileTags == null) {
			this.loadFileTags();
		}
		return this.fileTags;
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

	public ConcurrentHashMap<String, Object> getInfoFromDatabase() {
		final ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();

		resultMap.put("fileCount", this.getRowCount(new FileItem()));
		resultMap.put("keyCount", this.getRowCount(new Key<Object>()));
		resultMap.put("valueCount", this.getRowCount(new Value<Object>()));
		resultMap.put("filetypes", this.getFiletypesFromDatabase());
		resultMap.put("filesWithFiletype", this.getFilesWithFiletypeFromDatabase());
		resultMap.put("missingCount", this.getMissingFileItems().size());
		resultMap.put("tagCount", this.getRowCount(new Tag()));
		resultMap.put("fileTagCount", this.getRowCount(new FileTag()));

		return resultMap;
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

	/**
	 * 
	 * @return the missingFileItems
	 */
	public ArrayList<FileItem> getMissingFileItems() {
		if (this.missingFileItems == null) {
			this.loadMissingFileItems();
		}
		return this.missingFileItems;
	}

	/**
	 * @return the newFileItems
	 */
	public final ArrayList<FileItem> getNewFileItems() {
		return this.newFileItems;
	}

	/**
	 * @return the noInfoFileItems
	 */
	public ConcurrentHashMap<String, ArrayList<FileItem>> getNoInfoFileItems(final String infoType) {
		if (this.noInfoFileItems == null) {
			this.loadNoInfoFileItems();
		}
		if (infoType == null) {
			return this.noInfoFileItems;
		} else {
			final ConcurrentHashMap<String, ArrayList<FileItem>> tempList = new ConcurrentHashMap<String, ArrayList<FileItem>>();
			for (final Entry<String, ArrayList<FileItem>> entry : this.noInfoFileItems.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(infoType)) {
					tempList.put(entry.getKey(), entry.getValue());
				}
			}
			return tempList;
		}
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
	 * @return the tags
	 */
	public final ArrayList<Tag> getTags() {
		if (this.tags == null) {
			this.loadTags();
		}
		return this.tags;
	}

	/**
	 * @return the values
	 */
	public ArrayList<Value<?>> getValues() {
		return this.values;
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
	 * Reload the fileAttributes.
	 * 
	 * @return
	 */
	private DataHandler loadFileAttributes() {
		this.fileAttributes = ObjectHandler.castObjectListToFileAttributesList(this.findAll(new FileAttributes(), null, null));
		return this;
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

	private DataHandler loadFileTags() {
		this.fileTags = ObjectHandler.castObjectListToFileTagList(this.findAll(new FileTag(), null, null));
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

	private DataHandler loadMissingFileItems() {
		this.missingFileItems = this.getAllMissingFileItems();
		return this;
	}

	private DataHandler loadNoInfoFileItems() {
		this.noInfoFileItems = this.getAllFileItemsWithNoCollectorinfo();
		return this;
	}

	private DataHandler loadTags() {
		this.tags = ObjectHandler.castObjectListToTagList(this.findAll(new Tag(), null, null));
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
	 * 
	 * @param object
	 * @param doReplace
	 * @throws Exception
	 */
	public void persist(final Object object, final boolean doReplace) throws Exception {
		final ArrayList<Object> dummyList = new ArrayList<Object>();
		dummyList.add(object);
		DataHandler.persist(dummyList, doReplace);
	}

	/**
	 * Reload the data from the database to the current object stack to handle
	 * the objects fast and to reduce database selects.
	 * 
	 * @param reloadType
	 */
	public void reloadData(final int reloadType) {
		Debug.startTimer("DataHandler reloadData time");
		switch (reloadType) {
			default:
			case DataHandler.RELOAD_ALL:
				this.loadKeys();
				this.loadValues();
				this.loadFileAttributes();
				this.loadFileItems();
				this.loadNoInfoFileItems();
				this.loadMissingFileItems();
				this.loadTags();
				this.loadFileTags();
				break;
			case DataHandler.RELOAD_FILEITEMS:
				this.loadFileItems();
				break;
			case DataHandler.RELOAD_KEYS:
				this.loadKeys();
				break;
			case DataHandler.RELOAD_VALUES:
				this.loadValues();
				break;
			case DataHandler.RELOAD_FILEATTRIBUTES:
				this.loadFileAttributes();
				break;
			case DataHandler.RELOAD_NOINFOFILEITEMS:
				this.loadNoInfoFileItems();
				break;
			case DataHandler.RELOAD_MISSINGFILEITEMS:
				this.loadMissingFileItems();
				break;
			case DataHandler.RELOAD_TAGS:
				this.loadTags();
				break;
			case DataHandler.RELOAD_FILETAGS:
				this.loadFileTags();
				break;
		}
		Debug.stopTimer("DataHandler reloadData time");
	}

	public void removeFileTag(final int fileTagId) {
		final String sql = "DELETE FROM fileTags WHERE id = '" + fileTagId + "'";

		try {
			DB.update(sql);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public int removeMissingFilesFromDatabase() {
		int resultValue = -1;

		ResultSet rs = null;

		final ArrayList<Integer> listOfMissingItems = new ArrayList<Integer>();
		String sql = null;

		try {
			DB.beginTransaction();

			sql = "SELECT id FROM " + new FileItem().getDatabaseTable() + " WHERE status = '1'";
			rs = DB.query(sql);

			while (rs.next()) {
				listOfMissingItems.add(rs.getInt("id"));
			}
			resultValue = listOfMissingItems.size();

			sql = "DELETE FROM " + new FileItem().getDatabaseTable() + " WHERE status = '1';";
			DB.update(sql);

			sql = "DELETE FROM " + new FileAttributes().getDatabaseTable() + " WHERE file_id IN ("
					+ Helper.implode(listOfMissingItems, ", ", null, null) + ")";
			DB.update(sql);

			DB.endTransaction();
		} catch (final SQLException e) {
			try {
				DB.rollbackTransaction();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return resultValue;
	}

	public void setNoInformationFoundFlag(final FileItem fileItem, final String infoType) {
		final String sql = "INSERT OR IGNORE INTO collectorInformation " + "(collectorName, file_id, key, value) " + "VALUES(?, ?, ?, ?)";
		final ConcurrentHashMap<Integer, Object> insertValues = new ConcurrentHashMap<Integer, Object>();
		insertValues.put(1, infoType);
		insertValues.put(2, fileItem.getId());
		insertValues.put(3, "noinformation");
		insertValues.put(4, Boolean.TRUE);

		try {
			DB.updatePS(sql, insertValues);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Added in database version 1.
	 * 
	 * @param id
	 * @param status
	 */
	public void updateStatusOfFileItem(final Integer id, final int status) {
		if ((id != null) && (id > 0)) {
			final String sql = "UPDATE fileInformation SET status = ? WHERE id = ?";
			final ConcurrentHashMap<Integer, Object> values = new ConcurrentHashMap<Integer, Object>();
			values.put(1, status);
			values.put(2, id);
			try {
				DB.updatePS(sql, values);
			} catch (final SQLException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateUpdateTSForFileItem(final Integer id) {
		if ((id != null) && (id > 0)) {
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

}