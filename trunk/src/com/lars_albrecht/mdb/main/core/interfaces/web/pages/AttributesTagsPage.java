/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;

/**
 * @author lalbrecht
 * 
 */
public class AttributesTagsPage extends WebPage {

	public AttributesTagsPage(final String actionname, final Request request, final MainController mainController,
			final WebInterface webInterface) throws Exception {
		super(actionname, request, mainController, webInterface);
		this.setPageTemplate(this.generateAttributesTagsView());
	}

	private Template fillAttributesContainer(final Template attributesTagsTemplate) {
		final String tagsContainer = attributesTagsTemplate.getSubMarkerContent("attributesContainer");
		attributesTagsTemplate.replaceMarker("attributescontainer", tagsContainer, false);
		return attributesTagsTemplate;
	}

	private Template fillTagsContainer(final Template attributesTagsTemplate) {
		final String tagsContainer = attributesTagsTemplate.getSubMarkerContent("tagsContainer");
		attributesTagsTemplate.replaceMarker("tagscontainer", tagsContainer, false);
		return attributesTagsTemplate;
	}

	private Template generateAttributesTagsView() {
		Template attributesTagsTemplate = this.getPageTemplate();

		attributesTagsTemplate = this.fillAttributesContainer(attributesTagsTemplate);
		attributesTagsTemplate = this.fillTagsContainer(attributesTagsTemplate);

		return attributesTagsTemplate;
	}

	@Override
	public String getTemplateName() {
		return "attributestags";
	}

	@Override
	public String getTitle() {
		return "Attribute / Tags";
	}

	@Override
	public List<String> getPageNames() {
		final String[] names = {
				"attributestags", "AttributeTags"
		};
		return Arrays.asList(names);
	}

}
