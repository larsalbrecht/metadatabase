/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lalbrecht
 * 
 */
public class WebServerRequest {

	private ConcurrentHashMap<String, Object>	headerValues	= null;
	private String								content			= null;
	private String								fullUrl			= null;
	private String								url				= null;
	private String								method			= null;
	private ConcurrentHashMap<String, String>	getParams		= null;
	private ConcurrentHashMap<String, String>	postParams		= null;

	public WebServerRequest() {
		this.headerValues = new ConcurrentHashMap<String, Object>();
		this.getParams = new ConcurrentHashMap<String, String>();
	}

	/**
	 * @return the content
	 */
	public final String getContent() {
		return this.content;
	}

	/**
	 * @return the urlStr
	 */
	public final String getFullUrl() {
		return this.fullUrl;
	}

	/**
	 * @return the getParams
	 */
	public final ConcurrentHashMap<String, String> getGetParams() {
		return this.getParams;
	}

	/**
	 * @return the headerValues
	 */
	public final ConcurrentHashMap<String, Object> getHeaderValues() {
		return this.headerValues;
	}

	/**
	 * @return the method
	 */
	public final String getMethod() {
		return this.method;
	}

	/**
	 * @return the postParams
	 */
	public final ConcurrentHashMap<String, String> getParams() {
		final ConcurrentHashMap<String, String> newParamMap = new ConcurrentHashMap<String, String>();
		newParamMap.putAll(this.getParams);
		newParamMap.putAll(this.postParams);
		return newParamMap;
	}

	/**
	 * @return the postParams
	 */
	public final ConcurrentHashMap<String, String> getPostParams() {
		return this.postParams;
	}

	/**
	 * @return the url
	 */
	public final String getUrl() {
		return this.url;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public final void setContent(final String content) {
		this.content = content;
	}

	/**
	 * @param fullUrl
	 *            the fullUrl to set
	 */
	public final void setFullUrl(final String fullUrl) {
		this.fullUrl = fullUrl;
	}

	/**
	 * @param getParams
	 *            the getParams to set
	 */
	public final void setGetParams(final ConcurrentHashMap<String, String> getParams) {
		this.getParams = getParams;
	}

	/**
	 * @param headerValues
	 *            the headerValues to set
	 */
	public final void setHeaderValues(final ConcurrentHashMap<String, Object> headerValues) {
		this.headerValues = headerValues;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public final void setMethod(final String method) {
		this.method = method;
	}

	/**
	 * @param postParams
	 *            the postParams to set
	 */
	public final void setPostParams(final ConcurrentHashMap<String, String> postParams) {
		this.postParams = postParams;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public final void setUrl(final String url) {
		this.url = url;
	}

}
