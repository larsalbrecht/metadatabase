/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.WebServerRequest;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class HomePage extends WebPage {

	public HomePage(final String actionname, final WebServerRequest request, final MainController mainController,
			final WebInterface webInterface) throws Exception {
		super(actionname, request, mainController, webInterface);
	}

	@Override
	public String getTemplateName() {
		return "home";
	}

	@Override
	public String getTitle() {
		return "Home";
	}

}
