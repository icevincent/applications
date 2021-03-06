/*
	Copyright 2011-2012 Itaca-TSB, http://www.tsb.upv.es
	Tecnolog�as para la Salud y el Bienestar
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.AALapplication.nutritional.osgi;

//import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
//import org.osgi.framework.ServiceEvent;
//import org.osgi.framework.ServiceListener;
//import org.osgi.framework.ServiceReference;
import org.universAAL.AALapplication.nutritional.SharedResources;
import org.universAAL.AALapplication.nutritional.utils.Utils;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
//import org.universAAL.ucc.configuration.configdefinitionregistry.interfaces.ConfigurationDefinitionRegistry;

public class Activator implements BundleActivator/*, ServiceListener */{

    SharedResources sr;
//    private BundleContext osgiContext;
    private static ModuleContext moduleContext;
//    private static ConfigurationDefinitionRegistry configReg;

    public void start(BundleContext context) throws Exception {
	
//	osgiContext=context;
	moduleContext = uAALBundleContainer.THE_CONTAINER
		.registerModule(new Object[] { context });
//	URL configURL = this.getClass().getResource("/config/nutritional.client.conf.xml");

//	String filter = "(objectclass="
//		+ ConfigurationDefinitionRegistry.class.getName() + ")";
//	context.addServiceListener(this, filter);
//	ServiceReference[] references = context.getServiceReferences(null,
//		filter);
//	for (int i = 0; references != null && i < references.length; i++) {
//	    this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED,
//		    references[i]));
//	}
//	configReg.registerConfigurationDefinition(configURL);

	sr = new SharedResources(moduleContext);
	new Thread() {
	    public void run() {
		sr.start();
	    }
	}.start();
	Utils.println("************** Starting Nutritional Advisor Service **************");
    }

    public static ModuleContext getModuleContext() {
	return moduleContext;
    }

    public void stop(BundleContext context) throws Exception {
	sr.stop();
	Utils.println("************** Stopping Nutritional Advisor Service **************");
    }

//    public void serviceChanged(ServiceEvent event) {
//	// Update the parser of Hub (& store)
//	switch (event.getType()) {
//	case ServiceEvent.REGISTERED:
//	    configReg = (ConfigurationDefinitionRegistry) osgiContext
//	    .getService(event.getServiceReference());
//	    break;
//	case ServiceEvent.MODIFIED:
//	    configReg = (ConfigurationDefinitionRegistry) osgiContext
//		    .getService(event.getServiceReference());
//	    break;
//	case ServiceEvent.UNREGISTERING:
//	    configReg = null;
//	    break;
//	default:
//	    break;
//	}
//    }

}
