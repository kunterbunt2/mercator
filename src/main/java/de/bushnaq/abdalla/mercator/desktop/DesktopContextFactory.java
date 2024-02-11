package de.bushnaq.abdalla.mercator.desktop;

import de.bushnaq.abdalla.engine.IContextFactory;

/**
 * @author kunterbunt
 * 
 */
public class DesktopContextFactory implements IContextFactory {
	private Context context;

	@Override
	public Context create() {
		if (context == null) {
			context = new DesktopContext();
		}
		return getContext();
	}

	Context getContext() {
		return context;
	}

}
