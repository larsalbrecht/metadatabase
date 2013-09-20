/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class HomePage extends WebPage {

	public HomePage(final String actionname, final Request request, final MainController mainController, final WebInterface webInterface)
			throws Exception {
		super(actionname, request, mainController, webInterface);
	}

	@Override
	public List<String> getPageNames() {
		final String[] names = {
				"home", "index", "Start"
		};
		return Arrays.asList(names);
	}

	@Override
	public String getStaticName() {
		return "index";
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
