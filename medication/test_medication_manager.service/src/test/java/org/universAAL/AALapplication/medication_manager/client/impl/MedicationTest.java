package org.universAAL.AALapplication.medication_manager.client.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.util.Assert;
import org.universAAL.itests.IntegrationTest;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.ontology.medMgr.Precaution;
import org.universAAL.ontology.medMgr.UserIDs;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * @author George Fournadjiev
 */
public final class MedicationTest extends IntegrationTest {


  public MedicationTest() {
    //super("../../pom/launches/LightingExample_Complete_0_3_2.launch");
    //setBundleConfLocation("../../../itests/rundir/confadmin");\
  }

  /**
   * Helper method for logging.
   */
  protected void logInfo(String format, Object... args) {
    StackTraceElement callingMethod = Thread.currentThread()
        .getStackTrace()[2];
    LogUtils.logInfo(Activator.mc, getClass(), callingMethod
        .getMethodName(), new Object[]{formatMsg(format, args)},
        null);
  }

  /**
   * Helper method for logging.
   */
  protected void logError(Throwable t, String format, Object... args) {
    StackTraceElement callingMethod = Thread.currentThread()
        .getStackTrace()[2];
    LogUtils.logError(Activator.mc, getClass(), callingMethod
        .getMethodName(), new Object[]{formatMsg(format, args)}, t);
  }

  public void testComposite() {
    logAllBundles();
//    printAllBundlesInfo();

  }

  private void printAllBundlesInfo() {
    Bundle[] bundles = bundleContext.getBundles();
    for (int i = 0; i < bundles.length; i++) {
      /*if (bundles[i].getSymbolicName().equalsIgnoreCase("mw.container.xfaces-0.1.0-SNAPSHOT")) {
        b = bundles[i];
      }*/

      printBundleHeaders(bundles[i]);
    }
//    printBundleHeaders(b);
  }

  private void printBundleHeaders(Bundle b) {
    System.out.println("********** Begin for Bundle : " + b.getSymbolicName() + " *********");
    Dictionary d = b.getHeaders();
    Enumeration e = d.keys();
    while (e.hasMoreElements()) {
      Object key = e.nextElement();
      String value = (String) d.get(key);

      System.out.println("key=" + key + " value=" + value);
    }

    System.out.println("********** End for Bundle : " + b.getSymbolicName() + " ********");
  }

  /**
   * Verifies that runtime platform has correctly started. It prints basic
   * information about framework (vendor, version) and lists installed
   * bundles.
   *
   * @throws Exception
   */
  public void testOsgiPlatformStarts() throws Exception {
    logInfo("FRAMEWORK_VENDOR %s", bundleContext
        .getProperty(Constants.FRAMEWORK_VENDOR));
    logInfo("FRAMEWORK_VERSION %s", bundleContext
        .getProperty(Constants.FRAMEWORK_VERSION));
    logInfo("FRAMEWORK_EXECUTIONENVIRONMENT %s", bundleContext
        .getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

    logInfo("!!!!!!! Listing bundles in integration test !!!!!!!");
    for (int i = 0; i < bundleContext.getBundles().length; i++) {
      logInfo("name: " + bundleContext.getBundles()[i].getSymbolicName());
    }
    logInfo("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  }

  public void testRequestDetails() {
    Log.info("TEST %s", MedicationTest.class, "**************** Entering test ***************");
    String[] userIDs = UserIDs.getAllIDs();
    for (int i = 0; i < userIDs.length; i++) {
      String userID = userIDs[i];
      Precaution precaution = MedicationConsumer.requestDetails(userID);
      Assert.notNull(precaution, "The precaution cannot be null");
      System.out.println("******************** START printing for USER ID : " + userID + " ****************************");
      printPrecaution(precaution);
      System.out.println("*********END printing for USER ID : " + userID + " ***************");
    }
    Log.info("TEST %s", MedicationTest.class, "**************** Leaving test ****************");
  }

  private void printPrecaution(Precaution precaution) {
    List sideeffect = precaution.getSideeffect();

    System.out.println("**************** START PRINTING SIDEEFFECT****************");
    for (int i = 0; i < sideeffect.size(); i++) {
      String se = (String) sideeffect.get(i);
      Log.info("%s", getClass(), se);
    }

    System.out.println("**************** END PRINTING SIDEEFFECT****************");

    List incompliance = precaution.getIncompliance();

    System.out.println("**************** START PRINTING INCOMPLIANCE****************");
    for (int i = 0; i < incompliance.size(); i++) {
      String inc = (String) incompliance.get(i);
      Log.info("%s", getClass(), inc);
    }

    System.out.println("**************** END PRINTING SIDEEFFECT****************");
  }


}