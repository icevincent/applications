package org.universAAL.AALapplication.medication_manager.servlet.ui.impl.parser.script.forms;

import org.universAAL.AALapplication.medication_manager.persistence.layer.PersistentService;
import org.universAAL.AALapplication.medication_manager.persistence.layer.dto.IntakeDTO;
import org.universAAL.AALapplication.medication_manager.persistence.layer.dto.MealRelationDTO;
import org.universAAL.AALapplication.medication_manager.servlet.ui.impl.servlets.helpers.MedicineView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author George Fournadjiev
 */
public final class NewMedicineScriptForm extends ScriptForm {

  public static final String PREFIX = "medicineObj.";
  public static final String SUFFIX = "\';";
  public static final String ID = "id";
  public static final String PRESCRIPTION_ID = "prescriptionId";
  public static final String IS_NEW = "isNew";
  public static final String MEDICINE_OBJ_NAME = "name";
  public static final String MEDICINE_OBJ_DESCRIPTION = "description";
  public static final String MEDICINE_OBJ_SIDE_EFFECTS = "side_effects";
  public static final String MEDICINE_OBJ_INCOMPLIANCES = "incompliances";
  public static final String MEDICINE_OBJ_DAYS = "days";
  public static final String MEDICINE_OBJ_DOSE = "dose";
  public static final String MEDICINE_OBJ_UNIT = "unit";
  public static final String MEDICINE_OBJ_MEAL_RELATION = "meal_relation";
  public static final String MEDICINE_OBJ_HOURS = "hours";
  private final PersistentService persistentService;
  private final MedicineView medicineView;
  private final int prescriptionId;

  private static final String NEW_PRESCRIPTION_FUNCTION_CALL_TEXT = "prescriptionObj.medicines.push";


  public NewMedicineScriptForm(PersistentService persistentService, MedicineView medicineView, int prescriptionId) {
    super(NEW_PRESCRIPTION_FUNCTION_CALL_TEXT);

    this.persistentService = persistentService;
    this.medicineView = medicineView;
    this.prescriptionId = prescriptionId;
    setSingleJavascriptObjects();
  }

  @Override
  public void setSingleJavascriptObjects() {

    fillWithTempData();

    List<String> singleObjects = new ArrayList<String>();

    String medId = createSingleObjectText(ID, medicineView.getMedicineId());
    singleObjects.add(medId);

    String prescrId = createSingleObjectText(PRESCRIPTION_ID, prescriptionId);
    singleObjects.add(prescrId);

    String isNew = createSingleObjectText(IS_NEW, medicineView.isNew());
    String isNewText = PREFIX + IS_NEW + " = " + isNew;
    singleObjects.add(isNewText);

    String name = medicineView.getName();
    if (name != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_NAME, name);
      singleObjects.add(value);
    }

    String description = medicineView.getDescription();
    if (description != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_DESCRIPTION, description);
      singleObjects.add(value);
    }

    String sideeffects = medicineView.getSideeffects();
    if (sideeffects != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_SIDE_EFFECTS, sideeffects);
      singleObjects.add(value);
    }

    String incompliances = medicineView.getIncompliances();
    if (incompliances != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_INCOMPLIANCES, incompliances);
      singleObjects.add(value);
    }

    int days = medicineView.getDays();
    if (days > 0) {
      String value = PREFIX + MEDICINE_OBJ_DAYS + " = " + days;
      singleObjects.add(value);
    }

    int dose = medicineView.getDose();
    if (dose > 0) {
      String value = PREFIX + MEDICINE_OBJ_DOSE + " = " + dose;
      singleObjects.add(value);
    }

    String unit = medicineView.getUnit();
    if (unit != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_UNIT, unit);
      singleObjects.add(value);
    }

    MealRelationDTO mr = medicineView.getMealRelationDTO();
    if (mr != null) {
      String value = createSingleObjectText(MEDICINE_OBJ_MEAL_RELATION, mr.getValue());
      singleObjects.add(value);
    }

    String hours = medicineView.getHours();
    if (hours != null) {
      String value = PREFIX + MEDICINE_OBJ_HOURS + " = " + hours;
      singleObjects.add(value);
    }

    String[] objects = new String[singleObjects.size()];
    this.singleJavascriptObjects = singleObjects.toArray(objects);
  }

  private void fillWithTempData() {
    medicineView.setName("Analgin");
    medicineView.setDescription("Analgin Description");
    medicineView.setSideeffects("stomach problems");
    medicineView.setIncompliances("none");
    medicineView.setDays(7);
    medicineView.fillIntakeDTOSet(5, "[8, 14, 21]", "pill");
  }

  private String createSingleObjectText(String field, Object value) {

    return PREFIX + field + " = \'" + value + SUFFIX;
  }

  /*
  medicineObj.id='med1';
  	medicineObj.prescriptionId='p1';

  		isNew = false;//add ths if edit medicine
  	medicineObj.name='Medicine name 1 that is too long';
  	medicineObj.description='We recommend against using this property; please try to use feature detection instead (see jQuery.support). jQuery.browser may be moved to a plugin in a future release of jQuery.';
  	medicineObj.side_effects='sleeping problems';
  	medicineObj.incompliances='alcohol';
  	medicineObj.days=7;
  	medicineObj.dose=1;
  	medicineObj.unit='drops';
  	medicineObj.meal_relation='before';
    medicineObj.hours=[1,13, 22, 24];
   */

  @Override
  public void process() {

    //nothing we can do here
  }

  private String createHoursText(Set<IntakeDTO> intakeDTOs) {
    StringBuffer sb = new StringBuffer();
    sb.append('[');

    int count = 0;
    int size = intakeDTOs.size();
    for (IntakeDTO dto : intakeDTOs) {
      int hour = dto.getTime().getHour();
      sb.append(hour);
      count++;
      if (count < size) {
        sb.append(", ");
      }
    }

    sb.append("]");

    return sb.toString();
  }


}