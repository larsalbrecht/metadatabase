/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.ChecksumSHA1;
import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * This model class holds the FileItem itself.
 * 
 * @author lalbrecht
 * 
 */
public class FileItem implements IPersistable {

	private Integer									id			= null;
	private String									name		= null;
	private String									fullpath	= null;
	private String									dir			= null;
	private Long									size		= null;
	private String									ext			= null;
	private String									filehash	= null;
	private Integer									createTS	= null;
	private Integer									status		= null;
	private String									filetype	= null;
	private Integer									updateTS	= null;

	private ConcurrentHashMap<String, ArrayList<?>>	dataStore	= null;

	/**
	 * 
	 */
	public FileItem() {
		super();
		this.dataStore = new ConcurrentHashMap<String, ArrayList<?>>();
	}

	public FileItem(final Integer fileId) {
		super();
		this.id = fileId;
	}

	/**
	 * @param id
	 * @param name
	 * @param fullpath
	 * @param dir
	 * @param size
	 * @param ext
	 * @param filetype
	 * @param status
	 * @param createTS
	 * @param updateTS
	 */
	public FileItem(final Integer id, final String name, final String fullpath, final String dir, final Long size, final String ext,
			final String filetype, final Integer status, final Integer createTS, final Integer updateTS) {
		super();
		this.id = id;
		this.name = name;
		this.fullpath = fullpath;
		this.dir = dir;
		this.size = size;
		this.ext = ext;
		this.filetype = filetype;
		this.status = status;
		this.createTS = createTS;
		this.updateTS = updateTS;
	}

	/**
	 * @param fullpath
	 */
	public FileItem(final String fullpath) {
		super();
		this.fullpath = fullpath;
	}

	/**
	 * @param name
	 * @param fullpath
	 * @param dir
	 * @param size
	 * @param ext
	 * @param createTS
	 * @param updateTS
	 */
	public FileItem(final String name, final String fullpath, final String dir, final Long size, final String ext, final Integer createTS,
			final Integer updateTS) {
		super();
		this.name = name;
		this.fullpath = fullpath;
		this.dir = dir;
		this.size = size;
		this.ext = ext;
		this.createTS = createTS;
		this.updateTS = updateTS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * equals if: obj == this | id != null on both and equals | fullpath and all
	 * other are the same
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FileItem)) {
			return false;
		}
		final FileItem other = (FileItem) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		} else if ((this.id != null) && (other.id != null) && this.id.equals(other.id)) {
			return true;
		}
		if (this.fullpath == null) {
			if (other.fullpath != null) {
				return false;
			}
		} else if (!this.fullpath.equals(other.fullpath)) {
			return false;
		} else if (this.fullpath.equals(other.fullpath)) {
			return true;
		}

		/*
		 * if (this.attributes == null) { if (other.attributes != null) { return
		 * false; } } else if (!this.attributes.equals(other.attributes)) {
		 * return false; }
		 */
		if (this.createTS == null) {
			if (other.createTS != null) {
				return false;
			}
		} else if (!this.createTS.equals(other.createTS)) {
			return false;
		}
		if (this.updateTS == null) {
			if (other.updateTS != null) {
				return false;
			}
		} else if (!this.updateTS.equals(other.updateTS)) {
			return false;
		}
		if (this.dir == null) {
			if (other.dir != null) {
				return false;
			}
		} else if (!this.dir.equals(other.dir)) {
			return false;
		}
		if (this.filetype == null) {
			if (other.filetype != null) {
				return false;
			}
		} else if (!this.filetype.equals(other.filetype)) {
			return false;
		}
		if (this.ext == null) {
			if (other.ext != null) {
				return false;
			}
		} else if (!this.ext.equals(other.ext)) {
			return false;
		}
		if (this.filehash == null) {
			if (other.filehash != null) {
				return false;
			}
		} else if (!this.filehash.equals(other.filehash)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.size == null) {
			if (other.size != null) {
				return false;
			}
		} else if (!this.size.equals(other.size)) {
			return false;
		}
		return true;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final FileItem resultItem = new FileItem();
		if (map.containsKey("id")) {
			resultItem.setId((Integer) map.get("id"));
		}
		if (map.containsKey("name")) {
			resultItem.setName((String) map.get("name"));
		}
		if (map.containsKey("fullpath")) {
			resultItem.setFullpath((String) map.get("fullpath"));
		}

		if (map.containsKey("dir")) {
			resultItem.setDir((String) map.get("dir"));
		}

		if (map.containsKey("size")) {
			if (map.get("size") instanceof Integer) {
				resultItem.setSize(((Integer) map.get("size")).longValue());
			} else {
				resultItem.setSize((Long) map.get("size"));
			}
		}

		if (map.containsKey("ext")) {
			resultItem.setExt((String) map.get("ext"));
		}

		if (map.containsKey("filetype") && (map.get("filetype") != null) && (map.get("filetype") != "") && !map.get("filetype").equals("")) {
			resultItem.setFiletype((String) map.get("filetype"));
		}

		if (map.containsKey("filehash") && (map.get("filehash") != null) && (map.get("filehash") != "") && !map.get("filehash").equals("")) {
			resultItem.setFilehash((String) map.get("filehash"));
		}

		if (map.containsKey("status") && (map.get("status") != null) && (map.get("status") != "")) {
			resultItem.setStatus((Integer) map.get("status"));
		}

		if (map.containsKey("createTS") && (map.get("createTS") != null)) {
			final SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				resultItem.setCreateTS(((Long) (sdfToDate.parse((String) map.get("createTS")).getTime() / 1000)).intValue());
			} catch (final ParseException e) {
				e.printStackTrace();
			}
		}

		if (map.containsKey("updateTS") && (map.get("updateTS") != null)) {
			final SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				resultItem.setUpdateTS(((Long) (sdfToDate.parse((String) map.get("updateTS")).getTime() / 1000)).intValue());
			} catch (final ParseException e) {
				e.printStackTrace();
			}

		}

		return resultItem;
	}

	public FileItem generateFilehash() throws Exception {
		if ((this.getFullpath() != null) && new File(this.getFullpath()).exists()) {
			this.setFilehash(ChecksumSHA1.getSHA1Checksum(this.getFullpath()));
		}
		return this;
	}

	/**
	 * @return the createTS
	 */
	public Integer getCreateTS() {
		return this.createTS;
	}

	@Override
	public String getDatabaseTable() {
		return "fileInformation";
	}

	/**
	 * @return the dataStore
	 */
	public final ConcurrentHashMap<String, ArrayList<?>> getDataStore() {
		return this.dataStore;
	}

	public String getDir() {
		return this.dir;
	}

	public String getExt() {
		return this.ext;
	}

	/**
	 * @return the filehash
	 */
	public String getFilehash() {
		return this.filehash;
	}

	/**
	 * @return the filetype
	 */
	public String getFiletype() {
		return this.filetype;
	}

	public String getFullpath() {
		return this.fullpath;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Long getSize() {
		return this.size;
	}

	/**
	 * @return the status
	 */
	public final Integer getStatus() {
		return this.status;
	}

	/**
	 * @return the updateTS
	 */
	public Integer getUpdateTS() {
		return this.updateTS;
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
			// result = prime * result + ((this.attributes == null) ? 0 :
			// this.attributes.hashCode());
			result = (prime * result) + ((this.createTS == null) ? 0 : this.createTS.hashCode());
			result = (prime * result) + ((this.updateTS == null) ? 0 : this.updateTS.hashCode());
			result = (prime * result) + ((this.dir == null) ? 0 : this.dir.hashCode());
			result = (prime * result) + ((this.ext == null) ? 0 : this.ext.hashCode());
			result = (prime * result) + ((this.filehash == null) ? 0 : this.filehash.hashCode());
			result = (prime * result) + ((this.fullpath == null) ? 0 : this.fullpath.hashCode());
			result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
			result = (prime * result) + ((this.size == null) ? 0 : this.size.hashCode());
			result = (prime * result) + ((this.filetype == null) ? 0 : this.filetype.hashCode());
		} else {
			result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		}
		return result;
	}

	/**
	 * @param createTS
	 *            the createTS to set
	 */
	public void setCreateTS(final Integer createTS) {
		this.createTS = createTS;
	}

	/**
	 * @param dataStore
	 *            the dataStore to set
	 */
	public final void setDataStore(final ConcurrentHashMap<String, ArrayList<?>> dataStore) {
		this.dataStore = dataStore;
	}

	public void setDir(final String dir) {
		this.dir = dir;
	}

	public void setExt(final String ext) {
		this.ext = ext;
	}

	/**
	 * @param filehash
	 *            the filehash to set
	 */
	public void setFilehash(final String filehash) {
		this.filehash = filehash;
	}

	/**
	 * @param filetype
	 *            the filetype to set
	 */
	public void setFiletype(final String filetype) {
		this.filetype = filetype;
	}

	public void setFullpath(final String fullpath) {
		this.fullpath = fullpath;
	}

	@Override
	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSize(final Long size) {
		this.size = size;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public final void setStatus(final Integer status) {
		this.status = status;
	}

	/**
	 * @param updateTS
	 *            the updateTS to set
	 */
	public void setUpdateTS(final Integer updateTS) {
		this.updateTS = updateTS;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
		if (this.getId() != null) {
			tempHashMap.put("id", this.getId());
		}
		tempHashMap.put("name", this.getName());
		tempHashMap.put("fullpath", this.getFullpath());
		tempHashMap.put("dir", this.getDir());
		tempHashMap.put("size", this.getSize());
		tempHashMap.put("ext", this.getExt());
		tempHashMap.put("filehash", this.getFilehash());
		tempHashMap.put("filetype", this.getFiletype());

		if (this.getStatus() != null) {
			tempHashMap.put("status", this.getStatus());
		}

		if (this.getCreateTS() != null) {
			tempHashMap.put("createTS", this.getCreateTS());
		}

		if (this.getUpdateTS() != null) {
			tempHashMap.put("updateTS", this.getUpdateTS());
		}

		return tempHashMap;
	}

	@Override
	public String toString() {
		return "Id: " + this.id + " | " + "Name: " + this.name + " | " + "Fullpath: " + this.fullpath + " | " + "Dir: " + this.dir + " | "
				+ "Size: " + this.size + " | " + "Ext: " + this.ext + " | " + "CreatedTS: " + this.createTS + " | " + "UpdatedTS: "
				+ this.updateTS + " | " + this.dataStore;
	}

}
