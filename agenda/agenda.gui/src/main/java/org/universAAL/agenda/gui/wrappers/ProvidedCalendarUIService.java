/**
 * 
 */
package org.universAAL.agenda.gui.wrappers;

//j2se packages
import org.universAAL.agenda.ont.service.CalendarUIService;

import java.util.Hashtable;

import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.middleware.service.owl.InitialServiceDialog;
import org.universAAL.middleware.owl.Restriction;

public class ProvidedCalendarUIService extends CalendarUIService {
	public static final String CALENDAR_UI_NAMESPACE = "http://ui.calendar.universAAL.org/CalendarUIService.owl#";
	public static final String MY_URI = CALENDAR_UI_NAMESPACE
			+ "CalendarUIService";

	static final String SERVICE_START_UI = CALENDAR_UI_NAMESPACE
			+ "startUserInterface2";

	static final String INPUT_USER = CALENDAR_UI_NAMESPACE + "iUser";

	private static final int PROVIDED_SERVICES = 1; // The number of services
													// provided by this class

	static final ServiceProfile[] profiles = new ServiceProfile[PROVIDED_SERVICES];
	private static Hashtable serverPEditorRestrictions = new Hashtable();

	static {
		register(ProvidedCalendarUIService.class);

		addRestriction(
				(Restriction) CalendarUIService.getClassRestrictionsOnProperty(
						CalendarUIService.PROP_CONTROLS).copy(),
				new String[] { CalendarUIService.PROP_CONTROLS },
				serverPEditorRestrictions);

		/**********************************************************************
		 * INPUT(S), OUTPUT(S), RESTRICTION(S), PROPERTY_PATH(S) DECLERATIONS *
		 **********************************************************************/

		/********************************************************************
		 * start of UI *
		 ********************************************************************/
		profiles[0] = InitialServiceDialog.createInitialDialogProfile(
				CalendarUIService.MY_URI, "http://www.anco.gr", "Calendar UI",
				SERVICE_START_UI);

		// /****************************************************************
		// * start of service profile 1: void createUserProfile(User user)*
		// ****************************************************************/
		// ProvidedProfileEditorService createUserProfile = new
		// ProvidedProfileEditorService(SERVICE_SHOW_EDITOR);
		// createUserProfile.addInputWithAddEffect(INPUT_USER, User.MY_URI, 1,
		// 1, new String[] {ProfilingEditorService.PROP_CONTROLS});
		// profiles[0] = createUserProfile.myProfile;

	}

	private ProvidedCalendarUIService(String uri) {
		super(uri);
	}

	protected Hashtable getClassLevelRestrictions() {
		return serverPEditorRestrictions;
	}

	public int getPropSerializationType(String propURI) {
		return PROP_SERIALIZATION_FULL;
	}

	public boolean isWellFormed() {
		return true;
	}
}