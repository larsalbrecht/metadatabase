/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages.config;

import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class WebPageConfig {

	private Class<? extends WebPage>	pageClass	= null;
	private String						menuTitle	= null;
	private String[]					urlNames	= null;
	private boolean						showInMenu	= false;
	private int							menuSorting	= 0;

	public WebPageConfig() {
	}

	/**
	 * Constructor for pages that are not in menu.
	 * 
	 * @param pageClass
	 * @param urlNames
	 */
	public WebPageConfig(final Class<? extends WebPage> pageClass, final String[] urlNames) {
		super();
		this.pageClass = pageClass;
		this.urlNames = urlNames;
	}

	/**
	 * Full constructor.
	 * 
	 * @param pageClass
	 * @param urlNames
	 * @param menuTitle
	 * @param showInMenu
	 * @param menuSorting
	 */
	public WebPageConfig(final Class<? extends WebPage> pageClass, final String[] urlNames, final String menuTitle,
			final boolean showInMenu, final int menuSorting) {
		super();
		this.pageClass = pageClass;
		this.menuTitle = menuTitle;
		this.urlNames = urlNames;
		this.showInMenu = showInMenu;
		this.menuSorting = menuSorting;
	}

	/**
	 * @return the menuSorting
	 */
	public final int getMenuSorting() {
		return this.menuSorting;
	}

	/**
	 * @return the menuTitle
	 */
	public final String getMenuTitle() {
		return this.menuTitle;
	}

	/**
	 * @return the pageClass
	 */
	public final Class<? extends WebPage> getPageClass() {
		return this.pageClass;
	}

	/**
	 * @return the urlNames
	 */
	public final String[] getUrlNames() {
		return this.urlNames;
	}

	/**
	 * @return the showInMenu
	 */
	public final boolean isShowInMenu() {
		return this.showInMenu;
	}

	/**
	 * @param menuSorting
	 *            the menuSorting to set
	 */
	public final void setMenuSorting(final int menuSorting) {
		this.menuSorting = menuSorting;
	}

	/**
	 * @param menuTitle
	 *            the menuTitle to set
	 */
	public final void setMenuTitle(final String menuTitle) {
		this.menuTitle = menuTitle;
	}

	/**
	 * @param pageClass
	 *            the pageClass to set
	 */
	public final void setPageClass(final Class<? extends WebPage> pageClass) {
		this.pageClass = pageClass;
	}

	/**
	 * @param showInMenu
	 *            the showInMenu to set
	 */
	public final void setShowInMenu(final boolean showInMenu) {
		this.showInMenu = showInMenu;
	}

	/**
	 * @param urlNames
	 *            the urlNames to set
	 */
	public final void setUrlNames(final String[] urlNames) {
		this.urlNames = urlNames;
	}

}
