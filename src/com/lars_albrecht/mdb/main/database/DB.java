package com.lars_albrecht.mdb.main.database;

import java.awt.Image;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.mdb.main.database.interfaces.IDatabase;

/**
 * 
 * sqlite: https://bitbucket.org/xerial/sqlite-jdbc/overview
 * 
 * @author lalbrecht
 * 
 *         TODO Refactor. Move table creation to the specific model classes.
 * 
 */
public class DB implements IDatabase {

	private static Connection	connection			= null;

	private final static String	dbUsername			= "moviedb";
	private final static String	dbPassword			= "mypw";
	private final static String	dbName				= "moviedb";
	private final static String	dbUrlH2				= "jdbc:h2:";
	private final static String	StrdbH2				= "org.h2.Driver";
	private final static String	dbUrlSQLite			= "jdbc:sqlite:";
	private final static String	StrdbSQLite			= "org.sqlite.JDBC";
	private final static String	dboptions			= "";
	private final static String	dbFile				= "mdb.sqlite";

	public static final Integer	DBTYPE_SQLITE		= 0;
	public static final Integer	DBTYPE_H2			= 1;

	private static Integer		DBTYPE				= DB.DBTYPE_SQLITE;

	public static boolean		useQuotesForFields	= false;

	/**
	 * Sets a dynamic object in a PreparedStatement.
	 * 
	 * @param pst
	 *            PreparedStatement
	 * @param index
	 *            int
	 * @param objectToSet
	 *            Object
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public static PreparedStatement
			addDynamicValue(final PreparedStatement pst, final int index, final Object objectToSet) throws Exception {
		if (objectToSet.getClass() == Integer.class) {
			pst.setInt(index, ((Integer) objectToSet).intValue());
		} else if (objectToSet.getClass() == String.class) {
			pst.setString(index, (String) objectToSet);
		} else if (objectToSet instanceof Image) {
			// pst.setBytes(index, Helper.getBytesFromImage((Image)
			// objectToSet));
			throw new Exception("Unsupported type submitted: " + objectToSet.getClass().getName());
		} else if (objectToSet.getClass() == Boolean.class) {
			pst.setBoolean(index, ((Boolean) objectToSet));
		} else if (objectToSet.getClass() == Float.class) {
			pst.setFloat(index, ((Float) objectToSet).floatValue());
		} else if (objectToSet.getClass() == Long.class) {
			pst.setLong(index, ((Long) objectToSet).longValue());
		} else if (objectToSet.getClass() == Byte.class) {
			pst.setByte(index, ((Byte) objectToSet).byteValue());
		} else if (objectToSet.getClass() == Short.class) {
			pst.setShort(index, ((Short) objectToSet).shortValue());
		} else if (objectToSet.getClass() == BigDecimal.class) {
			pst.setBigDecimal(index, ((BigDecimal) objectToSet));
		} else if (objectToSet.getClass() == Double.class) {
			pst.setDouble(index, ((Double) objectToSet).doubleValue());
		} else if (objectToSet.getClass() == Time.class) {
			pst.setTime(index, ((Time) objectToSet));
		} else if (objectToSet.getClass() == Date.class) {
			pst.setDate(index, ((Date) objectToSet));
		} else if (objectToSet.getClass() == Timestamp.class) {
			pst.setTimestamp(index, ((Timestamp) objectToSet));
		} else if (objectToSet.getClass() == byte[].class) {
			pst.setBytes(index, ((byte[]) objectToSet));
		} else if (objectToSet instanceof URI) {
			pst.setString(index, ((URI) objectToSet).toString());
		} else if (objectToSet.getClass() == Object[].class) {
			pst.setArray(index, (Array) Arrays.asList(((Object[]) objectToSet)));
		} else {
			throw new Exception("Unknown type submitted: " + objectToSet.getClass().getName());
		}
		return pst;
	}

	/**
	 * Close the DB connection.
	 * 
	 * @throws SQLException
	 */
	public static void closeConnection() throws SQLException {
		if (DB.connection != null) {
			DB.connection.close();
		}
	}

	public static void commit() throws SQLException {
		if (DB.connection != null) {
			DB.connection.commit();
		}
	}

	/**
	 * Create a DB connection.
	 * 
	 * @return Connection
	 */
	private static Connection createConnection(final Integer dbType) {
		Connection con = null;
		try {
			switch (dbType) {
				default:
				case 0: // sqlite
					Class.forName(DB.StrdbSQLite);
					con = DriverManager.getConnection(DB.dbUrlSQLite + DB.dbFile);
					DB.useQuotesForFields = true;
					break;
				case 1: // h2
					Class.forName(DB.StrdbH2);
					con = DriverManager.getConnection(DB.dbUrlH2 + DB.dbName + DB.dboptions, DB.dbUsername, DB.dbPassword);
					DB.useQuotesForFields = false;
			}

		} catch (final ClassNotFoundException e) {
			System.err.println("Driver not found \"" + e.getMessage() + "\"");
			return null;
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return con;
	}

	/**
	 * 
	 * @param rs
	 *            ResultSet
	 * @throws SQLException
	 */
	public static void dump(final ResultSet rs) throws SQLException {

		// the order of the rows in a cursor
		// are implementation dependent unless you use the SQL ORDER statement
		final ResultSetMetaData meta = rs.getMetaData();
		final int colmax = meta.getColumnCount();
		int i;
		Object o = null;

		// the result set is a cursor into the data. You can only
		// point to one row at a time
		// assume we are pointing to BEFORE the first row
		// rs.next() points to next row and returns true
		// or false if there is no next row, which breaks the loop
		for (; rs.next();) {
			for (i = 0; i < colmax; ++i) {
				o = rs.getObject(i + 1); // Is SQL the first column is indexed

				// with 1 not 0
				// Debug.log(Debug.LEVEL_DEBUG, o.toString() + " ");
				System.out.println(o.toString() + " ");
			}

			// Debug.log(Debug.LEVEL_DEBUG, " ");
			System.out.println(" ");
		}
	}

	/**
	 * 
	 * @param tableName
	 * @return ArrayList<String>
	 * @throws SQLException
	 */
	public static ArrayList<String> getColumnsFromTable(final String tableName) throws SQLException {
		final ResultSet rs = DB.query("SHOW COLUMNS FROM " + tableName);
		final ArrayList<String> tempList = new ArrayList<String>();
		while (rs.next()) {
			tempList.add(rs.getString("COLUMN_NAME"));
		}

		return tempList;
	}

	/**
	 * Returns the DB connection.
	 * 
	 * @return the connection
	 * @throws SQLException
	 */
	public static synchronized final Connection getConnection() throws SQLException {
		if ((DB.connection == null) || DB.connection.isClosed() || DB.connection.isReadOnly()) {
			DB.connection = DB.createConnection(DB.DBTYPE);
		}
		return DB.connection;
	}

	public static int getDBType() {
		return DB.DBTYPE;
	}

	/**
	 * Returns the last inserted row id from the database. This method is using
	 * the "getGeneratedKeys()"-method from Statement st.
	 * 
	 * @param st
	 * @return last_inserted_rowid
	 * @throws SQLException
	 */
	private static synchronized int getLastInsertedRowId(final Statement st) throws SQLException {
		int result = -1;
		switch (DB.DBTYPE) {
			default:
			case 0: // sqlite
				result = st.getGeneratedKeys().getInt("last_insert_rowid()");
				break;
			case 1: // h2
				final ResultSet rs = DB.query("CALL IDENTITY()");
				if (rs.first()) {
					result = ((Long) rs.getObject(1)).intValue();
				}
		}

		return result;
	}

	/**
	 * 
	 * @return ArrayList<String>
	 * @throws SQLException
	 */
	public static ArrayList<String> getTables() throws SQLException {
		String dbInfoTable = null;
		String dbInfoField = null;
		String dbInfoWhere = null;
		switch (DB.DBTYPE) {
			default:
			case 0: // sqlite
				dbInfoTable = "sqlite_master";
				dbInfoField = "name";
				dbInfoWhere = "type = 'table'";
				break;
			case 1: // h2
				dbInfoTable = "INFORMATION_SCHEMA.TABLES";
				dbInfoField = "TABLE_NAME";
				dbInfoWhere = "TABLE_SCHEMA = 'PUBLIC'";
		}

		final ResultSet rs = DB.query("SELECT * FROM " + dbInfoTable + " WHERE " + dbInfoWhere + ";");
		final ArrayList<String> temp = new ArrayList<String>();
		for (; rs.next();) {
			temp.add(rs.getString(dbInfoField));
		}
		DB.closeConnection();

		return temp;
	}

	/**
	 * use for SQL command SELECT
	 * 
	 * @param expression
	 * @throws SQLException
	 */
	public static synchronized ResultSet query(final String expression) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		st = DB.getConnection().createStatement(); // statement objects can be
		// reused with

		// repeated calls to execute but we
		// choose to make a new one each time
		rs = st.executeQuery(expression); // run the query

		// do something with the result set.

		// st.close(); // NOTE!! if you close a statement the associated
		// ResultSet
		// is
		return rs;
		// closed too
		// so you should copy the contents to some other object.
		// the result set is invalidated also if you recycle an Statement
		// and try to execute some other query before the result set has been
		// completely examined.
	}

	/**
	 * use for SQL command SELECT
	 * 
	 * @param expression
	 * @param values
	 * @return ResultSet
	 * @throws Exception
	 */
	public static synchronized ResultSet queryPS(final String expression, final ConcurrentHashMap<Integer, Object> values) throws Exception {
		PreparedStatement st = null;
		ResultSet rs = null;
		st = DB.getConnection().prepareStatement(expression);
		Debug.log(Debug.LEVEL_DEBUG, "SQL: " + expression);
		Debug.log(Debug.LEVEL_DEBUG, "SQL VALUES: " + values);
		for (final Map.Entry<Integer, Object> entry : values.entrySet()) {
			st = DB.addDynamicValue(st, entry.getKey(), entry.getValue()); //
			// TODO check why it will not save lists with this -> see logs with
			// image org.h2.jdbc.JdbcSQLException: Serialisierung
			// fehlgeschlagen, Grund:
			// "java.io.NotSerializableException: sun.awt.image.ToolkitImage" -
			// Serialization failed, cause:
			// "java.io.NotSerializableException: sun.awt.image.ToolkitImage"
			// [90026-162]
			st.setObject(entry.getKey(), entry.getValue());
			st = DB.addDynamicValue(st, entry.getKey(), entry.getValue());
		}

		rs = st.executeQuery();

		return rs;
	}

	/**
	 * 
	 * @param sql
	 * @param fields
	 * @return ArrayList<ConcurrentHashMap<String, Object>>
	 * @throws SQLException
	 */
	public static ArrayList<ConcurrentHashMap<String, Object>>
			returnResultFromItems(final String sql, final ArrayList<String> fields) throws SQLException {
		final ArrayList<ConcurrentHashMap<String, Object>> resultSet = new ArrayList<ConcurrentHashMap<String, Object>>();
		final ResultSet rs = DB.query(sql);
		for (; rs.next();) {
			final ConcurrentHashMap<String, Object> temp = new ConcurrentHashMap<String, Object>();
			for (final String string : fields) {
				temp.put(string, rs.getObject(string));
			}
			resultSet.add(temp);
		}
		return resultSet;
	}

	public static void setDBType(final Integer dbType) throws Exception {
		if (dbType != DB.DBTYPE_SQLITE) {
			throw new Exception("Unsupported database type");
		} else {
			DB.DBTYPE = DB.DBTYPE_SQLITE;
		}
		// if ((dbType >= 0) && (dbType <= 1)) {
		// DB.DBTYPE = dbType;
		// } else {
		// DB.DBTYPE = DB.DBTYPE_SQLITE;
		// }
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public static void shutdown() throws SQLException {

		final Statement st = DB.getConnection().createStatement();

		// db writes out to files and performs clean shuts down
		// otherwise there will be an unclean shutdown
		// when program ends
		st.execute("SHUTDOWN");
		DB.connection.close(); // if there are no other open connection
	}

	/**
	 * use for SQL commands CREATE, DROP, INSERT, UPDATE and ALTER
	 * 
	 * @param expression
	 * @return lastInsertedId
	 * @throws SQLException
	 */
	public static synchronized int update(final String expression) throws SQLException {
		int lastInsertedId = -1;
		Statement st = null;
		st = DB.getConnection().createStatement(); // statements
		final int i = st.executeUpdate(expression); // run the query
		if (i == -1) {
			// Debug.log(Debug.LEVEL_ERROR, "db error : " + expression);
		}

		lastInsertedId = DB.getLastInsertedRowId(st);
		Debug.log(Debug.LEVEL_DEBUG, "LAST INSERTEDROWID: " + lastInsertedId);

		st.close();

		return lastInsertedId;
	}

	public static synchronized void beginTransaction() throws SQLException {
		DB.update("BEGIN TRANSACTION;");
	}

	public static synchronized void endTransaction() throws SQLException {
		DB.update("END TRANSACTION;");
	}

	public static synchronized void rollbackTransaction() throws SQLException {
		DB.update("ROLLBACK TRANSACTION;");
	}

	/**
	 * use for SQL commands CREATE, DROP, INSERT and UPDATE
	 * 
	 * @param expression
	 * @param values
	 * @throws Exception
	 */
	public static synchronized int updatePS(final String expression, final Map<Integer, Object> values) throws Exception {
		int lastInsertedId = -1;
		if (expression != null && values != null && values.size() > 0) {
			PreparedStatement st = null;
			Debug.log(Debug.LEVEL_DEBUG, "SQL: " + expression);
			Debug.log(Debug.LEVEL_DEBUG, "SQL VALUES: " + values);
			try {
				st = DB.getConnection().prepareStatement(expression); // statements
				for (final Map.Entry<Integer, Object> entry : values.entrySet()) {
					st = DB.addDynamicValue(st, entry.getKey(), entry.getValue());
				}

				final int result = st.executeUpdate(); // run the query
				if (result == -1) {
					// Debug.log(Debug.LEVEL_ERROR, "db error : " + expression);
				}

				lastInsertedId = DB.getLastInsertedRowId(st);
				Debug.log(Debug.LEVEL_DEBUG, "LAST INSERTEDROWID: " + lastInsertedId);
			} catch (final SQLException e) {
				Debug.log(Debug.LEVEL_ERROR, "ERROR ON SQL: " + expression + " | with (" + values.size() + ")values: " + values);
				e.printStackTrace();
			} finally {
				if (st != null) {
					st.close();
				}
			}
		}
		return lastInsertedId;
	}

	private void createTableFileInformation() throws SQLException {
		// fileInformation
		String sql = null;
		sql = "CREATE TABLE IF NOT EXISTS 'fileInformation' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'name' VARCHAR(255), ";
		sql += "'dir' VARCHAR(255), ";
		sql += "'ext' VARCHAR(255), ";
		sql += "'size' LONG, ";
		sql += "'fullpath' VARCHAR(255), ";
		sql += "'filehash' VARCHAR(255), ";
		sql += "'filetype' INTEGER, ";
		sql += "'createTS' DATE DEFAULT (datetime('now','localtime')), ";
		sql += "'updateTS' DATE DEFAULT (datetime('now','localtime')), ";
		sql += "'status' INTEGER NOT NULL DEFAULT '0' ";
		sql += ");";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_fileinformation_fullpath ON fileInformation (fullpath);";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_fileinformation_name_size ON fileInformation (name, size);";
		DB.update(sql);
		sql = "CREATE INDEX IF NOT EXISTS idx_fileinformation_id ON fileInformation (id);";
		DB.update(sql);
		sql = "CREATE INDEX IF NOT EXISTS idx_fileinformation_name ON fileInformation (name);";
		DB.update(sql);
	}

	private void createTableCollectorInformation() throws SQLException {
		String sql = null;
		// collectorInformation
		sql = "CREATE TABLE IF NOT EXISTS 'collectorInformation' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'collectorName' VARCHAR(255), ";
		sql += "'file_id' INTEGER, ";
		sql += "'key' VARCHAR(255), ";
		sql += "'value' VARCHAR(255), ";
		sql += "FOREIGN KEY (file_id) REFERENCES fileInformation(id) ON DELETE CASCADE ";
		sql += ");";
		DB.update(sql);
		sql = "CREATE INDEX IF NOT EXISTS idx_collectorinformation_collectorname_file_id ON collectorInformation (collectorName, file_id);";
		DB.update(sql);
	}

	private void createTableAttributesKey() throws SQLException {
		String sql = null;
		// attributes_key
		sql = "CREATE TABLE IF NOT EXISTS 'attributes_key' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'key' VARCHAR(255), ";
		sql += "'infoType' VARCHAR(255), ";
		sql += "'section' VARCHAR(255), ";
		sql += "'editable' INTEGER, ";
		sql += "'searchable' INTEGER ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_attributes_key ON attributes_key (key, infoType, section);";
		DB.update(sql);
	}

	private void createTableAttributesValue() throws SQLException {
		String sql = null;
		// attributes_value
		sql = "CREATE TABLE IF NOT EXISTS 'attributes_value' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'value' TEXT ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_attributes_value ON attributes_value (value);";
		DB.update(sql);
	}

	private void createTableFileAttributes() throws SQLException {
		String sql = null;
		// fileAttributes
		sql = "CREATE TABLE IF NOT EXISTS 'fileAttributes' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'file_id' INTEGER, ";
		sql += "'key_id' INTEGER, ";
		sql += "'value_id' INTEGER, ";
		// sql += "'value' INTEGER ";
		sql += "FOREIGN KEY (file_id) REFERENCES fileInformation(id) ON DELETE CASCADE, ";
		sql += "FOREIGN KEY (key_id) REFERENCES attributes_key(id) ON DELETE CASCADE, ";
		sql += "FOREIGN KEY (value_id) REFERENCES attributes_value(id) ON DELETE CASCADE ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_fileattributes_filekey ON fileattributes (file_id, key_id, value_id);";
		DB.update(sql);
	}

	private void createTableTags() throws SQLException {
		String sql = null;
		// tags
		sql = "CREATE TABLE IF NOT EXISTS 'tags' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'name' VARCHAR(255), ";
		sql += "'isuser' INTEGER ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_tags_name ON tags (name);";
		DB.update(sql);
	}

	private void createTableFileTags() throws SQLException {
		String sql = null;
		// fileTags
		sql = "CREATE TABLE IF NOT EXISTS 'fileTags' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'file_id' INTEGER, ";
		sql += "'tag_id' INTEGER, ";
		sql += "'isuser' INTEGER, ";
		sql += "FOREIGN KEY (file_id) REFERENCES fileInformation(id) ON DELETE CASCADE, ";
		sql += "FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE ";
		// sql += "'value' INTEGER ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_filetags_filekey ON fileTags (file_id, tag_id, isuser);";
		DB.update(sql);
		sql = "CREATE INDEX IF NOT EXISTS idx_filetags_id_fileid_tagid ON fileTags (id, file_id, tag_id);";
		DB.update(sql);
	}

	private void createTableOptions() throws SQLException {
		String sql = null;
		// options
		sql = "CREATE TABLE IF NOT EXISTS 'options' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'name' VARCHAR(255), ";
		sql += "'value' VARCHAR(255) ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_options ON options (name);";
		DB.update(sql);
	}

	private void createTableMediaItems() throws SQLException {
		String sql = null;
		// options
		sql = "CREATE TABLE IF NOT EXISTS 'mediaItems' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'name' VARCHAR(255), ";
		sql += "'type' INTEGER, ";
		sql += "'uri' VARCHAR(255), ";
		sql += "'options' TEXT ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_mediaItems ON mediaItems (name, type, uri);";
		DB.update(sql);
	}

	private void createTableFileMedia() throws SQLException {
		String sql = null;
		// options
		sql = "CREATE TABLE IF NOT EXISTS 'fileMedia' ( ";
		sql += "'id' INTEGER PRIMARY KEY AUTOINCREMENT, ";
		sql += "'file_id' INTEGER, ";
		sql += "'media_id' INTEGER, ";
		sql += "FOREIGN KEY (file_id) REFERENCES fileInformation(id) ON DELETE CASCADE, ";
		sql += "FOREIGN KEY (media_id) REFERENCES mediaItems(id) ON DELETE CASCADE ";
		sql += "); ";
		DB.update(sql);
		sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_fileMedia ON fileMedia (file_id, media_id);";
		DB.update(sql);
	}

	@Override
	public void init() throws Exception {
		String sql = null;
		try {
			sql = "PRAGMA foreign_keys = ON;";
			DB.update(sql);

			if (!this.updateDBWithVersion()) {
				throw new Exception("Database could not be updated");
			}

			this.createTableFileInformation();
			this.createTableCollectorInformation();
			this.createTableAttributesKey();
			this.createTableAttributesValue();
			this.createTableFileAttributes();
			this.createTableTags();
			this.createTableFileTags();
			this.createTableOptions();
			this.createTableMediaItems();
			this.createTableFileMedia();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the database version to the options. If the version is newer at a
	 * run, the database will be updated to the newer version.
	 * 
	 * @return
	 */
	private boolean updateDBWithVersion() {
		final int newDBVersion = 3;
		// INSERT A DATABASE VERSION
		String sql = "";
		ResultSet rs = null;

		int currentDBVersion = -1;

		try {
			// check if table exists or not (first start, table does not exists)
			sql = "SELECT count(*) AS count FROM sqlite_master WHERE type='table' AND name='options'";
			rs = DB.query(sql);
			if (rs.next() && rs.getInt("count") > 0) {
				sql = "SELECT COUNT(*) AS count, value FROM options WHERE name = 'dbversion'";
				rs = DB.query(sql);
				if (rs.next() && rs.getInt("count") > 0) {
					currentDBVersion = rs.getInt("value");
				} else {
					currentDBVersion = -1;
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		try {
			if (currentDBVersion > -1 && currentDBVersion < newDBVersion) {
				DB.beginTransaction();
				for (int i = (currentDBVersion + 1); i <= newDBVersion; i++) {
					sql = "REPLACE INTO options (id, name, value) VALUES (1, 'dbversion', " + newDBVersion + ")";
					DB.update(sql);

					if (i == 1) { // VERSION = 1 -> added
						// foreign keys
						// rename tables
						sql = "ALTER TABLE collectorInformation RENAME TO 'collectorInformation_old'";
						DB.update(sql);
						sql = "ALTER TABLE typeInformation RENAME TO 'typeInformation_old'";
						DB.update(sql);
						sql = "ALTER TABLE fileTags RENAME TO 'fileTags_old'";

						// recreate tables
						this.createTableCollectorInformation();
						this.createTableFileAttributes();
						this.createTableFileTags();

						// move old entries to new tables
						sql = "INSERT INTO 'collectorInformation' (SELECT * FROM 'collectorInformation_old')";
						DB.update(sql);
						sql = "INSERT INTO 'typeInformation' (SELECT * FROM 'typeInformation_old')";
						DB.update(sql);
						sql = "INSERT INTO 'fileTags' (SELECT * FROM 'fileTags_old')";

						DB.update(sql);
					} else if (i == 2) { // VERSION = 2 -> added tag isUser)
						sql = "ALTER TABLE 'tags' ADD COLUMN 'isUser' INTEGER NOT NULL DEFAULT '0'";
						DB.update(sql);
					} else if (i == 3) { // change table names
						sql = "ALTER TABLE 'typeInformation' RENAME TO 'fileAttributes'";
						DB.update(sql);
						sql = "ALTER TABLE 'typeInformation_key' RENAME TO 'attributes_key'";
						DB.update(sql);
						sql = "ALTER TABLE 'typeInformation_value' RENAME TO 'attributes_value'";
						DB.update(sql);
					}
				}
				DB.endTransaction();
			}
		} catch (final SQLException e) {
			try {
				DB.rollbackTransaction();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}
}