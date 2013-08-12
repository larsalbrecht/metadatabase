/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
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

	public static int							TYPE_WEB_IMAGE				= 1;
	public static int							TYPE_WEB_VIDEO				= 3;

	public static int							TYPE_LOC_IMAGE				= 2;
	public static int							TYPE_LOC_VIDEO				= 4;

	public static int							OPTION_WEB_IMAGE_PREFIXURL	= 1;
	public static int							OPTION_WEB_IMAGE_SUFFIXURL	= 3;

	public static int							OPTION_WEB_VIDEO_PREFIXURL	= 5;
	public static int							OPTION_WEB_VIDEO_SUFFIXURL	= 7;
	public static int							OPTION_WEB_VIDEO_HOSTER		= 9;

	private Integer								id							= null;
	private String								name						= null;
	private Integer								type						= null;
	private URI									uri							= null;

	private ConcurrentHashMap<Integer, Object>	options						= null;

	public MediaItem() {
		this.options = new ConcurrentHashMap<Integer, Object>();
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
		this.options = options;
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
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public final Integer getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(final Integer type) {
		this.type = type;
	}

	/**
	 * @return the options
	 */
	public final ConcurrentHashMap<Integer, Object> getOptions() {
		return this.options;
	}

	/**
	 * @return the uri
	 */
	public final URI getUri() {
		return this.uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public final void setUri(final URI uri) {
		this.uri = uri;
	}

	@Override
	public String getDatabaseTable() {
		return "mediaItems";
	}

	@SuppressWarnings("unchecked")
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

		if (map.containsKey("uri") && map.get("uri") instanceof String) {
			try {
				result.setUri(new URI((String) map.get("uri")));
			} catch (final URISyntaxException e) {
				e.printStackTrace();
			}
		}

		if (map.containsKey("options")) {
			result.getOptions().putAll((Map<? extends Integer, ? extends Object>) Helper.explode((String) map.get("options"), ";", "|"));
		}

		return result;
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
		if (this.options != null && this.options.size() > 0) {
			tempHashMap.put("options", Helper.implode(this.options, ";", "|", null, null, null, null, null, null, false));
		}

		return tempHashMap;
	}

	@Override
	public String toString() {
		return this.id + " | " + this.name + " | " + this.type + " | " + this.uri;
	}

}
