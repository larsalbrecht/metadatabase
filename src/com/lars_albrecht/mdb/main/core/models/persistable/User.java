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
	private String	password	= null;

	/**
	 * 
	 */
	public User() {
		super();
	}

	/**
	 * @param id
	 */
	public User(final Integer id) {
		super();
		this.id = id;
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

	/**
	 * @param id
	 * @param email
	 * @param name
	 * @param password
	 */
	public User(final Integer id, final String email, final String name, final String password) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
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
		if (!(obj instanceof User)) {
			return false;
		}
		final User other = (User) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
		}
		if (this.email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!this.email.equals(other.email)) {
			return false;
		}
		if (this.lastLoginTS == null) {
			if (other.lastLoginTS != null) {
				return false;
			}
		} else if (!this.lastLoginTS.equals(other.lastLoginTS)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!this.password.equals(other.password)) {
			return false;
		}
		return true;
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

		if (map.containsKey("password") && (map.get("password") != null) && (map.get("password") instanceof String)) {
			resultItem.setPassword((String) map.get("password"));
		}

		return resultItem;
	}

	@Override
	public String getDatabaseTable() {
		return "users";
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
	 * @return the password
	 */
	public final String getPassword() {
		return this.password;
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
			result = (prime * result) + ((this.email == null) ? 0 : this.email.hashCode());
			result = (prime * result) + ((this.lastLoginTS == null) ? 0 : this.lastLoginTS.hashCode());
			result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
			result = (prime * result) + ((this.password == null) ? 0 : this.password.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}

		return result;
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

	/**
	 * @param password
	 *            the password to set
	 */
	public final void setPassword(final String password) {
		this.password = password;
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

		if (this.password != null) {
			tempHashMap.put("password", this.password);
		}

		return tempHashMap;
	}

}
