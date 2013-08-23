/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.general.utilities.Template;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.handler.datahandler.AttributeHandler;
import com.lars_albrecht.mdb.main.core.handler.datahandler.MediaHandler;
import com.lars_albrecht.mdb.main.core.handler.datahandler.TagHandler;
import com.lars_albrecht.mdb.main.core.handler.datahandler.abstracts.ADataHandler;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.WebServerRequest;
import com.lars_albrecht.mdb.main.core.interfaces.web.abstracts.WebPage;
import com.lars_albrecht.mdb.main.core.models.FileAttributeList;
import com.lars_albrecht.mdb.main.core.models.KeyValue;
import com.lars_albrecht.mdb.main.core.models.persistable.FileItem;
import com.lars_albrecht.mdb.main.core.models.persistable.FileTag;
import com.lars_albrecht.mdb.main.core.models.persistable.MediaItem;

/**
 * @author lalbrecht
 * 
 */
public class FileDetailsPage extends WebPage {

	private WebInterface	webInterface	= null;

	public FileDetailsPage(final String actionname, final WebServerRequest request, final MainController mainController,
			final WebInterface webInterface) throws Exception {
		super(actionname, request, mainController, webInterface);
		this.webInterface = webInterface;

		if (request.getGetParams().containsKey("fileId") && (request.getGetParams().get("fileId") != null)) {
			final Integer fileId = Integer.parseInt(request.getGetParams().get("fileId"));

			if ((fileId != null) && (fileId > 0)) {
				final FileItem tempFileItem = this.mainController.getDataHandler().findAllInfoForAllByFileId(fileId);
				this.setPageTemplate(this.generateDetailView(tempFileItem));
			}
		} else {
			this.set404Error();
		}

	}

	@SuppressWarnings("unchecked")
	private Template generateDetailView(final FileItem item) {
		final Template detailViewTemplate = this.getPageTemplate();

		// if file is set
		if ((item != null) && (item.getId() != null)) {
			// set default infos
			detailViewTemplate.replaceMarker("content", detailViewTemplate.getSubMarkerContent("file"), Boolean.FALSE);

			detailViewTemplate.replaceMarker("title", item.getName() + " (" + item.getId() + ")", Boolean.TRUE);
			detailViewTemplate.replaceMarker("path", item.getFullpath().replaceAll("\\\\", "\\\\\\\\\\\\\\\\"), Boolean.FALSE);
			detailViewTemplate.replaceMarker("fileId", item.getId().toString(), Boolean.FALSE);

			if (item.getSize() != null) {
				String listWrapper = null;
				listWrapper = detailViewTemplate.getSubMarkerContent("listwrapper");
				listWrapper = Template.replaceMarker(listWrapper, "key", "Size", Boolean.TRUE);
				listWrapper = Template.replaceMarker(listWrapper, "value", Helper.getHumanreadableFileSize(item.getSize()), Boolean.TRUE);

				detailViewTemplate.replaceMarker("listwrapperSize", listWrapper, Boolean.TRUE);
			}

			if (item.getCreateTS() != null) {
				String listWrapper = null;
				listWrapper = detailViewTemplate.getSubMarkerContent("listwrapper");
				listWrapper = Template.replaceMarker(listWrapper, "key", "Added", Boolean.TRUE);
				listWrapper = Template.replaceMarker(listWrapper, "value",
						Helper.getFormattedTimestamp(item.getCreateTS().longValue(), null), Boolean.TRUE);

				detailViewTemplate.replaceMarker("listwrapperAdded", listWrapper, Boolean.TRUE);
			}

			if (item.getFiletype() != null) {
				String listWrapper = null;
				listWrapper = detailViewTemplate.getSubMarkerContent("listwrapper");
				listWrapper = Template.replaceMarker(listWrapper, "key", "Type", Boolean.TRUE);
				listWrapper = Template.replaceMarker(listWrapper, "value", item.getFiletype(), Boolean.TRUE);

				detailViewTemplate.replaceMarker("listwrapperType", listWrapper, Boolean.TRUE);
			}

			// add tags
			String tagsContainer = detailViewTemplate.getSubMarkerContent("tagsContainer");
			String tagsList = detailViewTemplate.getSubMarkerContent("tagsList");
			String tempTags = "";
			final String removeLink = detailViewTemplate.getSubMarkerContent("removeLink");

			final ConcurrentHashMap<String, String> tempReplacements = new ConcurrentHashMap<String, String>();
			final ArrayList<FileTag> itemFileTags = (ArrayList<FileTag>) ADataHandler.getHandlerDataFromFileItem(item, TagHandler.class);
			if ((itemFileTags != null) && (itemFileTags.size() > 0)) {
				String tempTagItem = detailViewTemplate.getSubMarkerContent("tagListItem");
				for (final FileTag fileTag : itemFileTags) {
					if ((fileTag != null) && (fileTag.getTag() != null) && (fileTag.getTag().getId() != null)
							&& (fileTag.getTag().getName() != null)) {

						tempReplacements.clear();

						tempReplacements.put("tagTitle", fileTag.getTag().getName());
						if (fileTag.getIsUser()) {
							tempTagItem = Template.replaceMarker(tempTagItem, "removeLink", removeLink, false);
							tempReplacements.put("fileTagId", fileTag.getId().toString());
						}
						tempTags += Template.replaceMarkers(tempTagItem, tempReplacements);
					}
				}
			}
			tagsList = Template.replaceMarker(tagsList, "tagList", tempTags, false);
			tagsContainer = Template.replaceMarker(tagsContainer, "tagList", tagsList, false);
			detailViewTemplate.replaceMarker("tags", tagsContainer, false);

			// add media
			final ArrayList<MediaItem> fileMediaItems = (ArrayList<MediaItem>) ((MediaHandler<?>) ADataHandler
					.getDataHandler(MediaHandler.class)).getHandlerDataForFileItem(item);
			final ArrayList<MediaItem> fileMediaItemsImages = new ArrayList<MediaItem>();
			final ArrayList<MediaItem> fileMediaItemsVideos = new ArrayList<MediaItem>();

			String mainImage = null;
			String gallery = null;

			for (final MediaItem mediaItem : fileMediaItems) {
				if ((mediaItem.getType() == MediaItem.TYPE_LOC_IMAGE) || mediaItem.getType().equals(MediaItem.TYPE_WEB_IMAGE)) {
					fileMediaItemsImages.add(mediaItem);
				} else if ((mediaItem.getType() == MediaItem.TYPE_LOC_VIDEO) || mediaItem.getType().equals(MediaItem.TYPE_WEB_VIDEO)) {
					fileMediaItemsVideos.add(mediaItem);
				}
			}

			if (fileMediaItemsImages.size() > 0) {

				// build file-image
				MediaItem fileImage = null;
				for (final MediaItem mediaItem : fileMediaItemsImages) {
					if (mediaItem.getName().equalsIgnoreCase("poster")) {
						fileImage = mediaItem;
						break;
					}
				}

				if ((fileImage != null)
						&& ((fileImage.getOptions().get(MediaItem.OPTION_WEB_ISDIRECT) == Boolean.TRUE) || ((!fileImage.getOptions()
								.containsKey(MediaItem.OPTION_WEB_ISDIRECT) || (fileImage.getOptions().get(MediaItem.OPTION_WEB_ISDIRECT) == Boolean.FALSE)) && (fileImage
								.getOptions().get(MediaItem.OPTION_WEB_BASE_PATH) != null)))) {

					mainImage = detailViewTemplate.getSubMarkerContent("image");
					mainImage = Template.replaceMarker(mainImage, "imageSrc", this.getUrlFromMediaItem(fileImage, 1), false);
					mainImage = Template.replaceMarker(mainImage, "imageClass", "posterImage", false);
					mainImage = Template.replaceMarker(mainImage, "imageTitle", fileImage.getName(), false);
				}

				// build gallery
				String tempGalleryItem = null;
				String tempGalleryItems = "";
				String galleryContainer = null;
				for (final MediaItem mediaItem : fileMediaItemsImages) {
					// exclude fileImage
					if ((fileImage != null) && fileImage.equals(mediaItem)) {
						continue;
					}

					if (galleryContainer == null) {
						galleryContainer = detailViewTemplate.getSubMarkerContent("gallery");
					}

					tempGalleryItem = detailViewTemplate.getSubMarkerContent("image");
					tempGalleryItem = Template.replaceMarker(tempGalleryItem, "imageSrc", this.getUrlFromMediaItem(mediaItem, 0), false);
					tempGalleryItem = Template.replaceMarker(tempGalleryItem, "imageClass",
							"galleryImage galleryImage-" + mediaItem.getType(), false);
					tempGalleryItem = Template.replaceMarker(tempGalleryItem, "imageTitle", mediaItem.getName(), false);

					tempGalleryItem = Template.replaceMarker(detailViewTemplate.getSubMarkerContent("galleryItem"), "image",
							tempGalleryItem, false);

					tempGalleryItem = Template.replaceMarker(tempGalleryItem, "imageSrcBig", this.getUrlFromMediaItem(mediaItem, 2), false);

					tempGalleryItems += tempGalleryItem;

				}

				gallery = Template.replaceMarker(galleryContainer, "galleryItems", tempGalleryItems, false);
			}

			// if file has attributes
			final ArrayList<FileAttributeList> itemFileAttributes = (ArrayList<FileAttributeList>) ADataHandler.getHandlerDataFromFileItem(
					item, AttributeHandler.class);
			if ((itemFileAttributes != null) && (itemFileAttributes.size() > 0)) {
				// get marker for attributes
				String attributes = detailViewTemplate.getSubMarkerContent("attributes");

				String currentInfoType = null;
				int i = 0;

				String attributesList = "";
				String sectionList = "";
				String attributeSectionList = "";
				String currentSection = null;
				// for each attribute ...
				for (final FileAttributeList attributeList : itemFileAttributes) {
					currentSection = attributeList.getSectionName();
					if ((currentInfoType == null) || !currentInfoType.equalsIgnoreCase(attributeList.getInfoType())) {
						if (i > 0) {
							// finish section and add to list
							attributeSectionList += Template.replaceMarker(attributesList, "sections", sectionList, Boolean.TRUE);
							attributesList = "";
							sectionList = "";
						}

						// create a new one for each infoType
						currentInfoType = attributeList.getInfoType();
						attributesList = detailViewTemplate.getSubMarkerContent("attributesList");
						attributesList = Template.replaceMarker(attributesList, "infotype-title", currentInfoType, Boolean.FALSE);
						attributesList = Template.replaceMarker(attributesList, "id", currentInfoType, Boolean.FALSE);
					}

					// fill sectionlist
					// TODO cut out the image-check and put it into an abstract
					// or an interface
					if ((!currentSection.equalsIgnoreCase("images")) && (attributeList.getKeyValues() != null)
							&& (attributeList.getKeyValues().size() > 0)) {
						sectionList += detailViewTemplate.getSubMarkerContent("attributeListSection");
						sectionList = Template.replaceMarker(sectionList, "sectionname", attributeList.getSectionName(), Boolean.TRUE);
						sectionList = Template.replaceMarker(sectionList, "keyTitle", "Key", Boolean.TRUE);
						sectionList = Template.replaceMarker(sectionList, "valueTitle", "Value", Boolean.TRUE);

						FileAttributeList attributeListCpy = null;
						try {
							attributeListCpy = (FileAttributeList) attributeList.clone();

							int evenOdd = 0;
							String rows = "";
							// fill rows
							for (final KeyValue<String, Object> keyValue : attributeList.getKeyValues()) {
								if ((keyValue != null)
										&& (keyValue.getKey() != null)
										&& attributeListCpy.getKeyValues().contains(keyValue)
										&& this.webInterface.getFileDetailsOutputItem().keyAllowed(currentInfoType,
												attributeList.getSectionName(), keyValue)) {
									rows += detailViewTemplate.getSubMarkerContent("attributesListSectionItem");
									rows = Template.replaceMarker(rows, "oddeven", ((evenOdd % 2) == 0 ? "even" : "odd"), Boolean.TRUE);
									rows = Template.replaceMarker(
											rows,
											"key",
											this.webInterface.getFileDetailsOutputItem().getKey(currentInfoType,
													attributeList.getSectionName(), keyValue), Boolean.TRUE);

									String value = "";
									final ArrayList<Object> tempList = this.getValuesForKey(attributeListCpy, keyValue.getKey().getKey());
									for (int j = 0; j < tempList.size(); j++) {
										if (j != 0) {
											value += ", ";
										}
										if (keyValue.getKey().getSearchable()) {
											String searchableValue = (String) tempList.get(j);
											final Pattern p = Pattern.compile("([^\\(\\)]+)");
											final Matcher m = p.matcher(searchableValue);
											if (m.find()) {
												searchableValue = m.group(1);
											}
											searchableValue = searchableValue.trim();

											value += "<a href=\"?"
													+ "action=showSearchresults&searchStr="
													+ URLEncoder.encode(keyValue.getKey().getKey() + "=" + "\"" + searchableValue + "\"",
															"utf-8") + "\">" + tempList.get(j) + "</a>";
										} else {
											value += this.webInterface.getFileDetailsOutputItem().getValue(currentInfoType,
													attributeList.getSectionName(), keyValue, (String) tempList.get(j));
										}
									}

									rows = Template.replaceMarker(rows, "value", value, Boolean.TRUE);
									attributeListCpy = this.removeKeysFromFileAttributeList(attributeListCpy, keyValue.getKey().getKey());
									evenOdd++;
								}
							}
							// replace row marker with real rows
							sectionList = Template.replaceMarker(sectionList, "rows", rows, Boolean.TRUE);
						} catch (final CloneNotSupportedException e) {
							e.printStackTrace();
						} catch (final UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					i++;
				}

				// add last attribute sections
				attributeSectionList += Template.replaceMarker(attributesList, "sections", sectionList, Boolean.TRUE);

				// add all attribute sections to attributes
				attributes = Template.replaceMarker(attributes, "attributesList", attributeSectionList, Boolean.TRUE);

				detailViewTemplate.replaceMarker("mainimage", mainImage, Boolean.FALSE);
				detailViewTemplate.replaceMarker("gallery", gallery, Boolean.FALSE);

				// add all attributes to template
				detailViewTemplate.replaceMarker("attributes", attributes, Boolean.TRUE);
			}
		} else {
			detailViewTemplate.replaceMarker("content", detailViewTemplate.getSubMarkerContent("nofile"), Boolean.TRUE);
			detailViewTemplate.replaceMarker("nofileString", "Keine Datei ausgewählt", Boolean.TRUE);
		}

		return detailViewTemplate;
	}

	@Override
	public String getTemplateName() {
		return "filedetails";
	}

	@Override
	public String getTitle() {
		String title;
		if (this.request.getGetParams().containsKey("fileId") && (this.request.getGetParams().get("fileId") != null)) {
			final Integer fileId = Integer.parseInt(this.request.getGetParams().get("fileId"));
			if ((fileId != null) && (fileId > 0)) {
				final FileItem tempFileItem = this.mainController.getDataHandler().findAllInfoForAllByFileId(fileId);
				if (tempFileItem != null) {
					title = "Detailansicht: " + tempFileItem.getName();
				} else {
					title = "Detailansicht: Keine gültige Datei gewählt";
				}
			} else {
				title = "Detailansicht: Keine gültige Datei gewählt";
			}
		} else {
			title = "Detailansicht: Keine Datei gewählt";
		}
		return title;
	}

	private String getUrlFromMediaItem(final MediaItem mediaItem, final Integer size) {
		if ((mediaItem.getOptions().get(MediaItem.OPTION_WEB_ISDIRECT) != null)
				&& (mediaItem.getOptions().get(MediaItem.OPTION_WEB_ISDIRECT) == Boolean.TRUE)) {
			return mediaItem.getUri().toString();
		} else {
			if (mediaItem.getOptions().get(MediaItem.OPTION_WEB_BASE_PATH) != null) {
				return mediaItem.getOptions().get(MediaItem.OPTION_WEB_BASE_PATH)
						+ ((ArrayList<?>) (Helper.explode((String) mediaItem.getOptions().get(MediaItem.OPTION_SIZES), ","))).get(size)
								.toString() + mediaItem.getUri().toString();
			} else {
				return mediaItem.getUri().toString();
			}
		}
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
