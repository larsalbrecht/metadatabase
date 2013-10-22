/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * @author lalbrecht
 * 
 *         TODO create a better method to dynamically add new types from outer
 *         classes
 * 
 */
public class MediaItem implements IPersistable {

	public static Integer						TYPE_WEB_IMAGE				= 1;
	public static Integer						TYPE_WEB_VIDEO				= 3;

	public static Integer						TYPE_LOC_IMAGE				= 2;
	public static Integer						TYPE_LOC_VIDEO				= 4;

	public static Integer						OPTION_WEB_BASE_PATH		= 1;
	public static Integer						OPTION_WEB_SECURE_BASE_PATH	= 3;
	public static Integer						OPTION_WEB_PREFIXURL		= 5;
	public static Integer						OPTION_WEB_SUFFIXURL		= 7;
	public static Integer						OPTION_WEB_ISDIRECT			= 9;

	public static Integer						OPTION_SIZES				= 100;

	private Integer								id							= null;
	private String								name						= null;
	private Integer								type						= null;
	private URI									uri							= null;

	private ConcurrentHashMap<Integer, Object>	options						= new ConcurrentHashMap<Integer, Object>();

	public MediaItem() {
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param uri
	 */
	public MediaItem(final Integer id, final String name, final Integer type, final URI uri) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.uri = uri;
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param uri
	 * @param options
	 */
	public MediaItem(final Integer id, final String name, final Integer type, final URI uri,
			final ConcurrentHashMap<Integer, Object> options) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.uri = uri;
		if (options != null) {
			this.options = options;
		}
	}

	/**
	 * @param name
	 * @param type
	 * @param uri
	 */
	public MediaItem(final String name, final Integer type, final URI uri) {
		super();
		this.name = name;
		this.type = type;
		this.uri = uri;
	}

	/**
	 * @param name
	 * @param type
	 * @param uri
	 * @param options
	 */
	public MediaItem(final String name, final Integer type, final URI uri, final ConcurrentHashMap<Integer, Object> options) {
		super();
		this.name = name;
		this.type = type;
		this.uri = uri;
		if (options != null) {
			this.options = options;
		}
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
		if (!(obj instanceof MediaItem)) {
			return false;
		}
		final MediaItem other = (MediaItem) obj;
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
		if ((this.options == null) && (other.options != null)) {
			return false;
		}/*
		 * else if (false && !this.options.equals(other.options)) { // TODO dont
		 * use equals here! Write an own compare method or // something else to
		 * replace this return false; }
		 */
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
			return false;
		}
		if (this.uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!this.uri.equals(other.uri)) {
			return false;
		}
		return true;
	}

	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final MediaItem result = new MediaItem();
		if (map.containsKey("id")) {
			result.setId((Integer) map.get("id"));
		}
		if (map.containsKey("name")) {
			result.setName((String) map.get("name"));
		}
		if (map.containsKey("type")) {
			result.setType((Integer) map.get("type"));
		}

		if (map.containsKey("uri") && (map.get("uri") instanceof String)) {
			try {
				result.setUri(new URI((String) map.get("uri")));
			} catch (final URISyntaxException e) {
				e.printStackTrace();
			}
		}

		if (map.containsKey("options") && (map.get("options") != null)) {
			result.getOptions().putAll(Helper.explodeIntKeys((String) map.get("options"), ";", "|"));
		}

		return result;
	}

	@Override
	public String getDatabaseTable() {
		return "mediaItems";
	}

	/**
	 * @return the id
	 */
	@Override
	public final Integer getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the options
	 */
	public final ConcurrentHashMap<Integer, Object> getOptions() {
		return this.options;
	}

	/**
	 * @return the type
	 */
	public final Integer getType() {
		return this.type;
	}

	/**
	 * @return the uri
	 */
	public final URI getUri() {
		return this.uri;
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
			result = (prime * result) + ((this.options == null) ? 0 : this.options.hashCode());
			result = (prime * result) + ((this.type == null) ? 0 : this.type.hashCode());
			result = (prime * result) + ((this.uri == null) ? 0 : this.uri.hashCode());
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
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(final Integer type) {
		this.type = type;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public final void setUri(final URI uri) {
		this.uri = uri;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();

		if (this.getId() != null) {
			tempHashMap.put("id", this.id);
		}

		tempHashMap.put("name", this.name);
		tempHashMap.put("type", this.type);
		tempHashMap.put("uri", this.uri);
		if ((this.options != null) && (this.options.size() > 0)) {
			tempHashMap.put("options", Helper.implode(this.options, ";", "|", null, null, null, null, null, null, false));
		}

		return tempHashMap;
	}

	@Override
	public String toString() {
		return this.id + " | " + this.name + " | " + this.type + " | " + this.uri + " | " + this.options;
	}

}
