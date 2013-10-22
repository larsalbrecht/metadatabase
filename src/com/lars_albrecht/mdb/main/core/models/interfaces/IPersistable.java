/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models.interfaces;

import java.util.HashMap;

/**
 * This interface contains the persist methods to persist a model.
 * 
 * @author lalbrecht
 * 
 */
public interface IPersistable {

	/**
	 * Function to transform a map from the database to an object.
	 * 
	 * @param map
	 * @return
	 */
	public Object fromHashMap(final HashMap<String, Object> map);

	/**
	 * The name of the database table.
	 * 
	 * @return
	 */
	public String getDatabaseTable();

	/**
	 * Get the ID of the element.
	 * 
	 * @return
	 */
	public Integer getId();

	/**
	 * Set the ID of the element.
	 * 
	 * @param id
	 */
	public void setId(final Integer id);

	/**
	 * Function to transform an object to a map for the database.
	 * 
	 * @return
	 */
	public HashMap<String, Object> toHashMap();

}
