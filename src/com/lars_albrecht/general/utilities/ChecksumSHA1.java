/**
 * 
 */
package com.lars_albrecht.general.utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @see "http://www.rgagnon.com/javadetails/java-0416.html"
 * 
 */
public class ChecksumSHA1 {

	public static byte[] createChecksum(final String filename) throws Exception {
		final InputStream fis = new FileInputStream(filename);

		final byte[] buffer = new byte[1024];
		final MessageDigest complete = MessageDigest.getInstance("SHA1");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public static byte[] createChecksumString(final String string) throws Exception {
		final MessageDigest complete = MessageDigest.getInstance("SHA1");
		complete.update(string.getBytes());
		return complete.digest();
	}

	public static String getSHA1Checksum(final String filename) throws Exception {
		final byte[] b = ChecksumSHA1.createChecksum(filename);
		String result = "";
		for (final byte element : b) {
			result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static String getSHA1ChecksumString(final String string) throws Exception {
		final byte[] b = ChecksumSHA1.createChecksumString(string);
		String result = "";
		for (final byte element : b) {
			result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
