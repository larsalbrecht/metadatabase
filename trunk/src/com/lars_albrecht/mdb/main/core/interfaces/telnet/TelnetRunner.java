/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.helper.InterfaceHelper;
import com.lars_albrecht.mdb.main.core.models.FileAttributeList;
import com.lars_albrecht.mdb.main.core.models.FileItem;
import com.lars_albrecht.mdb.main.core.models.KeyValue;

/**
 * @author lalbrecht
 * 
 */
public class TelnetRunner implements Runnable {

	private MainController	mainController	= null;
	private Socket			client			= null;
	private String			line			= null;

	public TelnetRunner(final MainController mainController, final Socket client) {
		this.mainController = mainController;
		this.client = client;
		System.out.println("created new runner");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		System.out.println("runner started");
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			final PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);

			this.printWelcome(out);

			while ((this.line = in.readLine()) != null && !this.line.equals(".")) {
				if (this.line.equalsIgnoreCase("exit")) {
					out.println("\033[32mSystem will shut down");
					this.mainController.exitProgram();
				} else if (this.line.equalsIgnoreCase("help")) {
					this.printHelp(out);
				} else if (this.line.matches("search\\s.*")) {
					final String searchString = this.line.substring(this.line.indexOf(" ") + 1);
					final ConcurrentHashMap<String, Object> searchResultMap = InterfaceHelper.searchItems(searchString,
							this.mainController.getDataHandler());

					if (searchResultMap.get("resultlist") != null) {
						final int resultCount = ((ArrayList<FileItem>) searchResultMap.get("resultlist")).size();
						out.println("\033[33m" + resultCount + " items found");
						if (resultCount > 0) {
							out.println("\033[37mID\tName");
							for (final FileItem fileItem : (ArrayList<FileItem>) searchResultMap.get("resultlist")) {
								out.println("\033[37m" + fileItem.getId() + "\t" + fileItem.getName());
							}
						}

					} else {
						out.println("\033[31mAn Error occured.");
					}

				} else if (this.line.matches("view\\s.*")) {
					final String viewIdStr = this.line.substring(this.line.indexOf(" ") + 1);
					if (this.line.matches("view\\s\\d+") && (viewIdStr != null && viewIdStr.matches("\\d"))) {
						final int viewId = Integer.parseInt(viewIdStr);
						final FileItem fileItem = this.mainController.getDataHandler().findAllInfoForAllByFileId(viewId);
						if (fileItem != null) {
							out.print("\033[37m"); // white
							out.println(fileItem.getName() + " [" + fileItem.getFiletype() + "]");
							out.println(fileItem.getFullpath());
							out.println(Helper.getHumanreadableFileSize(fileItem.getSize()));
							out.println("");
							for (final FileAttributeList attributeList : fileItem.getAttributes()) {
								out.println("");
								out.println(attributeList.getSectionName());
								for (final KeyValue<String, Object> keyValue : attributeList.getKeyValues()) {
									out.println(keyValue.getKey().getKey() + ": " + keyValue.getValue().getValue());
								}
							}

							out.print("\033[36m"); // cyan
						} else {
							out.println("\033[33mID not found in database");
						}
					} else {
						out.println("\033[33mNo ID found");
					}
				} else {
					for (final byte beight : this.line.getBytes(Charset.forName("UTF-8"))) {
						System.out.println(beight);
					}
					out.println("\033[33mCommand:\033[36m " + this.line);
				}
				out.print("\033[36m"); // cyan
				out.flush();

			}

			this.client.close();
		} catch (final IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}

	private void printWelcome(final PrintWriter out) {
		out.print("\033[33m"); // yellow
		out.println("***********************************************");
		out.println("* Welcome to the MDB Telnet Interface - MDBTI *");
		out.println("***********************************************");
		out.flush();
		this.printHelp(out);
	}

	private void printHelp(final PrintWriter out) {
		out.print("\033[37m"); // white
		out.println("Following commands are available:");
		out.println("- search <search-word>");
		out.println("- view <item-id> (work in progress)");
		out.println("");
		out.println("- help");
		out.println("- exit");
		out.println("");
		out.println("What do you want to do?");
		out.print("\033[36m"); // cyan
		out.flush();
	}
}
