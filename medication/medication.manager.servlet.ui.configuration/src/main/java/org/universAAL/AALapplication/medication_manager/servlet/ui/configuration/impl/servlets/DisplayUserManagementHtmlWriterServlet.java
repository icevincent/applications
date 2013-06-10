package org.universAAL.AALapplication.medication_manager.servlet.ui.configuration.impl.servlets;

import org.universAAL.AALapplication.medication_manager.persistence.layer.PersistentService;
import org.universAAL.AALapplication.medication_manager.persistence.layer.entities.Person;
import org.universAAL.AALapplication.medication_manager.servlet.ui.base.export.helpers.Session;
import org.universAAL.AALapplication.medication_manager.servlet.ui.base.export.helpers.SessionTracking;
import org.universAAL.AALapplication.medication_manager.servlet.ui.configuration.impl.Log;
import org.universAAL.AALapplication.medication_manager.servlet.ui.configuration.impl.servlets.forms.UserManagementForm;
import org.universAAL.AALapplication.medication_manager.user.management.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.universAAL.AALapplication.medication_manager.servlet.ui.base.export.helpers.ServletUtil.*;
import static org.universAAL.AALapplication.medication_manager.servlet.ui.configuration.impl.Activator.*;
import static org.universAAL.AALapplication.medication_manager.servlet.ui.configuration.impl.Util.*;


/**
 * @author George Fournadjiev
 */
public final class DisplayUserManagementHtmlWriterServlet extends BaseHtmlWriterServlet {

  private final Object lock = new Object();

  private static final String USER_MANAGEMENT_FILE_NAME = "user_management.html";

  public DisplayUserManagementHtmlWriterServlet(SessionTracking sessionTracking) {
    super(USER_MANAGEMENT_FILE_NAME, sessionTracking);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    synchronized (lock) {
      try {
        Log.info("Called DisplayUserManagementHtmlWriterServlet", getClass());

        isServletSet(displayLoginHtmlWriterServlet, "displayLoginHtmlWriterServlet");
        isServletSet(displayErrorPageWriterServlet, "displayErrorPageWriterServlet");

        Session session = getSession(req, resp, getClass());
        Person admin = (Person) session.getAttribute(LOGGED_ADMIN);
        Log.info("Checking admin session attribute : %s", getClass(), admin);

        if (admin == null) {
          Log.info("admin is not set. Redirecting to the log page!", getClass());
          debugSessions(session.getId(), "if(admin is null) the servlet doGet/doPost method", getClass());
          displayLoginHtmlWriterServlet.doGet(req, resp);
          return;
        }

        Log.info("Trying to get PersistentService object and UserManager object", getClass());

        PersistentService persistentService = getPersistentService();
        UserManager userManager = getUserManager();

        Log.info("Calling handleResponse(...) method ", getClass());

        handleResponse(req, resp, persistentService, userManager, session);


      } catch (Exception e) {
        Log.error(e.fillInStackTrace(), "Unexpected Error occurred", getClass());
        sendErrorResponse(req, resp, e);
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }


  private void handleResponse(HttpServletRequest req, HttpServletResponse resp, PersistentService persistentService,
                              UserManager userManager, Session session) throws IOException {

    Log.info("Creating UserManagementForm object", getClass());
    UserManagementForm scriptForm = new UserManagementForm(persistentService, userManager, session);
    scriptForm.prepareData();
    sendResponse(req, resp, scriptForm);

  }


}
