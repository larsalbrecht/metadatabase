/**
 * 
 */
package com.lars_albrecht.mdb.main.core.controller;

import java.util.ArrayList;

import com.lars_albrecht.mdb.main.core.abstracts.ThreadEx;
import com.lars_albrecht.mdb.main.core.controller.interfaces.IController;

/**
 * @author lalbrecht
 * 
 */
public class WatchlistController implements IController {

	final ArrayList<ThreadEx>	threadList	= new ArrayList<ThreadEx>();

	@Override
	public ArrayList<ThreadEx> getThreadList() {
		return this.threadList;
	}

	@Override
	public void run(final Object... params) throws Exception {
	}

}
