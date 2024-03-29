/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.io.File;
import java.util.ArrayList;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.models.persistable.FileAttributes;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.Key;
import com.lars_albrecht.mdb.main.core.models.persistable.Tag;
import com.lars_albrecht.mdb.main.core.models.persistable.Value;

/**
 * @author lalbrecht
 * 
 */
public class ObjectHandler {

	public static ArrayList<FileAttributes> castObjectListToFileAttributesList(final ArrayList<Object> oList) {
		final ArrayList<FileAttributes> resultList = new ArrayList<FileAttributes>();
		for (final Object oItem : oList) {
			if (oItem instanceof FileAttributes) {
				resultList.add((FileAttributes) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<FileItem> castObjectListToFileItemList(final ArrayList<Object> oList) {
		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();
		if (oList != null) {
			for (final Object oItem : oList) {
				if (oItem instanceof FileItem) {
					resultList.add((FileItem) oItem);
				}
			}
		}
		return resultList;
	}

	public static ArrayList<FileTag> castObjectListToFileTagList(final ArrayList<Object> oList) {

		final ArrayList<FileTag> resultList = new ArrayList<FileTag>();
		for (final Object oItem : oList) {
			if (oItem instanceof FileTag) {
				resultList.add((FileTag) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<Key<?>> castObjectListToKeyList(final ArrayList<Object> oList) {

		final ArrayList<Key<?>> resultList = new ArrayList<Key<?>>();
		for (final Object oItem : oList) {
			if (oItem instanceof Key) {
				resultList.add((Key<?>) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<Tag> castObjectListToTagList(final ArrayList<Object> oList) {
		final ArrayList<Tag> resultList = new ArrayList<Tag>();
		for (final Object oItem : oList) {
			if (oItem instanceof Tag) {
				resultList.add((Tag) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<Value<?>> castObjectListToValueList(final ArrayList<Object> oList) {

		final ArrayList<Value<?>> resultList = new ArrayList<Value<?>>();
		for (final Object oItem : oList) {
			if (oItem instanceof Value) {
				resultList.add((Value<?>) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<File> castStringListToFileList(final ArrayList<String> oList) {
		final ArrayList<File> resultList = new ArrayList<File>();
		if (oList != null) {
			for (final String oItem : oList) {
				if (oItem instanceof String) {
					resultList.add(new File(oItem));
				}
			}
		}

		return resultList;
	}

	public static String fileItemListToJSON(final ArrayList<FileItem> fileItemList) {
		String jsonString = null;

		if ((fileItemList != null) && (fileItemList.size() > 0)) {
			jsonString = "{";
			// "{\"BigBuckBunny\" : \"BigBuckBunny\"}"
			int i = 0;
			for (final FileItem fileItem : fileItemList) {
				jsonString += "\"" + fileItem.getName() + "\"" + ":" + "\"" + fileItem.getName() + "\"";
				if (i < (fileItemList.size() - 1)) {
					jsonString += ",";
				}
				i++;
			}
			jsonString += "}";

		}

		return jsonString;
	}

	/**
	 * 
	 * @param foundFilesList
	 * @return ArrayList<FileItem>
	 */
	public static ArrayList<FileItem> fileListToFileItemList(final ArrayList<File> foundFilesList) {
		final ArrayList<FileItem> tempFileItemList = new ArrayList<FileItem>();
		for (final File file : foundFilesList) {
			if ((file != null) && (file.getName() != null)) {
				try {
					tempFileItemList.add(new FileItem(file.getName(), file.getAbsolutePath(), file.getParent(), file.length(), Helper
							.getFileExtension(file.getName()), null, null));

				} catch (final NullPointerException e) {
					Debug.log(Debug.LEVEL_ERROR, "null pointer " + file + " " + file.getName() + " - " + file.getAbsolutePath());
				}
			}
		}

		return tempFileItemList;
	}

	public static String stringListToJSON(final ArrayList<String> stringList) {
		String jsonString = null;

		if ((stringList != null) && (stringList.size() > 0)) {
			jsonString = "{";
			int i = 0;
			for (final String string : stringList) {
				jsonString += "\"" + string + "\"" + ":" + "\"" + string + "\"";
				if (i < (stringList.size() - 1)) {
					jsonString += ",";
				}
				i++;
			}

			jsonString += "}";
		}

		return jsonString;
	}

	public static String tagListToJSON(final ArrayList<Tag> tags) {
		String jsonString = "";

		if ((tags != null) && (tags.size() > 0)) {
			jsonString = "{";
			final int i = 0;
			for (final Tag tag : tags) {
				jsonString += "\"id\"" + ":" + "\"" + tag.getId() + "\",";
				jsonString += "\"name\"" + ":" + "\"" + tag.getName() + "\"";
				if (i < (tags.size() - 1)) {
					jsonString += ",";
				}
			}

			jsonString += "}";
		}
		return jsonString;
	}
}
