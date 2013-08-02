/**
 * 
 */
package com.lars_albrecht.mdb.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import com.lars_albrecht.mdb.main.core.collector.abstracts.ACollector;
import com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter;
import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts.AbstractFileDetailsOutputItem;
import com.lars_albrecht.mdb.main.core.typer.abstracts.ATyper;

/**
 * @author lalbrecht
 * 
 */
public class MDBConfig {

	private FileFilter						finderFileFilter					= null;
	private AbstractFileDetailsOutputItem	webInterfaceFileDetailsOutputItem	= null;

	private int								loglevel							= 0;

	final private ArrayList<ATyper>			listOfTypers						= new ArrayList<ATyper>();

	final private ArrayList<AExporter>		listOfExporter						= new ArrayList<AExporter>();

	final private ArrayList<AInterface>		listOfInterfaces					= new ArrayList<AInterface>();

	final private ArrayList<ACollector>		listOfCollectors					= new ArrayList<ACollector>();

	File									systemTrayInterfaceIconImageFile	= null;

	/**
	 * @return the finderFileFilter
	 */
	public final FileFilter getFinderFileFilter() {
		return this.finderFileFilter;
	}

	/**
	 * @param finderFileFilter
	 *            the finderFileFilter to set
	 */
	public final void setFinderFileFilter(final FileFilter finderFileFilter) {
		this.finderFileFilter = finderFileFilter;
	}

	/**
	 * @return the webInterfaceFileDetailsOutputItem
	 */
	public final AbstractFileDetailsOutputItem getWebInterfaceFileDetailsOutputItem() {
		return this.webInterfaceFileDetailsOutputItem;
	}

	/**
	 * @param webInterfaceFileDetailsOutputItem
	 *            the webInterfaceFileDetailsOutputItem to set
	 */
	public final void setWebInterfaceFileDetailsOutputItem(final AbstractFileDetailsOutputItem webInterfaceFileDetailsOutputItem) {
		this.webInterfaceFileDetailsOutputItem = webInterfaceFileDetailsOutputItem;
	}

	/**
	 * @return the loglevel
	 */
	public final int getLoglevel() {
		return this.loglevel;
	}

	/**
	 * @param loglevel
	 *            the loglevel to set
	 */
	public final void setLoglevel(final int loglevel) {
		this.loglevel = loglevel;
	}

	/**
	 * @return the listOfExporter
	 */
	public final ArrayList<AExporter> getListOfExporter() {
		return this.listOfExporter;
	}

	/**
	 * @return the listOfInterfaces
	 */
	public final ArrayList<AInterface> getListOfInterfaces() {
		return this.listOfInterfaces;
	}

	/**
	 * @return the listOfCollectors
	 */
	public final ArrayList<ACollector> getListOfCollectors() {
		return this.listOfCollectors;
	}

	/**
	 * @param systemTrayInterfaceIconImageFile
	 *            the systemTrayInterfaceIconImageFile to set
	 */
	public final void setSystemTrayInterfaceIconImageFile(final File systemTrayInterfaceIconImageFile) {
		this.systemTrayInterfaceIconImageFile = systemTrayInterfaceIconImageFile;
	}

	/**
	 * @return the systemTrayInterfaceIconImageFile
	 */
	public final File getSystemTrayInterfaceIconImageFile() {
		return this.systemTrayInterfaceIconImageFile;
	}

	/**
	 * @return the listOfTypers
	 */
	public final ArrayList<ATyper> getListOfTypers() {
		return this.listOfTypers;
	}

}
