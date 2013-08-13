/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 */
public class Tag implements IPersistable {

	private Integer	id		= null;
	private String	name	= null;
	private Boolean	isUser	= null;

	public Tag() {
	}

	public Tag(final Integer id) {
		super();
		this.id = id;
	}

	public Tag(final Integer id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Tag(final Integer id, final String name, final Boolean isUser) {
		super();
		this.id = id;
		this.name = name;
		this.isUser = isUser;
	}

	public Tag(final String name) {
		super();
		this.name = name;
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
		if (!(obj instanceof Tag)) {
			return false;
		}
		final Tag other = (Tag) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.isUser == null) {
			if (other.isUser != null) {
				return false;
			}
		} else if (!this.isUser.equals(other.isUser)) {
			return false;
		}
		return true;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final Tag result = new Tag();
		if (map.containsKey("id")) {
			result.setId((Integer) map.get("id"));
		}
		if (map.containsKey("name")) {
			result.setName((String) map.get("name"));
		}
		if (map.containsKey("isuser")) {
			result.setIsUser((Boolean) map.get("isuser"));
		}

		return result;
	}

	@Override
	public String getDatabaseTable() {
		return "tags";
	}

	/**
	 * @return the id
	 */
	@Override
	public final Integer getId() {
		return this.id;
	}

	/**
	 * @return the isUser
	 */
	public final Boolean getIsUser() {
		return this.isUser;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
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
			result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
			result = (prime * result) + ((this.isUser == null) ? 0 : this.isUser.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}

		return result;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@Override
	public final void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * @param isUser
	 *            the isUser to set
	 */
	public final void setIsUser(final Boolean isUser) {
		this.isUser = isUser;
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
		tempHashMap.put("isuser", this.getIsUser());

		return tempHashMap;
	}

	@Override
	public String toString() {
		return this.id + " | " + this.name + " | " + this.isUser;
	}

}
