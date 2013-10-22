/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.lars_albrecht.general.utilities.ChecksumSHA1;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.models.persistable.User;

/**
 * @author lalbrecht
 * 
 *         This class handles the user-object. Currently there are only basic
 *         functions with test data.
 * 
 */
public class UserHandler {

	public static boolean	usersEnabled	= Boolean.FALSE;

	/**
	 * Do a login for a user. The password will be salted, peppered and than
	 * hashed.
	 * 
	 * @param identifier
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static User doLogin(final String identifier, final String password) throws Exception {
		final String hashedPassword = UserHandler.getPreparedPassword(identifier, password);
		if (hashedPassword == null) {
			return null; // no user available (no salt for identifier found)
		}

		// search for email / saltedHashedPassword in database and return the
		// complete user-object.
		final User databaseUser = UserHandler.getUser(identifier, hashedPassword);

		if (databaseUser != null) {
			return databaseUser;
		} else {
			return null;
		}
	}

	public static boolean doLogout(final String identifier) {
		return false;
	}

	public static String generateUserToken(final User user, final String[] additional) {
		String token = null;
		if ((user != null) && (additional.length > 0)) {
			token = user.getIdentifier() + user.getEmail() + Helper.implode(additional, "", null, null);
			try {
				return ChecksumSHA1.getSHA1ChecksumString(token);
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}
		return token;
	}

	/**
	 * Returns the hashed password.
	 * 
	 * @param identifier
	 * @param password
	 * @return hashed password
	 * @throws Exception
	 */
	public static String getPreparedPassword(final String identifier, final String password) throws Exception {
		final String salt = UserHandler.getSaltForIdentifier(identifier);
		if (salt == null) {
			return null;
		}
		final String saltedPassword = UserHandler.saltPassword(password, salt);
		final String pepperedPassword = UserHandler.pepperPassword(saltedPassword);
		final String hashedPassword = UserHandler.hashPassword(pepperedPassword);

		if (hashedPassword == null) {
			try {
				throw new Exception("\"hashedPassword\" could not be null, null found.");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return hashedPassword;
	}

	/**
	 * Fetch the salt from the db for the given identifier.
	 * 
	 * @param identifier
	 * @return salt
	 */
	public static String getSaltForIdentifier(final String identifier) {
		if (identifier.equalsIgnoreCase("email@example.com")) {
			return "";
		}
		return null;
	}

	public static User getUser(final int id) {
		if (id > -1) {
			final ArrayList<Object> tempList = DataHandler.findAll(new User(id), 1, null, null);
			if ((tempList.size() > 0) && (tempList.get(0) instanceof User)) {
				return (User) tempList.get(0);
			}
		}
		return null;
	}

	/**
	 * Get the whole user from the database.
	 * 
	 * @param identifier
	 * @param hashedPassword
	 * @return User
	 */
	public static User getUser(final String identifier, final String hashedPassword) {
		if ((identifier != null) && (hashedPassword != null)) {
			final ArrayList<Object> tempUserList = DataHandler.findAll(new User(), 1, "email = '" + identifier + "'", null);
			if ((tempUserList.size() > 0) && (tempUserList.get(0) != null) && (tempUserList.get(0) instanceof User)
					&& ((User) tempUserList.get(0)).getPassword().equals(hashedPassword)) {
				return (User) tempUserList.get(0);
			}
		}
		return null;
	}

	/**
	 * Hash a string.
	 * 
	 * @param password
	 * @return hashed password
	 * @throws Exception
	 */
	public static String hashPassword(final String password) throws Exception {
		try {
			return ChecksumSHA1.getSHA1ChecksumString(password);
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pepper a string. "pepper" is a global string, that is used to pepper all
	 * passwords. The pepper is saved on a secure place, not in the database.
	 * 
	 * @param password
	 * @return peppered password (password + pepper)
	 */
	public static String pepperPassword(final String password) {
		return password + "mysecret from secure place";
	}

	/**
	 * Register a user.
	 * 
	 * @param user
	 * @return
	 */
	public static User registerUser(final User user) {
		if (user == null) {
			return null;
		}

		return null;
	}

	/**
	 * Salt a string. "salt" is a user string, that is used to salt the user
	 * password. The salt is saved in the database for every user.
	 * 
	 * @param password
	 * @param salt
	 * @return salted password (password + salt)
	 */
	public static String saltPassword(final String password, final String salt) {
		return password + salt;
	}

	public static boolean useUserLogin() {
		return UserHandler.usersEnabled;
	}

	public static boolean useUserLogin(final boolean usersEnabled) {
		return UserHandler.usersEnabled = usersEnabled;
	}

}
