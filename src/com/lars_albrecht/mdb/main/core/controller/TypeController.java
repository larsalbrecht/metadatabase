/**
 * 
 */
package com.lars_albrecht.mdb.main.core.controller;

import java.util.ArrayList;

import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.typer.abstracts.ATyper;

/**
 * The TypeController controls the type of the files.
 * 
 * @author lalbrecht
 * 
 */
public class TypeController {

	private MainController					mainController	= null;
	private ArrayList<ATyper>				typers			= null;
	private static final ArrayList<String>	availableTypes	= new ArrayList<String>();

	/**
	 * Add a type to the availableTypes-list. If the type is already in the
	 * list, the type will not added again.
	 * 
	 * @param type
	 *            String
	 */
	public static void addType(final String type) {
		if (!TypeController.containsType(type)) {
			TypeController.availableTypes.add(type);
		}
	}

	/**
	 * Returns true if the availableTypes contains type.
	 * 
	 * @param type
	 *            String
	 * @return boolean
	 */
	public static boolean containsType(final String type) {
		if (TypeController.availableTypes.contains(type)) {
			return true;
		}
		return false;
	}

	public TypeController(final MainController mainController, final ArrayList<ATyper> typers) {
		this.mainController = mainController;
		if (typers == null) {
			this.typers = new ArrayList<ATyper>();
		} else {
			this.typers = typers;
		}
		this.initTyper();
	}

	/**
	 * Find the types for each given FileItem in fileItems.
	 * 
	 * @param fileItems
	 * @return ArrayList<FileItem>
	 */
	public ArrayList<FileItem> findOutType(final ArrayList<FileItem> fileItems) {
		final ArrayList<FileItem> tempFileItemList = new ArrayList<FileItem>();
		for (final ATyper typer : this.typers) {
			typer.setMainController(this.mainController);
			tempFileItemList.addAll(typer.fillFileItemsWithType(fileItems));
		}

		return tempFileItemList;
	}

	/**
	 * @return the availableTypes
	 */
	public final ArrayList<String> getAvailableTypes() {
		return TypeController.availableTypes;
	}

	/**
	 * Initialize the typer.
	 */
	private void initTyper() {
		for (final ATyper typer : this.typers) {
			final ArrayList<String> tempTypes = typer.getTypes();
			if ((tempTypes != null) && (tempTypes.size() > 0)) {
				TypeController.availableTypes.addAll(tempTypes);
			}
		}
	}
}
