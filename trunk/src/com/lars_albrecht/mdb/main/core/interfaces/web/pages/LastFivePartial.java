/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.util.ArrayList;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.DataHandler;
import com.lars_albrecht.mdb.main.core.handler.ObjectHandler;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPartial;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;

/**
 * @author lalbrecht
 * 
 */
public class LastFivePartial extends WebPartial {

	public LastFivePartial(final String actionname, final Request request, final MainController mainController,
			final WebInterface webInteface) throws Exception {
		super(actionname, request, mainController, webInteface);

		this.setPageTemplate(this.generateLastFivePartial());
	}

	private Template generateLastFivePartial() {
		final Template lastFivePartialTemplate = this.getPageTemplate();
		this.mainController.getDataHandler();
		final ArrayList<FileItem> lastFiveList = ObjectHandler.castObjectListToFileItemList(DataHandler.findAll(new FileItem(), 5, null,
				" ORDER BY fileInformation.createTS DESC"));

		if (lastFiveList.size() > 0) {
			String listContainer = lastFivePartialTemplate.getSubMarkerContent("lastfivelist");
			String listItems = "";
			String tempListItem = "";
			for (final FileItem fileItem : lastFiveList) {
				tempListItem = lastFivePartialTemplate.getSubMarkerContent("lastfivelistitem");
				tempListItem = Template.replaceMarker(tempListItem, "fileid", fileItem.getId().toString(), false);
				tempListItem = Template.replaceMarker(tempListItem, "filetitle", fileItem.getName(), false);

				listItems += tempListItem;
			}
			listContainer = Template.replaceMarker(listContainer, "lastfivelistitems", listItems, false);
			lastFivePartialTemplate.replaceMarker("content", listContainer, false);

		} else {
			lastFivePartialTemplate.replaceMarker("content", lastFivePartialTemplate.getSubMarkerContent("nolastfive"), false);
		}

		return lastFivePartialTemplate;
	}

	@Override
	public String getTemplateName() {
		return "lastfive";
	}

}
