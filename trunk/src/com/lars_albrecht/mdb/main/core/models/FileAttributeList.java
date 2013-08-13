/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This model class holds a list of KeyValues.
 * 
 * @author lalbrecht
 * 
 *         TODO Replace / Refactor this and keyValues to a better concept.
 */
public class FileAttributeList {

	private ArrayList<KeyValue<String, Object>>	keyValues	= new ArrayList<KeyValue<String, Object>>();
	private String								sectionName	= null;
	private int									hash		= -1;
	private Integer								fileId		= null;
	private String								infoType	= null;

	/**
	 * 
	 */
	public FileAttributeList() {
		super();
	}

	public FileAttributeList(final ArrayList<KeyValue<String, Object>> keyValue, final String sectionName, final Integer fileId,
			final String infoType) {
		this.keyValues = keyValue;
		this.sectionName = sectionName;
		this.fileId = fileId;
		this.infoType = infoType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		final FileAttributeList tempList = new FileAttributeList();
		tempList.fileId = this.fileId;
		tempList.hash = this.hash;
		tempList.keyValues.addAll((Collection<? extends KeyValue<String, Object>>) this.keyValues.clone());
		tempList.sectionName = this.sectionName;
		tempList.infoType = this.infoType;

		return tempList;
	}

	public FileAttributeList generateHash() {
		this.hash = this.keyValues.hashCode();
		return this;
	}

	/**
	 * @return the fileId
	 */
	public Integer getFileId() {
		return this.fileId;
	}

	/**
	 * @return the hash
	 */
	public int getHash() {
		return this.hash;
	}

	/**
	 * @return the infoType
	 */
	public final String getInfoType() {
		return this.infoType;
	}

	/**
	 * @return the keyValues
	 */
	public ArrayList<KeyValue<String, Object>> getKeyValues() {
		return this.keyValues;
	}

	/**
	 * @return the sectionName
	 */
	public String getSectionName() {
		return this.sectionName;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public void setFileId(final Integer fileId) {
		this.fileId = fileId;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(final int hash) {
		this.hash = hash;
	}

	/**
	 * @param infoType
	 *            the infoType to set
	 */
	public final void setInfoType(final String infoType) {
		this.infoType = infoType;
	}

	/**
	 * @param keyValues
	 *            the keyValues to set
	 */
	public void setKeyValues(final ArrayList<KeyValue<String, Object>> keyValues) {
		this.keyValues = keyValues;
	}

	/**
	 * @param sectionName
	 *            the sectionName to set
	 */
	public void setSectionName(final String sectionName) {
		this.sectionName = sectionName;
	}

	@Override
	public String toString() {
		return "KeyValues: " + this.keyValues + " | " + "Hash: " + this.hash;
	}

}
