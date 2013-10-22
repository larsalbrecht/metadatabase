/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPartial;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.config.WebPageMenuItemConfig;

/**
 * @author lalbrecht
 * 
 */
public class MainNavigationPartial extends WebPartial {

	public MainNavigationPartial(final String actionname, final Request request, final MainController mainController,
			final WebInterface webInteface) throws Exception {
		super(actionname, request, mainController, webInteface);

		this.setPageTemplate(this.generateMainNavigation());
	}

	private Template generateMainNavigation() {
		final Template mainNavigationTemplate = this.getPageTemplate();

		if (this.webInterface.getMenuPages().size() > 0) {
			String listContainer = mainNavigationTemplate.getSubMarkerContent("navigation");
			String listItems = "";
			String tempListItem = "";
			Collections.sort(this.webInterface.getMenuPages());
			for (final WebPageMenuItemConfig menuItemConfig : this.webInterface.getMenuPages()) {
				tempListItem = mainNavigationTemplate.getSubMarkerContent("navitem");
				tempListItem = Template.replaceMarker(tempListItem, "href",
						this.getHrefForNames(new ArrayList<String>(Arrays.asList(menuItemConfig.getNames()))), false);
				tempListItem = Template.replaceMarker(tempListItem, "title", menuItemConfig.getItemTitle(), false);

				listItems += tempListItem;
			}
			listContainer = Template.replaceMarker(listContainer, "navigationitems", listItems, false);
			mainNavigationTemplate.replaceMarker("content", listContainer, false);
		} else {
			mainNavigationTemplate.replaceMarker("content", mainNavigationTemplate.getSubMarkerContent("nonavitems"), false);
		}

		return mainNavigationTemplate;
	}

	private String getHrefForNames(final ArrayList<String> pageNames) {
		if ((pageNames != null) && (pageNames.size() > 0)) {
			return "?action=" + pageNames.get(0);
		}
		return null;
	}

	@Override
	public String getTemplateName() {
		return "mainnavigation";
	}

}
