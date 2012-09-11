package org.universAAL.AALapplication.safety_home.service.windowProvider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator{

    public static ModuleContext mc;

    public void start(final BundleContext context) throws Exception {
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { context });
		System.out.println("Window Provider started ...");
		new Thread() {
			public void run() {
				new CPublisher(mc);
			}
		}.start();
	}

	public void stop(BundleContext arg0) throws Exception {
	}

}