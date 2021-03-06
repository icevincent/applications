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

package org.universAAL.AALapplication.medication_manager.user.management.impl.insert.dummy.users;

import org.universAAL.AALapplication.medication_manager.user.management.impl.Log;
import org.universAAL.AALapplication.medication_manager.user.management.impl.MedicationManagerUserManagementException;
import org.universAAL.ontology.profile.PersonalInformationSubprofile;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import static org.universAAL.AALapplication.medication_manager.user.management.impl.insert.dummy.users.Util.*;
import static org.universAAL.ontology.profile.PersonalInformationSubprofile.*;

/**
 * @author George Fournadjiev
 */
public final class VCardPropertiesParser {


  private Properties props = new Properties();

  public PersonalInformationSubprofile createSubprofile(String propertyFileName) {
    loadProperties(propertyFileName);

    PersonalInformationSubprofile subprofile = createPersonalInformationSubprofile(props);


    return subprofile;
  }

  public PersonalInformationSubprofile createSubprofile(File propertyFile) {
    loadProperties(propertyFile);

    PersonalInformationSubprofile subprofile = createPersonalInformationSubprofile(props);


    return subprofile;
  }

  public Properties getProps() {
    return props;
  }

  private void loadProperties(String propertyFileName) {
    try {
      InputStream inputStream =
          VCardPropertiesParser.class.getClassLoader().getResourceAsStream(propertyFileName);
      props = new Properties();
      props.load(inputStream);
      inputStream.close();
    } catch (IOException e) {
      throw new MedicationManagerUserManagementException(e);
    }
  }

  private void loadProperties(File propertyFile) {
    try {
      InputStream inputStream = new FileInputStream(propertyFile);
      props = new Properties();
      props.load(inputStream);
      inputStream.close();
    } catch (IOException e) {
      throw new MedicationManagerUserManagementException(e);
    }
  }

  private PersonalInformationSubprofile createPersonalInformationSubprofile(Properties props) {

    Log.info("Creating a PersonalInformationSubprofile object", getClass());

    checkForUnknownProperties(props);
    checkForUnImplementedProperties(props);

    String userUri = props.getProperty(USER_URI);
    PersonalInformationSubprofile personalInformationSubprofile =
        new PersonalInformationSubprofile(userUri + "InfoSubProf");

    populateTextProperty(DISPLAY_NAME, props,
        personalInformationSubprofile, PROP_DISPLAY_NAME);

    populateTextProperty(VCARD_VERSION, props,
        personalInformationSubprofile, PROP_VCARD_VERSION);

    populateDateProperty(LAST_REVISION, props,
        personalInformationSubprofile, PROP_LAST_REVISION);

    populateTextProperty(NICKNAME, props,
        personalInformationSubprofile, PROP_NICKNAME);


    populateTextProperty(UCI_LABEL, props,
        personalInformationSubprofile, PROP_UCI_LABEL);

    populateTextProperty(UCI_ADDITIONAL_DATA, props,
        personalInformationSubprofile, PROP_UCI_ADDITIONAL_DATA);

    populateTextProperty(ABOUT_ME, props,
        personalInformationSubprofile, PROP_ABOUT_ME);

    populateDateProperty(BDAY, props,
        personalInformationSubprofile, PROP_BDAY);

    populateTextProperty(FN, props,
        personalInformationSubprofile, PROP_FN);

    populateTextProperty(USER_URI, props,
        personalInformationSubprofile, PROP_URL);

    if (personalInformationSubprofile.numberOfProperties() < 2) {
      throw new MedicationManagerUserManagementException("There is not any property set in this VCard");
    }

    return personalInformationSubprofile;
  }

  private void checkForUnknownProperties(Properties props) {

    Enumeration en = props.propertyNames();

    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      boolean supported = false;
      for (String propName : PROPERTIES) {
        if (name.equals(propName)) {
          supported = true;
        }
      }

      if (!supported) {
        throw new MedicationManagerUserManagementException("The property : " + name + " is not supported");
      }
    }
  }

  private void checkForUnImplementedProperties(Properties props) {

    Enumeration en = props.propertyNames();

    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      for (String propName : UN_IMPLEMENTED_PROPERTIES) {
        if (name.equals(propName)) {
          throw new MedicationManagerUserManagementException("The property : " + propName + " is not implemented");
        }
      }
    }
  }

  private void populateDateProperty(String propNameSource, Properties props,
                                    PersonalInformationSubprofile personalInformationSubprofile,
                                    String propNameDestination) {

    String value = props.getProperty(propNameSource);
    Log.info("Getting property in the property file: %s and the value: %s", getClass(), propNameSource, value);
    if (value != null) {
      Log.info("Setting property to the PersonalInformationSubprofile object: name=%s and value=",
          getClass(), propNameSource, value);
      XMLGregorianCalendar calendar = getXMLGregorianCalendar(value);
      personalInformationSubprofile.setProperty(propNameDestination, calendar);
    }

  }

  private XMLGregorianCalendar getXMLGregorianCalendar(String value) {
    try {
      Date date = DATE_FORMAT.parse(value);
      return getCalendar(date);
    } catch (ParseException e) {
      throw new MedicationManagerUserManagementException(e);
    }
  }

  private void populateTextProperty(String propNameSource, Properties props,
                                    PersonalInformationSubprofile personalInformationSubprofile,
                                    String propNameDestination) {

    String value = props.getProperty(propNameSource);

    Log.info("Getting property in the property file: %s and the value: %s", getClass(), propNameSource, value);

    if (value != null) {
      Log.info("Setting property to the PersonalInformationSubprofile object: name=%s and value=",
          getClass(), propNameSource, value);
      personalInformationSubprofile.setProperty(propNameDestination, value);
    }

  }

  public String getName() {
    return props.getProperty(FN);
  }

  public String getSms() {
    return props.getProperty(UCI_ADDITIONAL_DATA);
  }

  public String getUsername() {
    return props.getProperty(NICKNAME);
  }


}
