/**
 * 
 */
package com.lars_albrecht.mdb.main.core.finder.event;

import java.util.EventListener;

/**
 * @author lalbrecht
 * 
 */
public interface IFinderListener extends EventListener {

	/**
	 * Called after all directories are searched for new files. Event contains
	 * an ArrayList<File> with all found files.
	 * 
	 * @param e
	 */
	void finderAddFinish(FinderEvent e);

	/**
	 * Called after found file-list is added. Event contains an ArrayList<File>
	 * with all found files.
	 * 
	 * @param e
	 */
	void finderAfterAdd(FinderEvent e);

	/**
	 * Called when a directory is found. Event contains an ArrayList<File> with
	 * the found directory.
	 * 
	 * @param e
	 */
	void finderFoundDir(FinderEvent e);

	/**
	 * Called when a file is found. Event contains an ArrayList<File> with the
	 * found file.
	 * 
	 * @param e
	 */
	void finderFoundFile(FinderEvent e);

	/**
	 * Called before found file-list is added. Event contains an ArrayList<File>
	 * with the found files that will be added.
	 * 
	 * @param e
	 */
	void finderPreAdd(FinderEvent e);

}
