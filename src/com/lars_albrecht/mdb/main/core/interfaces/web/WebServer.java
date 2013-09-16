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
		http.setName("Metadatabase WebServer-Interface");

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
		this.server.setHandler(handlers);

		try {
			this.server.start();
			this.server.join();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}