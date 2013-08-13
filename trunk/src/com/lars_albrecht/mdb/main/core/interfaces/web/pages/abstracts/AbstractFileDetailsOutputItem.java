/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts;

import com.lars_albrecht.mdb.main.core.models.KeyValue;

/**
 * @author lalbrecht
 * 
 *         Use this abstract to modify the output of the FileDetailsPage.
 * 
 */
public abstract class AbstractFileDetailsOutputItem {

	protected final String getDefaultKey(final String infoType, final String sectionName, final KeyValue<String, Object> keyValue) {
		return keyValue.getKey().getKey();
	}

	protected final String getDefaultValue(final String infoType,
			final String sectionName,
			final KeyValue<String, Object> keyValue,
			final String value) {
		return value;
	}

	public abstract String getKey(final String infoType, final String sectionName, final KeyValue<String, Object> keyValue);

	public abstract String getValue(final String infoType,
			final String sectionName,
			final KeyValue<String, Object> keyValue,
			final String value);

	public abstract boolean keyAllowed(final String infoType, final String sectionName, final KeyValue<String, Object> keyValue);

}
