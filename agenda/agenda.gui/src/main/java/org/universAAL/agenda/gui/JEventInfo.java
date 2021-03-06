package org.universAAL.agenda.gui;

import org.universAAL.ontology.agenda.Event;
import org.universAAL.ontology.agenda.EventDetails;
import org.universAAL.ontology.agenda.Reminder;
import org.universAAL.ontology.agenda.ReminderType;
import org.universAAL.ontology.agenda.TimeInterval;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.xml.datatype.XMLGregorianCalendar;

import org.universAAL.middleware.rdf.TypeMapper;

import org.universAAL.ontology.location.address.PhysicalAddress;

import org.universAAL.agenda.gui.components.DatePanel;
import org.universAAL.agenda.gui.components.ImagePanel;
import org.universAAL.agenda.gui.components.ReminderPanel;
import org.universAAL.agenda.gui.components.ReminderScreen;
import org.universAAL.agenda.gui.util.ButtonCreator;
import org.universAAL.agenda.gui.util.DateInstance;
import org.universAAL.agenda.gui.util.DateTimeInstance;
import org.universAAL.agenda.gui.util.DateUtilities;
import org.universAAL.agenda.gui.util.GuiConstants;

/**
 * 
 * Event info view. When adding new Event. Has calendar selection, start and end
 * dates, event type, description, place and reminder info sections
 * 
 */
public class JEventInfo implements IPersonaWindow, IEventInfoListener {
    public static final String CARD_NAME = "JEventInfoCard"; //$NON-NLS-1$
    private static final int UPDATE_EVENT = 1;
    private static final int SAVE_NEW_EVENT = 2;

    private int currentStatus; // update or save

    private JPanel mainScreen;
    private JPanel navScreen;
    private JPanel titleScreen;
    private JLabel headerMessage;

    private CalendarGUI parent;

    private boolean isEditable = false;
    private Event event;
    private JButton remove, save, mainb, back;
    
    final ImageIcon bigIcon = new ImageIcon(getClass().getResource(
	    "/icons/big_button.jpeg")); //$NON-NLS-1$
    final ImageIcon bigPressedIcon = new ImageIcon(getClass().getResource(
    "/icons/big_button_pressed.jpeg")); //$NON-NLS-1$

    // KS
    private JCalendar activeCalendarScreen = null;

    public void setActiveCalendarScreen(JCalendar activeCalendarScreen) {
	this.activeCalendarScreen = activeCalendarScreen;
    }

    private JEventList activeJEventListScreen = null;

    public void setActiveJEventListScreen(JEventList activeJEventListScreen) {
	this.activeJEventListScreen = activeJEventListScreen;
    }

    // KS

    public JEventInfo(CalendarGUI parent) {
	this.parent = parent;
	initComponents();
    }

    private void initComponents() {
	this.navScreen = createNavScreen();
	this.mainScreen = createMainScreen();
	this.titleScreen = createTitleScreen();

	JPanel mainPanel = new JPanel(new BorderLayout());
	JPanel centerPanel = new JPanel(new BorderLayout());
	centerPanel.add(this.titleScreen, BorderLayout.NORTH);
	centerPanel.add(this.mainScreen, BorderLayout.CENTER);
	mainPanel.add(centerPanel, BorderLayout.CENTER);
	mainPanel.add(this.navScreen, BorderLayout.EAST);
	this.parent.addNewCardFrame(mainPanel, CARD_NAME);
    }

    private JPanel createTitleScreen() {
	ImageIcon logo = new ImageIcon(getClass().getResource(
		"/icons/month_header.jpg")); //$NON-NLS-1$
	headerMessage = new JLabel();

	updateTitleScreen();

	headerMessage.setForeground(GuiConstants.headerMessageForeground);
	headerMessage.setFont(GuiConstants.headerFont); //$NON-NLS-1$

	JPanel headPanel = new ImagePanel(logo.getImage());
	headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	headPanel.setBackground(GuiConstants.headerPanelBackground);

	headPanel.add(headerMessage);

	// breadcrumbs
	JLabel l = new JLabel(Messages
		.getString("JEventInfo.Breadcrumb.Home.EventInfo"));
	l.setBackground(GuiConstants.breadcrumbsLabelColor);
	l.setFont(GuiConstants.breadcrumbsLabelFont);

	JPanel whole = new JPanel(new FlowLayout(FlowLayout.LEFT));
	whole.setBackground(GuiConstants.wholePanelBackground);
	whole.add(headPanel);
	whole.add(l);
	return whole;
    }

    public void updateTitleScreen() {
	int date[] = extractDate(this.event);
	if (date == null) {
	    headerMessage
		    .setText("<html><font face=\"MyriadPro\" size=\"7\" color=\"white\">" + //$NON-NLS-1$
			    Messages.getString("JEventInfo.AppointmentInfo") + //$NON-NLS-1$
			    "</font></html>"); //$NON-NLS-1$
	} else {
	    headerMessage
		    .setText("<html><font face=\"MyriadPro\" size=\"7\" color=\"white\">" + date[2] //$NON-NLS-1$
			    + "</font><font size=\"6\">" + DateUtilities.forEnglishLangReturnDayEnding("" + date[2]) //$NON-NLS-1$ //$NON-NLS-2$
			    + "</font><font size=\"7\"> " + DateUtilities.months[date[1]] + ", </font>" //$NON-NLS-1$ //$NON-NLS-2$
			    + "<font size=\"7\" color=\"white\">" + date[0] + "<p></font>" //$NON-NLS-1$ //$NON-NLS-2$
			    + "<font size=\"6\" color=\"#2f594f\">" + //$NON-NLS-1$
			    Messages.getString("JEventInfo.AppointmentInfo") + //$NON-NLS-1$
			    "</font></html>"); //$NON-NLS-1$
	}
    }

    private static int[] extractDate(Event event) {
	TimeInterval ti = null;
	try {
	    ti = event.getEventDetails().getTimeInterval();
	} catch (NullPointerException npe) {
	    return null;
	}
	if (ti == null)
	    return null;

	XMLGregorianCalendar time;
	time = ti.getStartTime();
	if (time == null)
	    time = ti.getEndTime();

	if (time == null)
	    return null;

	int[] date = { time.getYear(), time.getMonth() - 1, time.getDay() }; // -1:
	// convert
	// XML
	// ->
	// Calendar
	// repres.
	return date;

    }

    private JPanel createMainScreen() {
	JPanel main = mainScreen();

	JPanel whole = new JPanel(new BorderLayout());
	whole.setBackground(GuiConstants.wholePanelBackground);

	JLabel dummy1 = new JLabel("dummyTextTextText"); //$NON-NLS-1$
	dummy1.setBackground(GuiConstants.wholePanelBackground);
	dummy1.setForeground(GuiConstants.wholePanelBackground);
	dummy1.setOpaque(true);
	JLabel dummy2 = new JLabel("dummyTextTextText"); //$NON-NLS-1$
	dummy2.setBackground(GuiConstants.wholePanelBackground);
	dummy2.setOpaque(true);
	dummy2.setForeground(GuiConstants.wholePanelBackground);
	JLabel dummy3 = new JLabel("dummyTextText"); //$NON-NLS-1$
	dummy3.setBackground(GuiConstants.wholePanelBackground);
	dummy3.setOpaque(true);
	dummy3.setForeground(GuiConstants.wholePanelBackground);
	JLabel dummy4 = new JLabel("dummyText"); //$NON-NLS-1$
	dummy4.setBackground(GuiConstants.wholePanelBackground);
	dummy4.setOpaque(true);
	dummy4.setForeground(GuiConstants.wholePanelBackground);

	whole.add(dummy1, BorderLayout.EAST);
	whole.add(dummy2, BorderLayout.WEST);
	whole.add(dummy3, BorderLayout.SOUTH);
	whole.add(dummy4, BorderLayout.NORTH);
	whole.add(main, BorderLayout.CENTER);
	return whole;
    }

    private JPanel mainScreen() {
	GridBagLayout gl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	JPanel center = new JPanel(gl);
	center.setBackground(GuiConstants.wholePanelBackground);

	// TYPE ELEMENTS
	typeField = new JTextField();
	typeField.setBackground(GuiConstants.textActiveBackground);
	typeField.setForeground(GuiConstants.textActiveForeground);
	typeField.setHorizontalAlignment(JLabel.LEFT);
	typeField.setFont(GuiConstants.mediumFont); //$NON-NLS-1$
	typeField.setOpaque(true);
	typeField.setEditable(isEditable);

	// 1. Type panel
	typePanel = new JPanel(new GridLayout(1, 1));
	TitledBorder b = BorderFactory.createTitledBorder(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.EventType") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	b.setTitleFont(GuiConstants.jEventInfoSectionTitleFont); //$NON-NLS-1$
	b.setTitleColor(GuiConstants.labelColor);
	typePanel.setBorder(b);
	typePanel.setBackground(GuiConstants.wholePanelBackground);
	typePanel.add(typeField);

	// DATE ELEMENT
	// 2. Date Panel
	datePanel = new JPanel(new GridLayout(1, 2));
	datePanel.setBackground(GuiConstants.wholePanelBackground);
	// START DATE ELEMENT
	startTimeP = new DatePanel(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.StartDate") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	// END TIME ELEMENT
	endTimeP = new DatePanel(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.EndDate") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	datePanel.add(startTimeP);
	datePanel.add(endTimeP);

	// CALENDAR ELEMENTS
	List<org.universAAL.ontology.agenda.Calendar> calList = parent
		.getActiveCalendars();
	// org.universAAL.ontology.agenda.Calendar[] calArray = new
	// org.universAAL.ontology.agenda.Calendar[calList.size()];
	// calArray = calList.toArray(calArray);

	// calendarCombo = new JComboBox(calArray);
	calendarCombo = new JComboBox();

	updateCalendarComboBox(calList);
	calendarCombo.setFont(GuiConstants.mediumFont);
	calendarCombo.setBackground(GuiConstants.textActiveBackground);
	calendarCombo.setForeground(GuiConstants.textActiveForeground);
	calendarCombo.setOpaque(true);

	// 3. calendar panel
	calPanel = new JPanel(new GridLayout(1, 1));
	b = BorderFactory.createTitledBorder(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.Calendar") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	b.setTitleFont(GuiConstants.jEventInfoSectionTitleFont); //$NON-NLS-1$
	b.setTitleColor(GuiConstants.labelColor);
	calPanel.setBorder(b);
	calPanel.setBackground(GuiConstants.wholePanelBackground);
	calPanel.add(calendarCombo);

	// DESCRIPTION ELEMENT
	descField = new JTextField();
	descField.setHorizontalAlignment(JLabel.LEFT);
	descField.setFont(GuiConstants.mediumFont); //$NON-NLS-1$
	descField.setBackground(GuiConstants.textActiveBackground);
	descField.setForeground(GuiConstants.textActiveForeground);
	descField.setOpaque(true);
	descField.setEditable(isEditable);

	// 4. description panel
	descPanel = new JPanel(new GridLayout(1, 1));
	b = BorderFactory.createTitledBorder(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.Description") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	b.setTitleFont(GuiConstants.jEventInfoSectionTitleFont); //$NON-NLS-1$
	b.setTitleColor(GuiConstants.labelColor);
	descPanel.setBorder(b);
	descPanel.setBackground(GuiConstants.wholePanelBackground);
	descPanel.add(descField);

	// PLACE ELEMENT
	placeField = new JTextField();
	placeField.setHorizontalAlignment(JLabel.LEFT);
	placeField.setFont(GuiConstants.mediumFont); //$NON-NLS-1$
	placeField.setBackground(GuiConstants.textActiveBackground);
	placeField.setForeground(GuiConstants.textActiveForeground);
	placeField.setOpaque(true);
	placeField.setEditable(isEditable);

	// 5. place panel
	placePanel = new JPanel(new GridLayout(1, 1));
	b = BorderFactory.createTitledBorder(" " + //$NON-NLS-1$
		Messages.getString("JEventInfo.Place") + //$NON-NLS-1$
		" "); //$NON-NLS-1$
	b.setTitleFont(GuiConstants.jEventInfoSectionTitleFont); //$NON-NLS-1$
	b.setTitleColor(GuiConstants.labelColor);
	b.setTitle(Messages.getString("JEventInfo.Place")); //$NON-NLS-1$
	placePanel.setBorder(b);
	placePanel.setBackground(GuiConstants.wholePanelBackground);
	placePanel.add(placeField);

	// REMINDER ELEMENT
	remPanel = new ReminderPanel(this);

	remScreen = new ReminderScreen(remPanel);
	remScreen.setVisible(false);

	setGridBagConstraints(gbc, 0, 0, 2, 7, 1, 1,
		GridBagConstraints.FIRST_LINE_START);
	gbc.fill = GridBagConstraints.BOTH;
	center.add(remScreen, gbc);
	setGridBagConstraints(gbc, 0, 1, 1, 1, 0.5, 0.3,
		GridBagConstraints.LINE_START);
	center.add(startTimeP, gbc);
	setGridBagConstraints(gbc, 1, 1, 1, 1, 0.5, 0.3,
		GridBagConstraints.LINE_END);
	center.add(endTimeP, gbc);
	setGridBagConstraints(gbc, 0, 0, 2, 1, 1, 0.1,
		GridBagConstraints.LINE_START);
	gbc.fill = GridBagConstraints.HORIZONTAL;
	center.add(calPanel, gbc);
	setGridBagConstraints(gbc, 0, 2, 2, 1, 1, 0.1,
		GridBagConstraints.LINE_START);
	center.add(typePanel, gbc);
	setGridBagConstraints(gbc, 0, 4, 2, 1, 1, 0.1,
		GridBagConstraints.LINE_START);
	center.add(descPanel, gbc);
	setGridBagConstraints(gbc, 0, 6, 2, 1, 1, 0.1,
		GridBagConstraints.LINE_START);
	center.add(placePanel, gbc);
	setGridBagConstraints(gbc, 0, 7, 2, 1, 1, 0.1,
		GridBagConstraints.LINE_START);
	center.add(remPanel, gbc);
	return center;
    }

    private JPanel createNavScreen() {
	back =ButtonCreator.createButton(
		bigIcon, Messages.getString("JEventInfo.Back"),
		GuiConstants.bigButtonsBigFont); 
	back.setFocusPainted(false);
	back.setBorderPainted(false);
	back.setContentAreaFilled(false);
	back.setPressedIcon(bigPressedIcon);
	back.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent a) {

		// ks
		// int[] datePressed = this.extractDate(this.event);
		int year = activeJEventListScreen.getSelectedDate().year;
		int month = activeJEventListScreen.getSelectedDate().month;
		int day = activeJEventListScreen.getSelectedDate().day;

		List<Event> events = parent.getEvents(year, (month - 1), day);
		activeJEventListScreen.updateMainScreen(events);
		activeJEventListScreen.updateTitleScreen(events.size());
		System.out.println("*** Events Size: " + events.size());
		// ks

		closeReminderInfoScreen();
		parent.showScreen(JEventList.CARD_NAME);
	    }
	});

	save =ButtonCreator.createButton(
		bigIcon, Messages.getString("JEventInfo.Edit"),
		GuiConstants.bigButtonsBigFont); 
	save.setFocusPainted(false);
	save.setBorderPainted(false);
	save.setContentAreaFilled(false);
	save.setPressedIcon(bigPressedIcon);
	save.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent a) {
		closeReminderInfoScreen();
		isEditable = !isEditable;
		setEditableForm(isEditable);
		if (isEditable) {
		    // enable field to edit them
		    // currentStatus = UPDATE_EVENT;
		    storeCurrentValues();
		    save.setText(Messages.getString("JEventInfo.Save"));
		} else {
		    // disable field and save new values
		    updateOrSaveEvent();
		    save.setText(Messages.getString("JEventInfo.Edit"));
		    currentStatus = UPDATE_EVENT;
		}
	    }
	});

	remove =ButtonCreator.createButton(
		bigIcon, Messages.getString("JEventInfo.Delete"),
		GuiConstants.bigButtonsBigFont); 
	remove.setToolTipText(Messages.getString("JEventInfo.RemoveThisEvent")); //$NON-NLS-1$
	remove.setFocusPainted(false);
	remove.setBorderPainted(false);
	remove.setContentAreaFilled(false);
	remove.setPressedIcon(bigPressedIcon);
	remove.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent a) {
		closeReminderInfoScreen();
		// remove the event from db
		// go back to calendar
		if (event != null)
		    parent.removeEvent(event.getParentCalendar(), event
			    .getEventID());

		parent.showScreen(JCalendar.CARD_NAME);
	    }
	});
	mainb =ButtonCreator.createButton(
		bigIcon, Messages.getString("JEventInfo.Main"),
		GuiConstants.bigButtonsBigFont);  
	mainb.setFocusPainted(false);
	mainb.setBorderPainted(false);
	mainb.setContentAreaFilled(false);
	mainb.setPressedIcon(bigPressedIcon);
	mainb.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent a) {
		activeCalendarScreen
			.populateCalendar(activeCalendarScreen.calendar);
		closeReminderInfoScreen();
		parent.showScreen(JCalendar.CARD_NAME);
	    }
	});

	// Create the right zone of buttons
	JPanel home = new JPanel(new GridLayout(6, 1));
	home.setBackground(GuiConstants.wholePanelBackground);

	// the necessary buttons are included
	// before clock added
	// home.add(new JLabel()); //<<Create a void combined with
	// nav.setLayout(new GridLayout(6, 1));

	AnalogClock clock = new AnalogClock();

	home.add(clock);
	home.add(remove);
	home.add(back);
	home.add(save);
	home.add(mainb);
	home.add(CurrentDateAndDigitalClock.getDateAndDigitalClockPanel());

	return home;
    }

    public JPanel getMainScreenPanel() {
	return this.mainScreen;
    }

    public JPanel getNavigationScreenPanel() {
	return this.navScreen;
    }

    public JPanel getTitleScreenPanel() {
	return this.titleScreen;
    }

    private void initEventEditProcedure(boolean editMode) {
	clearFields();
	storeCurrentValues();
	isEditable = editMode;
	if (editMode) {
	    // currentStatus = UPDATE_EVENT;
	    save.setText(Messages.getString("JEventInfo.Save"));
	} else {
	    // currentStatus = SAVE_NEW_EVENT;
	    save.setText(Messages.getString("JEventInfo.Edit"));
	}

	// ks
	if (currentStatus == SAVE_NEW_EVENT) {

	}
	// ks

    }

    public void setStartTime(DateInstance date) {
	System.out.println("DAY: " + date.day); //$NON-NLS-1$
	System.out.println("MONTH: " + date.month); //$NON-NLS-1$
	System.out.println("YEAR: " + date.year); //$NON-NLS-1$
	startTimeP.setDate(date);
    }

    private void updateCalendarComboBox(
	    List<org.universAAL.ontology.agenda.Calendar> calendars) {
	System.out.println("Combo size1: " + calendarCombo.getItemCount());
	calendarCombo.removeAllItems();
	System.out.println("Combo size2: " + calendarCombo.getItemCount());
	for (org.universAAL.ontology.agenda.Calendar calendar : calendars) {
	    calendarCombo.addItem(calendar);
	    System.out.println("Combo size3: " + calendarCombo.getItemCount());
	}
    }

    // edited. 15/5
    public void populateData(Event event, boolean editMode) {
	this.event = event;

	if (event == null) {
	    // do not set any value
	    // run this for new event
	    editMode = true;
	    currentStatus = SAVE_NEW_EVENT;
	    initEventEditProcedure(editMode);
	    setEditableForm(editMode);

	    // populateReminderWithDefaultValues(event,
	    // startTimeP.getDateTime());
	    return;
	}

	currentStatus = UPDATE_EVENT;
	initEventEditProcedure(editMode);
	setEditableForm(editMode);

	updateCalendarComboBox(parent.getActiveCalendars());
	org.universAAL.ontology.agenda.Calendar parentCal = event
		.getParentCalendar();
	System.out.println("Parent Calendar: " + parentCal.getName());
	calendarCombo.setSelectedItem(parentCal);
	System.out.println("Set Parent Calendar: "
		+ ((org.universAAL.ontology.agenda.Calendar) calendarCombo
			.getSelectedItem()).getName());

	if (event.getEventDetails() != null) {
	    String s = event.getEventDetails().getCategory();
	    if (s != null) {
		typeField.setText(s);
	    }

	    s = event.getEventDetails().getDescription();
	    if (s != null) {
		descField.setText(s);
	    }

	    s = event.getEventDetails().getPlaceName();
	    if (s != null) {
		placeField.setText(s);
	    }

	    TimeInterval ti = event.getEventDetails().getTimeInterval();
	    if (ti != null) {
		XMLGregorianCalendar time = ti.getStartTime();
		// update event start date panel
		if (time != null) {
		    Calendar c = (Calendar) time.toGregorianCalendar();
		    DateTimeInstance dti = DateUtilities.calendar2dti(c);
		    startTimeP.setDateTime(dti);
		}
		time = ti.getEndTime();
		// update event end date panel
		if (time != null) {
		    Calendar c = (Calendar) time.toGregorianCalendar();
		    DateTimeInstance dti = DateUtilities.calendar2dti(c);
		    endTimeP.setDateTime(dti);
		}
	    }
	}

	Reminder r = event.getReminder();
	if (r != null) {
	    ReminderType rt = r.getReminderType();
	    if ((rt != null) && (rt.ord() != 0)) {
		// set selected index for Reminder Screen
		remScreen.setReminderType(rt.ord());
		remPanel.setReminderType(rt.toString());
		remPanel.setActiveReminder();
		// //
	    } else {
		// inactive Reminder
		remScreen.setEditable(false);
		remPanel.setInactiveReminder();
	    }
	    XMLGregorianCalendar cal = r.getReminderTime();
	    if (cal != null) {
		// update event reminder time panel
		Calendar c = (Calendar) cal.toGregorianCalendar();
		remPanel.setReminderDate(c);
		remScreen.setReminderDate(c);
	    } else {
		remScreen.setReminderDate((DateTimeInstance) null);
	    }

	    // update reminder message panel
	    remScreen.setReminderMessage(r.getMessage());
	    remScreen.setInterval(r.getRepeatInterval());
	    remScreen.setRepeatTimes(r.getTimesToBeTriggered());
	} else { // Reminder == null
	    remScreen.setEditable(false);
	    remPanel.setInactiveReminder();
	    remScreen.setReminderType(0);
	    // populateReminderWithDefaultValues(event,
	    // startTimeP.getDateTime());
	}
	remScreen.storeAllCurrentValues();
    }

    // K.S
    private void populateReminderWithDefaultValues(DateTimeInstance dt) {
	if (currentStatus == SAVE_NEW_EVENT) {

	    System.out
		    .println(">>>> **** Setting default values in reminder screen & panel");
	    if (dt != null) {

		Calendar c = (Calendar) DateUtilities.dti2calendar(dt);
		c.add(Calendar.HOUR, -1);
		c.add(Calendar.MONTH, +1);
		// update event reminder time panel
		remPanel.setReminderDate(c);
		remScreen.setReminderDate(c);
		remScreen.setReminderType(ReminderType.VISUAL_MESSAGE);
		remScreen.storeReminderMessage(" ");
		// remScreen.storeAllCurrentValues();

	    }//
	}// currentStatus

    }

    // edited. 15/5
    private void clearFields() {
	System.out.println(">>> Combo reset: 0");
	calendarCombo.setSelectedIndex(0);
	startTimeP.clearFields();
	endTimeP.clearFields();
	typeField.setText(""); //$NON-NLS-1$
	descField.setText(""); //$NON-NLS-1$
	placeField.setText(""); //$NON-NLS-1$
	remScreen.clearFields();
	remPanel.setInactiveReminder();
    }

    // edited. 15/5
    private void setEditableForm(boolean isEditable) {
	System.out.println(">>> Combo value (before): "
		+ calendarCombo.getSelectedIndex());
	calendarCombo.setEnabled(isEditable);
	System.out.println(">>> Combo value (after): "
		+ calendarCombo.getSelectedIndex());
	startTimeP.setEditable(isEditable);
	endTimeP.setEditable(isEditable);
	typeField.setEditable(isEditable);
	descField.setEditable(isEditable);
	placeField.setEditable(isEditable);
	if (remScreen.getReminderType() != 0)
	    remScreen.setEditable(isEditable);
	remPanel.setEnabled(isEditable);
    }

    // edited. 15/5
    private void storeCurrentValues() {
	remScreen.storeAllCurrentValues();
	oldCalendarComboValue = calendarCombo.getSelectedIndex();
	System.out.println(">>> Combo old value: " + oldCalendarComboValue);
	oldStartDTI = startTimeP.getDateTime();
	oldEndDTI = endTimeP.getDateTime();
	oldTypeFieldValue = typeField.getText();
	oldDescFieldValue = descField.getText();
	oldPlaceFieldValue = placeField.getText();
	oldRemActive = remScreen.isReminderActive();
	//System.err.println("REM STATUS: " + oldRemActive); //$NON-NLS-1$
	oldRemDTI = remScreen.getDateTime();
	oldRemType = remScreen.getReminderType();
	oldRemMessage = remScreen.getReminderMessage();
	oldRemInterval = remScreen.getInterval();
	oldRemRepeatTimes = remScreen.getRepeatTimes();
    }

    private void updateOrSaveEvent() {

	Event e = this.event;
	Reminder rm;
	PhysicalAddress ad;
	EventDetails ed;
	TimeInterval ti;

	if (event == null) {
	    e = initialiseUserEvent();
	} else {
	    if (event.getEventID() != -1) {
		//System.err.println("xxx:Event id: " + event.getEventID()); //$NON-NLS-1$
		e.setEventID(event.getEventID());
	    }
	}
	rm = e.getReminder();
	ed = e.getEventDetails();
	if (ed != null) {
	    ti = ed.getTimeInterval();
	    ad = ed.getAddress();
	    if (ad == null)
		ad = new PhysicalAddress();
	} else {
	    ed = new EventDetails();
	    ti = new TimeInterval();
	    ad = new PhysicalAddress();
	}
	if (ti == null) {
	    ti = new TimeInterval();
	}
	if (rm == null) {
	    rm = new Reminder();
	}

	boolean entryUpdated = false;

	org.universAAL.ontology.agenda.Calendar calendar = null;
	System.out.println(">>> Combo current value: "
		+ calendarCombo.getSelectedIndex());
	if (currentStatus == SAVE_NEW_EVENT
		|| (oldCalendarComboValue != calendarCombo.getSelectedIndex())) {
	    calendar = (org.universAAL.ontology.agenda.Calendar) calendarCombo
		    .getSelectedItem();
	    e.setParentCalendar(calendar.shallowCopy());
	    System.out
		    .println(">>>Combo parent changed: " + calendar.getName());
	    entryUpdated = true;
	} else {
	    calendar = (org.universAAL.ontology.agenda.Calendar) calendarCombo
		    .getSelectedItem();
	    e.setParentCalendar(calendar.shallowCopy());
	}

	DateTimeInstance newST = startTimeP.getDateTime();
	if ((newST != null && !(newST.equals(oldStartDTI)))
		|| ((newST == null) && oldStartDTI != null)) {
	    Calendar cs = startTimeP.getDate();
	    ti.setStartTime(TypeMapper.getDataTypeFactory()
		    .newXMLGregorianCalendar((GregorianCalendar) cs));
	    entryUpdated = true;
	}

	DateTimeInstance newET = endTimeP.getDateTime();
	if ((newET != null && !(newET.equals(oldEndDTI)))
		|| ((newET == null) && oldEndDTI != null)) {
	    Calendar cs = endTimeP.getDate();
	    ti.setEndTime(TypeMapper.getDataTypeFactory()
		    .newXMLGregorianCalendar((GregorianCalendar) cs));
	    entryUpdated = true;
	}

	if (!(oldTypeFieldValue.equals(typeField.getText()))) {
	    ed.setCategory(typeField.getText());
	    entryUpdated = true;
	}

	if (!(oldDescFieldValue.equals(descField.getText()))) {
	    ed.setDescription(descField.getText());
	    entryUpdated = true;
	}

	if (!(oldPlaceFieldValue.equals(placeField.getText()))) {
	    ed.setPlaceName(placeField.getText());
	    entryUpdated = true;
	}

	if (remScreen.isReminderActive() != oldRemActive) {
	    if (remScreen.isReminderActive()) {
		entryUpdated = true;
		Calendar c1 = remScreen.getDate();
		if (c1 != null) {
		    rm.setReminderTime(TypeMapper.getDataTypeFactory()
			    .newXMLGregorianCalendar(
				    (GregorianCalendar) remScreen.getDate()));
		}
		rm.setMessage(remScreen.getReminderMessage());
		rm.setReminderType(ReminderType
			.getReminderTypeByOrder(remScreen.getReminderType()));
		rm.setRepeatInterval(remScreen.getInterval());
		rm.setTimesToBeTriggered(remScreen.getRepeatTimes());
	    } else {
		rm = new Reminder();
	    }
	    entryUpdated = true;
	} else {
	    if (!remScreen.isReminderActive()) {
		rm = new Reminder();
	    } else {
		DateTimeInstance newRT = remScreen.getDateTime();
		if ((newRT != null && !(newRT.equals(oldRemDTI)))
			|| ((newRT == null) && oldRemDTI != null)) {
		    rm.setReminderTime(TypeMapper.getDataTypeFactory()
			    .newXMLGregorianCalendar(
				    (GregorianCalendar) remScreen.getDate()));
		    entryUpdated = true;
		}

		if (!(oldRemMessage.equals(remScreen.getReminderMessage()))) {
		    rm.setMessage(remScreen.getReminderMessage());
		    entryUpdated = true;
		}

		if (oldRemType != remScreen.getReminderType()) {
		    rm
			    .setReminderType(ReminderType
				    .getReminderTypeByOrder(remScreen
					    .getReminderType()));
		    entryUpdated = true;
		}

		if (oldRemInterval != remScreen.getInterval()) {
		    rm.setRepeatInterval(remScreen.getInterval());
		    entryUpdated = true;
		}

		if (oldRemRepeatTimes != remScreen.getRepeatTimes()) {
		    rm.setTimesToBeTriggered(remScreen.getRepeatTimes());
		    entryUpdated = true;
		}
	    }
	}

	ed.setTimeInterval(ti);
	e.setEventDetails(ed);
	e.setReminder(rm);
	if (entryUpdated)
	    if (currentStatus == JEventInfo.UPDATE_EVENT) {
		parent.updateEvent(e);
		this.event = e;
	    } else {
		int eID = parent.saveNewEvent(calendar, e);
		e.setEventID(eID);
		this.event = e;
	    }
    }

    private void setGridBagConstraints(GridBagConstraints c, int gridx,
	    int gridy, int gridwidth, int gridheight, double weightx,
	    double weighty, int anchor) {
	c.gridx = gridx;
	c.gridy = gridy;
	c.gridwidth = gridwidth;
	c.gridheight = gridheight;
	c.weightx = weightx;
	c.weighty = weighty;
	c.anchor = anchor;
    }

    private Event initialiseUserEvent() {
	Event e = new Event();
	e.setEventID(-1);
	Reminder rm = new Reminder();
	rm.setTimesToBeTriggered(0);
	rm.setMessage(Messages.getString("JEventInfo.Reminder")); //$NON-NLS-1$
	rm.setReminderType(ReminderType.visualMessage);
	PhysicalAddress ad = new PhysicalAddress();
	EventDetails ed = new EventDetails();
	ed.setSpokenLanguage(Locale.getDefault().getISO3Language());
	TimeInterval ti = new TimeInterval();
	ed.setAddress(ad);
	ed.setTimeInterval(ti);
	e.setEventDetails(ed);
	e.setReminder(rm);
	return e;
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() instanceof JButton) {
	    populateReminderWithDefaultValues(startTimeP.getDateTime());
	    setReminderScreenVisible(true);
	}
    }

    private void setReminderScreenVisible(boolean isVisible) {
	remScreen.setVisible(isVisible);
	calPanel.setVisible(!isVisible);
	datePanel.setVisible(!isVisible);
	typePanel.setVisible(!isVisible);
	placePanel.setVisible(!isVisible);
	descPanel.setVisible(!isVisible);
	startTimeP.setVisible(!isVisible);
	endTimeP.setVisible(!isVisible);
    }

    public void closeReminderInfoScreen() {
	setReminderScreenVisible(false);

    }

    public void showReminderInfoScreen() {
	setReminderScreenVisible(true);
    }

    private ReminderScreen remScreen;
    private ReminderPanel remPanel;
    private JTextField typeField, descField, placeField;
    private DatePanel startTimeP, endTimeP;
    private JComboBox calendarCombo;
    private String oldTypeFieldValue, oldDescFieldValue, oldPlaceFieldValue;
    private int oldCalendarComboValue;
    private DateTimeInstance oldStartDTI, oldEndDTI, oldRemDTI;
    private int oldRemType, oldRemInterval, oldRemRepeatTimes;
    private String oldRemMessage;
    private boolean oldRemActive;

    private JPanel calPanel, datePanel, typePanel, placePanel, descPanel;

}
