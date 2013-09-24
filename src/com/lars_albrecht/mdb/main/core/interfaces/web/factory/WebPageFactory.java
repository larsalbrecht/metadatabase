/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class WebPageFactory {

	private static final ConcurrentHashMap<String[], Class<? extends WebPage>>	webPages	= new ConcurrentHashMap<String[], Class<? extends WebPage>>();

	@SuppressWarnings("unchecked")
	public static WebPage getWebPage(final String identifier,
			final String actionname,
			final Request request,
			final MainController mainController,
			final WebInterface webInterface) {

		Class<WebPage> clazz = null;

		for (final Map.Entry<String[], Class<? extends WebPage>> entry : WebPageFactory.webPages.entrySet()) {
			for (final String string : Arrays.asList(entry.getKey())) {
				if (string.equalsIgnoreCase(identifier)) {
					clazz = (Class<WebPage>) entry.getValue();
					break;
				}
			}
			if (clazz != null) {
				break;
			}

		}
		if (clazz == null) {
			return null;
		}
		final Constructor<WebPage>[] constructors = (Constructor<WebPage>[]) clazz.getConstructors();
		Constructor<WebPage> webPageConstructor = null;
		for (final Constructor<WebPage> constructor : constructors) {
			if (constructor.getParameterTypes().length == 4) {
				webPageConstructor = constructor;
				break;
			}
		}
		try {
			return webPageConstructor.newInstance(actionname, request, mainController, webInterface);

		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void addWebPage(final String[] identifier, final Class<? extends WebPage> webPage) {
		WebPageFactory.webPages.put(identifier, webPage);
	}

	public static void removeWebPage(final String[] identifier) {
		WebPageFactory.webPages.remove(identifier);
	}

	public static void clearWebPages() {
		WebPageFactory.webPages.clear();
	}

}
