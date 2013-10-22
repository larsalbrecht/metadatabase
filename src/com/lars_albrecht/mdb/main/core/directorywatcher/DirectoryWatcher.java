/**
 * 
 */
package com.lars_albrecht.mdb.main.core.directorywatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.lars_albrecht.mdb.main.core.controller.MainController;

/**
 * @author lalbrecht
 * 
 *         TODO improve this class/function
 * 
 */
public class DirectoryWatcher implements Runnable {

	private WatchService	watcher					= null;

	private ArrayList<File>	monitoredDirectories	= null;

	private MainController	mainController			= null;

	public DirectoryWatcher(final MainController mainController) {
		this.mainController = mainController;
		this.monitoredDirectories = new ArrayList<File>();
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void addDirecotry(final File path) {
		try {
			if (!this.monitoredDirectories.contains(path)) {
				Paths.get(path.getAbsolutePath()).register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
				System.out.println("Path added: " + path.getAbsolutePath());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void doEntryCreate(final File eventFile, final ArrayList<File> tempList) throws Exception {
		System.out.println("CREATE");
		if (eventFile.isDirectory()) {
			System.out.println("FIND");
			this.addDirecotry(eventFile);
			this.mainController.getfController().findFiles(tempList);
		} else if (eventFile.isFile()) {
			System.out.println("FIND");
			this.mainController.getfController().findFiles(tempList);
		}
	}

	private void doEntryDelete(final File eventFile) {
		System.out.println("DELETE");
		if (this.monitoredDirectories.contains(eventFile)) {
			System.out.println("DELETE FROM LIST");
			this.monitoredDirectories.remove(eventFile);
		}
	}

	private void doEntryModify(final File eventFile, final ArrayList<File> tempList) throws Exception {
		System.out.println("MODIFY");
		if (eventFile.isDirectory()) {
			System.out.println("FIND");
			this.mainController.getfController().findFiles(tempList);
		}
	}

	@Override
	public void run() {
		System.out.println("RUN");
		while (true) {
			WatchKey key;
			try {
				key = this.watcher.poll(1, TimeUnit.SECONDS);

				if (key != null) {
					for (final WatchEvent<?> event : key.pollEvents()) {
						System.out.println("Kind: " + event.kind() + ", Path: " + event.context());

						final Path fullPath = ((Path) key.watchable()).resolve((Path) event.context());
						final File eventFile = new File(fullPath.toAbsolutePath().toString());

						final ArrayList<File> tempList = new ArrayList<File>();
						tempList.add(eventFile);

						if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							this.doEntryCreate(eventFile, tempList);
						} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							this.doEntryModify(eventFile, tempList);
						} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
							this.doEntryDelete(eventFile);
						}
					}
					key.reset();
				}
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}
	}
}
