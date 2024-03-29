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

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.helper.WebServerHelper;

/**
 * @author lalbrecht
 * 
 */
public class MetadatabaseHTMLHandler extends AbstractHandler {

	private MainController	mainController	= null;
	private WebInterface	webInterface	= null;

	public MetadatabaseHTMLHandler(final MainController mainController, final WebInterface webInterface) {
		this.mainController = mainController;
		this.webInterface = webInterface;
	}

	@Override
	public void
			handle(final String str, final Request req, final HttpServletRequest httpReq, final HttpServletResponse httpRes) throws IOException,
					ServletException {
		if (this.webInterface.isLoggedIn(req)) {
			// logged in
		} else {
			// not logged in
		}
		Debug.startTimer("MetadatabaseHTMLHandler handleTime: " + str);
		String content = new WebServerHelper(this.mainController, this.webInterface).getFileContent(req.getRequestURI().substring(1), req);
		Debug.stopTimer("MetadatabaseHTMLHandler handleTime: " + str);
		if (content != null) {
			content += "<!-- Server Time " + Debug.getTime("MetadatabaseHTMLHandler handleTime: " + str) + " -->";
			Debug.log(Debug.LEVEL_INFO, "Content loaded for request-uri: " + str);
			httpRes.setContentType("text/html;charset=utf-8");
			httpRes.setStatus(200);
			req.setHandled(true);
			httpRes.getWriter().println(content);
		} else {
			Debug.log(Debug.LEVEL_INFO, "Content NOT loaded for request-uri: " + str);
		}
	}
}
