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

import org.universAAL.AALapplication.medication_manager.shell.commands.impl.Log;
import org.universAAL.AALapplication.medication_manager.shell.commands.impl.MedicationManagerShellException;
import org.universAAL.AALapplication.medication_manager.ui.prescription.Doctor;
import org.universAAL.AALapplication.medication_manager.ui.prescription.IntakeDTO;
import org.universAAL.AALapplication.medication_manager.ui.prescription.MealRelationDTO;
import org.universAAL.AALapplication.medication_manager.ui.prescription.MedicineDTO;
import org.universAAL.AALapplication.medication_manager.ui.prescription.PrescriptionDTO;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author George Fournadjiev
 */
public final class PrescriptionParser {


  private static final String PATTERN = "dd/MM/yyyy";
  private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);
  private static final String COULD_NOT_PARSE_THE_PRESCRIPTION_XML_FILE = "Could not parse the prescription xml file";
  private static final String ID = "id";

  public PrescriptionDTO parse(File prescriptionFile) {

    PrescriptionDTO prescriptionDTO;

    try {

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Log.info("Parsing the prescription xml file : %s", PrescriptionParser.class, prescriptionFile.getCanonicalPath());
      Document doc = dBuilder.parse(prescriptionFile);
      Log.info("Trying to get the root element", PrescriptionParser.class);
      Element documentElement = doc.getDocumentElement();
      documentElement.normalize();
      prescriptionDTO = createPrescription(documentElement);
      Log.info("The file successfully parsed", PrescriptionParser.class);
    } catch (MedicationManagerShellException e) {
      throw e;
    } catch (ParserConfigurationException e) {
      throw new MedicationManagerShellException(COULD_NOT_PARSE_THE_PRESCRIPTION_XML_FILE, e);
    } catch (SAXException e) {
      throw new MedicationManagerShellException(COULD_NOT_PARSE_THE_PRESCRIPTION_XML_FILE, e);
    } catch (IOException e) {
      throw new MedicationManagerShellException(COULD_NOT_PARSE_THE_PRESCRIPTION_XML_FILE, e);
    }

    return prescriptionDTO;
  }

  private PrescriptionDTO createPrescription(Element documentElement) {
    String rootTagName = documentElement.getTagName();

    if (rootTagName == null) {
      throw new MedicationManagerShellException("The root tag is null. Must be prescription");
    }

    Log.info("The root element found. Check if the root element is prescription", PrescriptionParser.class);

    if (!"prescription".equals(rootTagName.trim())) {
      throw new MedicationManagerShellException("The root tag must be prescription, instead it is:" + rootTagName);
    }

    Log.info("The root element is correct: prescription", PrescriptionParser.class);

    Log.info("Getting the attributes of the prescription", PrescriptionParser.class);

    int id = getInt(documentElement, ID);

    String description = getText(documentElement, "description");

    Date startDate = getDate(documentElement);

    Log.info("Trying to get the prescription child nodes", PrescriptionParser.class);
    NodeList nodeList = documentElement.getChildNodes();

    Set<MedicineDTO> medicineDTOs = getMedicineSet(nodeList);

    Doctor doctor = getDoctor(nodeList);

    return new PrescriptionDTO(id, description, startDate, medicineDTOs, doctor);

  }

  private String getText(Element element, String attributeName) {
    Log.info("Trying to get the " + attributeName + " attribute", PrescriptionParser.class);
    String text = getAttributeValue(element, attributeName);
    Log.info("the " + attributeName + " attribute successfully resolved: %s", PrescriptionParser.class, text);
    return text;
  }

  private Date getDate(Element documentElement) {
    Log.info("Trying to get the start_date attribute", PrescriptionParser.class);
    String value = getAttributeValue(documentElement, "start_date");

    try {
      Date date = SIMPLE_DATE_FORMAT.parse(value);
      Log.info("the start_date attribute successfully resolved: %s", PrescriptionParser.class, date);
      return date;
    } catch (ParseException e) {
      throw new MedicationManagerShellException("The value of the start_date attribute is not correct: " + value +
          ". The expected format is: " + PATTERN, e);
    }
  }

  private int getInt(Element element, String attributeName) {
    Log.info("Trying to get the " + attributeName + " attribute", PrescriptionParser.class);

    String value = getAttributeValue(element, attributeName);

    try {
      int number = Integer.valueOf(value);
      if (number <= 0) {
        throw new MedicationManagerShellException("The " + attributeName + " attribute must must have positive int value");
      }
      Log.info("The " + attributeName + " attribute successfully resolved: %s", PrescriptionParser.class, number);
      return number;
    } catch (NumberFormatException e) {
      throw new MedicationManagerShellException("Could not parse the " + attributeName +
          " attribute value. Expecting int value", e);
    }

  }

  private String getAttributeValue(Element documentElement, String attributeName) {
    Attr attr = documentElement.getAttributeNode(attributeName);
    if (attr == null) {
      throw new MedicationManagerShellException("The root tag prescription is missing " + attributeName + " attribute");
    }

    String value = attr.getValue();

    Log.info("The " + attributeName + " attribute value is: %s", PrescriptionParser.class, value);

    if (value == null || value.trim().isEmpty()) {
      throw new MedicationManagerShellException("The " + attributeName +
          " attribute is missing value. Must not be null or empty");
    }
    return value;
  }

  private Doctor getDoctor(NodeList nodeList) {
    Node doctorNode = getNode(nodeList, "doctor");

    NodeList doctorChildNodes = doctorNode.getChildNodes();

    Node nameNode = getNode(doctorChildNodes, "name");

    return new Doctor(nameNode.getTextContent());

  }

  private Set<MedicineDTO> getMedicineSet(NodeList nodeList) {
    Log.info("Trying to get medicines tag", PrescriptionParser.class);
    Node medicinesNode = getNode(nodeList, "medicines");
    Log.info("Trying to get medicines child nodes", PrescriptionParser.class);
    NodeList medicineNodeList = medicinesNode.getChildNodes();
    Set<MedicineDTO> medicineDTOs = getMedicines(medicineNodeList);

    return medicineDTOs;
  }

  private Set<MedicineDTO> getMedicines(NodeList medicineNodeList) {
    Log.info("Trying to create Medicine Set", PrescriptionParser.class);
    List<Node> medicineNodes = new ArrayList<Node>();
    for (int i = 0; i < medicineNodeList.getLength(); i++) {
      Node node = medicineNodeList.item(i);
      if ("medicine".equals(node.getNodeName())) {
        medicineNodes.add(node);
      }
    }

    return createMedicineSet(medicineNodes);
  }

  private Set<MedicineDTO> createMedicineSet(List<Node> medicineNodes) {
    Set<MedicineDTO> medicinesSet = new HashSet<MedicineDTO>();

    for (Node node : medicineNodes) {
      if ("medicine".equals(node.getNodeName()) && node.getNodeType() == Node.ELEMENT_NODE) {
        MedicineDTO medicineDTO = createMedicine(node);
        medicinesSet.add(medicineDTO);
      }
    }

    if (medicinesSet.isEmpty()) {
      throw new MedicationManagerShellException("Missing medicine tag which is child tag of the medicines tag");
    }

    return medicinesSet;

  }

  private MedicineDTO createMedicine(Node node) {
    Element element = (Element) node;
    int id = getInt(element, ID);
    String name = getText(element, "name");
    int days = getInt(element, "days");

    NodeList nodeList = node.getChildNodes();

    Node descriptionNode = getNode(nodeList, "description");

    String description = descriptionNode.getTextContent();

    Log.info("description = %s", PrescriptionParser.class, description);

    Node mealRelationNode = getNode(nodeList, "meal_relation");

    String mealRelationText = mealRelationNode.getTextContent();

    MealRelationDTO mealRelationDTO = MealRelationDTO.getEnumValueFor(mealRelationText);

    Log.info("mealRelationDTO = %s", PrescriptionParser.class, mealRelationDTO);

    Set<IntakeDTO> intakeDTOSet = getIntakeSet(node.getChildNodes());

    return new MedicineDTO(id, name, days, description, mealRelationDTO, intakeDTOSet);

  }

  private Set<IntakeDTO> getIntakeSet(NodeList intakeNodes) {
    for (int i = 0; i < intakeNodes.getLength(); i++) {
      Node node = intakeNodes.item(i);
      if ("intakes".equals(node.getNodeName())) {
        return createIntakeSet(node.getChildNodes());
      }
    }

    throw new MedicationManagerShellException("Missing intakes tag, which mus be child of the medicine tag");
  }

  private Set<IntakeDTO> createIntakeSet(NodeList intakeNodes) {
    List<Node> intakeList = new ArrayList<Node>();
    for (int i = 0; i < intakeNodes.getLength(); i++) {
      Node node = intakeNodes.item(i);
      if ("intake".equals(node.getNodeName())) {
        intakeList.add(node);
      }
    }

    if (intakeList.isEmpty()) {
      throw new MedicationManagerShellException("Missing intake tag, which mus be child of the intakes tag");
    }

    return createIntakeSetFromNodes(intakeList);
  }

  private Set<IntakeDTO> createIntakeSetFromNodes(List<Node> intakeList) {
    Set<IntakeDTO> intakeDTOSet = new HashSet<IntakeDTO>();
    for (Node node : intakeList) {
      IntakeDTO intakeDTO = createIntakeFromNode(node);
      intakeDTOSet.add(intakeDTO);
    }

    return intakeDTOSet;
  }

  private IntakeDTO createIntakeFromNode(Node intakeNode) {
    NodeList nodeList = intakeNode.getChildNodes();
    String time = getTime(nodeList);

    IntakeDTO.Unit unit = getUnit(nodeList);

    int dose = getDose(nodeList);

    return new IntakeDTO(time, unit, dose);

  }

  private int getDose(NodeList nodeList) {
    Node doseNode = getNode(nodeList, "dose");

    String value = doseNode.getTextContent();

    int dose = Integer.parseInt(value);

    if (dose <= 0) {
      throw new MedicationManagerShellException("The dose must be positive number");
    }

    return dose;

  }

  private IntakeDTO.Unit getUnit(NodeList nodeList) {
    Node unitNode = getNode(nodeList, "unit");

    String value = unitNode.getTextContent();

    return IntakeDTO.Unit.getEnumValueFor(value);
  }

  private String getTime(NodeList nodeList) {
    Node timeNode = getNode(nodeList, "time");

    String value = timeNode.getTextContent();

    validateTime(value);

    return value;
  }

  private void validateTime(String value) {
    String message = "The time must be between 0:00 and 23:59 (in that format)";
    int index = value.indexOf(':');
    if (index <= 0) {
      throw new MedicationManagerShellException(message);
    }
    String hourText = value.substring(0, index);

    int hour = Integer.parseInt(hourText);

    if (hour < 0 || hour > 23) {
      throw new MedicationManagerShellException(message);
    }

    String minuteString = value.substring(index + 1);

    int minute = Integer.parseInt(minuteString);

    if (minute < 0 || minute > 59) {
      throw new MedicationManagerShellException(message);
    }

  }

  private Node getNode(NodeList nodeList, String nodeName) {
    Log.info("Trying to find tag with the name: %s", PrescriptionParser.class, nodeName);
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (nodeName.equals(node.getNodeName())) {
        return node;
      }
    }

    throw new MedicationManagerShellException("Missing the following prescription child node : " + nodeName);
  }

}