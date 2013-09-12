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

package org.universAAL.AALapplication.medication_manager.ui.impl;

import org.universAAL.AALapplication.medication_manager.persistence.layer.PersistentService;
import org.universAAL.AALapplication.medication_manager.persistence.layer.dao.IntakeDao;
import org.universAAL.AALapplication.medication_manager.persistence.layer.dao.MedicineInventoryDao;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Intake;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Medicine;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.MedicineInventory;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Person;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Treatment;

import java.util.List;

/**
 * @author George Fournadjiev
 */
public final class InventoryStatusHelper {


  public String getInventoryStatusText(Person patient, PersistentService persistentService) {

    MedicineInventoryDao medicineInventoryDao = persistentService.getMedicineInventoryDao();

    List<MedicineInventory> medicineInventories = medicineInventoryDao.getAllMedicineInventoriesForPatient(patient);
    IntakeDao intakeDao = persistentService.getIntakeDao();
    List<Intake> intakes = intakeDao.getIntakesByUserAndTimePlanDue(patient);
    String inventoryStatusText = createInventoryText(patient.getName(), medicineInventories, intakes);

    Log.info("Loaded from the database the following inventory status text:\n%s", getClass(), inventoryStatusText);

    return inventoryStatusText;

  }

  private String createInventoryText(String name, List<MedicineInventory> medicineInventories, List<Intake> intakes) {
    StringBuffer sb = new StringBuffer();

    sb.append("Medicine inventory for : ");
    sb.append(name);

    sb.append('\n');

    int counter = 0;
    for (MedicineInventory medicineInventory : medicineInventories) {
      String row = createRow(medicineInventory, intakes);
      Log.info("Added single medicineInventory row: %s", getClass(), row);
      sb.append("\n\n\t");
      counter++;
      sb.append(counter);
      sb.append(". ");
      sb.append(row);
    }

    if (counter == 0) {
      sb.append("There is no available medicine inventory");
    }

    sb.append('\n');


    return sb.toString();
  }

  private String createRow(MedicineInventory medicineInventory, List<Intake> intakes) {
    StringBuffer sb = new StringBuffer();

    sb.append("Medicine: ");
    sb.append(medicineInventory.getMedicine().getMedicineName());
    sb.append(", units: ");
    sb.append(medicineInventory.getUnitClass().getType());
    sb.append(", quantity: ");
    sb.append(medicineInventory.getQuantity());
    sb.append(", threshold: ");
    sb.append(medicineInventory.getWarningThreshold());
    sb.append(", needed quantity: ");
    sb.append(getNeededQuantity(medicineInventory.getMedicine(), intakes));

    return sb.toString();
  }

  private int getNeededQuantity(Medicine medicine, List<Intake> intakes) {
    int quantity = 0;

    for (Intake intake : intakes) {
      Treatment treatment = intake.getTreatment();
      Medicine med = treatment.getMedicine();
      if (med.getId() == medicine.getId()) {
         quantity = quantity + intake.getQuantity();
      }
    }

    return quantity;
  }
}