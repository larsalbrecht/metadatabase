/**
 * 
 */
package com.lars_albrecht.mdb.main;

import com.lars_albrecht.general.utilities.Debug;
import com.lars_albrecht.general.utilities.RessourceBundleEx;
import com.lars_albrecht.mdb.main.core.collector.abstracts.ACollector;
import com.lars_albrecht.mdb.main.core.controller.CollectorController;
import com.lars_albrecht.mdb.main.core.controller.ExportController;
import com.lars_albrecht.mdb.main.core.controller.FinderController;
import com.lars_albrecht.mdb.main.core.controller.InterfaceController;
import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.controller.TypeController;
import com.lars_albrecht.mdb.main.core.exporter.PDFExport;
import com.lars_albrecht.mdb.main.core.interfaces.SystemTrayInterface;
import com.lars_albrecht.mdb.main.core.interfaces.TelnetInterface;
import com.lars_albrecht.mdb.main.core.interfaces.WebInterface;
import com.lars_albrecht.mdb.main.database.DB;

/**
 * @author lalbrecht
 * 
 */
public class MDB {

	private MainController		mainController	= null;
	private MDBConfig			mdbConfig		= null;

	private FinderController	fController		= null;
	private TypeController		tController		= null;
	private CollectorController	cController		= null;
	private InterfaceController	iController		= null;
	private ExportController	eController		= null;

	public MDB(final MDBConfig mdbConfig) throws Exception {
		if (mdbConfig != null) {
			this.mdbConfig = mdbConfig;

			this.init();
			// new PDFExport().exportItem(new File("D:\\lalbrecht\\test.pdf"),
			// this.mainController.getDataHandler().findAllInfoForAllByFileId(7),
			// null);

		} else {
			throw new Exception("No config for mdb or null");
		}
	}

	private void init() {
		Debug.loglevel = this.mdbConfig.getLoglevel();
		RessourceBundleEx.setPrefix("mdb");

		this.initDB();

		this.mainController = new MainController(this.mdbConfig);

		// Initialize controllers
		this.initTypeController();
		this.initFinderController();
		this.initExporterController();
		this.initInterfaceController();
		this.initCollectorController();

		// Set initialized Controllers
		this.mainController.settController(this.tController);
		this.mainController.setfController(this.fController);
		this.mainController.seteController(this.eController);
		this.mainController.setiController(this.iController);
		this.mainController.setcController(this.cController);
	}

	private void initCollectorController() {
		for (final ACollector collector : this.mdbConfig.getListOfCollectors()) {
			this.mainController.getDataHandler().addControllerTypes(collector.getInfoType(), collector.getTypes());
		}
		this.cController = new CollectorController(this.mainController);
		this.cController.setCollectors(this.mdbConfig.getListOfCollectors());
	}

	private void initDB() {
		try {
			new DB().init();
		} catch (final Exception e) {
			e.printStackTrace();
			System.err.println("SYSTEM SHUT DOWN");
			System.exit(-1);
		}
	}

	private void initExporterController() {
		this.eController = new ExportController(this.mainController);

		// TODO TMP only, make it configurable (enable/disable/plugins)
		this.mdbConfig.getListOfExporter().add(new PDFExport());

		this.eController.setExporters(this.mdbConfig.getListOfExporter());
	}

	private void initFinderController() {
		this.fController = new FinderController(this.mainController);
		this.fController.setFileFilter(this.mdbConfig.getFinderFileFilter());
	}

	private void initInterfaceController() {
		try {

			this.iController = new InterfaceController(this.mainController);

			// TODO TMP only, make it configurable (enable/disable/plugins)
			if (this.mdbConfig.getSystemTrayInterfaceIconImageFile() != null) {
				final SystemTrayInterface systemTrayInterface = new SystemTrayInterface(this.mainController);
				systemTrayInterface.setTrayIconImagePath(this.mdbConfig.getSystemTrayInterfaceIconImageFile());
				this.mdbConfig.getListOfInterfaces().add(systemTrayInterface);
			}
			this.mdbConfig.getListOfInterfaces().add(new TelnetInterface(this.mainController));

			if (this.mdbConfig.getWebInterfaceFileDetailsOutputItem() != null) {
				final WebInterface webInterface = new WebInterface(this.mainController);
				webInterface.setFileDetailsOutputItem(this.mdbConfig.getWebInterfaceFileDetailsOutputItem());
				this.mdbConfig.getListOfInterfaces().add(webInterface);
			}

			this.iController.setInterfaces(this.mdbConfig.getListOfInterfaces());

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void initTypeController() {
		this.tController = new TypeController(this.mainController, this.mdbConfig.getListOfTypers());
	}

	public void run() {
		Debug.log(Debug.LEVEL_INFO, RessourceBundleEx.getInstance("mdb").getProperty("application.name") + " ("
				+ RessourceBundleEx.getInstance("mdb").getProperty("application.version") + ")");

		this.mainController.run();
	}

}
