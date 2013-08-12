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
 *         TODO TYPES must be controlled by collectors!
 * 
 */
public class TypeController {

	public static final int		TYPE_ALL		= 0;
	public static final int		TYPE_MOVIE		= 1;
	public static final int		TYPE_SERIE		= 2;

	private MainController		mainController	= null;
	private ArrayList<ATyper>	typers			= null;
	private ArrayList<String>	availableTypes	= null;

	public TypeController(final MainController mainController) {
		this.mainController = mainController;
		this.typers = new ArrayList<ATyper>();
		this.availableTypes = new ArrayList<String>();
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
	 * Initialize the typer.
	 */
	private void initTyper() {
		for (final ATyper typer : this.typers) {
			final ArrayList<String> tempTypes = typer.getTypes();
			if (tempTypes != null && tempTypes.size() > 0) {
				this.availableTypes.addAll(tempTypes);
			}
		}
	}

	/**
	 * @param typers
	 *            the typers to set
	 */
	public final void setTypers(final ArrayList<ATyper> typers) {
		this.typers = typers;
	}

}
