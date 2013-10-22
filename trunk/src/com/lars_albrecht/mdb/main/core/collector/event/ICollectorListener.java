/**
 * 
 */
package com.lars_albrecht.mdb.main.core.collector.event;

import java.util.EventListener;

/**
 * @author lalbrecht
 * 
 */
public interface ICollectorListener extends EventListener {

	/**
	 * Called after all collectors has finished.
	 * 
	 * @param e
	 */
	void collectorsEndAll(CollectorEvent e);

	/**
	 * Called after a single collector has finished.
	 * 
	 * @param e
	 */
	void collectorsEndSingle(CollectorEvent e);

}
