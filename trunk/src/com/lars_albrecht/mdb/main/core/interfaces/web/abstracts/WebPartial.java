/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.abstracts;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;

/**
 * @author lalbrecht
 * 
 */
public abstract class WebPartial extends AWebPart {

	public WebPartial(final String actionname, final Request request, final MainController mainController, final WebInterface webInterface)
			throws Exception {
		super(actionname, request, mainController, webInterface);
	}
}
