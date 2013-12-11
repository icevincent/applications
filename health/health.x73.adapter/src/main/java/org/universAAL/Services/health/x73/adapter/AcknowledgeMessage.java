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

package org.universAAL.Services.health.x73.adapter;

import java.util.Locale;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.middleware.ui.UICaller;
import org.universAAL.middleware.ui.UIRequest;
import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.middleware.ui.owl.PrivacyLevel;
import org.universAAL.middleware.ui.rdf.Form;
import org.universAAL.ontology.profile.AssistedPerson;

/**
 * @author amedrano
 *
 */
public class AcknowledgeMessage extends UICaller {

    /**
     * @param context
     */
    public AcknowledgeMessage(ModuleContext context) {
	super(context);
    }

    /** {@ inheritDoc}	 */
    @Override
    public void communicationChannelBroken() {

    }

    /** {@ inheritDoc}	 */
    @Override
    public void dialogAborted(String dialogID) {

    }

    /** {@ inheritDoc}	 */
    @Override
    public void handleUIResponse(UIResponse uiResponse) {

    }

    void show(AssistedPerson ap){
	Form f = Form.newMessage("Measurement Received", "A health measurement has been detected, and assigned.");
	sendUIRequest(new UIRequest(ap, f, LevelRating.low, Locale.ENGLISH, PrivacyLevel.insensible));
    }
}