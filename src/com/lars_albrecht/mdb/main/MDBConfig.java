/**
 * 
 */
package com.lars_albrecht.mdb.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.mdb.main.core.collector.abstracts.ACollector;
import com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter;
import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts.AbstractFileDetailsOutputItem;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageConfig;
import com.lars_albrecht.mdb.main.core.typer.abstracts.ATyper;

/**
 * @author lalbrecht
 * 
 */
public class MDBConfig {

	private FileFilter									finderFileFilter					= null;
	private AbstractFileDetailsOutputItem				webInterfaceFileDetailsOutputItem	= null;

	private int											loglevel							= 0;

	final private ArrayList<ATyper>						listOfTypers						= new ArrayList<ATyper>();

	final private ArrayList<AExporter>					listOfExporter						= new ArrayList<AExporter>();

	final private ArrayList<AInterface>					listOfInterfaces					= new ArrayList<AInterface>();

	final private ArrayList<ACollector>					listOfCollectors					= new ArrayList<ACollector>();

	private File										systemTrayInterfaceIconImageFile	= null;

	private final ConcurrentHashMap<String, String[]>	itemTitleExtraction					= new ConcurrentHashMap<String, String[]>();

	private ArrayList<WebPageConfig>					webInterfacePageConfigs				= new ArrayList<WebPageConfig>();

	private boolean										useUserAuthentication				= Boolean.FALSE;

	public final void addTitleExtraction(final String fileType, final String infoType, final String section, final String key) {
		this.itemTitleExtraction.put(fileType, new String[] {
				infoType, section, key
		});
	}

	/**
	 * @return the finderFileFilter
	 */
	public final FileFilter getFinderFileFilter() {
		return this.finderFileFilter;
	}

	/**
	 * @return the listOfCollectors
	 */
	public final ArrayList<ACollector> getListOfCollectors() {
		return this.listOfCollectors;
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
	 * @return the listOfTypers
	 */
	public final ArrayList<ATyper> getListOfTypers() {
		return this.listOfTypers;
	}

	/**
	 * @return the loglevel
	 */
	public final int getLoglevel() {
		return this.loglevel;
	}

	/**
	 * @return the systemTrayInterfaceIconImageFile
	 */
	public final File getSystemTrayInterfaceIconImageFile() {
		return this.systemTrayInterfaceIconImageFile;
	}

	public final String[] getTitleExtractionForFileType(final String fileType) {
		return this.itemTitleExtraction.get(fileType);
	}

	/**
	 * @return the webInterfaceFileDetailsOutputItem
	 */
	public final AbstractFileDetailsOutputItem getWebInterfaceFileDetailsOutputItem() {
		return this.webInterfaceFileDetailsOutputItem;
	}

	/**
	 * @return the webInterfacePageConfigs
	 */
	public final ArrayList<WebPageConfig> getWebInterfacePageConfigs() {
		return this.webInterfacePageConfigs;
	}

	/**
	 * @return the useUserAuthentication
	 */
	public final boolean isUseUserAuthentication() {
		return this.useUserAuthentication;
	}

	/**
	 * @param finderFileFilter
	 *            the finderFileFilter to set
	 */
	public final void setFinderFileFilter(final FileFilter finderFileFilter) {
		this.finderFileFilter = finderFileFilter;
	}

	/**
	 * @param loglevel
	 *            the loglevel to set
	 */
	public final void setLoglevel(final int loglevel) {
		this.loglevel = loglevel;
	}

	/**
	 * @param systemTrayInterfaceIconImageFile
	 *            the systemTrayInterfaceIconImageFile to set
	 */
	public final void setSystemTrayInterfaceIconImageFile(final File systemTrayInterfaceIconImageFile) {
		this.systemTrayInterfaceIconImageFile = systemTrayInterfaceIconImageFile;
	}

	/**
	 * @param useUserAuthentication
	 *            the useUserAuthentication to set
	 */
	public final void setUseUserAuthentication(final boolean useUserAuthentication) {
		this.useUserAuthentication = useUserAuthentication;
	}

	/**
	 * @param webInterfaceFileDetailsOutputItem
	 *            the webInterfaceFileDetailsOutputItem to set
	 */
	public final void setWebInterfaceFileDetailsOutputItem(final AbstractFileDetailsOutputItem webInterfaceFileDetailsOutputItem) {
		this.webInterfaceFileDetailsOutputItem = webInterfaceFileDetailsOutputItem;
	}

	/**
	 * @param webInterfacePageConfigs
	 *            the webInterfacePageConfigs to set
	 */
	public final void setWebInterfacePageConfigs(final ArrayList<WebPageConfig> webInterfacePageConfigs) {
		this.webInterfacePageConfigs = webInterfacePageConfigs;
	}

}
