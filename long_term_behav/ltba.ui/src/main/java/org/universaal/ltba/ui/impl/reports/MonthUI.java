/*
	Copyright 2008-2014 TSB, http://www.tsbtecnologias.es
	TSB - Tecnolog�as para la Salud y el Bienestar
	
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
package org.universaal.ltba.ui.impl.reports;

import java.util.Locale;

import org.universaal.ltba.ui.activator.MainLTBAUIProvider;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.ui.UICaller;
import org.universAAL.middleware.ui.UIRequest;
import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.middleware.ui.owl.PrivacyLevel;
import org.universAAL.middleware.ui.rdf.Form;
import org.universAAL.middleware.ui.rdf.Label;
import org.universAAL.middleware.ui.rdf.SimpleOutput;
import org.universAAL.middleware.ui.rdf.Submit;
import org.universaal.ltba.ui.impl.common.LTBACaller;
import org.universaal.ltba.ui.impl.common.SharedResources;

public class MonthUI extends UICaller {
	static final String MY_UI_NAMESPACE = "http://www.tsbtecnolgias.es/MonthUI.owl#";
	static final String SUBMISSION_SHOW = MY_UI_NAMESPACE + "showMonth";
	private String SUBMISSION_BACK = MY_UI_NAMESPACE + "backFromMonth";
	ModuleContext mc;

	public MonthUI(ModuleContext context) {
		super(context);
		mc = context;
	}

	@Override
	public void communicationChannelBroken() {
	}

	public void dialogAborted(String dialogID) {
	}

	@Override
	public void handleUIResponse(UIResponse input) {
		if (input != null) {
			if (input.getSubmissionID().equalsIgnoreCase(SUBMISSION_BACK)) {
				new MainLTBAUIProvider(mc).showDialog(input);
			} else if (input.getSubmissionID().equals(SUBMISSION_SHOW)) {
				SharedResources.processingReport = true;
				this.showDialog(input);
				new MonthGraphicThread(mc, input).start();
			}
		}

	}

	public Form showDialog(Resource inputUser) {
		Form f = Form.newDialog("MONTH UI", new Resource());
		if (SharedResources.ltbaIsOn && SharedResources.processingReport) {
			new Submit(f.getIOControls(), new Label("SHOW MONTH REPORT", null),
					SUBMISSION_SHOW);
			new SimpleOutput(f.getIOControls(), null, null, "    Processing...");
		} else if (SharedResources.ltbaIsOn
				&& !SharedResources.processingReport) {
			new Submit(f.getIOControls(), new Label("SHOW MONTH REPORT", null),
					SUBMISSION_SHOW);
		} else {
			new SimpleOutput(f.getIOControls(), null, null,
					"THE LTBA IS CURRENTLY TURNED OFF");
			new Submit(f.getSubmits(), new Label("Back", null), SUBMISSION_BACK);
		}
		UIRequest req = new UIRequest(inputUser, f, LevelRating.none,
				Locale.ENGLISH, PrivacyLevel.insensible);
		this.sendUIRequest(req);
		return f;
	}

	@Override
	public void dialogAborted(String arg0, Resource arg1) {
		// TODO Auto-generated method stub
		
	}
}

class MonthGraphicThread extends Thread {
	ModuleContext mc;
	Resource inputUser;

	public MonthGraphicThread(ModuleContext context, Resource inputUser) {
		mc = context;
		this.inputUser = inputUser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedResources.processingReport = false;
		new LTBACaller(mc, inputUser);
		LTBACaller.printMonth();
		super.run();
	}
}
