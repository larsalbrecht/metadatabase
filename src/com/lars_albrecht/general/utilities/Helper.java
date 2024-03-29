/**
 * 
 */
package com.lars_albrecht.general.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author lalbrecht
 * 
 */
public class Helper {

	final static String	WRITEIMAGE_JPEG	= "jpeg";

	final static String	WRITEIMAGE_PNG	= "jpeg";

	final static String	WRITEIMAGE_GIF	= "jpeg";

	static final String	HEXES			= "0123456789ABCDEF";

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param bits
	 * @return
	 */
	public static byte[] bitsTo8Bytes(final boolean[] bits) {
		final byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				b[i] += (((bits[(8 * i) + j]) ? 1 : 0) << (7 - j));
			}
		}

		return b;
	}

	/**
	 * 
	 * @param bi
	 * @return String
	 */
	public static Image bufferedImageToImage(final BufferedImage bi) {
		return Toolkit.getDefaultToolkit().createImage(bi.getSource());
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param b
	 * @return
	 */
	public static String bytesToHex(final byte[] b) {
		String s = "";
		for (int i = 0; i < b.length; i++) {
			if ((i > 0) && ((i % 4) == 0)) {
				s += " ";
			}
			s += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return s;
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param b
	 * @return
	 */
	public static int bytesToInt(final byte[] b) {
		return ((b[0] << 24) & 0xff000000) | ((b[1] << 16) & 0xff0000) | ((b[2] << 8) & 0xff00) | (b[3] & 0xff);
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param b
	 * @return
	 */
	public static boolean[] byteToBits(final byte b) {
		final boolean[] bits = new boolean[8];
		for (int i = 0; i < 8; i++) {
			bits[7 - i] = ((b & (1 << i)) != 0);
		}
		return bits;
	}

	/**
	 * 
	 * @param methodName
	 *            String
	 * @param obj
	 *            Object
	 * @param params
	 *            Object...
	 * @return Object
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object call(final String methodName, final Object obj, final Object... params) throws NoSuchMethodException,
			SecurityException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		final ArrayList<Class<?>> types = new ArrayList<Class<?>>();
		final ArrayList<Object> values = new ArrayList<Object>();
		if ((params != null) && (params.length > 0)) {
			for (final Object object : params) {
				values.add(object);
				types.add((object != null ? ((object instanceof Class<?>) ? (Class<?>) object : object.getClass()) : null));
			}
		}
		Method m = null;
		if (types.size() > 0) {
			final Class<?>[] list = new Class<?>[types.size()];
			m = obj.getClass().getMethod(methodName, types.toArray(list));
			return m.invoke(obj, values.toArray());
		} else {
			m = obj.getClass().getMethod(methodName);
			return m.invoke(obj);
		}
	}

	/**
	 * 
	 * @param list
	 * @param s
	 * @return inList
	 */
	public static Boolean containsIgnoreCase(final List<String> list, final String s) {
		final Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			if (it.next().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param s
	 * @param delim
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> explode(final String s, final String delim) {
		final String[] sParted = s.split(delim);
		if ((sParted != null) && (sParted.length > 0)) {
			return new ArrayList<String>(Arrays.asList(sParted));
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<? extends K, V> explode(final String s, final String entryDelimiter, final String keyValueDelimiter) {
		final String[] sParted = s.split(entryDelimiter);
		Map<K, V> resultMap = null;
		if ((sParted != null) && (sParted.length > 0)) {
			resultMap = new ConcurrentHashMap<K, V>();
			K key = null;
			V value = null;
			Object[] eParted = null;
			for (final String string : sParted) {
				eParted = string.split("\\" + keyValueDelimiter);
				if ((eParted != null) && (eParted.length == 2)) {
					key = ((K) eParted[0]);
					key.getClass();
					value = ((V) eParted[1]);
					resultMap.put(key, value);
				}
			}
			return resultMap;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <V> Map<Integer, V> explodeIntKeys(final String s, final String entryDelimiter, final String keyValueDelimiter) {
		final String[] sParted = s.split(entryDelimiter);
		Map<Integer, V> resultMap = null;
		if ((sParted != null) && (sParted.length > 0)) {
			resultMap = new ConcurrentHashMap<Integer, V>();
			Integer key = null;
			V value = null;
			Object[] eParted = null;
			for (final String string : sParted) {
				eParted = string.split("\\" + keyValueDelimiter);
				if ((eParted != null) && (eParted.length == 2)) {
					if (eParted[0] instanceof String) {
						key = Integer.parseInt((String) eParted[0]);
					} else {
						key = ((Integer) eParted[0]);
					}
					value = ((V) eParted[1]);
					resultMap.put(key, value);
				}
			}
			return resultMap;
		}
		return null;
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param x
	 * @param i
	 * @return
	 */
	public static int getBit(final int x, final int i) {
		return (x >>> i) & 0x01;
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param x
	 * @param i
	 * @return
	 */
	public static int getBit(final int[] x, final int i) {
		return (x[i / 32] >>> (i % 32)) & 0x01;
	}

	/**
	 * 
	 * @param image
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getBytesFromImage(final Image image) throws Exception {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			if (!ImageIO.write(Helper.toBufferedImage(image), "jpeg", byteArrayOutputStream)) {
				throw new Exception("cant write jpeg");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();

	}

	public static Long getCurrentTimestamp() {
		return (System.currentTimeMillis() / 1000L);
	}

	/**
	 * 
	 * @param type
	 * @return String
	 */
	public static String getDatabaseTypeForType(final Class<?> type) {
		if (type == Integer.class) {
			return "INT";
		} else if ((type == String.class) || (type == File.class)) {
			return "VARCHAR(512)";
		} else if (type == Image.class) {
			return "BLOB";
		} else if (type == Boolean.class) {
			return "BOOLEAN";
		} else if (type == Float.class) {
			return "REAL";
		} else if (type == Long.class) {
			return "BIGINT";
		} else if (type == Byte.class) {
			return "TINYINT";
		} else if (type == Short.class) {
			return "SMALLINT";
		} else if (type == BigDecimal.class) {
			return "DECIMAL";
		} else if (type == Double.class) {
			return "DOUBLE";
		} else if ((type == Time.class)) {
			return "TIME";
		} else if ((type == Date.class)) {
			return "DATE";
		} else if (type == Timestamp.class) {
			return "TIMESTAMP";
		} else if (type == byte[].class) {
			return "BINARY";
		} else if (type == Object[].class) {
			return "ARRAY";
		}
		return null;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return ArrayList<String>
	 */
	public static ArrayList<Field> getFieldsFromClass(final Class<?> cClass) {
		final Field[] fields = cClass.getDeclaredFields();
		final ArrayList<Field> tempList = new ArrayList<Field>();
		for (final Field field : fields) {
			tempList.add(field);
		}
		return tempList;
	}

	public static String getFileContents(final File file) throws IOException {
		String content = "";
		if (file.exists() && file.isFile() && file.canRead()) {
			String line = "";
			final BufferedReader buReader = new BufferedReader(new FileReader(file));
			line = buReader.readLine();

			while (line != null) {
				content += line;
				line = buReader.readLine();
			}
			buReader.close();
		}
		return content;
	}

	/**
	 * Returns file extension.
	 * 
	 * @param filename
	 *            String
	 * @return String
	 */
	public static String getFileExtension(final String filename) {
		if (filename.contains(".")) {
			return filename.substring(filename.lastIndexOf("."));
		} else {
			return "";
		}
	}

	/**
	 * Returns a filename without the file extension.
	 * 
	 * @param filename
	 *            String
	 * @return String
	 */
	public static String getFileNameWithoutExtension(final String filename) {
		if (filename != null && filename.contains(".")) {
			return filename.substring(0, filename.length() - (filename.length() - filename.lastIndexOf(".")));
		} else if (!filename.contains(".")) {
			return filename;
		} else {
			return null;
		}
	}

	/**
	 * Format the timestamp with pattern. The timestamp must be a real
	 * unixtimestamp like "1357002000" for 2013-01-01 00:00:00.
	 * 
	 * @param timestamp
	 * @param pattern
	 * @return String
	 */
	public static String getFormattedTimestamp(final Long timestamp, String pattern) {
		if (pattern == null) {
			pattern = "dd.MM.yyyy HH:mm:ss";
		}
		final DateFormat dfmt = new SimpleDateFormat(pattern);
		return dfmt.format(new Date(timestamp * 1000));
	}

	public static String getHex(final byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(Helper.HEXES.charAt((b & 0xF0) >> 4)).append(Helper.HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * Returns a human readable string with the filesize.
	 * 
	 * @see "http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc"
	 * 
	 * @param size
	 *            long
	 * @return String
	 */
	public static String getHumanreadableFileSize(final long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[] {
				"B", "KB", "MB", "GB", "TB"
		};
		final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String getInputStreamContents(final InputStream inputStream, final Charset charset) throws IOException {
		String content = "";
		if (inputStream != null) {
			String line = "";
			final BufferedReader buReader = new BufferedReader(charset == null ? new InputStreamReader(inputStream)
					: new InputStreamReader(inputStream, charset));
			line = buReader.readLine();

			while (line != null) {
				content += line;
				line = buReader.readLine();
			}
			buReader.close();
		}
		return content;
	}

	/**
	 * 
	 * @param map
	 * @param o
	 * @return Key
	 */
	public static Object getKeyFromMapObject(final Map<?, ?> map, final Object o) {
		if (map.containsValue(o)) {
			for (final Entry<?, ?> entry : map.entrySet()) {
				if (o == entry.getValue()) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param map
	 * @param pos
	 * @return Object
	 */
	public static Object getKeyFromMapPos(final Map<?, ?> map, final Integer pos) {
		int resultVal = 0;
		for (final Entry<?, ?> entry : map.entrySet()) {
			if (pos == resultVal) {
				return entry.getKey();
			}
			resultVal++;
		}
		return null;
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param x
	 * @param i
	 * @return
	 */
	public static int getNibble(final int x, final int i) {
		return (x >>> (4 * i)) & 0x0F;
	}

	/**
	 * 
	 * @param img
	 * @param maxWidth
	 * @param maxHeight
	 * @return Point (x=width/y=height)
	 */
	public static Point getProportionalWidthHeightImage(final BufferedImage img, final Double maxWidth, final Double maxHeight) {

		Double w = img.getWidth() / maxWidth;
		Double h = img.getHeight() / maxHeight;
		if (w > h) {
			h = img.getHeight() / w;
			w = maxWidth;
		} else {
			w = img.getWidth() / h;
			h = maxHeight;
		}
		return new Point(w.intValue(), h.intValue());
	}

	/**
	 * Generate a list of ? and a list of values for a prepared statement.
	 * 
	 * @param list
	 * @return Map.Entry<String, ConcurrentHashMap<Integer, Object>>
	 */
	public static Map.Entry<String, ConcurrentHashMap<Integer, Object>> getQuestionMarksValuesForSQLFromList(final List<?> list) {
		Map.Entry<String, ConcurrentHashMap<Integer, Object>> resultEntry = null;
		if ((list != null) && (list.size() > 0)) {
			String resultStr = null;
			final ConcurrentHashMap<Integer, Object> valueList = new ConcurrentHashMap<Integer, Object>();
			resultStr = "";

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null) {
					if (i > 0) {
						resultStr += ",";
					}
					resultStr += "?";
					valueList.put(i + 1, list.get(i));
				}
			}
			resultEntry = new AbstractMap.SimpleEntry<String, ConcurrentHashMap<Integer, Object>>(resultStr, valueList);
		}
		return resultEntry;
	}

	/**
	 * Returns the center-position of the screen.
	 * 
	 * @param width
	 *            Integer
	 * @param height
	 *            Integer
	 * @return Point center-point for given width / height
	 */
	public static Point getScreenCenterPoint(final Integer width, final Integer height) {
		final Point resultPoint = new Point();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		resultPoint.x = (screenSize.width - width) / 2;
		resultPoint.y = (screenSize.height - height) / 2;

		return resultPoint;
	}

	/**
	 * Returns a Color-Object from a hex-color (#AAABBB or AAABBB).
	 * 
	 * @param colorStr
	 * @return
	 */
	public static Color hex2Rgb(final String colorStr) {
		if ((colorStr != null) && colorStr.matches("#?[A-Fa-f0-9]{6}")) {
			return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(
					colorStr.substring(5, 7), 16));
		} else {
			return null;
		}
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param s
	 * @return
	 */
	public static byte[] hexToBytes(final String s) {
		final byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < s.length(); i += 2) {
			b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return b;
	}

	/**
	 * 
	 * @param arrayList
	 * @param delim
	 * @param prefix
	 * @param suffix
	 * @return String
	 */
	public static String implode(final ArrayList<?> arrayList, final String delim, final String prefix, final String suffix) {
		String temp = "";
		for (int i = 0; i < arrayList.size(); i++) {
			if (i != 0) {
				temp += delim;
			}
			temp += (prefix != null ? prefix : "") + arrayList.get(i) + (suffix != null ? suffix : "");
		}
		return temp;
	}

	/**
	 * 
	 * @param collection
	 * @param delim
	 * @param prefix
	 * @param suffix
	 * @return String
	 */
	public static String implode(final Collection<?> collection, final String delim, final String prefix, final String suffix) {
		String temp = "";
		int i = 0;
		for (final Object object : collection) {
			if (i != 0) {
				temp += delim;
			}
			temp += (prefix != null ? prefix : "") + object + (suffix != null ? suffix : "");

			i++;
		}
		return temp;
	}

	/**
	 * 
	 * @param map
	 * @param entryDelimiter
	 * @param keyPrefix
	 * @param keySuffix
	 * @param valuePrefix
	 * @param valueSuffix
	 * @param entryPrefix
	 * @param entrySuffix
	 * @param valueFirst
	 * @return String
	 */
	public static String implode(final Map<?, ?> map,
			final String entryDelimiter,
			final String keyValueDelimiter,
			final String keyPrefix,
			final String keySuffix,
			final String valuePrefix,
			final String valueSuffix,
			final String entryPrefix,
			final String entrySuffix,
			final boolean valueFirst) {
		String temp = "";
		final Iterator<?> iterator = map.entrySet().iterator();
		Map.Entry<?, ?> entry = null;
		for (int i = 0; i < map.entrySet().size(); i++) {
			entry = (Entry<?, ?>) iterator.next();
			if (i != 0) {
				temp += entryDelimiter;
			}
			if (valueFirst) {
				temp += (entryPrefix != null ? entryPrefix : "") + (valuePrefix != null ? valuePrefix : "") + entry.getValue()
						+ (valueSuffix != null ? valueSuffix : "") + (keyValueDelimiter != null ? keyValueDelimiter : "")
						+ (keyPrefix != null ? keyPrefix : "") + entry.getKey() + (keySuffix != null ? keySuffix : "")
						+ (entrySuffix != null ? entrySuffix : "");
				;

			} else {
				temp += (entryPrefix != null ? entryPrefix : "") + (keyPrefix != null ? keyPrefix : "") + entry.getKey()
						+ (keySuffix != null ? keySuffix : "") + (keyValueDelimiter != null ? keyValueDelimiter : "")
						+ (valuePrefix != null ? valuePrefix : "") + entry.getValue() + (valueSuffix != null ? valueSuffix : "")
						+ (entrySuffix != null ? entrySuffix : "");
			}
		}
		return temp;
	}

	/**
	 * 
	 * @param list
	 * @param delim
	 * @param prefix
	 * @param suffix
	 * @return String
	 */
	public static String implode(final String[] list, final String delim, final String prefix, final String suffix) {
		String temp = "";
		if ((list != null) && (list.length > 0)) {
			for (int i = 0; i < list.length; i++) {
				if ((delim != null) && (i != 0)) {
					temp += delim;
				}
				temp += (prefix != null ? prefix : "") + list[i] + (suffix != null ? suffix : "");
			}
		}
		return temp;
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes(final int i) {
		final byte[] b = new byte[4];
		for (int c = 0; c < 4; c++) {
			b[c] = (byte) ((i >>> (56 - (8 * c))) & 0xff);
		}
		return b;
	}

	/**
	 * 
	 * @param rs
	 * @param name
	 * @return Boolean
	 * @throws SQLException
	 */
	public static Boolean isFieldInResult(final ResultSet rs, final String name) throws SQLException {
		final ResultSetMetaData meta = rs.getMetaData();
		final int numCol = meta.getColumnCount();

		for (int i = 1; i < (numCol + 1); i++) {
			if (meta.getColumnName(i).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if the given string is a valid string (not null and not equals "").
	 * 
	 * @param str
	 *            String
	 * @return Boolean
	 */
	public static Boolean isValidString(final String str) {
		if ((str == null) || (str.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/**
	 * Make a string's first character lowercase.
	 * 
	 * @param text
	 *            String
	 * @return String
	 */
	public static String lcfirst(final String text) {
		if (text == null) {
			return null;
		}
		if (text.length() == 0) {
			return text;
		}
		return Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param l
	 * @return
	 */
	public static byte[] longToBytes(final long l) {
		final byte[] b = new byte[8];
		for (int c = 0; c < 8; c++) {
			b[c] = (byte) ((l >>> (56 - (8 * c))) & 0xffL);
		}
		return b;
	}

	/**
	 * Reads an image from filesystem.
	 * 
	 * @param f
	 *            File
	 * @return Image
	 */
	public static Image readImage(final File f) {
		if (f.exists() && f.isFile() && f.canRead()) {
			return Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
		}
		return null;
	}

	/**
	 * Removes duplicated entries from an arrayList.
	 * 
	 * @param <T>
	 * 
	 * @param arrayList
	 */
	public static final <T> ArrayList<T> removeDuplicatedEntries(final ArrayList<T> arrayList) {
		return new ArrayList<T>(new HashSet<T>(arrayList));
	}

	public static String repeatString(final String s, final String delim, final Integer count) {
		String tempStr = "";
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				tempStr += delim;
			}
			tempStr += s;
		}
		return tempStr;
	}

	/**
	 * @see "http://stackoverflow.com/questions/2282728/java-replacelast"
	 * 
	 * @param string
	 * @param toReplace
	 * @param replacement
	 * @return String
	 */
	public static String replaceLast(final String string, final String toReplace, final String replacement) {
		final int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}

	/**
	 * 
	 * @param sourceImage
	 * @param width
	 * @param height
	 * @return BufferedImage
	 */
	public static BufferedImage scaleImage(final Image sourceImage, final int width, final int height) {
		final ImageFilter filter = new ReplicateScaleFilter(width, height);
		final ImageProducer producer = new FilteredImageSource(sourceImage.getSource(), filter);
		final Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

		return Helper.toBufferedImage(resizedImage);
	}

	/**
	 * @see "https://code.google.com/p/a9cipher/source/browse/src/cosc385final/A9Utility.java"
	 * @param x
	 * @param i
	 * @param v
	 */
	public static void setBit(final int[] x, final int i, final int v) {
		if ((v & 0x01) == 1) {
			x[i / 32] |= 1 << (i % 32); // set it
		} else {
			x[i / 32] &= ~(1 << (i % 32)); // clear it
		}
	}

	/**
	 * @see "http://www.exampledepot.com/egs/java.awt.image/image2buf.html"
	 * 
	 * @param image
	 * @return BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		final boolean hasAlpha = false;

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			final GraphicsDevice gs = ge.getDefaultScreenDevice();
			final GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (final HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		final Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * 
	 * @param image
	 * @return BufferedImage
	 * 
	 * @see "https://forums.oracle.com/forums/thread.jspa?threadID=1287249"
	 * 
	 */
	public static BufferedImage toBufferedImage2(Image image) {
		image = new ImageIcon(image).getImage();
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		final Graphics g = bufferedImage.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}

	/**
	 * Make a string's first character uppercase.
	 * 
	 * @param text
	 *            String
	 * @return String
	 */
	public static String ucfirst(final String text) {
		if (text == null) {
			return null;
		}
		if (text.length() == 0) {
			return text;
		}
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	public static <C> ArrayList<C> uniqueList(final ArrayList<C> list) {
		final ArrayList<C> resultList = new ArrayList<C>();
		final Iterator<C> iterator = list.iterator();

		while (iterator.hasNext()) {
			final C o = iterator.next();
			if (!resultList.contains(o)) {
				resultList.add(o);
			}
		}

		return resultList;
	}

	/**
	 * Writes a given image to filesystem.
	 * 
	 * @param image
	 *            BufferedImage
	 * @param type
	 *            String
	 * @throws IOException
	 */
	public static void
			writeImage(final BufferedImage image, final String type, final File newFile, final Boolean overWrite) throws IOException {
		if (image == null) {
			throw new NullPointerException("image is null!");
		} else if ((overWrite == Boolean.TRUE) || ((overWrite == Boolean.FALSE) && (newFile.exists() == Boolean.FALSE))) {
			ImageIO.write(image, type, newFile);
		}
	}

	/**
	 * Writes a given image to filesystem.
	 * 
	 * @param image
	 *            Image
	 * @param type
	 *            String
	 * @throws IOException
	 */
	public static void writeImage(final Image image, final String type, final File newFile, final Boolean overWrite) throws IOException {
		if (image == null) {
			throw new NullPointerException("image is null!");
		} else if ((overWrite == Boolean.TRUE) || ((overWrite == Boolean.FALSE) && (newFile.exists() == Boolean.FALSE))) {
			ImageIO.write(Helper.toBufferedImage(image), type, newFile);
		}
	}

}
