/*******************************************************************************
 * Copyright 2011 Universidad Polit�cnica de Madrid
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
package deprecated.org.universAAL.AALapplication.health.manager.ui;


import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.middleware.ui.rdf.Form;

import deprecated.HealthManager;

/**
 * @author amedrano
 *
 */
public abstract class InputListener {
	
	protected void listenTo(String DialogID){
		HealthManager.getInstance().getIsubcriber().registerUI(DialogID,this);
	}
	
	abstract public Form getDialog();
	public void handleEvent(UIResponse ie) {
		HealthManager.getInstance().getIsubcriber().unresgisterUI(ie.getDialogID());
	}
}
