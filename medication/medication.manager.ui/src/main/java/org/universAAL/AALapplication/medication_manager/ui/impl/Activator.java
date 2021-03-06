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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.universAAL.AALapplication.medication_manager.configuration.ConfigurationProperties;
import org.universAAL.AALapplication.medication_manager.configuration.Message;
import org.universAAL.AALapplication.medication_manager.configuration.MessageCreator;
import org.universAAL.AALapplication.medication_manager.persistence.layer.PersistentService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.ontology.profile.User;

import java.io.File;

import static java.io.File.*;


public class Activator implements BundleActivator {

  static ModuleContext mc;
  static BundleContext context;
  static ServiceTracker persistenceServiceTracker;
  private static ServiceTracker configurationPropertiesServiceTracker;
  private static ServiceTracker messageServiceTracker;
  private static Message message;
  public MedicationManagerServiceButtonProvider medicationManagerServiceButtonProvider;
  public static final User SAIED = new User("urn:org.universAAL.aal_space:test_environment#saied");

  public void start(BundleContext bundleContext) throws Exception {
    mc = uAALBundleContainer.THE_CONTAINER
        .registerModule(new Object[]{bundleContext});
    context = bundleContext;
    persistenceServiceTracker = new ServiceTracker(context, PersistentService.class.getName(), null);
    configurationPropertiesServiceTracker = new ServiceTracker(context, ConfigurationProperties.class.getName(), null);
    messageServiceTracker = new ServiceTracker(context, MessageCreator.class.getName(), null);

    persistenceServiceTracker.open();
    configurationPropertiesServiceTracker.open();
    messageServiceTracker.open();

    message = getMessage();

    ReminderDialogProvider reminderDialogProvider = new ReminderDialogProvider(mc);
    IntakeReviewDialogProvider intakeReviewDialogProvider = new IntakeReviewDialogProvider(mc);
    medicationManagerServiceButtonProvider = new MedicationManagerServiceButtonProvider(mc);
    RequestMedicationInfoDialogProvider requestMedicationInfoDialogProvider = new RequestMedicationInfoDialogProvider(mc);
    MainMedicationManagerMenu mainMedicationManagerMenu = new MainMedicationManagerMenu(mc);
    DispenserUpsideDownDialogProvider dispenserUpsideDownDialogProvider = new DispenserUpsideDownDialogProvider(mc);
    DispenserDisplayInstructionsDialogProvider dispenserDisplayInstructionsDialogProvider =
        new DispenserDisplayInstructionsDialogProvider(mc);

    InventoryStatusDialogProvider inventoryStatusDialogProvider = new InventoryStatusDialogProvider(mc);

  }


  public void stop(BundleContext arg0) throws Exception {
    // TODO Auto-generated method stub

  }

  public static PersistentService getPersistentService() {
    if (persistenceServiceTracker == null) {
      throw new MedicationManagerUIException("The PersistentService ServiceTracker is not set");
    }
    PersistentService service = (PersistentService) persistenceServiceTracker.getService();
    if (service == null) {
      throw new MedicationManagerUIException("The PersistentService is missing");
    }

    return service;
  }

  public static File getMedicationManagerConfigurationDirectory() {

    String pathToMedicationManagerConfigurationDirectory;
    try {
      File currentDir = new File(".");
      String pathToCurrentDir = currentDir.getCanonicalPath();
      String bundlesConfigurationLocationProperty = System.getProperty("bundles.configuration.location");
      pathToMedicationManagerConfigurationDirectory = pathToCurrentDir + separator +
          bundlesConfigurationLocationProperty + separator + "medication_manager";
    } catch (Exception e) {
      throw new MedicationManagerUIException(e);
    }

    File directory = new File(pathToMedicationManagerConfigurationDirectory);
    if (!directory.exists()) {
      throw new MedicationManagerUIException("The directory does not exists:" + directory);
    }

    if (!directory.isDirectory()) {
      throw new MedicationManagerUIException("The following file:" + directory + " is not a valid directory");
    }

    return directory;
  }


  public static void validateParameter(int parameter, String parameterName) {

    if (parameter <= 0) {
      throw new MedicationManagerUIException("The parameter : " +
          parameterName + " must be positive number");
    }

  }

  public static void validateParameter(Object parameter, String parameterName) {

    if (parameter == null) {
      throw new MedicationManagerUIException("The parameter : " + parameterName + " cannot be null");
    }

  }

  public static ConfigurationProperties getConfigurationProperties() {
    if (configurationPropertiesServiceTracker == null) {
      throw new MedicationManagerUIException("The ConfigurationProperties ServiceTracker is not set");
    }
    ConfigurationProperties service = (ConfigurationProperties) configurationPropertiesServiceTracker.getService();
    if (service == null) {
      throw new MedicationManagerUIException("The ConfigurationProperties is missing");
    }

    return service;
  }

  private static Message getMessage() {
    if (messageServiceTracker == null) {
      throw new MedicationManagerUIException("The MessageServiceTracker is not set");
    }
    MessageCreator service = (MessageCreator) messageServiceTracker.getService();
    if (service == null) {
      throw new MedicationManagerUIException("The MessageCreator is missing");
    }


    return service.createMessage(Activator.class.getClassLoader(), "medication-ui");

  }

  public static String getMessage(String key, Object... objects) {
    return message.getMessage(key, objects);
  }

}
