/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.util.ArrayList;
import java.util.List;

import com.lars_albrecht.mdb.main.core.models.persistable.User;
import com.lars_albrecht.mdb.main.core.models.persistable.Watchlist;
import com.lars_albrecht.mdb.main.core.models.persistable.WatchlistEntry;

/**
 * @author lalbrecht
 * 
 */
public class WatchlistHandler {

	public static boolean	watchlistEnabled	= Boolean.FALSE;

	/**
	 * Persists a watchlist with watchlistentries.
	 * 
	 * @param watchlist
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static boolean addWatchlist(final Watchlist watchlist, final User user) throws Exception {
		if ((watchlist != null) && (user != null)) {
			DataHandler.persist(watchlist, Boolean.FALSE);

			final Watchlist completeWatchlist = WatchlistHandler.getFilledWatchlistFromWatchlist(watchlist);
			watchlist.setId(completeWatchlist.getId());
			watchlist.setCreateTS(completeWatchlist.getCreateTS());

			if ((watchlist.getFileIdList() != null) && (watchlist.getFileIdList().size() > 0)) {
				for (final WatchlistEntry entry : watchlist.getFileIdList()) {
					entry.setWatchlist(watchlist);
				}

				WatchlistHandler.addWatchlistEntries(watchlist.getFileIdList());
			}

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Persists watchlistEntries.
	 * 
	 * @param watchlistEntries
	 * @return
	 * @throws Exception
	 */
	public static boolean addWatchlistEntries(final ArrayList<WatchlistEntry> watchlistEntries) throws Exception {
		if (watchlistEntries != null) {
			DataHandler.persist(watchlistEntries, false);
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static Watchlist getFilledWatchlistFromWatchlist(final Watchlist watchlist) {
		Watchlist resultList = null;
		if ((watchlist != null) && (watchlist.getName() != null) && (watchlist.getUser() != null)) {
			ArrayList<Object> watchlistsObj = null;
			watchlistsObj = DataHandler.findAll(new Watchlist(), 1, "user_id = '" + watchlist.getUser().getId() + "' AND name = '"
					+ watchlist.getName() + "'", "createTS");
			if ((watchlistsObj != null) && (watchlistsObj.size() > 0)) {
				resultList = (Watchlist) watchlistsObj.get(0);
			}

		}

		return resultList;
	}

	/**
	 * Returns a list of WatchlistEntries of a watchlist.
	 * 
	 * @param watchlist
	 * @return ArrayList<WatchlistEntry>
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<WatchlistEntry> getWatchlistEntriesForWatchlist(final Watchlist watchlist) {
		ArrayList<WatchlistEntry> resultList = null;
		if (watchlist != null) {
			resultList = new ArrayList<WatchlistEntry>();
			final ArrayList<Object> tempList = DataHandler.findAll(new WatchlistEntry(), null, "watchlist_id = " + watchlist.getId(),
					"createTS");
			resultList = (ArrayList<WatchlistEntry>) (List<?>) tempList;
		}
		return resultList;
	}

	/**
	 * Search all watchlists by user, ordered by createTS.
	 * 
	 * @param user
	 * @return ArrayList<Watchlist>
	 */
	public static ArrayList<Watchlist> getWatchlistsForUser(final User user) {
		ArrayList<Object> watchlistsObj = null;
		ArrayList<Watchlist> watchlists = null;
		if ((user != null) && (user.getId() != null)) {
			watchlistsObj = DataHandler.findAll(new Watchlist(), null, "user_id = " + user.getId(), "createTS");
			if ((watchlistsObj != null) && (watchlistsObj.size() > 0)) {
				watchlists = new ArrayList<Watchlist>();
				for (final Object object : watchlistsObj) {
					if (object != null) {
						watchlists.add((Watchlist) object);
					}
				}
			}
		}
		return watchlists;
	}

}
