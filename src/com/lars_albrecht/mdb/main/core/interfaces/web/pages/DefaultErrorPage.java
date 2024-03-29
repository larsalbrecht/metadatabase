/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class DefaultErrorPage extends WebPage {

	public DefaultErrorPage(final String actionname, final Request request, final MainController mainController,
			final WebInterface webInterface) throws Exception {
		super(actionname, request, mainController, webInterface);
	}

	@Override
	public String getTemplateName() {
		if (this.actionname.equalsIgnoreCase("404")) {
			return "404";
		} else {
			return "500";
		}
	}

	@Override
	public String getTitle() {
		if (this.actionname.equalsIgnoreCase("404")) {
			return "Page not found";
		} else {
			return "Server error";
		}
	}

}
