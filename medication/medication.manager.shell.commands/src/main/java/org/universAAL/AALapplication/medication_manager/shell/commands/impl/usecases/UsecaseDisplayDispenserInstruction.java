/*******************************************************************************
 * Copyright 2012 , http://www.prosyst.com - ProSyst Software GmbH
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


package org.universAAL.AALapplication.medication_manager.shell.commands.impl.usecases;

import org.universAAL.AALapplication.medication_manager.persistence.layer.PersistentService;
import org.universAAL.AALapplication.medication_manager.persistence.layer.dao.PersonDao;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Person;
import org.universAAL.AALapplication.medication_manager.shell.commands.impl.Log;
import org.universAAL.AALapplication.medication_manager.shell.commands.impl.MedicationManagerShellException;
import org.universAAL.AALapplication.medication_manager.ui.DispenserDisplayInstructionsDialog;
import org.universAAL.ontology.profile.User;

import static org.universAAL.AALapplication.medication_manager.shell.commands.impl.Activator.*;

/**
 * @author George Fournadjiev
 */
public final class UsecaseDisplayDispenserInstruction extends Usecase {

  private static final String PARAMETER_MESSAGE = "Expected one additional parameter (except usecase id), which is: personId." +
        " Please check the dispenser table for the valid ids";

  private static final String USECASE_ID = "UC06";
  private static final String USECASE_TITLE = "UC06: Fill the medication in the pill dispenser - ";
  private static final String USECASE = USECASE_TITLE +
      "The service notifies the AP how to fill the dispenser (display instructions via dialog)" +
      "\n Parameters: " + PARAMETER_MESSAGE;

  public UsecaseDisplayDispenserInstruction() {
    super(USECASE_ID);
  }

  @Override
  public void execute(String... parameters) {

    try {
      if (parameters == null || parameters.length != 1) {
        throw new MedicationManagerShellException(PARAMETER_MESSAGE);
      }

      int id = Integer.parseInt(parameters[0]);

      PersistentService persistentService = getPersistentService();
      PersonDao personDao = persistentService.getPersonDao();

      Person patient = personDao.getById(id);

      User user = new User(patient.getPersonUri());

      DispenserDisplayInstructionsDialog dispenserDisplayInstructionsDialog = new DispenserDisplayInstructionsDialog(mc);
      dispenserDisplayInstructionsDialog.showDialog(user);


    } catch (Exception e) {
      Log.error(e, "Error while processing the the shell command for usecase id:  %s", getClass(), USECASE_ID);
    }
  }


  @Override
  public String getDescription() {
    return USECASE;
  }
}
