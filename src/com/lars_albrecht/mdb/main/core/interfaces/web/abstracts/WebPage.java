/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.abstracts;

import java.util.List;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;

/**
 * @author lalbrecht
 * 
 */
public abstract class WebPage extends AWebPart {

	public WebPage(final String actionname, final Request request, final MainController mainController, final WebInterface webInterface)
			throws Exception {
		super(actionname, request, mainController, webInterface);
	}

	public abstract String getTitle();

	public abstract List<String> getPageNames();

	final protected void set404Error() {
		this.setPageTemplate(new Template("404"));
	}

	final protected void set500Error() {
		this.setPageTemplate(new Template("500"));
	}

}
