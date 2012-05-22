package org.universAAL.AALapplication.medication_manager.ui.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator {

	static ModuleContext context;
	public ReminderDialogProvider service;

	public void start(BundleContext arg0) throws Exception {
		context = uAALBundleContainer.THE_CONTAINER
                .registerModule(new Object[] {arg0});
		context.logDebug("Initialising Project", null);

		/*
		 * uAAL stuff
		 */
		service = new ReminderDialogProvider(context);

		context.logInfo("Project started", null);
	}


	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}