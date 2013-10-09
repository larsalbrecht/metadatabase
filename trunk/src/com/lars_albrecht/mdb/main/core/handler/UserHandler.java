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

	private static final User	exampleUserObject	= new User(0, "email@example.com", "example", null);

	public static User doLogin(final String email, final String password) {
		final String salt = ""; // get salt for user
		final String saltedPassword = UserHandler.saltPassword(password, salt);
		final String pepperedPassword = UserHandler.pepperPassword(saltedPassword);
		final String hashedPassword = UserHandler.hashPassword(pepperedPassword);
		if (hashedPassword == null) {
			return null;
		}

		// search for email / saltedHashedPassword in database and return the
		// complete user-object.
		final User databaseUser = UserHandler.getUser(email, hashedPassword);

		if (databaseUser != null) {
			return databaseUser;
		} else {
			return null;
		}
	}

	/**
	 * Pepper a string. "pepper" is a global string, that is used to pepper all
	 * passwords. The pepper is saved on a secure place, not in the database.
	 * 
	 * @param password
	 * @return
	 */
	public static String pepperPassword(final String password) {
		return password + "mysecret from secure place";
	}

	/**
	 * Salt a string. "salt" is a user string, that is used to salt the user
	 * password. The salt is saved in the database for every user.
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	public static String saltPassword(final String password, final String salt) {
		return password + salt;
	}

	/**
	 * Hash a string.
	 * 
	 * @param password
	 * @return
	 */
	public static String hashPassword(final String password) {
		try {
			return new String(new ChecksumSHA2("SHA-512").digest(password.getBytes()));
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
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

	public static User getUser(final String identifier, final String hashedPassword) {
		if (identifier.equalsIgnoreCase(UserHandler.exampleUserObject.getIdentifier())) {
			return UserHandler.exampleUserObject;
		} else {
			return null;
		}
	}

}
