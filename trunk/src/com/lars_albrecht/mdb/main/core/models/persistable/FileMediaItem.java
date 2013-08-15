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
public class FileMediaItem implements IPersistable {

	private Integer	id		= null;
	private Integer	fileId	= null;
	private Integer	mediaId	= null;

	public FileMediaItem() {
	}

	/**
	 * @param fileId
	 * @param mediaId
	 */
	public FileMediaItem(final Integer fileId, final Integer mediaId) {
		super();
		this.fileId = fileId;
		this.mediaId = mediaId;
	}

	/**
	 * @param id
	 * @param fileId
	 * @param mediaId
	 */
	public FileMediaItem(final Integer id, final Integer fileId, final Integer mediaId) {
		super();
		this.id = id;
		this.fileId = fileId;
		this.mediaId = mediaId;
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
		if (!(obj instanceof FileMediaItem)) {
			return false;
		}
		final FileMediaItem other = (FileMediaItem) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
		}
		if (this.fileId == null) {
			if (other.fileId != null) {
				return false;
			}
		} else if (!this.fileId.equals(other.fileId)) {
			return false;
		}
		if (this.mediaId == null) {
			if (other.mediaId != null) {
				return false;
			}
		} else if (!this.mediaId.equals(other.mediaId)) {
			return false;
		}
		return true;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final FileMediaItem result = new FileMediaItem();
		if (map.containsKey("id")) {
			result.setId((Integer) map.get("id"));
		}
		if (map.containsKey("file_id")) {
			result.setFileId((Integer) map.get("file_id"));
		}
		if (map.containsKey("media_id")) {
			result.setMediaId((Integer) map.get("media_id"));
		}
		return result;
	}

	@Override
	public String getDatabaseTable() {
		return "fileMedia";
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
	 * @return the mediaId
	 */
	public final Integer getMediaId() {
		return this.mediaId;
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
			result = (prime * result) + ((this.mediaId == null) ? 0 : this.mediaId.hashCode());
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
	 * @param mediaId
	 *            the mediaId to set
	 */
	public final void setMediaId(final Integer mediaId) {
		this.mediaId = mediaId;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();

		if (this.getId() != null) {
			tempHashMap.put("id", this.id);
		}

		tempHashMap.put("file_id", this.fileId);
		tempHashMap.put("media_id", this.mediaId);

		return tempHashMap;
	}

}
