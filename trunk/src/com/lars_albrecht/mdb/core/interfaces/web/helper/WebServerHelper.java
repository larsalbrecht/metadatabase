/**
 * 
 */
package com.lars_albrecht.mdb.core.interfaces.web.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.FileFinder;
import com.lars_albrecht.general.utilities.HTML;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.Main;
import com.lars_albrecht.mdb.core.abstracts.ThreadEx;
import com.lars_albrecht.mdb.core.controller.MainController;
import com.lars_albrecht.mdb.core.handler.DataHandler;
import com.lars_albrecht.mdb.core.handler.ObjectHandler;
import com.lars_albrecht.mdb.core.models.FileAttributeList;
import com.lars_albrecht.mdb.core.models.FileItem;
import com.lars_albrecht.mdb.core.models.KeyValue;

/**
 * @author lalbrecht
 * 
 */
public class WebServerHelper {

	private MainController	mainController			= null;

	public final static int	SEARCHTYPE_MIXED		= 0;
	public final static int	SEARCHTYPE_TEXTALL		= 1;
	public final static int	SEARCHTYPE_ATTRIBUTE	= 2;

	public WebServerHelper(final MainController mainController) {
		this.mainController = mainController;
	}

	public String generateAttributesView(final ConcurrentHashMap<String, String> GETParams) {
		String resultStr = "<div id=\"attributesView\" class=\"contentPart\">";
		resultStr += "<p>Diese Anzeige ist aktuell nicht verfügbar.</p>";
		resultStr += "</div>";
		return resultStr;
	}

	/**
	 * Generate content for the given content, filename and parameters.
	 * 
	 * @param content
	 * @param filename
	 * @param GETParams
	 * @param headerKeyValue
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public String generateContent(final String content,
			final String filename,
			final ConcurrentHashMap<String, String> GETParams,
			final ConcurrentHashMap<String, String> headerKeyValue) throws UnsupportedEncodingException {
		String generatedContent = content;
		String contentMarkerReplacement = "";
		// System.out.println("Params: " + GETParams);
		String pageTitle = "JMovieDB - Webinterface";
		String subTitle = "";
		if (filename.equalsIgnoreCase("index.html")) {
			String action = null;
			if (GETParams.containsKey("action")) {
				action = GETParams.get("action");
			} else {
				action = "index";
			}

			if (action.equalsIgnoreCase("index")) {
				contentMarkerReplacement = "Auf dieser Seite kann man vorhandene Filme suchen und sich verschiedene Informationen anzeigen lassen.";
			} else if (action.equalsIgnoreCase("showSearchresults")) {
				contentMarkerReplacement = this.generateSearchresults(GETParams);
				subTitle = this.getTitleForSearchresults(GETParams);
				pageTitle += " | " + subTitle;
			} else if (action.equalsIgnoreCase("showFileDetails")) {
				contentMarkerReplacement = "showFileDetails";
				if (GETParams.containsKey("fileId") && (GETParams.get("fileId") != null)) {
					final Integer fileId = Integer.parseInt(GETParams.get("fileId"));

					if ((fileId != null) && (fileId > 0)) {
						final FileItem tempFileItem = this.mainController.getDataHandler().findAllInfoForAllByFileId(fileId);
						contentMarkerReplacement = this.generateDetailView(tempFileItem);
						subTitle = this.getTitleForDetailview(tempFileItem);
						pageTitle += " | " + subTitle;
					}

				}
			} else if (action.equalsIgnoreCase("showInfoControl")) {
				contentMarkerReplacement = this.generateInfoControlView(GETParams);
				subTitle = this.getTitleForInfoview();
				pageTitle += " | " + subTitle;
			} else if (action.equalsIgnoreCase("showAttributes")) {
				contentMarkerReplacement = this.generateAttributesView(GETParams);
				subTitle = this.getTitleForAttributesView();
				pageTitle += " | " + subTitle;
			}

			// replace contentmarker with "contentMarkerReplacement" if marker
			// exists.
			if (Template.containsMarker(generatedContent, "content")) {
				generatedContent = Template.replaceMarker(generatedContent, "content", contentMarkerReplacement);
			}

			// replace "free" marker.
			if (Template.containsMarker(content, "searchTerm")) {
				if (GETParams.containsKey("searchStr") && (GETParams.get("searchStr") != null)) {
					try {
						generatedContent = Template.replaceMarker(generatedContent, "searchTerm",
								URLDecoder.decode(GETParams.get("searchStr"), "utf-8"));
					} catch (final UnsupportedEncodingException e) {
						generatedContent = e.getMessage();
					}
				} else {
					generatedContent = Template.replaceMarker(generatedContent, "searchTerm", "");
				}
			}
			if (Template.containsMarker(generatedContent, "lastFiveAdded")) {
				final ArrayList<FileItem> lastFiveList = ObjectHandler.castObjectListToFileItemList(this.mainController.getDataHandler()
						.findAll(new FileItem(), 5));
				final String listOutput = HTML.generateListOutput(lastFiveList, null, null, false);
				generatedContent = Template.replaceMarker(generatedContent, "lastFiveAdded", listOutput);
			}
			if (Template.containsMarker(generatedContent, "title")) {
				generatedContent = Template.replaceMarker(generatedContent, "title", pageTitle);
			}
			if (Template.containsMarker(generatedContent, "subTitle")) {
				generatedContent = Template.replaceMarker(generatedContent, "subTitle", subTitle);
			}
		}

		return generatedContent;
	}

	public String generateDetailView(final FileItem item) {
		String resultStr = "<div id=\"detailView\" class=\"contentPart\">";
		if ((item != null) && (item.getId() != null)) {
			resultStr += "<h2>" + item.getName() + " (" + item.getId() + ")" + "</h2>";
			resultStr += "<div class=\"path\">" + item.getFullpath().replaceAll("\\\\", "\\\\\\\\") + "</div>";
			resultStr += "<div class=\"listWrapper\"><div class=\"key\">Dir</div><div class=\"value\">"
					+ item.getDir().replaceAll("\\\\", "\\\\\\\\") + "</div></div>";

			if (item.getSize() != null) {
				resultStr += "<div class=\"listWrapper\"><div class=\"key\">Size</div><div class=\"value\">"
						+ Helper.getHumanreadableFileSize(item.getSize()) + "</div></div>";
			}
			if (item.getCreateTS() != null) {
				resultStr += "<div class=\"listWrapper\"><div class=\"key\">Added</div><div class=\"value\">"
						+ Helper.getFormattedTimestamp(item.getCreateTS().longValue(), null) + "</div></div>";
			}

			if ((item.getAttributes() != null) && (item.getAttributes().size() > 0)) {
				resultStr += "<hr />";
				resultStr += "<div id=\"attributes\">";

				resultStr += "<nav><ul><li><a href=\"#MediaInfo\">MediaInfo</a></li><li><a href=\"#themoviedb\">The Movie DB</a></li><li><a href=\"#thetvdb\">The TV DB</a></li></ul></nav>";

				resultStr += "<h3>Attributes</h3>";
				String currentInfoType = null;
				int i = 0;
				for (final FileAttributeList attributeList : item.getAttributes()) {
					if ((currentInfoType == null)
							|| !currentInfoType.equalsIgnoreCase(attributeList.getKeyValues().get(0).getKey().getInfoType())) {
						currentInfoType = attributeList.getKeyValues().get(0).getKey().getInfoType();
						if (i > 0) {
							resultStr += "</div>";
						}
						resultStr += "<div class=\"infoSection\">";
						resultStr += "<h4>" + currentInfoType + "</h4>" + "<a name=\"" + currentInfoType + "\"></a>";
					}
					if ((attributeList.getKeyValues() != null) && (attributeList.getKeyValues().size() > 0)) {
						resultStr += "<div class=\"sectionSection\">";
						resultStr += "<h5 class=\"tableHeader\">" + attributeList.getSectionName() + "</h5>";
						resultStr += "<table>";
						resultStr += "<tr>";
						resultStr += "<th>" + "Key" + "</th>";
						resultStr += "<th>" + "Value" + "</th>";
						resultStr += "</tr>";
						// copy to reduce the count of loops in search for
						// values
						FileAttributeList attributeListCpy = null;
						try {
							attributeListCpy = (FileAttributeList) attributeList.clone();

							int evenOdd = 0;
							for (final KeyValue<String, Object> keyValue : attributeList.getKeyValues()) {
								if (attributeListCpy.getKeyValues().contains(keyValue)) {
									resultStr += "<tr class=\"" + ((evenOdd % 2) == 0 ? "even" : "odd") + "\">";
									resultStr += "<td>" + keyValue.getKey().getKey() + "</td>";
									resultStr += "<td>";
									final ArrayList<Object> tempList = this.getValuesForKey(attributeListCpy, keyValue.getKey().getKey());
									for (int j = 0; j < tempList.size(); j++) {
										if (j != 0) {
											resultStr += ", ";
										}
										if (keyValue.getKey().getSearchable()) {
											resultStr += "<a href=\"?"
													+ "action=showSearchresults&searchStr="
													+ URLEncoder.encode(keyValue.getKey().getKey() + "=" + "\"" + tempList.get(j) + "\"",
															"utf-8") + "\">" + tempList.get(j) + "</a>";
										} else {
											resultStr += tempList.get(j);
										}
									}
									resultStr += "</td>";
									resultStr += "</tr>";
									attributeListCpy = this.removeKeysFromFileAttributeList(attributeListCpy, keyValue.getKey().getKey());
									evenOdd++;
								}
							}
						} catch (final CloneNotSupportedException e) {
							e.printStackTrace();
						} catch (final UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						resultStr += "</table>";
						resultStr += "</div>";
					}
					i++;
				}
				resultStr += "</div>";
			}

		} else {
			resultStr += "<p>Nichts gefunden</p>";
		}
		resultStr += "</div>";
		return resultStr;
	}

	public String generateInfoControlView(final ConcurrentHashMap<String, String> GETParams) {
		String resultStr = "<div id=\"infoView\" class=\"contentPart\">";
		this.mainController.getDataHandler();
		this.mainController.getDataHandler().reloadData(DataHandler.RELOAD_ALL);
		final ConcurrentHashMap<String, Object> info = this.mainController.getDataHandler().getInfoFromDatabase();
		if (info != null) {
			resultStr += "<h2>Informationen</h2>";
			resultStr += "<h3 class=\"tableHeader\">Anzahl Einträge</h3>";
			resultStr += "<table>";
			resultStr += "<tr>";
			resultStr += "<th>Typ</th>";
			resultStr += "<th>Anzahl</th>";
			resultStr += "</tr>";
			resultStr += "<tr>";
			resultStr += "<td>File Count</td>";
			resultStr += "<td>" + info.get("fileCount") + "</td>";
			resultStr += "</tr>";
			resultStr += "<tr>";
			resultStr += "<td>Key Count</td>";
			resultStr += "<td>" + info.get("keyCount") + "</td>";
			resultStr += "</tr>";
			resultStr += "<tr>";
			resultStr += "<td>Value Count</td>";
			resultStr += "<td>" + info.get("valueCount") + "</td>";
			resultStr += "</tr>";
			resultStr += "<tr>";
			resultStr += "<td>Filetypes (count of files)</td>";
			resultStr += "<td>"
					+ Helper.implode((Map<?, ?>) info.get("filesWithFiletype"), ", ", null, null, " (", ")",
							"<span class=\"infoListEntry\">", "</span>", false) + "</td>";
			resultStr += "</tr>";
			resultStr += "</table>";
		} else {
			resultStr += "<p>Ein Fehler ist aufgetreten. Konnte keine Informationen sammeln.</p>";
		}
		resultStr += "</div>";

		resultStr += "<div id=\"controlView\" class=\"contentPart\">";
		resultStr += "<h3>Control</h3>";
		boolean isStartFinder = false;
		if (GETParams.containsKey("do") && (GETParams.get("do") != null) && GETParams.get("do").equalsIgnoreCase("startFinder")) {
			isStartFinder = true;
			resultStr += "<div id=\"statusArea\">";
			resultStr += "<p>" + "Files will be refreshed ..." + "</p>";
			resultStr += "</div>";
			this.mainController.startSearch();
		}

		boolean isStartCollectors = false;
		if (GETParams.containsKey("do") && (GETParams.get("do") != null) && GETParams.get("do").equalsIgnoreCase("startCollectors")) {
			isStartCollectors = true;
			resultStr += "<div id=\"statusArea\">";

			resultStr += "<p>" + "Collections will be refreshed ..." + "<br />";
			final ArrayList<FileItem> fileList = ObjectHandler.castObjectListToFileItemList(this.mainController.getDataHandler().findAll(
					new FileItem(), null));
			if ((fileList != null) && (fileList.size() > 0)) {
				resultStr += "Collections can be refreshed ... work in progress" + "</p>";
				this.mainController.getcController().collectInfos(fileList);
			} else {
				resultStr += "Collections cannot be refreshed" + (fileList.size() == 0 ? ", because no files are available" : "")
						+ ". Process stopped." + "</p>";
			}

			resultStr += "</div>";
		}
		resultStr += "<nav><ul>";
		resultStr += "<li><a href=\""
				+ (isStartFinder ? "javascript:void(0)\" class=\"disabled" : "?action=showInfoControl&do=startFinder") + "\">"
				+ "Start Finder" + "</a></li>";
		resultStr += "<li><a href=\""
				+ (isStartCollectors ? "javascript:void(0)\" class=\"disabled" : "?action=showInfoControl&do=startCollectors") + "\">"
				+ "Start Collectors" + "</a></li>";
		resultStr += "</ul></nav>";
		resultStr += "</div>";
		return resultStr;
	}

	/**
	 * Search with "=" for attributes must check if the search string is end
	 * after " " or if it is surrounded by """.
	 * 
	 * @param GETParams
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public String generateSearchresults(final ConcurrentHashMap<String, String> GETParams) throws UnsupportedEncodingException {
		String resultStr = "<div id=\"searchresultsView\" class=\"contentPart\">";
		if (GETParams.containsKey("searchStr") && (GETParams.get("searchStr") != null)) {
			// get DATA for output
			final String searchStr = (URLDecoder.decode(GETParams.get("searchStr"), "utf-8")).replaceAll("[\"]", "");
			int searchType = WebServerHelper.SEARCHTYPE_TEXTALL;

			String searchKey = null;
			String searchValue = null;
			String[] searchStrList = null;
			final ArrayList<FileItem> foundList = new ArrayList<FileItem>();
			if (searchStr.contains(" ")) {
				searchStrList = searchStr.split(" ");
			} else {
				searchStrList = new String[] {
					searchStr
				};
			}

			for (final String searchStrItem : searchStrList) {
				searchType = WebServerHelper.SEARCHTYPE_TEXTALL;
				if (searchStrItem.contains("=")) {
					final String[] searchArr = searchStrItem.split("=");
					if (searchArr.length == 2) {
						searchKey = searchArr[0];
						searchValue = searchArr[1];
						if (this.mainController.getDataHandler().isKeyInKeyList(searchKey)) {
							searchType = WebServerHelper.SEARCHTYPE_ATTRIBUTE;
						}

					}
				}

				switch (searchType) {
					default:
					case SEARCHTYPE_TEXTALL:
						foundList.addAll(ObjectHandler.castObjectListToFileItemList(Helper.uniqueList(this.mainController.getDataHandler()
								.findAllFileItemForStringInAll(searchStrItem))));
						break;
					case SEARCHTYPE_ATTRIBUTE:
						foundList.addAll(ObjectHandler.castObjectListToFileItemList(Helper.uniqueList(this.mainController.getDataHandler()
								.findAllFileItemForStringInAttributesByKeyValue(searchKey, searchValue))));
						break;
				}
			}
			resultStr += HTML.generateListOutput(Helper.uniqueList(foundList), searchStrList,
					searchStrList.length > 1 ? WebServerHelper.SEARCHTYPE_MIXED : searchType, true);
		} else {
			resultStr += "<p>Suchen Sie mit hilfe der Suche</p>";
		}

		resultStr += "</div>";
		return resultStr;
	}

	/**
	 * Returns the content of the file from the url. It is like "index.html".
	 * The file must be in /web/
	 * 
	 * @param url
	 * @param GETParams
	 * @param headerKeyValue
	 * @return String
	 */
	public String getFileContent(final String url,
			final ConcurrentHashMap<String, String> GETParams,
			final ConcurrentHashMap<String, String> headerKeyValue) {
		File file = null;
		if (url != null) {
			Debug.log(Debug.LEVEL_INFO, "Try to load file for web interface: " + url);
			final InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("web/" + url);
			file = new File(url);
			try {
				String content = "";
				if (inputStream != null) {
					content = Helper.getInputStreamContents(inputStream, Charset.forName("UTF-8"));
					return content;
				} else if (file != null && (file = FileFinder.getInstance().findFile(new File(new File(url).getName()), false)) != null
						&& file.exists() && file.isFile() && file.canRead()) {
					content = this.generateContent(Helper.getFileContents(file), file.getName(), GETParams, headerKeyValue);
					return content;
				} else {
					Debug.log(Debug.LEVEL_ERROR, "InputStream == null && File == null: " + file);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Returns the content of the file from the url. It is like "index.html".
	 * The file must be in /web/
	 * 
	 * @param url
	 * @param GETParams
	 * @param headerKeyValue
	 * @return String
	 */
	public String getAjaxContent(final String url,
			final ConcurrentHashMap<String, String> GETParams,
			final ConcurrentHashMap<String, String> headerKeyValue,
			final boolean isJSON) {
		String content = null;
		if (url != null) {
			content = "";
			if (GETParams != null && GETParams.size() > 0 && GETParams.containsKey("action") && GETParams.get("action") != null) {
				final String action = GETParams.get("action");
				if (action.equalsIgnoreCase("getStatus")) {
					if (this.mainController.getfController().getThreadList().size() > 0) {
						content += "<p>Finder is running</p>";
					}
					if (this.mainController.getcController().getThreadList().size() > 0) {
						final String[] collectorNameList = new String[this.mainController.getcController().getThreadList().size()];

						int i = 0;
						for (final ThreadEx t : this.mainController.getcController().getThreadList()) {
							if (t.getInfo() != null && t.getInfo().length > 0 && t.getInfo()[0].equals("Collector")) {
								collectorNameList[i] = t.getName();
							}
							i++;
						}

						content += "<p>Collector is running:" + "<ul>" + Helper.implode(collectorNameList, null, "<li>", "</li>") + "</ul>"
								+ "</p>";
					}

					if (content.equalsIgnoreCase("")) {
						content = "<p>No activities</p>";
					}
				} else if (action.equalsIgnoreCase("autocomplete") && GETParams.get("term") != null) {
					content = ObjectHandler.fileItemListToJSON(ObjectHandler.castObjectListToFileItemList(this.mainController
							.getDataHandler().findAllFileItemForStringInAll(GETParams.get("term"))));
					if (content == null) {
						content = "";
					}
				}
			}
		}

		return content;
	}

	public String getTitleForAttributesView() {
		return "Attributes";
	}

	public String getTitleForDetailview(final FileItem fileItem) {
		String titleStr = "Kein Titel gewählt";
		if (fileItem != null) {
			titleStr = fileItem.getName();
		}
		return "Detailansicht: " + titleStr;
	}

	public String getTitleForInfoview() {
		return "Infos";
	}

	public String getTitleForSearchresults(final ConcurrentHashMap<String, String> GETParams) {
		String searchStr = "";
		if (GETParams.containsKey("searchStr") && (GETParams.get("searchStr") != null)) {
			searchStr = GETParams.get("searchStr");
		}
		return "Suchergebnisse für: " + searchStr;
	}

	private ArrayList<Object> getValuesForKey(final FileAttributeList list, final String key) {
		final ArrayList<Object> resultList = new ArrayList<Object>();
		if ((list != null) && (list.getKeyValues().size() > 0) && (key != null)) {
			for (final KeyValue<String, Object> keyValue : list.getKeyValues()) {
				if ((keyValue != null) && (keyValue.getKey() != null) && keyValue.getKey().getKey().equals(key)
						&& (keyValue.getValue() != null)) {
					resultList.add(keyValue.getValue().getValue());
				}
			}
		}
		return resultList;
	}

	private FileAttributeList
			removeKeysFromFileAttributeList(final FileAttributeList list, final String key) throws CloneNotSupportedException {
		final FileAttributeList resultList = (FileAttributeList) list.clone();
		if ((list != null) && (list.getKeyValues().size() > 0) && (key != null)) {
			for (final KeyValue<String, Object> keyValue : list.getKeyValues()) {
				if ((keyValue != null) && (keyValue.getKey() != null) && keyValue.getKey().getKey().equals(key)
						&& (keyValue.getValue() != null)) {
					resultList.getKeyValues().remove(keyValue);
				}
			}
		}
		return resultList;
	}

}
