/*******************************************************************************
 * Copyright 2013 Universidad Polit�cnica de Madrid
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
 ******************************************************************************/
package org.universAAL.AALApplication.health.motivation;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceResponse;

public class SCaller extends ServiceCaller{

	protected SCaller(ModuleContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}

	public void handleResponse(String reqID, ServiceResponse response) {
		// TODO Auto-generated method stub
		
	}

}
