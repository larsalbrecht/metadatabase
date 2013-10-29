/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 */
public class Watchlist implements IPersistable {

	private Integer						id			= null;
	private String						name		= null;
	private ArrayList<WatchlistEntry>	fileIdList	= null;
	private User						user		= null;

	public Watchlist() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable#fromHashMap
	 * (java.util.HashMap)
	 */
	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable#
	 * getDatabaseTable()
	 */
	@Override
	public String getDatabaseTable() {
		return "watchlist";
	}

	public ArrayList<WatchlistEntry> getFileIdList() {
		return this.fileIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable#getId()
	 */
	@Override
	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public User getUser() {
		return this.user;
	}

	public void setFileIdList(final ArrayList<WatchlistEntry> fileIdList) {
		this.fileIdList = fileIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable#setId(
	 * java.lang.Integer)
	 */
	@Override
	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable#toHashMap
	 * ()
	 */
	@Override
	public HashMap<String, Object> toHashMap() {
		return null;
	}

}
