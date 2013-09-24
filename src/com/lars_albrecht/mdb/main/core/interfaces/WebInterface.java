/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.WebServerInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;
import com.lars_albrecht.mdb.main.core.interfaces.web.factory.WebPageFactory;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts.AbstractFileDetailsOutputItem;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageConfig;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageMenuItemConfig;

/**
 * A WebInterface to control the files and services.
 * 
 * @author lalbrecht
 * 
 */
public class WebInterface extends AInterface {

	final ArrayList<Thread>								threadList				= new ArrayList<Thread>();
	protected AbstractFileDetailsOutputItem				fileDetailsOutputItem	= null;
	private int											port					= 8080;
	private final ArrayList<Class<? extends WebPage>>	usePages				= new ArrayList<Class<? extends WebPage>>();
	private final ArrayList<WebPageMenuItemConfig>		menuPages				= new ArrayList<WebPageMenuItemConfig>();

	public WebInterface(final MainController mainController) {
		super(mainController);

		for (final WebPageConfig config : this.mainController.getMdbConfig().getWebInterfacePageConfigs()) {
			this.usePages.add(config.getPageClass());
			WebPageFactory.addWebPage(config.getUrlNames(), config.getPageClass());
			if (config.isShowInMenu() && (config.getMenuTitle() != null) && !config.getMenuTitle().equalsIgnoreCase("")) {
				this.menuPages.add(new WebPageMenuItemConfig(config.getUrlNames(), config.getPageClass(), config.getMenuTitle(), config
						.getMenuSorting()));
			}
		}

		this.canOpened = true;
	}

	/**
	 * @return the fileDetailsOutputItem
	 */
	public final AbstractFileDetailsOutputItem getFileDetailsOutputItem() {
		return this.fileDetailsOutputItem;
	}

	/**
	 * @return the menuPages
	 */
	public final ArrayList<WebPageMenuItemConfig> getMenuPages() {
		return this.menuPages;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return this.port;
	}

	/**
	 * @return the usePages
	 */
	public final ArrayList<Class<? extends WebPage>> getUsePages() {
		return this.usePages;
	}

	@Override
	public void openInterface() {
		try {
			Desktop.getDesktop().browse(new URI("http://localhost:" + this.getPort()));
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param fileDetailsOutputItem
	 */
	public void setFileDetailsOutputItem(final AbstractFileDetailsOutputItem fileDetailsOutputItem) {
		this.fileDetailsOutputItem = fileDetailsOutputItem;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public final void setPort(final int port) {
		this.port = port;
	}

	@Override
	public void startInterface() {
		this.threadList.add(new Thread(new WebServerInterface(this.mainController, this)));
		this.threadList.get(this.threadList.size() - 1).start();
	}

}
