/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.UserHandler;
import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.WebServerInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;
import com.lars_albrecht.mdb.main.core.interfaces.web.factory.WebPageFactory;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts.AbstractFileDetailsOutputItem;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageConfig;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageMenuItemConfig;
import com.lars_albrecht.mdb.main.core.models.persistable.User;

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

	public boolean doLogin(final Request request, final User user) {
		if (user == null) {
			return false;
		}
		final HttpSession session = request.getSession(true);
		final String token = UserHandler.generateUserToken(user, new String[] {
			session.getId()
		});
		if (token == null) {
			return false;
		}
		session.setAttribute("user", user.getId());
		session.setAttribute("token", token);

		return true;
	}

	public boolean doLogout(final Request request) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}
		session.setMaxInactiveInterval(1);
		for (final Cookie cookie : request.getCookies()) {
			cookie.setMaxAge(0);
		}
		session.invalidate();
		return true;
	}

	public User getCurrentUser(final Request request) {
		if (request.getSession(false) == null) {
			return null;
		}
		final HttpSession session = request.getSession(false);

		final Integer userId = session.getAttribute("user") != null ? (Integer) session.getAttribute("user") : null;
		final String roToken = session.getAttribute("token") != null ? (String) session.getAttribute("token") : null;
		if ((userId == null) || (roToken == null)) {
			return null;
		}

		final User user = UserHandler.getUser(userId);

		if (user == null) {
			return null;
		}

		final String token = UserHandler.generateUserToken(user, new String[] {
			session.getId()
		});

		if ((token == null) || !token.equals(roToken)) {
			return null;
		}

		return user;
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

	public boolean isLoggedIn(final Request request) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}

		return this.getCurrentUser(request) == null ? false : true;
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
