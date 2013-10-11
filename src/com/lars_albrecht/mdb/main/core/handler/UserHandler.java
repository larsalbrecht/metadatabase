/**
 * 
 */
package com.lars_albrecht.mdb.main.core.handler;

import java.security.NoSuchAlgorithmException;

import com.lars_albrecht.general.utilities.ChecksumSHA2;
import com.lars_albrecht.mdb.main.core.models.persistable.User;

/**
 * @author lalbrecht
 * 
 *         This class handles the user-object. Currently there are only basic
 *         functions with test data.
 * 
 */
public class UserHandler {

	public static boolean		usersEnabled		= Boolean.TRUE;

	/**
	 * Only a var to test the login
	 */
	public static boolean		isLoggedIn			= Boolean.FALSE;

	private static final User	exampleUserObject	= new User(0, "email@example.com", "example", null);

	/**
	 * Do a login for a user. The password will be salted, peppered and than
	 * hashed.
	 * 
	 * @param identifier
	 * @param password
	 * @return
	 */
	public static User doLogin(final String identifier, final String password) {
		final String hashedPassword = UserHandler.getPreparedPassword(identifier, password);
		if (hashedPassword == null) {
			return null; // no user available (no salt for identifier found)
		}

		// search for email / saltedHashedPassword in database and return the
		// complete user-object.
		final User databaseUser = UserHandler.getUser(identifier, hashedPassword);

		if (databaseUser != null) {
			UserHandler.isLoggedIn = Boolean.TRUE;
			return databaseUser;
		} else {
			return null;
		}
	}

	public static boolean doLogout(final String identifier) {
		UserHandler.isLoggedIn = false;

		return !UserHandler.isLoggedIn;
	}

	public static User getCurrentUser() {
		if (!UserHandler.isLoggedIn()) {
			return null;
		}
		return UserHandler.exampleUserObject;
	}

	/**
	 * Returns the hashed password.
	 * 
	 * @param identifier
	 * @param password
	 * @return hashed password
	 */
	public static String getPreparedPassword(final String identifier, final String password) {
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
		if (identifier.equalsIgnoreCase(UserHandler.exampleUserObject.getIdentifier())) {
			return "mysalt";
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
		if (identifier.equalsIgnoreCase(UserHandler.exampleUserObject.getIdentifier())) {
			return UserHandler.exampleUserObject;
		} else {
			return null;
		}
	}

	/**
	 * Hash a string.
	 * 
	 * @param password
	 * @return hashed password
	 */
	public static String hashPassword(final String password) {
		try {
			return new String(new ChecksumSHA2("SHA-512").digest(password.getBytes()));
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isLoggedIn() {
		return UserHandler.isLoggedIn;
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

		return UserHandler.exampleUserObject;
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

}
