/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.abstracts;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;

/**
 * @author lalbrecht
 * 
 */
public abstract class AWebPart {

	private Template			pageTemplate	= null;
	protected MainController	mainController	= null;
	protected Request			request			= null;
	protected String			actionname		= null;

	public AWebPart(final String actionname, final Request request, final MainController mainController, final WebInterface webInterface)
			throws Exception {
		this.request = request;
		this.mainController = mainController;
		this.actionname = actionname;

		if (this.getTemplateName() != null) {
			this.pageTemplate = new Template(this.getTemplateName());
		} else {
			throw new Exception("Template is not set");
		}
	}

	final public String getGeneratedContent() {
		return this.pageTemplate.getClearedContent();
	}

	final protected Template getPageTemplate() {
		return this.pageTemplate;
	}

	public abstract String getTemplateName();

	final protected void setPageTemplate(final Template pageTemplate) {
		this.pageTemplate = pageTemplate;
	}

}
