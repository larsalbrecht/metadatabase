/**
 * 
 */
package com.lars_albrecht.general.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.mdb.main.core.utilities.Paths;

/**
 * @author lalbrecht
 * 
 */
public class Template {

	public static Boolean containsMarker(final String content, final String marker) {
		Pattern pattern = null;
		if (marker == null) {
			pattern = Pattern.compile("\\{(.*)\\}");
		} else {
			pattern = Pattern.compile("\\{" + marker + "\\}");
		}
		final Matcher matcher = pattern.matcher(content);
		return matcher.find();
	}

	public static String getClearedContent(String content) {
		content = content.replaceAll("(\\{([a-zA-Z0-9]+?-start)\\})(.*?)(\\{([a-zA-Z0-9]+?-end)\\})", "");
		content = content.replaceAll("(\\{(.*?)\\})", "");

		return content;
	}

	/**
	 * Returns the first markername.
	 * 
	 * @param content
	 * @return String
	 */
	public static String getNextMarkername(final String content) {
		final Pattern pattern = Pattern.compile("\\{(.*)\\}");
		final Matcher matcher = pattern.matcher(content);
		String name = null;
		if (matcher.find()) {
			name = matcher.group(1);
		}
		return name;
	}

	public static String getSubMarkerContent(final String content, final String markername) {
		if (content == null) {
			return null;
		}
		String markerContent = null;

		final String markerStart = "{" + markername + "-start}";
		final String markerEnd = "{" + markername + "-end}";
		final Pattern pattern = Pattern.compile(Pattern.quote(markerStart) + "(.*)?" + Pattern.quote(markerEnd));
		final Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			markerContent = matcher.group(1);
		}

		return markerContent;
	}

	/**
	 * Replace a marker "markername" in "content" with "replacement".
	 * 
	 * @param content
	 * @param markername
	 * @param replacement
	 * @param replaceAll
	 * @return String
	 */
	public static String replaceMarker(String content, final String markername, final String replacement, final boolean replaceAll) {
		if ((content != null) && (markername != null) && (replacement != null)) {
			if (replaceAll) {
				content = content.replaceAll("(\\{" + markername + "\\})+", replacement);
			} else {
				content = content.replaceFirst("(\\{" + markername + "\\})+", replacement);
			}
		}
		return content;
	}

	public static String replaceMarkers(final String content, final ConcurrentHashMap<String, String> markerReplacements) {
		String resultContent = content;
		if ((resultContent != null) && (markerReplacements != null) && (markerReplacements.size() > 0)) {
			for (final Entry<String, String> entry : markerReplacements.entrySet()) {

				resultContent = Template.replaceMarker(resultContent, entry.getKey(), entry.getValue(), false);
			}
		}
		return resultContent;
	}

	private String	content	= null;

	public Template() {

	}

	public Template(final String templateName) {
		this.loadTemplateFile(templateName);
	}

	public Template(final String templateName, final ConcurrentHashMap<String, String> markerReplacements) {
		this.loadTemplateFile(templateName);
		if ((this.content != null) && (markerReplacements != null) && (markerReplacements.size() > 0)) {
			for (final Entry<String, String> entry : markerReplacements.entrySet()) {
				this.replaceMarker(entry.getKey(), entry.getValue(), Boolean.FALSE);
			}
		}
	}

	public Boolean containsMarker(final String marker) {
		return Template.containsMarker(this.content, marker);
	}

	public String getClearedContent() {
		if (this.content != null) {
			return Template.getClearedContent(this.content);
		} else {
			return null;
		}
	}

	/**
	 * @return the content
	 */
	public final String getContent() {
		return this.content;
	}

	/**
	 * Returns the content for a file. The file must be in /folder/
	 * 
	 * TODO replace this with helper if helper can read from input stream and
	 * filesystem in one function.
	 * 
	 * @param filename
	 * @param folder
	 * @return String
	 */
	@SuppressWarnings("unused")
	private String getFileContent(final File file) {
		if (file != null) {
			Debug.log(Debug.LEVEL_INFO, "Try to load file: " + file);
			// TODO FIX this inputStream
			// final InputStream inputStream =
			// Main.class.getClassLoader().getResourceAsStream(folder + "/" +
			// url);
			final InputStream inputStream = null;
			try {
				String content = "";
				if (inputStream != null) {
					content = Helper.getInputStreamContents(inputStream, Charset.forName("UTF-8"));
					return content;
				} else if ((file != null) && file.exists() && file.isFile() && file.canRead()) {
					content = Helper.getFileContents(file);
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

	public String getNextMarkername() {
		return Template.getNextMarkername(this.content);
	}

	public String getSubMarkerContent(final String markername) {
		return Template.getSubMarkerContent(this.content, markername);
	}

	public final void loadTemplateFile(final String templateName) {
		if ((templateName != null) && !templateName.equalsIgnoreCase("")) {
			File file = null;
			if (new File(Paths.WEB_PAGES + File.separator + templateName + ".page").exists()) {
				file = new File(Paths.WEB_PAGES + File.separator + templateName + ".page");
			} else if (new File(Paths.WEB_PAGES_PARTIALS + File.separator + templateName + ".partial").exists()) {
				file = new File(Paths.WEB_PAGES_PARTIALS + File.separator + templateName + ".partial");
			}
			if ((file != null) && file.exists() && file.canRead()) {
				this.content = this.getFileContent(file);
				if (this.content == null) {
					this.content = this.getFileContent(file);
				}
				if (this.content != null) {
					Debug.log(Debug.LEVEL_INFO, "loaded template file: " + file + "(" + templateName + ")");
				} else {
					Debug.log(Debug.LEVEL_ERROR, "Template could not be loaded: " + file + "(" + templateName + ")");
				}
			} else {
				Debug.log(Debug.LEVEL_ERROR, "Templatefile could not be found: " + file + "(" + templateName + ")");
			}
		} else {
			this.content = "no valid template file specified";
		}
	}

	public String replaceMarker(final String markername, final String replacement, final boolean replaceAll) {
		this.content = Template.replaceMarker(this.content, markername, replacement, replaceAll);
		return this.content;
	}

	public String replaceMarkers(final ConcurrentHashMap<String, String> markerReplacements) {
		if ((markerReplacements != null) && (markerReplacements.size() > 0)) {
			for (final Entry<String, String> entry : markerReplacements.entrySet()) {
				this.replaceMarker(entry.getKey(), entry.getValue(), Boolean.FALSE);
			}
		}
		return this.content;
	}

}
