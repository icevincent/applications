/*******************************************************************************
 * Copyright 2013 Universidad Politécnica de Madrid
 * Copyright 2013 Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
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

package org.universAAL.AALapplication.health.manager.service.genericUIs;

import org.universAAL.AALapplication.health.manager.ui.AbstractHealthForm;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.service.DefaultServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.middleware.ui.rdf.Form;
import org.universAAL.middleware.ui.rdf.Label;
import org.universAAL.middleware.ui.rdf.Submit;
import org.universAAL.ontology.health.owl.HealthProfile;
import org.universAAL.ontology.health.owl.Treatment;
import org.universAAL.ontology.health.owl.services.DisplayTreatmentService;
import org.universAAL.ontology.health.owl.services.ProfileManagementService;
import org.universAAL.ontology.profile.AssistedPerson;
import org.universAAL.ontology.profile.User;

public class EditTreatmentForm extends AbstractHealthForm{
	
	/**
	 * @param ahf
	 */
	public EditTreatmentForm(AbstractHealthForm ahf) {
		super(ahf);
	}

	/**
	 * @param context
	 * @param inputUser
	 * @param targetUser
	 */
	public EditTreatmentForm(ModuleContext context, User inputUser,
			AssistedPerson targetUser) {
		super(context, inputUser, targetUser);
	}

	private static final String EDIT_CMD = "updateTreatment"; 
	private static final String BACK_CMD = "goBack"; 
	private static final String REM_CMD = "trashTreatment"; 



	/** {@ inheritDoc}	 */
	@Override
	public void handleUIResponse(UIResponse uiResponse) {
	    close();
		String cmd = uiResponse.getSubmissionID();
		if (cmd.startsWith(EDIT_CMD)){
			Treatment t = (Treatment) uiResponse.getSubmittedData();
			// Call add Treatment
//			ServiceRequest sr = new ServiceRequest(new TreatmentManagementService(null), inputUser);
//			sr.addAddEffect(new String[]{TreatmentManagementService.PROP_MANAGES_TREATMENT}, t);
////			sr.addValueFilter(new String[]{TreatmentManagementService.PROP_ASSISTED_USER}, targetUser);
			
			// alternatively udpate the health profile
			HealthProfile hp = getHealthProfile();
			Treatment[] tr = hp.getTreatments();
			for (int i = 0; i <tr.length; i++) {
			    if (tr[i].getURI().equals(t.getURI())){
				tr[i]= t;
			    }
			}
			ServiceRequest sr = new ServiceRequest(new ProfileManagementService(null), inputUser);
			sr.addChangeEffect(new String[]{ProfileManagementService.PROP_ASSISTED_USER_PROFILE}, hp);
			new DefaultServiceCaller(owner).call(sr);
		}
		if (cmd.startsWith(REM_CMD)){
			// Call confirmation Service
			DisplayTreatmentService dts = new DisplayTreatmentService(null);
			dts.setProperty(DisplayTreatmentService.PROP_TREATMENT, uiResponse.getSubmittedData());
			ServiceRequest sr = new ServiceRequest(dts,inputUser);
			sr.addRemoveEffect(new String[]{DisplayTreatmentService.PROP_TREATMENT});
			sr.addValueFilter(new String[]{DisplayTreatmentService.PROP_AFFECTED_USER}, targetUser);
			new DefaultServiceCaller(owner).call(sr);
		}
		//Call list treatment Service
		ServiceRequest sr = new ServiceRequest(new DisplayTreatmentService(null), inputUser);
		sr.addValueFilter(new String[]{DisplayTreatmentService.PROP_AFFECTED_USER}, targetUser);
		new DefaultServiceCaller(owner).call(sr);
		
	}
	
	public void show(Treatment t){
		Form f = NewTreatmentForm.getGenericTreatmentForm(t, this);
		new Submit(f.getSubmits(), 
			new Label(getString("generic.editTreatment.edit"), 
				getString("generic.editTreatment.edit.icon")),
				EDIT_CMD); 
		new Submit(f.getSubmits(), 
			new Label(getString("generic.editTreatment.remove"), 
				getString("generic.editTreatment.remove.icon")), 
				REM_CMD); 
		new Submit(f.getSubmits(), 
			new Label(getString("generic.editTreatment.back"), 
				getString("generic.editTreatment.back.icon")), 
				BACK_CMD); 
		sendForm(f);
	}

	@Override
	public void dialogAborted(String dialogID, Resource data) {
		// TODO Auto-generated method stub
		
	}
	
	
}