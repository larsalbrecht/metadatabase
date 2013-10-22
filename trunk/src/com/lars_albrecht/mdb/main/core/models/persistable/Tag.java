/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 */
public class Tag implements IPersistable {

	private Integer	id		= null;
	private String	name	= null;
	private Boolean	isUser	= null;
	private User	user	= null;

	public Tag() {
	}

	public Tag(final Integer id) {
		super();
		this.id = id;
	}

	public Tag(final Integer id, final String name, final Boolean isUser) {
		super();
		this.id = id;
		this.name = name;
		this.isUser = isUser;
	}

	public Tag(final Integer id, final String name, final Boolean isUser, final User user) {
		super();
		this.id = id;
		this.name = name;
		this.isUser = isUser;
		this.user = user;
	}

	public Tag(final String name, final Boolean isUser) {
		super();
		this.name = name;
		this.isUser = isUser;
	}

	public Tag(final String name, final Boolean isUser, final User user) {
		super();
		this.name = name;
		this.isUser = isUser;
		this.user = user;
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
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
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
			if (map.get("isuser") instanceof Boolean) {
				result.setIsUser((Boolean) map.get("isuser"));
			} else if (map.get("isuser") instanceof Integer) {
				result.setIsUser((Integer) map.get("isuser") == 0 ? false : true);
			}
		}
		if (map.containsKey("user_id")) {
			if (map.get("user_id") instanceof Integer) {
				final ArrayList<Object> userList = DataHandler.findAll(new User((Integer) map.get("user_id")), 1, null, null);
				if (userList.size() > 0) {
					result.setUser((User) userList.get(0));
				}
			}
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

	/**
	 * @return the user
	 */
	public final User getUser() {
		return this.user;
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
			result = (prime * result) + ((this.user == null) ? 0 : this.user.hashCode());
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

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(final User user) {
		this.user = user;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
		if (this.getId() != null) {
			tempHashMap.put("id", this.getId());
		}
		tempHashMap.put("name", this.getName());
		tempHashMap.put("isuser", this.getIsUser());
		if ((this.getUser() != null) && (this.getUser().getId() != null)) {
			tempHashMap.put("user_id", this.user.getId());
		}

		return tempHashMap;
	}

	@Override
	public String toString() {
		return this.id + " | " + this.name + " | " + this.isUser + " | " + this.user.getIdentifier();
	}

}
