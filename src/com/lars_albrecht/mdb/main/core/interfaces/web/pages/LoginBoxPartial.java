/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPartial;
import com.lars_albrecht.mdb.main.core.models.persistable.User;

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

		final User currUser = this.webInterface.getCurrentUser(this.request);

		// is logged in?
		if (currUser != null) {
			String loginBoxText = loginBoxPartialTemplate.getSubMarkerContent("loginBoxText");
			loginBoxText = Template.replaceMarker(loginBoxText, "name", this.webInterface.getCurrentUser(this.request).getName(), false);

			content = loginBoxText;
		} else {
			// fill form
			final String fullRequestUrl = this.request.getRootURL() + this.request.getRequestURI() + "?" + this.request.getQueryString();
			String loginBoxForm = loginBoxPartialTemplate.getSubMarkerContent("loginBoxForm");

			final String identifier = this.request.getParameter("loginBoxEmail");
			final String password = this.request.getParameter("loginBoxPassword");

			loginBoxForm = Template.replaceMarker(loginBoxForm, "identifier", (identifier != null ? identifier : ""), false);
			loginBoxForm = Template.replaceMarker(loginBoxForm, "password", (password != null ? password : ""), false);

			try {
				loginBoxForm = Template.replaceMarker(loginBoxForm, "action",
						"/index.html?action=login&do=login&req=" + URLEncoder.encode(fullRequestUrl, "UTF-8"), false);
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
			}
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
