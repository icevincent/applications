/*****************************************************************************************
 * Copyright 2012 CERTH, http://www.certh.gr - Center for Research and Technology Hellas
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************************/

package org.universAAL.AALapplication.safety_home.service.uiclient;

import org.universAAL.AALapplication.safety_home.service.uiclient.dialogs.door.FrontDoorControl;
import org.universAAL.AALapplication.safety_home.service.uiclient.dialogs.environmental.EnvironmentalControl;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.profile.AssistedPerson;
import org.universAAL.ontology.profile.User;

/**
 * @author dimokas
 * 
 */
public class SharedResources {

	public static final String CLIENT_SAFETY_UI_NAMESPACE = "urn:safety";
	
    public static ModuleContext moduleContext;

    public static SafetyClient safetyClient;
    public static ServiceProvider serviceProvider;
    public static UIProvider uIProvider;

    public static User testUser = new User(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + "saied");
    public static User caregiver = new User(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + "caregiver");
    public static User currentUser = testUser;
    
    public SharedResources(ModuleContext context) {
    	moduleContext = context;
    }

    public void start() throws Exception {
	new Thread() {
	    public void run() {
			SharedResources.safetyClient = new SafetyClient(moduleContext);
			SharedResources.serviceProvider = new ServiceProvider(moduleContext);
			SharedResources.uIProvider = new UIProvider(moduleContext);
			//SharedResources.fdc = new FrontDoorControl(SharedResources.moduleContext);
			//SharedResources.ec = new EnvironmentalControl(SharedResources.moduleContext);
	    }
	}.start();
    }
    
    public void stop(){
    	SharedResources.safetyClient.close();
    	SharedResources.serviceProvider.close();
		SharedResources.uIProvider.close();
    }
}
