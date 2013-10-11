/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.helper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.eclipse.jetty.server.Request;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.abstracts.ThreadEx;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.ObjectHandler;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;
import com.lars_albrecht.mdb.main.core.interfaces.web.factory.WebPageFactory;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.DefaultErrorPage;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.LastFivePartial;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.LoginBoxPartial;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.MainNavigationPartial;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.Tag;
import com.lars_albrecht.mdb.main.core.utilities.Cache;
import com.lars_albrecht.mdb.main.core.utilities.Paths;

/**
 * @author lalbrecht TODO Do better (Each "page" is an own Object/class and all
 *         inherit from one superclass)! Declare html not in class, load a file
 *         and replace marker (use Template class).
 * 
 */
public class WebServerHelper {

	private MainController	mainController			= null;
	private WebInterface	webInterface			= null;

	public final static int	SEARCHTYPE_MIXED		= 0;
	public final static int	SEARCHTYPE_TEXTALL		= 1;
	public final static int	SEARCHTYPE_ATTRIBUTE	= 2;

	public WebServerHelper(final MainController mainController, final WebInterface webInterface) {
		this.mainController = mainController;
		this.webInterface = webInterface;
	}

	/**
	 * Generate content for the given content, filename and parameters.
	 * 
	 * @param content
	 * @param filename
	 * @param request
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	private String generateContent(final String content, final String filename, final Request request) throws UnsupportedEncodingException {
		if ((content == null) || (filename == null) || (request == null)) {
			return null;
		}
		String generatedContent = content;
		String contentMarkerReplacement = "";
		String pageTitle = "JMovieDB - Webinterface";
		String subTitle = "";

		String action = null;
		if (request.getParameter("action") != null) {
			action = request.getParameter("action");
		} else {
			action = "index";
		}

		WebPage page = null;
		// TODO! enable caching, but not for all pages (fileDetailsPage)
		// if (WebServerHelper.pageList.size() == 0) {

		page = WebPageFactory.getWebPage(action, action, request, this.mainController, this.webInterface);
		if (page == null) {
			try {
				page = new DefaultErrorPage("404", request, this.mainController, this.webInterface);
				return null;
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		contentMarkerReplacement = page.getGeneratedContent();
		subTitle = page.getTitle();
		pageTitle = subTitle + " | " + pageTitle;

		// replace contentmarker with "contentMarkerReplacement" if marker
		// exists.
		if (Template.containsMarker(generatedContent, "content")) {
			generatedContent = Template.replaceMarker(generatedContent, "content", contentMarkerReplacement, Boolean.FALSE);
			Debug.log(Debug.LEVEL_DEBUG, "marker content exist. Replace it");
		} else {
			Debug.log(Debug.LEVEL_ERROR, "marker content DOES NOT exist");
		}

		// replace "free" marker.
		if (Template.containsMarker(content, "searchTerm")) {
			if ((request.getParameter("searchStr") != null) && (request.getParameter("searchStr") != null)) {
				generatedContent = Template.replaceMarker(generatedContent, "searchTerm",
						request.getParameter("searchStr").replaceAll("\"", "&quot;"), Boolean.FALSE);
			} else {
				generatedContent = Template.replaceMarker(generatedContent, "searchTerm", "", Boolean.FALSE);
			}
		}
		if (Template.containsMarker(generatedContent, "mainNavigation")) {
			try {
				final MainNavigationPartial mainNavigation = new MainNavigationPartial(action, request, this.mainController,
						this.webInterface);
				generatedContent = Template.replaceMarker(generatedContent, "mainNavigation", mainNavigation.getGeneratedContent(), false);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		if (Template.containsMarker(generatedContent, "lastFiveAdded")) {
			try {
				final LastFivePartial lastFive = new LastFivePartial(action, request, this.mainController, this.webInterface);
				generatedContent = Template.replaceMarker(generatedContent, "lastFiveAdded", lastFive.getGeneratedContent(), false);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		if (Template.containsMarker(generatedContent, "loginbox")) {
			try {
				final LoginBoxPartial loginBox = new LoginBoxPartial(action, request, this.mainController, this.webInterface);
				generatedContent = Template.replaceMarker(generatedContent, "loginbox", loginBox.getGeneratedContent(), false);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		if (Template.containsMarker(generatedContent, "title")) {
			generatedContent = Template.replaceMarker(generatedContent, "title", pageTitle, Boolean.FALSE);
		}
		if (Template.containsMarker(generatedContent, "subTitle")) {
			generatedContent = Template.replaceMarker(generatedContent, "subTitle", subTitle, Boolean.FALSE);
		}

		return generatedContent;
	}

	/**
	 * Returns the content of the file from the url. It is like "index.html".
	 * The file must be in /web/
	 * 
	 * @param url
	 * @param request
	 * @return String
	 */
	public String getAjaxContent(final String url, final Request request, final boolean isJSON) {
		String content = null;
		if (url != null) {
			content = "";
			if ((request.getParameter("action") != null) && (request.getParameter("action") != null)) {
				final String action = request.getParameter("action");
				if (action.equalsIgnoreCase("getStatus")) {
					if (this.mainController.getfController().getThreadList().size() > 0) {
						content += "<p>Finder läuft</p>";
					}
					if (this.mainController.getcController().getThreadList().size() > 0) {
						final String[] collectorNameList = new String[this.mainController.getcController().getThreadList().size()];

						int i = 0;
						for (final ThreadEx t : this.mainController.getcController().getThreadList()) {
							if ((t.getInfo() != null) && (t.getInfo().length > 0) && t.getInfo()[0].equals("Collector")) {
								collectorNameList[i] = t.getName();
							}
							i++;
						}

						content += "<p>Collector läuft:" + "<ul>" + Helper.implode(collectorNameList, null, "<li>", "</li>") + "</ul>"
								+ "</p>";
					}

					if (content.equalsIgnoreCase("")) {
						content = "<p>Keine Aktivitäten</p>";
					}
				} else if (action.equalsIgnoreCase("autocompleteSearch") && (request.getParameter("term") != null)) {
					if (request.getParameter("term").contains("=")) {
						ArrayList<String> keyList = null;
						final String searchKey = request.getParameter("term").substring(0, request.getParameter("term").indexOf("="));
						final String searchValue = request.getParameter("term").substring(request.getParameter("term").indexOf("=") + 1);
						if ((searchValue != null) && !searchValue.equalsIgnoreCase("")) {
							keyList = this.mainController.getDataHandler().findAllValuesForKeyWithValuePart(searchKey, searchValue);
						} else {
							keyList = this.mainController.getDataHandler().findAllValuesForKey(searchKey);
						}
						final ArrayList<String> newKeyList = new ArrayList<String>();
						for (final String string : keyList) {
							if (string != null) {
								newKeyList.add(searchKey + "=" + string);
								// System.out.println(searchKey + " - " +
								// string);
							}
						}
						// TODO only show real value, but set with (example)
						// "type="
						content = ObjectHandler.stringListToJSON(newKeyList);
					} else {
						content = ObjectHandler.fileItemListToJSON(this.mainController.getDataHandler().findAllFileItemForStringInAll(
								request.getParameter("term")));
					}
					if (content == null) {
						content = "";
					}
				} else if (action.equalsIgnoreCase("autocompleteTags") && (request.getParameter("term") != null)) {
					content = ObjectHandler.tagListToJSON(this.mainController.getDataHandler().getTags());

				} else if (action.equalsIgnoreCase("addTag") && (request.getParameter("value") != null)
						&& (request.getParameter("fileId") != null)) {
					try {
						final Tag tempTag = new Tag(request.getParameter("value"), Boolean.TRUE);
						final Integer fileId = Integer.parseInt(request.getParameter("fileId"));
						final Integer id = this.mainController.getDataHandler().addFileTag(new FileTag(fileId, tempTag, Boolean.TRUE));
						final ArrayList<Tag> tempTagList = new ArrayList<Tag>();
						tempTag.setId(id);
						tempTagList.add(tempTag);
						content = ObjectHandler.tagListToJSON(tempTagList);
					} catch (final Exception e) {
						content = null;
						e.printStackTrace();
					}
				} else if (action.equalsIgnoreCase("removeTagFromFile") && (request.getParameter("fileTagId") != null)
						&& (Integer.parseInt(request.getParameter("fileTagId")) > 0)) {
					this.mainController.getDataHandler().removeFileTag(Integer.parseInt(request.getParameter("fileTagId")));
					content = "success";
				}
			}
		}

		return content;
	}

	/**
	 * Returns the content of the file from the url. It is like "index.html".
	 * 
	 * @param requestedRessource
	 * @param request
	 * @return String
	 */
	public String getFileContent(String requestedRessource, final Request request) {
		File indexPath = (File) Cache.getCacheEntry(Cache.CACHE_WEB, "indexPath");
		if (indexPath == null) {
			indexPath = new File(Paths.WEB_ROOT + File.separator + "index.html");
			Cache.addToCache(Cache.CACHE_WEB, "indexPath", indexPath);
		}
		if (requestedRessource != null) {
			if (requestedRessource.equalsIgnoreCase("")) {
				requestedRessource = "index.html";
			}
			Debug.log(Debug.LEVEL_INFO, "Try to load file for web interface: " + indexPath);
			try {
				String content = "";
				if ((indexPath != null) && (indexPath != null) && indexPath.exists() && indexPath.isFile() && indexPath.canRead()) {
					String indexContent = (String) Cache.getCacheEntry(Cache.CACHE_WEB, "indexContent");
					if (indexContent == null) {
						indexContent = Helper.getFileContents(indexPath);
						Cache.addToCache(Cache.CACHE_WEB, "indexContent", indexContent);
					}
					content = this.generateContent(indexContent, requestedRessource, request);
					if (content == null) {
						System.out.println("NULL");
					}
					return content;
				} else {
					Debug.log(Debug.LEVEL_ERROR, "InputStream == null && File == null: " + requestedRessource);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
