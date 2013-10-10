/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 */
public class User implements IPersistable {

	private Integer	id			= null;
	private String	email		= null;
	private String	name		= null;
	private Integer	lastLoginTS	= null;

	/**
	 * 
	 */
	public User() {
		super();
	}

	/**
	 * @param id
	 * @param email
	 * @param name
	 * @param lastLoginTS
	 */
	public User(final Integer id, final String email, final String name, final Integer lastLoginTS) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.lastLoginTS = lastLoginTS;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final User resultItem = new User();
		if (map.containsKey("id")) {
			resultItem.setId((Integer) map.get("id"));
		}
		if (map.containsKey("name")) {
			resultItem.setName((String) map.get("name"));
		}
		if (map.containsKey("email")) {
			resultItem.setEmail((String) map.get("email"));
		}

		if (map.containsKey("lastLogin") && (map.get("lastLogin") != null)) {
			final SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				resultItem.setLastLoginTS(((Long) (sdfToDate.parse((String) map.get("lastLogin")).getTime() / 1000)).intValue());
			} catch (final ParseException e) {
				e.printStackTrace();
			}

		}

		return resultItem;
	}

	@Override
	public String getDatabaseTable() {
		return "user";
	}

	/**
	 * @return the email
	 */
	public final String getEmail() {
		return this.email;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public final String getIdentifier() {
		return this.email;
	}

	/**
	 * @return the lastLogin
	 */
	public final Integer getLastLoginTS() {
		return this.lastLoginTS;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public final void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * @param lastLogin
	 *            the lastLogin to set
	 */
	public final void setLastLoginTS(final Integer lastLogin) {
		this.lastLoginTS = lastLogin;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
		if (this.getId() != null) {
			tempHashMap.put("id", this.getId());
		}
		tempHashMap.put("name", this.getName());
		tempHashMap.put("email", this.getEmail());

		if (this.getLastLoginTS() != null) {
			tempHashMap.put("lastLogin", this.getLastLoginTS());
		}

		return tempHashMap;
	}

}
