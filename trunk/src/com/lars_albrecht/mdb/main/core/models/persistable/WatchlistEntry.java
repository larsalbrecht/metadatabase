/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 */
public class WatchlistEntry implements IPersistable {

	private Integer		id			= null;
	private User		user		= null;
	private Integer		createTS	= null;
	private FileItem	fileItem	= null;
	private Watchlist	watchlist	= null;

	public WatchlistEntry() {
	}

	public WatchlistEntry(final Integer id) {
		super();
		this.id = id;
	}

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
		final WatchlistEntry other = (WatchlistEntry) obj;
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
		if (this.fileItem == null) {
			if (other.fileItem != null) {
				return false;
			}
		} else if (!this.fileItem.equals(other.fileItem)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		if (this.watchlist == null) {
			if (other.watchlist != null) {
				return false;
			}
		} else if (!this.watchlist.equals(other.watchlist)) {
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
		final WatchlistEntry resultItem = new WatchlistEntry();

		if (map.containsKey("id")) {
			resultItem.setId((Integer) map.get("id"));
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

		if (map.containsKey("watchlist_id") && (map.get("watchlist_id") instanceof Integer)) {
			resultItem.setWatchlist(new Watchlist((Integer) map.get("watchlist_id")));
			// TODO add caching to use this
			// final ArrayList<Object> watchlist = DataHandler.findAll(new
			// Watchlist((Integer) map.get("watchlist_id")), 1, null, null);
			// if ((watchlist != null) && (watchlist.size() > 0)) {
			// resultItem.setWatchlist((Watchlist) watchlist.get(0));
			// }
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
		return "watchlistEntry";
	}

	public FileItem getFileItem() {
		return this.fileItem;
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

	public User getUser() {
		return this.user;
	}

	public Watchlist getWatchlist() {
		return this.watchlist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (this.id == null) {
			result = (prime * result) + ((this.user == null) ? 0 : this.user.hashCode());
			result = (prime * result) + ((this.createTS == null) ? 0 : this.createTS.hashCode());
			result = (prime * result) + ((this.fileItem == null) ? 0 : this.fileItem.hashCode());
			result = (prime * result) + ((this.watchlist == null) ? 0 : this.watchlist.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}
		return result;
	}

	public void setCreateTS(final Integer createTS) {
		this.createTS = createTS;
	}

	public void setFileItem(final FileItem fileItem) {
		this.fileItem = fileItem;
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

	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @param watchlist
	 *            the watchlist to set
	 */
	public void setWatchlist(final Watchlist watchlist) {
		this.watchlist = watchlist;
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

		if (this.getCreateTS() != null) {
			tempHashMap.put("createTS", this.getCreateTS());
		}

		if ((this.getUser() != null) && (this.getUser().getId() != null)) {
			tempHashMap.put("user_id", this.getUser().getId());
		}

		if ((this.getFileItem() != null) && (this.getFileItem().getId() != null)) {
			tempHashMap.put("file_id", this.getFileItem().getId());
		}

		if ((this.getWatchlist() != null) && (this.getWatchlist().getId() != null)) {
			tempHashMap.put("watchlist_id", this.getWatchlist().getId());
		}

		return tempHashMap;
	}

}
