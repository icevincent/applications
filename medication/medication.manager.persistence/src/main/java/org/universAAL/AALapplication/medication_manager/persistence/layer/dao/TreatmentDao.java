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

package org.universAAL.AALapplication.medication_manager.persistence.layer.dao;

import org.universAAL.AALapplication.medication_manager.persistence.impl.Log;
import org.universAAL.AALapplication.medication_manager.persistence.impl.MedicationManagerPersistenceException;
import org.universAAL.AALapplication.medication_manager.persistence.impl.database.AbstractDao;
import org.universAAL.AALapplication.medication_manager.persistence.impl.database.Column;
import org.universAAL.AALapplication.medication_manager.persistence.impl.database.Database;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Medicine;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Person;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Prescription;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Treatment;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.TreatmentStatus;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.universAAL.AALapplication.medication_manager.persistence.impl.Activator.*;
import static org.universAAL.AALapplication.medication_manager.persistence.layer.entities.TreatmentStatus.*;

/**
 * @author George Fournadjiev
 */
public final class TreatmentDao extends AbstractDao {

  private PrescriptionDao prescriptionDao;
  private MedicineDao medicineDao;

  private static final String TABLE_NAME = "TREATMENT";
  private static final String PRESCRIPTION_FK_ID = "PRESCRIPTION_FK_ID";
  private static final String MEDICINE_FK_ID = "MEDICINE_FK_ID";
  private static final String START_DATE = "START_DATE";
  private static final String END_DATE = "END_DATE";
  private static final String STATUS = "STATUS";
  private static final String MISSED_INTAKE_ALERT = "MISSED_INTAKE_ALERT";
  private static final String NEW_DOSE_ALERT = "NEW_DOSE_ALERT";
  private static final String SHORTAGE_ALERT = "SHORTAGE_ALERT";

  public TreatmentDao(Database database) {
    super(database, TABLE_NAME);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Treatment getById(int id) {
    Log.info("Looking for the treatment with id=%s", getClass(), id);
    Map<String, Column> columns = getTableColumnsValuesById(id);

    return getTreatment(columns);
  }

  public void setPrescriptionDao(PrescriptionDao prescriptionDao) {
    this.prescriptionDao = prescriptionDao;
  }

  public void setMedicineDao(MedicineDao medicineDao) {
    this.medicineDao = medicineDao;
  }

  public List<Treatment> getByPrescriptionAndNotInActive(int prescriptionId) {
    String sql = "select * from MEDICATION_MANAGER.TREATMENT where PRESCRIPTION_FK_ID = ? and UPPER(STATUS) <> ?";

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      return getTreatments(prescriptionId, sql, statement);
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  private Treatment getTreatment(Map<String, Column> columns) {

    checkForSetDao(prescriptionDao, "prescriptionDao");
    checkForSetDao(medicineDao, "medicineDao");

    Column col = columns.get(ID);
    int treatmentId = (Integer) col.getValue();

    col = columns.get(PRESCRIPTION_FK_ID);
    int prescriptionId = (Integer) col.getValue();
    Prescription prescription = prescriptionDao.getById(prescriptionId);

    col = columns.get(MEDICINE_FK_ID);
    int medicineId = (Integer) col.getValue();

    Medicine medicine = medicineDao.getById(medicineId);

    col = columns.get(START_DATE);
    Date startDate = (Date) col.getValue();

    col = columns.get(END_DATE);
    Date endDate = (Date) col.getValue();

    col = columns.get(STATUS);
    String statusValue = (String) col.getValue();
    TreatmentStatus treatmentStatus = TreatmentStatus.getEnumValueFor(statusValue);

    col = columns.get(MISSED_INTAKE_ALERT);
    Boolean bool = (Boolean) col.getValue();
    boolean missedIntakeAlert = false;
    if (bool != null) {
      missedIntakeAlert = bool;
    }

    col = columns.get(NEW_DOSE_ALERT);
    bool = (Boolean) col.getValue();
    boolean newDoseAlert = false;
    if (bool != null) {
      newDoseAlert = bool;
    }

    col = columns.get(SHORTAGE_ALERT);
    bool = (Boolean) col.getValue();
    boolean shortageAlert = false;
    if (bool != null) {
      shortageAlert = bool;
    }

    Treatment treatment = new Treatment(treatmentId, prescription, medicine,
        missedIntakeAlert, newDoseAlert, shortageAlert, startDate, endDate, treatmentStatus);

//    Log.info("Treatment found: %s", getClass(), treatment);
    Log.info("Treatment found with id : %s", getClass(), treatment.getId());

    return treatment;
  }

  private List<Treatment> getTreatments(int prescriptionId, String sql,
                                        PreparedStatement statement) throws SQLException {
    statement.setInt(1, prescriptionId);
    statement.setString(2, INACTIVE.getValue().toUpperCase());
    List<Map<String, Column>> results = executeQueryExpectedMultipleRecord(TABLE_NAME, sql, statement);
    return createTreatments(results);
  }

  private List<Treatment> createTreatments(List<Map<String, Column>> results) {

    List<Treatment> treatments = new ArrayList<Treatment>();

    for (Map<String, Column> columns : results) {
      Treatment treatment = getTreatment(columns);
      treatments.add(treatment);
    }

    return treatments;
  }

  public Treatment findTreatment(int patientId, int medicineId) {
    String sql = "select * from MEDICATION_MANAGER.TREATMENT " +
        "where MEDICINE_FK_ID = ? and UPPER(STATUS) = ?\n" +
        " and PRESCRIPTION_FK_ID IN (select ID from MEDICATION_MANAGER.PRESCRIPTION where PATIENT_FK_ID = ?)";

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      statement.setInt(1, medicineId);
      statement.setString(2, ACTIVE.getValue().toUpperCase());
      statement.setInt(3, patientId);
      Map<String, Column> result = executeQueryExpectedSingleRecord(TABLE_NAME, statement);
      if (result.isEmpty()) {
        throw new MedicationManagerPersistenceException("Missing treatment record for the patientId :" +
            patientId + " and medicineId: " + medicineId);
      }
      return getTreatment(result);
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  public Set<Treatment> getAllActiveTreatments() {
    String sql = "select * from MEDICATION_MANAGER.TREATMENT where UPPER(STATUS) = ?";

    Set<Treatment> treatmentSet = new HashSet<Treatment>();

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      statement.setString(1, TreatmentStatus.ACTIVE.getValue());
      List<Map<String, Column>> result = executeQueryMultipleRecordsPossible(TABLE_NAME, sql, statement);
      for (Map<String, Column> columnMap : result) {
        Treatment treatment = getTreatment(columnMap);
        treatmentSet.add(treatment);
      }
      return treatmentSet;
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  public Set<Treatment> getAllTreatments() {
    String sql = "select * from MEDICATION_MANAGER.TREATMENT";

    Set<Treatment> treatmentSet = new HashSet<Treatment>();

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      List<Map<String, Column>> result = executeQueryMultipleRecordsPossible(TABLE_NAME, sql, statement);
      for (Map<String, Column> columnMap : result) {
        Treatment treatment = getTreatment(columnMap);
        treatmentSet.add(treatment);
      }
      return treatmentSet;
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  public void updateTreatmentTable(int treatmentId, boolean missed,
                                   boolean shortage, boolean dose) throws SQLException {

    Log.info("Setting new alerts values for a treatments with id: %s - missed : %s, shortage : %s, dose : %s, ",
        getClass(), treatmentId, missed, shortage, dose);

    String sql = "update MEDICATION_MANAGER.TREATMENT " +
        "set MISSED_INTAKE_ALERT = ?, SHORTAGE_ALERT = ?, NEW_DOSE_ALERT = ? where ID = ?";

    PreparedStatement ps = null;

    try {
      ps = getPreparedStatement(sql);
      ps.setBoolean(1, missed);
      ps.setBoolean(2, shortage);
      ps.setBoolean(3, dose);
      ps.setInt(4, treatmentId);
      ps.execute();
    } finally {
      closeStatement(ps);
    }
  }

  public Set<Treatment> findTreatments(Person patient) {

    if (patient == null) {
      throw new MedicationManagerPersistenceException("patient parameter is null");
    }

    String sql = "SELECT tr.*\n" +
        "\n" +
        "FROM\n" +
        "    MEDICATION_MANAGER.TREATMENT AS tr,\n" +
        "    MEDICATION_MANAGER.PRESCRIPTION AS p\n" +
        "\n" +
        "WHERE tr.PRESCRIPTION_FK_ID = p.ID\n" +
        "      AND p.PATIENT_FK_ID = ?\n" +
        "      AND (UPPER(tr.STATUS) = ? OR UPPER(tr.STATUS) = ?)";

    Set<Treatment> treatmentSet = new HashSet<Treatment>();

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      statement.setInt(1, patient.getId());
      statement.setString(2, TreatmentStatus.ACTIVE.getValue());
      statement.setString(3, TreatmentStatus.PENDING.getValue());
      List<Map<String, Column>> result = executeQueryMultipleRecordsPossible(TABLE_NAME, sql, statement);
      for (Map<String, Column> columnMap : result) {
        Treatment treatment = getTreatment(columnMap);
        treatmentSet.add(treatment);
      }
      return treatmentSet;
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  public List<Treatment> getPendingTreatments(Person patient, Medicine medicine) {
    String sql = "SELECT * " +
        "FROM MEDICATION_MANAGER.TREATMENT TR," +
        "MEDICATION_MANAGER.PRESCRIPTION P" +
        " WHERE " +
        " P.PATIENT_FK_ID = ? " +
        " AND TR.PRESCRIPTION_FK_ID = P.ID " +
        " AND TR.MEDICINE_FK_ID = ? " +
        " AND UPPER(TR.STATUS) = ?";

    PreparedStatement statement = null;
    try {
      statement = getPreparedStatement(sql);
      return getPendingTreatments(patient.getId(), medicine.getId(), sql, statement);
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }
  }

  private List<Treatment> getPendingTreatments(int patientId, int medicineId,
                                               String sql, PreparedStatement statement) throws SQLException {
    statement.setInt(1, patientId);
    statement.setInt(2, medicineId);
    statement.setString(3, PENDING.getValue().toUpperCase());
    List<Map<String, Column>> results = executeQueryMultipleRecordsPossible(TABLE_NAME, sql, statement);

    return createTreatments(results);
  }

  public void changeStatusFromPendingToActive(List<Treatment> treatments) {

    try {

      for (Treatment treatment : treatments) {
        updateTreatmentStatusToActive(treatment);
      }

    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    }

  }

  private void updateTreatmentStatusToActive(Treatment treatment) throws SQLException {
    Log.info("Setting status from PENDING to ACTIVE for the following treatment with id : %s",
        getClass(), treatment);

    String sql = "update MEDICATION_MANAGER.TREATMENT " +
        "set STATUS = ? where ID = ?";

    PreparedStatement ps = null;

    try {
      ps = getPreparedStatement(sql);
      ps.setString(1, TreatmentStatus.ACTIVE.getValue());
      ps.setInt(2, treatment.getId());

      ps.execute();
    } finally {
      closeStatement(ps);
    }
  }


}
