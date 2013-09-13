/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.helper.WebServerHelper;

/**
 * @author lalbrecht
 * 
 */
public class MetadatabaseJSONHandler extends AbstractHandler {

	private MainController	mainController	= null;
	private WebInterface	webInterface	= null;

	public MetadatabaseJSONHandler(final MainController mainController, final WebInterface webInterface) {
		this.mainController = mainController;
		this.webInterface = webInterface;
	}

	@Override
	public void
			handle(final String str, final Request req, final HttpServletRequest httpReq, final HttpServletResponse httpRes) throws IOException,
					ServletException {

		String content = null;
		if (str.startsWith("/json.html")) {
			content = new WebServerHelper(this.mainController, this.webInterface).getAjaxContent(str.substring(1), req, true);
			if (content != null) {
				httpRes.setContentType("application/json; charset=utf-8");
				httpRes.setStatus(200);
				req.setHandled(true);
				httpRes.getWriter().println(content);
			}
		}

	}
}
