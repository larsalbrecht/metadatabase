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
public class FileTag implements IPersistable {

	private Integer	id		= null;
	private Integer	fileId	= null;
	private Tag		tag		= null;
	private Boolean	isUser	= null;
	private User	user	= null;

	public FileTag() {
	}

	/**
	 * @param id
	 * @param fileId
	 * @param tag
	 * @param isUser
	 */
	public FileTag(final Integer id, final Integer fileId, final Tag tag, final Boolean isUser) {
		super();
		this.id = id;
		this.fileId = fileId;
		this.tag = tag;
		this.isUser = isUser;
	}

	/**
	 * @param id
	 * @param fileId
	 * @param tag
	 * @param isUser
	 * @param user
	 */
	public FileTag(final Integer id, final Integer fileId, final Tag tag, final Boolean isUser, final User user) {
		super();
		this.id = id;
		this.fileId = fileId;
		this.tag = tag;
		this.isUser = isUser;
		this.user = user;
	}

	/**
	 * @param fileId
	 * @param tag
	 * @param isUser
	 */
	public FileTag(final Integer fileId, final Tag tag, final Boolean isUser) {
		super();
		this.fileId = fileId;
		this.tag = tag;
		this.isUser = isUser;
	}

	/**
	 * @param fileId
	 * @param tag
	 * @param isUser
	 * @param user
	 */
	public FileTag(final Integer fileId, final Tag tag, final Boolean isUser, final User user) {
		super();
		this.fileId = fileId;
		this.tag = tag;
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
		if (!(obj instanceof FileTag)) {
			return false;
		}
		final FileTag other = (FileTag) obj;
		if (this.fileId == null) {
			if (other.fileId != null) {
				return false;
			}
		} else if (!this.fileId.equals(other.fileId)) {
			return false;
		}
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
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
		if (this.tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!this.tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final FileTag result = new FileTag();
		if (map.containsKey("id")) {
			result.setId((Integer) map.get("id"));
		}
		if (map.containsKey("file_id")) {
			result.setFileId((Integer) map.get("file_id"));
		}
		if (map.containsKey("tag_id") && map.containsKey("tag_name") && map.containsKey("tagIsUser")) {
			result.setTag(new Tag((Integer) map.get("tag_id"), (String) map.get("tag_name"), (Boolean) map.get("tagIsUser")));
		} else if (map.containsKey("tag_name") && map.containsKey("tagIsUser")) {
			result.setTag(new Tag((String) map.get("tag_name"), (Boolean) map.get("tagIsUser")));
		} else if (map.containsKey("tag_id")) {
			result.setTag(new Tag((Integer) map.get("tag_id")));
		}
		if (map.containsKey("isuser")) {
			result.setIsUser(map.get("isuser") instanceof Integer ? (Integer) map.get("isuser") == 0 ? false : true : (Boolean) map
					.get("isuser"));
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
		return "fileTags";
	}

	/**
	 * @return the fileId
	 */
	public final Integer getFileId() {
		return this.fileId;
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
	 * @return the tag
	 */
	public final Tag getTag() {
		return this.tag;
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
			result = (prime * result) + ((this.fileId == null) ? 0 : this.fileId.hashCode());
			result = (prime * result) + ((this.isUser == null) ? 0 : this.isUser.hashCode());
			result = (prime * result) + ((this.tag == null) ? 0 : this.tag.hashCode());
			result = (prime * result) + ((this.user == null) ? 0 : this.user.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}
		return result;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public final void setFileId(final Integer fileId) {
		this.fileId = fileId;
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
	 * @param tag
	 *            the tag to set
	 */
	public final void setTag(final Tag tag) {
		this.tag = tag;
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
		tempHashMap.put("file_id", this.getFileId());
		tempHashMap.put("tag_id", this.getTag().getId());
		tempHashMap.put("isuser", this.getIsUser());
		if ((this.getUser() != null) && (this.getUser().getId() != null)) {
			tempHashMap.put("user_id", this.user.getId());
		}

		return tempHashMap;
	}

	@Override
	public String toString() {
		return this.id + " | " + this.fileId + " | " + this.tag + " | " + this.isUser + " | " + this.user.getIdentifier();
	}

}
