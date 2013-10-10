/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.UserHandler;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPartial;

/**
 * @author lalbrecht
 * 
 */
public class LoginBoxPartial extends WebPartial {

	public LoginBoxPartial(final String actionname, final Request request, final MainController mainController,
			final WebInterface webInteface) throws Exception {
		super(actionname, request, mainController, webInteface);

		this.setPageTemplate(this.generateLoginBoxPartial());
	}

	private Template generateLoginBoxPartial() {
		final Template loginBoxPartialTemplate = this.getPageTemplate();
		String content = "";

		if (UserHandler.isLoggedIn()) {
			String loginBoxText = loginBoxPartialTemplate.getSubMarkerContent("loginBoxText");
			loginBoxText = Template.replaceMarker(loginBoxText, "name", UserHandler.getCurrentUser().getName(), false);

			content = loginBoxText;
		} else {
			final String loginBoxForm = loginBoxPartialTemplate.getSubMarkerContent("loginBoxForm");

			content = loginBoxForm;
		}
		loginBoxPartialTemplate.replaceMarker("content", content, false);

		return loginBoxPartialTemplate;
	}

	@Override
	public String getTemplateName() {
		return "loginbox";
	}

}
