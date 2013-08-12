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

	/**
	 * @return the id
	 */
	@Override
	public final Integer getId() {
		return this.id;
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
	 * @return the fileId
	 */
	public final Integer getFileId() {
		return this.fileId;
	}

	/**
	 * @return the mediaId
	 */
	public final Integer getMediaId() {
		return this.mediaId;
	}

	/**
	 * @param mediaId
	 *            the mediaId to set
	 */
	public final void setMediaId(final Integer mediaId) {
		this.mediaId = mediaId;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public final void setFileId(final Integer fileId) {
		this.fileId = fileId;
	}

	@Override
	public String getDatabaseTable() {
		return "fileMedia";
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
