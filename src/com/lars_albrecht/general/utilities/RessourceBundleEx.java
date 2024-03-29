/**
 * 
 */
package com.lars_albrecht.general.utilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Read out properties from *.properties-files to access strings for e.g.
 * localisation.
 * 
 * @author lalbrecht
 * @version 1.0.1.0
 * 
 */
public final class RessourceBundleEx {

	private static RessourceBundleEx							instance	= null;
	private static ConcurrentHashMap<String, RessourceBundleEx>	instances	= null;

	private static Locale										locale		= Locale.getDefault();

	private static String										prefix		= "";

	/**
	 * 
	 * @return PropertiesReader
	 */
	@Deprecated
	public static RessourceBundleEx getInstance() {

		if (RessourceBundleEx.instance == null) {
			RessourceBundleEx.instance = new RessourceBundleEx();
		}
		return RessourceBundleEx.instance;
	}

	/**
	 * Returns a RessourceBundleEx with prefix "key".
	 * 
	 * @param key
	 *            String
	 * @return PropertiesReader
	 */
	public static RessourceBundleEx getInstance(final String key) {
		if (RessourceBundleEx.instances == null) {
			RessourceBundleEx.instances = new ConcurrentHashMap<String, RessourceBundleEx>();
		}
		if (!RessourceBundleEx.instances.containsKey(key) || (RessourceBundleEx.instances.get(key) == null)) {
			RessourceBundleEx.instances.put(key, new RessourceBundleEx());
			RessourceBundleEx.instances.get(key);
			RessourceBundleEx.setPrefix(key);
		}

		return RessourceBundleEx.instances.get(key);
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public static synchronized final void setLocale(final Locale locale) {
		RessourceBundleEx.locale = locale;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public static synchronized final void setPrefix(final String prefix) {
		RessourceBundleEx.prefix = prefix;
	}

	/**
	 * Private default constructor.
	 */
	private RessourceBundleEx() {
	}

	public Boolean contains(final String key) {
		return ResourceBundle.getBundle(RessourceBundleEx.prefix, RessourceBundleEx.locale).containsKey(key);
	}

	/**
	 * @return the locale
	 */
	public synchronized final Locale getLocale() {
		return RessourceBundleEx.locale;
	}

	/**
	 * @return the prefix
	 */
	public synchronized final String getPrefix() {
		return RessourceBundleEx.prefix;
	}

	public ArrayList<String> getProperties(final String key) {
		final ArrayList<String> resultList = new ArrayList<String>();
		int i = 1;
		while (this.contains(key + "." + i)) {
			resultList.add(this.getProperty(key + "." + i));
			i++;
		}
		return resultList;
	}

	/**
	 * Returns the value of the key. Converts the iso-8859-1 Strings to UTF-8.
	 * 
	 * @param key
	 * @return String
	 */
	public String getProperty(final String key) {
		try {
			final ResourceBundle bundle = ResourceBundle.getBundle(RessourceBundleEx.prefix, RessourceBundleEx.locale);
			return new String(bundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
		} catch (final MissingResourceException e) {
			e.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
