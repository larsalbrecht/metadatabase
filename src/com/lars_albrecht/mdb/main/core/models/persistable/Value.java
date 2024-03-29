/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.persistable;

import java.util.HashMap;

import com.lars_albrecht.mdb.main.core.models.interfaces.IPersistable;

/**
 * This model class holds the value.
 * 
 * @author lalbrecht
 * 
 */
public class Value<V> implements IPersistable {

	private Integer	id		= null;
	private V		value	= null;

	public Value() {
	}

	/**
	 * @param id
	 * @param value
	 */
	public Value(final Integer id, final V value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * @param value
	 */
	public Value(final V value) {
		super();
		this.value = value;
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
		if (!(obj instanceof Value)) {
			return false;
		}
		final Value<?> other = (Value<?>) obj;
		if ((this.id != null) && (other.id != null) && !this.id.equals(other.id)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object fromHashMap(final HashMap<String, Object> map) {
		final Value<V> result = new Value<V>();
		if (map.containsKey("id")) {
			result.setId((Integer) map.get("id"));
		}
		if (map.containsKey("value")) {
			result.setValue((V) map.get("value"));
		}

		return result;
	}

	@Override
	public String getDatabaseTable() {
		return "attributes_value";
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return this.value;
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
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@Override
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final V value) {
		this.value = value;
	}

	@Override
	public HashMap<String, Object> toHashMap() {
		final HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
		if (this.getId() != null) {
			tempHashMap.put("id", this.getId());

		}
		tempHashMap.put("value", this.getValue());

		return tempHashMap;
	}

	@Override
	public String toString() {
		return "Id: " + this.id + " | " + "Value: " + this.value;
	}

}
