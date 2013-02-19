/**
 * 
 */
package com.lars_albrecht.mdb.core.handler;

import java.io.File;
import java.util.ArrayList;

import com.lars_albrecht.general.Helper;
import com.lars_albrecht.mdb.core.models.FileItem;
import com.lars_albrecht.mdb.core.models.Key;
import com.lars_albrecht.mdb.core.models.Value;

/**
 * @author albrela
 * 
 */
public class TypeHandler {

	public static ArrayList<FileItem> castObjectListToFileItemList(
			final ArrayList<Object> oList) {

		final ArrayList<FileItem> resultList = new ArrayList<FileItem>();
		for (final Object oItem : oList) {
			if (oItem instanceof FileItem) {
				resultList.add((FileItem) oItem);
			}
		}
		return resultList;
	}

	public static ArrayList<Key<?>> castObjectListToKeyList(
			final ArrayList<Object> oList) {

		final ArrayList<Key<?>> resultList = new ArrayList<Key<?>>();
		for (final Object oItem : oList) {
			if (oItem instanceof Key) {
				resultList.add((Key<?>) oItem);
			}
		}

		return resultList;
	}

	public static ArrayList<Value<?>> castObjectListToValueList(
			final ArrayList<Object> oList) {

		final ArrayList<Value<?>> resultList = new ArrayList<Value<?>>();
		for (final Object oItem : oList) {
			if (oItem instanceof Value) {
				resultList.add((Value<?>) oItem);
			}
		}

		return resultList;
	}

	/**
	 * 
	 * @param foundFilesList
	 * @return
	 */
	public static ArrayList<FileItem> fileListToFileItemList(
			final ArrayList<File> foundFilesList) {
		final ArrayList<FileItem> tempFileItemList = new ArrayList<FileItem>();
		for (final File file : foundFilesList) {
			tempFileItemList.add(new FileItem(file.getName(), file
					.getAbsolutePath(), file.getParent(), file.length(), Helper
					.getFileExtension(file.getName())));
		}

		return tempFileItemList;
	}

}