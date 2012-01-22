package com.lars_albrecht.general.utilities;

/**
 * 
 */

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.lars_albrecht.moviedb.annotation.DatabaseOptions;
import com.lars_albrecht.moviedb.annotation.FilterOptions;
import com.lars_albrecht.moviedb.annotation.ParseOptions;
import com.lars_albrecht.moviedb.annotation.ViewInTab;
import com.lars_albrecht.moviedb.annotation.ViewInTable;
import com.lars_albrecht.moviedb.model.FieldList;
import com.lars_albrecht.moviedb.model.FieldModel;

/**
 * @author lalbrecht
 * 
 */
public class Helper {

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
	 * Make a string's first character uppercase.
	 * 
	 * @param text
	 *            String
	 * @return String
	 */
	public static String ucfirst(final String text) {
		if(text == null) {
			return null;
		}
		if(text.length() == 0) {
			return text;
		}

		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	/**
	 * Returns a filename without the file extension.
	 * 
	 * @param filename
	 *            String
	 * @return String
	 */
	public static String getFileNameWithoutExtension(final String filename) {
		return filename.substring(0, filename.length() - (filename.length() - filename.lastIndexOf(".")));
	}

	/**
	 * Returns file extension.
	 * 
	 * @param filename
	 *            String
	 * @return String
	 */
	public static String getFileExtension(final String filename) {
		return filename.substring(filename.lastIndexOf("."));
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
		for(final Field field : fields) {
			tempList.add(field);
		}
		return tempList;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return FieldList
	 */
	public static FieldList getTableFieldModelFromClass(final Class<?> cClass) {
		final FieldList tempList = new FieldList();
		if(cClass != null) {
			final Field[] fields = cClass.getDeclaredFields();
			for(final Field field : fields) {
				final ViewInTable vit = field.getAnnotation(ViewInTable.class);
				if(vit != null) {
					tempList.add(new FieldModel(field, vit.as(), null, null, vit.sort(), null, null));
				}
			}
		}
		return tempList;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return FieldList
	 */
	public static FieldList getFilterFieldModelFromClass(final Class<?> cClass) {
		final FieldList tempList = new FieldList();
		if(cClass != null) {
			final Field[] fields = cClass.getDeclaredFields();
			for(final Field field : fields) {
				final FilterOptions fo = field.getAnnotation(FilterOptions.class);
				if(fo != null) {
					tempList.add(new FieldModel(field, fo.as(), null, null, fo.sort(), null, null));
				}
			}
		}
		return tempList;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return FieldList
	 */
	public static FieldList getParseFieldModelFromClass(final Class<?> cClass) {
		final FieldList tempList = new FieldList();
		if(cClass != null) {
			final Field[] fields = cClass.getDeclaredFields();
			for(final Field field : fields) {
				final ParseOptions po = field.getAnnotation(ParseOptions.class);
				if(po != null) {
					tempList.add(new FieldModel(field, po.as(), po.type(), po.typeConf(), null, null, null));
				}
			}
		}
		return tempList;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return FieldList
	 */
	public static FieldList getDBFieldModelFromClass(final Class<?> cClass) {
		final FieldList tempList = new FieldList();
		if(cClass != null) {
			final Field[] fields = cClass.getDeclaredFields();
			for(final Field field : fields) {
				final DatabaseOptions dbo = field.getAnnotation(DatabaseOptions.class);
				if(dbo != null) {
					final ConcurrentHashMap<String, Object> additionalList = new ConcurrentHashMap<String, Object>();
					additionalList.put("additionalType", dbo.additionalType());
					additionalList.put("defaultValues", dbo.defaultValues());
					additionalList.put("isUnique", dbo.isUnique());
					tempList.add(new FieldModel(field, dbo.as(), dbo.type(), null, null, null, additionalList));
				}
			}
		}
		return tempList;
	}

	/**
	 * 
	 * @param cClass
	 *            Class<?>
	 * @return FieldList
	 */
	public static FieldList getTabFieldModelFromClass(final Class<?> cClass) {
		final FieldList tempList = new FieldList();
		if(cClass != null) {
			final Field[] fields = cClass.getDeclaredFields();
			for(final Field field : fields) {
				final ViewInTab vit = field.getAnnotation(ViewInTab.class);
				if(vit != null) {
					final ConcurrentHashMap<String, Object> additionalList = new ConcurrentHashMap<String, Object>();
					additionalList.put("editable", vit.editable());
					tempList.add(new FieldModel(field, vit.as(), vit.type(), null, vit.sort(), vit.tabname(), additionalList));
				}
			}
		}
		return tempList;
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
		for(int i = 0; i < arrayList.size(); i++) {
			if(i != 0) {
				temp += delim;
			}
			temp += (prefix != null ? prefix : "") + arrayList.get(i) + (suffix != null ? suffix : "");
		}
		return temp;
	}

	/**
	 * 
	 * @param s
	 * @param delim
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> explode(final String s, final String delim) {
		final String[] sParted = s.split(delim);
		if((sParted != null) && (sParted.length > 0)) {
			return new ArrayList<String>(Arrays.asList(sParted));
		}
		return null;
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
		for(int i = 0; i < list.length; i++) {
			if(i != 0) {
				temp += delim;
			}
			temp += (prefix != null ? prefix : "") + list[i] + (suffix != null ? suffix : "");
		}
		return temp;
	}

	/**
	 * 
	 * @param map
	 * @param pos
	 * @return Object
	 */
	public static Object getKeyFromMapPos(final Map<?, ?> map, final Integer pos) {
		int resultVal = 0;
		for(final Entry<?, ?> entry : map.entrySet()) {
			if(pos == resultVal) {
				return entry.getKey();
			}
			resultVal++;
		}
		return null;
	}

	/**
	 * 
	 * @param map
	 * @param o
	 * @return Key
	 */
	public static Object getKeyFromMapObject(final Map<?, ?> map, final Object o) {
		if(map.containsValue(o)) {
			for(final Entry<?, ?> entry : map.entrySet()) {
				if(o == entry.getValue()) {
					return entry.getKey();
				}
			}
		}
		return null;
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

		for(int i = 1; i < numCol + 1; i++) {
			if(meta.getColumnName(i).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param list
	 * @param s
	 * @return inList
	 */
	public static Boolean containsIgnoreCase(final List<String> list, final String s) {
		final Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			if(it.next().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see "http://www.exampledepot.com/egs/java.awt.image/image2buf.html"
	 * 
	 * @param image
	 * @return BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if(image instanceof BufferedImage) {
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
			if(hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			final GraphicsDevice gs = ge.getDefaultScreenDevice();
			final GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch(final HeadlessException e) {
			// The system does not have a screen
		}

		if(bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if(hasAlpha) {
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
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		final Graphics g = bufferedImage.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}

	/**
	 * 
	 * @param image
	 * @return byte[]
	 */
	public static byte[] getBytesFromImage(final Image image) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			if(!ImageIO.write(Helper.toBufferedImage(image), "jpeg", byteArrayOutputStream)) {
				// TODO THROW Exception
			}
		} catch(final IOException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();

	}

	/**
	 * 
	 * @param bi
	 * @return String
	 */
	public static Image bufferedImageToImage(final BufferedImage bi) {
		return Toolkit.getDefaultToolkit().createImage(bi.getSource());
	}

	public static String repeatString(final String s, final String delim, final Integer count) {
		String tempStr = "";
		for(int i = 0; i < count; i++) {
			if(i != 0) {
				tempStr += delim;
			}
			tempStr += s;
		}
		return tempStr;
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
		if(w > h) {
			h = img.getHeight() / w;
			w = maxWidth;
		} else {
			w = img.getWidth() / h;
			h = maxHeight;
		}
		return new Point(w.intValue(), h.intValue());
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
	 * 
	 * @param type
	 * @return String
	 */
	public static String getDatabaseTypeForType(final Class<?> type) {
		if(type == Integer.class) {
			return "INT";
		} else if((type == String.class) || (type == File.class)) {
			return "VARCHAR(512)";
		} else if(type == Image.class) {
			return "BLOB";
		}

		return null;
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
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final ArrayList<Class<?>> types = new ArrayList<Class<?>>();
		final ArrayList<Object> values = new ArrayList<Object>();
		if((params != null) && (params.length > 0)) {
			for(final Object object : params) {
				values.add(object);
				types.add((object != null ? ((object instanceof Class<?>) ? (Class<?>) object : object.getClass()) : null));
			}
		}
		Method m = null;
		if(types.size() > 0) {
			final Class<?>[] list = new Class<?>[types.size()];
			m = obj.getClass().getMethod(methodName, types.toArray(list));
			return m.invoke(obj, values.toArray());
		} else {
			m = obj.getClass().getMethod(methodName);
			return m.invoke(obj);
		}
	}

	/**
	 * Test if the given string is a valid string (not null and not equals "").
	 * 
	 * @param str
	 *            String
	 * @return Boolean
	 */
	public static Boolean isValidString(final String str) {
		if((str == null) || (str.trim().equals(""))) {
			return false;
		}
		return true;
	}

	final static String WRITEIMAGE_JPEG = "jpeg";
	final static String WRITEIMAGE_PNG = "jpeg";
	final static String WRITEIMAGE_GIF = "jpeg";

	/**
	 * Writes a given image to filesystem.
	 * 
	 * @param image
	 *            Image
	 * @param type
	 *            String
	 * @throws IOException
	 */
	public static void writeImage(final Image image, final String type, final File newFile, final Boolean overWrite)
			throws IOException {
		if((overWrite == Boolean.TRUE) || ((overWrite == Boolean.FALSE) && (newFile.exists() == Boolean.FALSE))) {
			ImageIO.write(Helper.toBufferedImage(image), type, newFile);
		}
	}

	/**
	 * Reads an image from filesystem.
	 * 
	 * @param f
	 *            File
	 * @return Image
	 */
	public static Image readImage(final File f) {
		if(f.exists() && f.isFile() && f.canRead()) {
			return Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
		}
		return null;
	}
}