package org.universAAL.eventSelectionTool.server.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.universAAL.agenda.ont.EventDetails;
import org.universAAL.agenda.ont.Reminder;
import org.universAAL.agenda.ont.ReminderType;
import org.universAAL.agenda.ont.TimeInterval;
import org.universAAL.agenda.ont.Event;
import org.universAAL.agenda.ont.Calendar;
import org.universAAL.eventSelectionTool.database.EventSelectionToolDBInterface;
import org.universAAL.eventSelectionTool.ont.EventComparator;
import org.universAAL.eventSelectionTool.ont.EventSelectionTool;
import org.universAAL.eventSelectionTool.ont.FilterParams;
import org.universAAL.eventSelectionTool.ont.TimeSearchType;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.ontology.location.address.PhysicalAddress;
import org.universAAL.ontology.location.address.Address;
import org.universAAL.ontology.location.outdoor.Country;
import org.universAAL.ontology.location.outdoor.Region;

public class MyEventSelectionTool implements EventSelectionToolDBInterface {
	private String DB_URL;
	private String DB_USER;
	private String DB_PWD;
	//private ArrayList listeners;
	private EventSelectionTool esTool;
	

	private Connection conn;
	private Object theLock;

	public MyEventSelectionTool(String url, String user, String pwd) {
		this.DB_URL = url;
		this.DB_USER = user;
		this.DB_PWD = pwd;
		this.theLock = new Object();
		this.esTool = new EventSelectionTool(EventSelectionTool.MY_URI + "1");
		connect();
	}

	public Object getLock() {
		return theLock;
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
			initDB();
			System.err.println("OK THERE");
		} catch (Exception e) {
			System.out.println("Exception trying to get connection to database: " + e);
		}
	}

	private void initDB() throws SQLException {

	}

	public void disconnect() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Exception trying to close connection to database: " + e);
		}
	}

	/******************************
	 * database queries
	 ******************************/
	
	// The following function is just for test; creates events
	private Event createEvent(int id, String category, String spokenLanguage, String place, String description,
			XMLGregorianCalendar BeginxmlGregorianCalendar, XMLGregorianCalendar EndxmlGregorianCalendar) {

		// start Event Details
		EventDetails ed = new EventDetails(EventDetails.MY_URI + "Detail" + id);
		ed.setCategory(category);
		ed.setSpokenLanguage(spokenLanguage);
		ed.setPlaceName(place);
		ed.setDescription(description);

		TimeInterval timeInterval = new TimeInterval(TimeInterval.MY_URI + id);
		timeInterval.setStartTime(BeginxmlGregorianCalendar);
		timeInterval.setEndTime(EndxmlGregorianCalendar);
		ed.setTimeInterval(timeInterval);

		Event event = new Event(Event.MY_URI + "DummyEvent" + id);
		event.setEventDetails(ed);

		return event;
	}


	/**
	 * Returns a list of events which are stored in db, no matter
	 * in what calendar may belong to. 
	 * Events are returned in chronological order (older to newer)
	 */
	public List requestEvents(FilterParams filterParams) {
		esTool.clearPreviousData();
		esTool.setFilterParams(filterParams);
		esTool.setCalendars(new ArrayList(0));
		
		return genericEventRequest();	
	}
	
	
	//INFO: db call
	private List sqlRequestEvents(String query) {
		List allEvents = new ArrayList();
		try {
			PreparedStatement ps;
			System.out.println("Filter query: " + query);
			ps = conn.prepareStatement(query);
			//auto-commit enabled
			ResultSet result = ps.executeQuery();
			
			while (result.next()) {
				int eID = result.getInt(1);
				String eCategory = result.getString(2);
				String ePlace = result.getString(3);
				String eSL = result.getString(4);
				long eStartTime = result.getLong(5);
				long eEndTime = result.getLong(6);
				String eDescription = result.getString(7);
				String rMessage = result.getString(8);
				long rTime = result.getLong(9);
				int rRepeat = result.getInt(10);
				int rType = result.getInt(11);
				String aCountry = result.getString(12);
				String aExtAddress = result.getString(13);
				String aLocality = result.getString(14);
				String aPostalCode = result.getString(15);
				String aRegion = result.getString(16);
				String aStreet = result.getString(17);
				String aBuilding = result.getString(18);
				int parentCalendarID = result.getInt(19); //event->hasParentCalendar
				String parentCalendarName = result.getString(20);
				int rInterval = result.getInt(21); //reminder->interval
				boolean isVisible = result.getBoolean(22);
				boolean isPersistent = result.getBoolean(23);
				Event e = new Event(Event.MY_URI + eID);
				e.setEventID(eID);
				e.setParentCalendar(new Calendar(Calendar.MY_URI + parentCalendarID, parentCalendarName));
				e.setVisible(isVisible);
				e.setPersistent(isPersistent);

				EventDetails ed = new EventDetails(EventDetails.MY_URI + eID);
				boolean storeEDobject = false;
				if (eCategory != null) {
					ed.setCategory(eCategory);
					storeEDobject = true;
				}
				if (eDescription != null) {
					ed.setDescription(eDescription);
					storeEDobject = true;
				}
				if (ePlace != null) {
					ed.setPlaceName(ePlace);
					storeEDobject = true;
				}
				if (eSL != null) {
					ed.setSpokenLanguage(eSL);
					storeEDobject = true;
				}

				TimeInterval ti = new TimeInterval(TimeInterval.MY_URI + eID);
				boolean storeTIobject = false;
				if (eStartTime != 0) {
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTimeInMillis(eStartTime);
					ti.setStartTime(TypeMapper.getDataTypeFactory().newXMLGregorianCalendar(gc));
					storeTIobject = true;
				}
				if (eEndTime != 0) {
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTimeInMillis(eEndTime);
					ti.setEndTime(TypeMapper.getDataTypeFactory().newXMLGregorianCalendar(gc));
					storeTIobject = true;
				}
				if (storeTIobject) {
					ed.setTimeInterval(ti);
					storeEDobject = true;
				}

				if (aLocality != null) {
					Address address = new Address(Address.MY_URI);

					address.setExtAddress(aStreet);
					address.setPostalCode(aPostalCode);
					address.setRegion(new Region(Region.MY_URI+aRegion));
					address.setCountry(new Country(Country.MY_URI+aCountry));

					ed.setAddress(address);
				}

				boolean storeReminderObject = false;
				Reminder reminder = new Reminder(Reminder.MY_URI + eID);
				if (rTime != 0) {
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTimeInMillis(rTime);
					reminder.setReminderTime(TypeMapper.getDataTypeFactory().newXMLGregorianCalendar(gc));
					storeReminderObject = true;
					reminder.setTimesToBeTriggered(rRepeat);
					reminder.setReminderType(ReminderType.getReminderTypeByOrder(rType));
					reminder.setRepeatInterval(rInterval);
				}
				if (rMessage != null) {
					reminder.setMessage(rMessage);
					storeReminderObject = true;
				}

				if (storeEDobject) {
					e.setEventDetails(ed);
				}
				if (storeReminderObject) {
					e.setReminder(reminder);
				}
				allEvents.add(e);
			}
			System.err.println("MyEventSelectionTool: list size = " + allEvents.size());
			Collections.sort(allEvents, new EventComparator());
			return allEvents;
		} catch (NumberFormatException nfe) {
			// Activator.log.log(LogService.LOG_ERROR, nfe.getMessage());
			System.err.println("12");
			return new ArrayList(0);
		} catch (SQLException sqle) {
			// Activator.log.log(LogService.LOG_ERROR, sqle.getMessage());
			System.err.println("11");
			return new ArrayList(0);
		} catch (Exception e) {
			// Activator.log.log(LogService.LOG_ERROR, e.getMessage());
			System.err.println("10");
			return new ArrayList(0);
		}
	}

	//INFO: db call
	/**
	 * Returns a list of events which are stored in db, belong to
	 * any of the calendars in <code>calendarList</code> and their start time are after now().
	 * Maximum size of event list is <code>maxEventNo</code>. Additional events are dropped
	 * Events are returned in chronological order (older to newer) 
	 */
	public List requestFollowingEvents(List calendarList, int maxEventNo) {
		esTool.clearPreviousData();
		esTool.setFilterParams(EventSelectionTool.buildCurrentTimeFilter());
		esTool.setCalendars(calendarList);

		List l = genericEventRequest();
		if (l.size() > maxEventNo) 
			return l.subList(0, maxEventNo);
		
		return l;
	}
	
	//INFO: db call - generic call
	//with new feature event.hasParentCalendar()
	private List genericEventRequest() {
		String getAllEvents = "SELECT e.eventID, e.eCategory, e.ePlace, e.eSpokenLang, e.eDbStartTime, e.eDbEndTime, e.eDescription, " //1-7
			+ "e.rMessage, e.rDbTime, e.rRepeatTimes, e.rReminderType, " //8 - 11
			+ "e.aCountryName1, e.aExtAddress, e.aLocality, e.aPostalCode, e.aRegion, e.aStreetName, e.aBuilding, e.parentID, c.name, e.rInterval, e.isVisible, e.isPersistent " //12 - 22
			+ "FROM extendedEvent e, calendar c "
			+ "WHERE e.parentID = c.calendarID AND " 
			+ createCalendarFilter("e") + " AND " 
			+ createFilterClauseInWhere("e");
	
//		System.err.println("------------------------");
//		System.err.println(getAllEvents);
//		System.err.println("------------------------");
		return sqlRequestEvents(getAllEvents);
	}

	//INFO: db call
	private String createCalendarFilter(String tableName) {
		StringBuffer sb = new StringBuffer(20);
		List cals = esTool.getCalendars();
		if ((cals == null || cals.size() == 0))
			return "(true)";
		
		boolean hasCalendars = false;
		sb.append("(");
		for (Iterator it = cals.iterator(); it.hasNext();) {
			Calendar c = (Calendar) it.next();
			int i = extractIdFromURI(c.getURI());
			if (i > 0) {
				sb.append(tableName + ".parentID = " + i + " OR ");
				hasCalendars = true;
			}
		}
		if (hasCalendars)
			sb.append("false)");
		else
			sb.append("true)");	
		
		return sb.toString();
	}
	
	//INFO: new
	private static int extractIdFromURI(String calendarURI) {
		int id = -1;
		try {
			id = new Integer(calendarURI.substring(Calendar.MY_URI.length())).intValue();
		} catch (NumberFormatException nfe) {
			return -1;
		}
		return id;
	}
	
	/**
	 * Returns a list of events which are stored in db and belong to
	 * any of the calendars in <code>calendarList</code>.
	 * Events are returned in chronological order (older to newer) 
	 */
	public List requestFromCalendarEvents(FilterParams filterParams, List calendarList) {
		esTool.clearPreviousData();
		esTool.setFilterParams(filterParams);
		esTool.setCalendars(calendarList);

		return genericEventRequest();
	}

	//INFO: db call
	/**
	 * Returns a list of events which are stored in db, belong to
	 * any of the calendars in <code>calendarList</code>.
	 * Maximum size of event list is <code>maxEventNo</code>. Additional events are dropped
	 * Events are returned in chronological order (older to newer) 
	 */
	public List requestFromCalendarLimitedEvents(FilterParams filterParams, List calendarList, int maxEventNo) {
		esTool.clearPreviousData();
		esTool.setFilterParams(filterParams);
		esTool.setCalendars(calendarList);

		List l = genericEventRequest();
		if (l.size() > maxEventNo)
			return l.subList(0, maxEventNo);
		
		return l;
	}

	//INFO: new
	private String createFilterClauseInWhere(String tableName) {
		FilterParams fp = esTool.getFilterParams();
		StringBuffer query = new StringBuffer(30);
		// boolean hasWhereClause = false;
		boolean hasPrevious = false;
		// query.append("WHERE ");
		if (fp.getCategory() != null) {
			if (hasPrevious) {
				query.append("AND ");
			}
			query.append(tableName + ".eCategory LIKE '%" + fp.getCategory() + "%' ");
			hasPrevious = true;
		}

		if (fp.getSpokenLanguage() != null) {
			if (hasPrevious) {
				query.append("AND ");
			}
			query.append(tableName + ".eSpokenLang = '" + fp.getSpokenLanguage() + "' ");
			hasPrevious = true;
		}

		if (fp.getDescription() != null) {
			if (hasPrevious) {
				query.append("AND ");
			}
			query.append(tableName + ".eDescription LIKE '%" + fp.getDescription() + "%' ");
			hasPrevious = true;
		}

		if (fp.getTimeSearchType() != null) {
			if (hasPrevious) {
				query.append("AND ");
			}
			long start = 0;
			long end = 0;
			if(fp.getDTbegin() != null)
				start = fp.getDTbegin().toGregorianCalendar().getTimeInMillis();
			if (fp.getDTend() != null)
				end = fp.getDTend().toGregorianCalendar().getTimeInMillis();
			
			query.append(createDateClause(start, end, fp.getTimeSearchType().ord(), tableName));
			hasPrevious = true;
		}
		if (hasPrevious)
			return query.toString();

		return "(true)";
	}

	private static String createDateClause(long startEventSearch, long endEventSearch, int timeSearchType, String tableName) {

		System.err.println("start: " + startEventSearch);
		System.err.println("end: " + endEventSearch);
		if ((startEventSearch == 0) && (endEventSearch != 0))
			startEventSearch = endEventSearch;
		else if ((startEventSearch != 0) && (endEventSearch == 0))
			endEventSearch = startEventSearch;
		else if ((startEventSearch == 0) && (endEventSearch == 0))
			return "(true)";

		switch (timeSearchType) {
		case TimeSearchType.STARTS_BETWEEN:
			/* Event starts inside a period */
			return "(" + tableName + ".eDbStartTime > " + startEventSearch  + " AND " + tableName + ".eDbStartTime < " +  + endEventSearch + ")";
			//return "(" + startEventSearch + " < " + tableName + ".eDbStartTime < " + endEventSearch + ")";
		case TimeSearchType.ENDS_BETWEEN:
			/* Event ends inside a period */
			//return "(" + startEventSearch + " < " + tableName + ".eDbEndTime < " + endEventSearch + ")";
			return "(" +tableName + ".eDbEndTime > "+ startEventSearch + " AND " + tableName + ".eDbEndTime < " + endEventSearch + ")";
		case TimeSearchType.STARTS_AND_ENDS_BETWEEN:
			/* Event starts - ends inside a period */
			return "(" + tableName + ".eDbStartTime > " + startEventSearch + " AND " + tableName + ".eDbEndTime < "
					+ endEventSearch + ")";
		case TimeSearchType.START_BEFORE_AND_ENDS_AFTER:
			/* Event is running throughout a period */
			return "(" + tableName + ".eDbStartTime < " + startEventSearch + " AND " + tableName + ".eDbEndTime > "
					+ endEventSearch + ")";
		case TimeSearchType.ALL_BEFORE:
			/* Event starts - ends before a period */
			return "(" + tableName + ".eDbEndTime < " + startEventSearch + ")";
		case TimeSearchType.ALL_AFTER:
			/* Event starts - ends after a period */
			return "(" + tableName + ".eDbStartTime > " + endEventSearch + ")";
		case TimeSearchType.ALL_CASES:
		default:
			return "(true)";
		}
	}
	
	public static void main(String[] str) {
		MyEventSelectionTool db = new MyEventSelectionTool("jdbc:mysql://localhost/agenda_reminder", "root", "sc2011");
		FilterParams fp = new FilterParams(FilterParams.MY_URI + "test");
		List cals = new ArrayList();
		cals.add(new Calendar(Calendar.MY_URI + 6));
		List eList = db.requestFollowingEvents(cals, 10);
		for(Iterator it = eList.iterator(); it.hasNext();) {
			System.out.println("Hello: " + it.next());
		}
		
		
	}
}