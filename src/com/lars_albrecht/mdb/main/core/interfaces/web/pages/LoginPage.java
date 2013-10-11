/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.UserHandler;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;
import com.lars_albrecht.mdb.main.core.models.persistable.User;

/**
 * @author lalbrecht
 * 
 */
public class LoginPage extends WebPage {

	public static final int	INFO_OK					= 0;
	public static final int	INFO_IDENTIFIER			= 1;
	public static final int	INFO_PASSWORD			= 2;
	public static final int	INFO_NOIDENTIFIER		= 3;
	public static final int	INFO_NOPASSWORD			= 4;
	public static final int	INFO_INVALIDIDENTIFIER	= 5;
	public static final int	INFO_INVALIDPASSWORD	= 6;

	public LoginPage(final String actionname, final Request request, final MainController mainController, final WebInterface webInterface)
			throws Exception {
		super(actionname, request, mainController, webInterface);

		// try to login
		if ((request.getParameter("do") != null) && request.getParameter("do").equalsIgnoreCase("login")) {
			final ConcurrentHashMap<Integer, Object> loginInfos = this.checkLoginForm(request);
			User user = null;
			if ((loginInfos != null) && loginInfos.containsKey(LoginPage.INFO_OK)) {
				user = UserHandler.doLogin((String) loginInfos.get(LoginPage.INFO_IDENTIFIER),
						(String) loginInfos.get(LoginPage.INFO_PASSWORD));
			}
			// if logininfos == null, no login tried
			// if logininfos OK AND user == null, login tried, but mismatch
			if ((loginInfos == null) || (loginInfos.containsKey(LoginPage.INFO_OK) && (user == null))) {
				this.setPageTemplate(this.generateLoginPage(this.getPageTemplate()));
			} else {
				// no form
				if (request.getParameter("req") != null) {
					// redirect to req
				}
			}
		} else if ((request.getParameter("do") != null) && request.getParameter("do").equalsIgnoreCase("logout")) {
			UserHandler.doLogout(UserHandler.getCurrentUser().getIdentifier());
		}
	}

	private ConcurrentHashMap<Integer, Object> checkLoginForm(final Request request) {
		ConcurrentHashMap<Integer, Object> loginInfos = null;
		loginInfos = new ConcurrentHashMap<Integer, Object>();

		final String identifier = request.getParameter("loginBoxEmail");
		final String password = request.getParameter("loginBoxPassword");
		if (identifier == null) {
			loginInfos.put(LoginPage.INFO_NOIDENTIFIER, Boolean.TRUE);
		} else if (false) {// check identifier rules
			loginInfos.put(LoginPage.INFO_INVALIDIDENTIFIER, true);
		} else {
			loginInfos.put(LoginPage.INFO_IDENTIFIER, identifier);
		}
		if (password == null) {
			loginInfos.put(LoginPage.INFO_NOPASSWORD, true);
		} else if (false) { // check password rules
			loginInfos.put(LoginPage.INFO_INVALIDPASSWORD, true);
		} else {
			loginInfos.put(LoginPage.INFO_PASSWORD, password);
		}

		if (loginInfos.containsKey(LoginPage.INFO_IDENTIFIER) && loginInfos.containsKey(LoginPage.INFO_PASSWORD)) {
			loginInfos.put(LoginPage.INFO_OK, true);
		}

		return loginInfos;
	}

	private Template generateLoginPage(final Template pageTemplate) {
		final Template loginPageTemplate = pageTemplate;

		LoginBoxPartial loginBox = null;
		try {
			loginBox = new LoginBoxPartial(this.actionname, this.request, this.mainController, this.webInterface);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		loginPageTemplate.replaceMarker("loginbox", loginBox.getGeneratedContent(), false);

		return loginPageTemplate;
	}

	@Override
	public String getTemplateName() {
		return "login";
	}

	@Override
	public String getTitle() {
		return "Login";
	}

}
