# Introduction #

If you want to use the watchlist in your code, use this code example.


# Example code #
```java


// created an empty watchlist
final Watchlist testlist = new Watchlist();
// created an emptry entry
final WatchlistEntry we = new WatchlistEntry();
// set the user to the watchlist entry
we.setUser(new User(0));
// set the fileitem to the watchlist entry
we.setFileItem(new FileItem(1));
// set the name of the watchlist
testlist.setName("Testname");
// add items to the watchlist
testlist.getFileIdList().add(we);
// add user to the watchlist
testlist.setUser(new User(0));
try {
// persist all data to the database
// the entries will be saved automatically
WatchlistHandler.addWatchlist(testlist);
} catch (final Exception e) {
e.printStackTrace();
}
```

# More code explanation #
The Watchlist is designed to handle different use cases:
  * Every user use (a) global watchlist(s)
  * Every user has own watchlists
  * Every user has own watchlists and global watchlists

In the database are all information for this. Every watchlist has an user and every watchlist-entry has an user.

More infos about [user](user.md)