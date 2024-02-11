package de.bushnaq.abdalla.mercator.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import de.bushnaq.abdalla.engine.IContext;
import de.bushnaq.abdalla.engine.util.logger.Logger;
import de.bushnaq.abdalla.engine.util.logger.LoggerFactory;
import de.bushnaq.abdalla.mercator.util.MavenPropertiesProvider;

import java.io.File;
import java.io.IOException;

/**
 * @author kunterbunt
 */
public abstract class Context extends ApplicationProperties implements IContext {
	private static String	appFolderName		= "app";
	private static String	configFolderName	= "app/config";
	private static String	homeFolderName;
	protected static Logger	logger				= LoggerFactory.getLogger(Context.class);

	protected static String cleanupPath(String path) {
		try {
			path = new File(path).getCanonicalPath();// get rid of all the /..
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return path;
	}

	public static String getAppFolderName() {
		return appFolderName;
	}

	public static String getConfigFolderName() {
		return configFolderName;
	}

	public static String getHomeFolderName() {
		return homeFolderName;
	}

	public static OperatingSystem getOeratingSystemType() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return OperatingSystem.windows;
		} else if (os.contains("mac")) {
			return OperatingSystem.osx;
		} else if (os.contains("nix") || os.contains("nux")) {
			return OperatingSystem.linux;
		} else if (os.contains("ios simulator")) {
			return OperatingSystem.iosSimulator;
		} else if (os.contains("ios")) {
			return OperatingSystem.ios;
		} else
			return OperatingSystem.unknonw;
	}

	public static boolean isIos() {
		return getOeratingSystemType().equals(OperatingSystem.ios) || getOeratingSystemType().equals(OperatingSystem.iosSimulator);
	}

	public static boolean isRunningInEclipse() {
		String	path		= System.getProperty("java.class.path").toLowerCase();
		boolean	isEclipse	= path.contains(".m2");
		return isEclipse;
	}

	private String				appVersion		= "0.0.0";

	public long					currentTime		= 8L * 10000;
	private boolean				enableTime		= true;
	private String				installationFolder;
	private long				lastTime		= 0;
	private OperatingSystem		operatingSystem;
	public boolean				restart			= false;
	public Object				selected		= null;
	public long					timeDelta		= 0L;

	public Context() {
		try {
			appVersion = MavenPropertiesProvider.getProperty("project.version");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		homeFolderName = ".mercator";
		operatingSystem = getOeratingSystemType();
		switch (operatingSystem) {
		case windows:
		default:
			if (isRunningInEclipse()) {
				logger.info("Detected 'Windows' system and we are running inside of 'IDE'.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../..");
				appFolderName = installationFolder + "/app";
				configFolderName = homeFolderName + "/config";
			} else {
				logger.info("Detected 'Windows' system.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../..");
				appFolderName = installationFolder + "/app";
				configFolderName = homeFolderName + "/config";
			}
			break;
		case linux:
			if (isRunningInEclipse()) {
				logger.info("Detected 'Linux' system and we are running inside of 'Eclipse'.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../../..");
				appFolderName = installationFolder + "/app";
				configFolderName = homeFolderName + "/config";
			} else {
				logger.info("Detected 'Linux' system.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../../../bin");
				appFolderName = cleanupPath(installationFolder + "/../lib/app");
				configFolderName = homeFolderName + "/config";
			}
			break;
		case osx:
			if (isRunningInEclipse()) {
				logger.info("Detected 'macOS' system and we are running inside of 'Eclipse'.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../../..");
				appFolderName = installationFolder + "/app";
				configFolderName = homeFolderName + "/config";
			} else {
				logger.info("Detected 'macOS' system.");
				installationFolder = cleanupPath(getInstallationFolder() + "/../../MacOS");
				appFolderName = cleanupPath(installationFolder + "/../app");
				configFolderName = homeFolderName + "/config";
			}
			break;
		case iosSimulator: {
			logger.info("Detected 'iOS' system and we are running inside of 'simulator'.");
			homeFolderName = ".";
			installationFolder = ".";
			appFolderName = installationFolder;
			configFolderName = getHomeFolderName() + "/config";
		}
			break;
		case ios: {
			logger.info("Detected 'iOS' system.");
			homeFolderName = ".";
			installationFolder = ".";
			appFolderName = installationFolder;
			configFolderName = homeFolderName + "/config";
		}
			break;

		}
		logger.info("Detected 'home' folder = " + homeFolderName);
		logger.info("Detected 'installation' folder = " + installationFolder);
		logger.info("Detected 'app' folder = " + appFolderName);
		logger.info("Detected 'configuration' folder = " + configFolderName);
		createFolder(homeFolderName);
		createFolder(configFolderName);
		init();

	}

	public void advanceInTime() throws Exception {
		advanceInTime(enableTime);
	}

	public void advanceInTime(final boolean enable) throws Exception {
	}

	private void createFolder(String folderName) {
		FileHandle local = Gdx.files.external(folderName);
		if (!local.exists()) {
			local.mkdirs(); // If you require it to make the entire directory path including parents, use directory.mkdirs(); here instead.
		}
	}

	public void dispose() {
	}

	public String getAppVersion() {
		return appVersion;
	}


	protected abstract String getInstallationFolder();

	public boolean isEnableTime() {
		return enableTime;
	}
	public void setSelected(final Object selected, final boolean setDirty) throws Exception {
		this.selected = selected;
	}


}

enum OperatingSystem {
	android, applet, headlessDesktop, ios, iosSimulator, linux, osx, unknonw, webgl, windows
}