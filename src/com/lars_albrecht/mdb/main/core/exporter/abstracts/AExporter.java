/**
 * 
 */
package com.lars_albrecht.mdb.main.core.exporter.abstracts;

import java.io.File;
import java.util.List;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.controller.interfaces.IController;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;

/**
 * @author lalbrecht
 * 
 */
public abstract class AExporter {

	protected MainController	mainController	= null;
	protected IController		controller		= null;

	public AExporter() {
	}

	public abstract void exportItem(final File file, final FileItem fileItem, final List<Object> options);

	public abstract void exportList(final File file, final List<FileItem> fileList, final List<Object> options);

	public abstract String getExporterDescription();

	public abstract String getExporterName();

	/**
	 * @param controller
	 *            the controller to set
	 */
	public final void setController(final IController controller) {
		this.controller = controller;
	}

	/**
	 * @param mainController
	 *            the mainController to set
	 */
	public final void setMainController(final MainController mainController) {
		this.mainController = mainController;
	}

}
