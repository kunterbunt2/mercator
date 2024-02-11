package de.bushnaq.abdalla.mercator.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author kunterbunt
 *
 */
public class MavenPropertiesProvider {
	private static final MavenPropertiesProvider	INSTANCE	= new MavenPropertiesProvider();// ---initialize the static field
	static ResourceBundle							rb;

	public static String getProperty(String name) throws Exception {
		if (MavenPropertiesProvider.rb == null) {
			throw new Exception("Resource bundle 'maven' was not found or error while reading current version.");
		}
		return MavenPropertiesProvider.rb.getString(name);
	}

	private MavenPropertiesProvider() {
		try {
			MavenPropertiesProvider.rb = ResourceBundle.getBundle("maven");
		} catch (MissingResourceException e) {
			// ---rb stays null
		}
	}
}
