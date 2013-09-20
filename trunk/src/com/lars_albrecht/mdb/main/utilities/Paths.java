/**
 * 
 */
package com.lars_albrecht.mdb.main.utilities;

import java.io.File;

/**
 * @author lalbrecht
 * 
 */
public class Paths {

	/**
	 * Path to the current working directory.
	 */
	public static final File	ROOT				= new File(new File("").getAbsolutePath());

	/**
	 * Path to the current resource directory (path to trunk).
	 */
	public static final File	WORKING				= new File(Paths.ROOT + File.separator + "trunk");

	/**
	 * Path to the web root directory for the web interface.
	 */
	public static final File	WEB_ROOT			= new File(Paths.WORKING + File.separator + "web");

	/**
	 * Path to the resource directory of the web interface.
	 */
	public static final File	WEB_RESOURCES		= new File(Paths.WEB_ROOT + File.separator + "resources");

	/**
	 * Path to the pages directory of the web interface.
	 */
	public static final File	WEB_PAGES			= new File(Paths.WEB_ROOT + File.separator + "pages");

	/**
	 * Path to the partials directory of the web interface.
	 */
	public static final File	WEB_PAGES_PARTIALS	= new File(Paths.WEB_PAGES + File.separator + "partials");

	/**
	 * Path to the resources of the tray interface.
	 */
	public static final File	TRAY				= new File(Paths.WORKING + File.separator + "tray");

}
