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

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseAjaxHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseDefaultHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseHTMLHandler;
import com.lars_albrecht.mdb.main.core.interfaces.web.handler.MetadatabaseJSONHandler;

public class WebServer {

	private MainController	mainController	= null;
	private WebInterface	webInterface	= null;

	@Override
	protected void finalize() throws Throwable {
	}

	/**
	 * WebServer constructor.
	 */
	protected void start(final MainController mainController, final WebInterface webInterface) {
		this.mainController = mainController;
		this.webInterface = webInterface;
		final Server server = new Server();

		/**
		 * http connector
		 */
		final HttpConfiguration http_config = new HttpConfiguration();
		final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(8080);
		http.setIdleTimeout(30000);
		http.setName("Metadatabase WebServer-Interface");

		server.setConnectors(new Connector[] {
			http
		});

		/**
		 * Own handler
		 */
		final MetadatabaseDefaultHandler mdbDefaultHandler = new MetadatabaseDefaultHandler();
		final MetadatabaseAjaxHandler ajaxHandler = new MetadatabaseAjaxHandler(this.mainController, this.webInterface);
		final MetadatabaseJSONHandler jsonHandler = new MetadatabaseJSONHandler(this.mainController, this.webInterface);
		final MetadatabaseHTMLHandler mdbHTMLHandler = new MetadatabaseHTMLHandler(this.mainController, this.webInterface);

		final ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(false);
		resource_handler.setResourceBase(".");
		resource_handler.setWelcomeFiles(new String[] {
		// "index.html"
				});

		resource_handler.setResourceBase("trunk/web/ressources");
		final HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				mdbDefaultHandler, resource_handler, ajaxHandler, jsonHandler, mdbHTMLHandler, new DefaultHandler()
		});
		server.setHandler(handlers);

		try {
			server.start();
			server.join();

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}
}