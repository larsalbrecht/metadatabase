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

/**
 * @author albrela
 * 
 */
public class MetadatabaseDefaultHandler extends AbstractHandler {

	public MetadatabaseDefaultHandler() {
	}

	@Override
	public void
			handle(final String str, final Request req, final HttpServletRequest httpReq, final HttpServletResponse httpRes) throws IOException,
					ServletException {
		if (str.equalsIgnoreCase("/")) {
			httpRes.sendRedirect("/index.html");
			req.setHandled(true);
		}
	}

}
