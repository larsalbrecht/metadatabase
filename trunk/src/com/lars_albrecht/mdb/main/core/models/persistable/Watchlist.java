/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.handler.WatchlistHandler;
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
	private Integer						createTS	= null;

	public Watchlist() {
	}

	public Watchlist(final Integer id) {
		super();
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Watchlist other = (Watchlist) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
		}
		if (this.createTS == null) {
			if (other.createTS != null) {
				return false;
			}
		} else if (!this.createTS.equals(other.createTS)) {
			return false;
		}
		if (this.fileIdList == null) {
			if (other.fileIdList != null) {
				return false;
			}
		} else if (!this.fileIdList.equals(other.fileIdList)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		return true;
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
		final Watchlist resultItem = new Watchlist();

		if (map.containsKey("id")) {
			resultItem.setId((Integer) map.get("id"));
		}

		if (map.containsKey("name") && (map.get("name") != null)) {
			resultItem.setName((String) map.get("name"));
		}

		if (map.containsKey("createTS") && (map.get("createTS") != null)) {
			final SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				resultItem.setCreateTS(((Long) (sdfToDate.parse((String) map.get("createTS")).getTime() / 1000)).intValue());
			} catch (final ParseException e) {
				e.printStackTrace();
			}
		}

		if (map.containsKey("user_id") && (map.get("user_id") instanceof Integer)) {
			final ArrayList<Object> userList = DataHandler.findAll(new User((Integer) map.get("user_id")), 1, null, null);
			if (userList.size() > 0) {
				resultItem.setUser((User) userList.get(0));
			}
		}

		if (map.containsKey("file_id") && (map.get("file_id") instanceof Integer)) {
			final ArrayList<Object> userList = DataHandler.findAll(new FileItem((Integer) map.get("user_id")), 1, null, null);
			if (userList.size() > 0) {
				resultItem.setUser((User) userList.get(0));
			}
		}

		if (resultItem.getId() != null) {
			resultItem.setFileIdList(WatchlistHandler.getWatchlistEntriesForWatchlist(resultItem));
		}

		return resultItem;
	}

	public Integer getCreateTS() {
		return this.createTS;
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
		if (this.fileIdList == null) {
			this.fileIdList = new ArrayList<WatchlistEntry>();
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (this.id == null) {
			result = (prime * result) + ((this.createTS == null) ? 0 : this.createTS.hashCode());
			result = (prime * result) + ((this.fileIdList == null) ? 0 : this.fileIdList.hashCode());
			result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
			result = (prime * result) + ((this.user == null) ? 0 : this.user.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}
		return result;
	}

	public void setCreateTS(final Integer createTS) {
		this.createTS = createTS;
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
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
		if (this.getId() != null) {
			tempHashMap.put("id", this.getId());
		}

		if ((this.getName() != null)) {
			tempHashMap.put("name", this.getName());
		}

		if ((this.getUser() != null) && (this.getUser().getId() != null)) {
			tempHashMap.put("user_id", this.getUser().getId());
		}

		if (this.getCreateTS() != null) {
			tempHashMap.put("createTS", this.getCreateTS());
		}

		return tempHashMap;
	}

}
