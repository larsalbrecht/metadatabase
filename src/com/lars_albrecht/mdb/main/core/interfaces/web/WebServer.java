/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseAjaxHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseDefaultHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseHTMLHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseJSONHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.servlets.JChartServlet;
import com.lars_albrecht.mdb.main.core.utilities.Paths;

public class WebServer {

	private MainController	mainController	= null;
	private WebInterface	webInterface	= null;
	private Server			server			= null;

	/**
	 * @param mainController
	 * @param webInterface
	 */
	public WebServer(final MainController mainController, final WebInterface webInterface) {
		super();
		this.mainController = mainController;
		this.webInterface = webInterface;
	}

	@Override
	protected void finalize() throws Throwable {
	}

	/**
	 * WebServer constructor.
	 */
	protected void start() {
		this.server = new Server();

		/**
		 * http connector
		 */
		final HttpConfiguration http_config = new HttpConfiguration();
		final ServerConnector http = new ServerConnector(this.server, new HttpConnectionFactory(http_config));
		http.setPort(8080);
		http.setIdleTimeout(30000);

		// TODO comment this in! [commented out, because jetty 9.0.0 does not
		// have this method]
		// http.setName("Metadatabase WebServer-Interface");

		this.server.setConnectors(new Connector[] {
			http
		});

		/**
		 * Own handler
		 */
		final MetadatabaseDefaultHandler mdbDefaultHandler = new MetadatabaseDefaultHandler();
		final MetadatabaseAjaxHandler ajaxHandler = new MetadatabaseAjaxHandler(this.mainController, this.webInterface);
		final MetadatabaseJSONHandler jsonHandler = new MetadatabaseJSONHandler(this.mainController, this.webInterface);
		final MetadatabaseHTMLHandler mdbHTMLHandler = new MetadatabaseHTMLHandler(this.mainController, this.webInterface);

		final ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.addServlet(new ServletHolder(new JChartServlet(this.mainController, JChartServlet.TYPE_ADDSPERDAY)),
				"/statistics/addsPerDay.jpg");

		final ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(false);
		resource_handler.setWelcomeFiles(new String[] {
		// "index.html"
				});

		if (Paths.WEB_RESOURCES.exists()) {
			resource_handler.setResourceBase(Paths.WEB_RESOURCES.getAbsolutePath());
		} else {
			try {
				throw new Exception("ResourceHandler could not be initiated. Path not exists: " + Paths.WEB_RESOURCES);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		final HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				servletContextHandler, mdbDefaultHandler, resource_handler, ajaxHandler, jsonHandler, mdbHTMLHandler, new DefaultHandler()
		});
		this.server.setHandler(handlers);

		try {
			this.server.start();
			this.server.join();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}