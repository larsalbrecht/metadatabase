/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages.config;

import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class WebPageMenuItemConfig implements Comparable<WebPageMenuItemConfig> {

	private String[]					names		= null;
	private Class<? extends WebPage>	clazz		= null;
	private String						itemTitle	= null;
	private int							sorting		= 0;

	public WebPageMenuItemConfig() {
	}

	/**
	 * @param names
	 * @param clazz
	 * @param itemTitle
	 */
	public WebPageMenuItemConfig(final String[] names, final Class<? extends WebPage> clazz, final String itemTitle, final int sorting) {
		super();
		this.names = names;
		this.clazz = clazz;
		this.itemTitle = itemTitle;
		this.sorting = sorting;
	}

	@Override
	public int compareTo(final WebPageMenuItemConfig o) {
		final int otherValue = o.sorting;
		if (otherValue < this.sorting) {
			return 1;
		} else if (otherValue > this.sorting) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * @return the clazz
	 */
	public final Class<? extends WebPage> getClazz() {
		return this.clazz;
	}

	/**
	 * @return the itemTitle
	 */
	public final String getItemTitle() {
		return this.itemTitle;
	}

	/**
	 * @return the names
	 */
	public final String[] getNames() {
		return this.names;
	}

	/**
	 * @return the sorting
	 */
	public final int getSorting() {
		return this.sorting;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public final void setClazz(final Class<? extends WebPage> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @param itemTitle
	 *            the itemTitle to set
	 */
	public final void setItemTitle(final String itemTitle) {
		this.itemTitle = itemTitle;
	}

	/**
	 * @param names
	 *            the names to set
	 */
	public final void setNames(final String[] names) {
		this.names = names;
	}

	/**
	 * @param sorting
	 *            the sorting to set
	 */
	public final void setSorting(final int sorting) {
		this.sorting = sorting;
	}
}
