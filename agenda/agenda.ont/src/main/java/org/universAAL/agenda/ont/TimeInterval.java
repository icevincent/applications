package org.universAAL.agenda.ont;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.TypeMapper;

/**
 * @author kagnantis
 * 
 */
public class TimeInterval extends ManagedIndividual {
    public static final String MY_URI;
    public static final String PROP_TIME_PERIOD;
    public static final String PROP_START_TIME;
    public static final String PROP_END_TIME;

    static {
	MY_URI = Calendar.CALENDAR_NAMESPACE + "TimeInterval";
	PROP_TIME_PERIOD = Calendar.CALENDAR_NAMESPACE + "timePeriod";
	PROP_START_TIME = Calendar.CALENDAR_NAMESPACE + "startTime";
	PROP_END_TIME = Calendar.CALENDAR_NAMESPACE + "endTime";
	register(TimeInterval.class);
    }

    public static Restriction getClassRestrictionsOnProperty(String propURI) {
	if (PROP_TIME_PERIOD.equals(propURI))
	    return Restriction.getAllValuesRestrictionWithCardinality(propURI,
		    TypeMapper.getDatatypeURI(Duration.class), 1, 0);
	if (PROP_START_TIME.equals(propURI))
	    return Restriction
		    .getAllValuesRestrictionWithCardinality(propURI, TypeMapper
			    .getDatatypeURI(XMLGregorianCalendar.class), 1, 1);
	if (PROP_END_TIME.equals(propURI))
	    return Restriction
		    .getAllValuesRestrictionWithCardinality(propURI, TypeMapper
			    .getDatatypeURI(XMLGregorianCalendar.class), 1, 1);

	return ManagedIndividual.getClassRestrictionsOnProperty(propURI);
    }

    public static String[] getStandardPropertyURIs() {
	String[] inherited = ManagedIndividual.getStandardPropertyURIs();
	String[] toReturn = new String[inherited.length + 3];
	int i = 0;
	while (i < inherited.length) {
	    toReturn[i] = inherited[i];
	    i++;
	}
	toReturn[i++] = PROP_TIME_PERIOD;
	toReturn[i++] = PROP_START_TIME;
	toReturn[i++] = PROP_END_TIME;

	return toReturn;
    }

    public static String getRDFSComment() {
	return "The class of a time interval.";
    }

    public static String getRDFSLabel() {
	return "Time interval";
    }

    public TimeInterval() {
	super();
    }

    public TimeInterval(String uri) {
	super(uri);
    }

    public TimeInterval(String uri, XMLGregorianCalendar startTime,
	    Duration duration) {
	super(uri);
	if (startTime == null || duration == null)
	    throw new IllegalArgumentException();

	props.put(PROP_START_TIME, startTime);
	props.put(PROP_TIME_PERIOD, duration);
	XMLGregorianCalendar endTime = startTime;
	endTime.add(duration);
	props.put(PROP_END_TIME, endTime);
    }

    public TimeInterval(String uri, XMLGregorianCalendar startTime,
	    XMLGregorianCalendar endTime) {
	super(uri);
	if (startTime == null || endTime == null)
	    throw new IllegalArgumentException();

	props.put(PROP_START_TIME, startTime);
	props.put(PROP_END_TIME, endTime);
    }

    /**
     * better not to be used, in order to avoid inconsistency between duration
     * and start/end times i.e., cases where endTime - startTime != duration
     * 
     * @param uri
     * @param startTime
     * @param endTime
     * @param duration
     * 
     *            public TimeInterval(String uri, XMLGregorianCalendar
     *            startTime, XMLGregorianCalendar endTime, Duration duration){
     *            super(uri); if (startTime == null || duration == null) throw
     *            new IllegalArgumentException();
     * 
     *            props.put(PROP_HAS_TIME_TABLE_ENTRIES, startTime);
     *            props.put(PROP_TIME_PERIOD, duration); if (endTime == null) {
     *            endTime = startTime; endTime.add(duration); }
     *            props.put(PROP_HAS_PUBLIC_TRANSPORT, endTime); }
     */

    /**
     * There is no implementation of subtracting a duration from a calendar
     * 
     * @param uri
     * @param duration
     * @param endTime
     * 
     *            public TimeInterval(String uri, Duration duration,
     *            XMLGregorianCalendar endTime){ super(uri);
     * 
     *            props.put(PROP_HAS_PUBLIC_TRANSPORT, endTime); }
     */

    /**
     * There is no implementation of subtracting a calendar from a calendar
     * 
     * public TimeInterval(String uri, XMLGregorianCalendar startTime,
     * XMLGregorianCalendar endTime){ super(uri);
     * 
     * props.put(PROP_HAS_TIME_TABLE_ENTRIES, startTime);
     * props.put(PROP_HAS_PUBLIC_TRANSPORT, endTime); }
     */

    /*
     * The time the event starts
     */
    public XMLGregorianCalendar getStartTime() {
	return (XMLGregorianCalendar) props.get(PROP_START_TIME);
    }

    /*
     * The time the event is over
     */
    public XMLGregorianCalendar getEndTime() {
	return (XMLGregorianCalendar) props.get(PROP_END_TIME);
    }

    /*
     * The duration of the event Never used
     */
    public XMLGregorianCalendar getTimePeriod() {
	return (XMLGregorianCalendar) props.get(PROP_TIME_PERIOD);
    }

    public void setStartTime(XMLGregorianCalendar startTime) {
	if (startTime == null)
	    return;
	props.put(PROP_START_TIME, startTime);

	// update end time
	/*
	 * Duration d = (Duration) props.get(PROP_TIME_PERIOD);
	 * XMLGregorianCalendar temp = startTime; temp.add(d); setEndTime(temp);
	 */
    }

    /*
     * only accessible from this class, to avoid inconsistency matters
     */
    public void setEndTime(XMLGregorianCalendar endTime) {
	if (endTime == null)
	    return;
	props.put(PROP_END_TIME, endTime);
    }

    /*
     * only accessible from this class, to avoid inconsistency matters
     * 
     * private void setTimePeriod(Duration duration) { if (duration == null)
     * return; props.put(PROP_TIME_PERIOD, duration);
     * 
     * //update end time XMLGregorianCalendar temp = (XMLGregorianCalendar)
     * props.get(PROP_START_TIME); temp.add(duration); setEndTime(temp); }
     */

    public int getPropSerializationType(String propURI) {
	return PROP_END_TIME.equals(propURI) ? PROP_SERIALIZATION_OPTIONAL
		: PROP_SERIALIZATION_FULL;

    }

    public boolean isWellFormed() {
	return (props.containsKey(PROP_START_TIME) && props
		.contains(PROP_END_TIME));
    }

    public String toString(String fieldSeperator) {
	StringBuffer s = new StringBuffer(50);

	s.append("Time Interval:" + fieldSeperator);
	s.append("\tBegin time    :\t" + this.getStartTime() + fieldSeperator);
	s.append("\tEnd time      :\t" + this.getEndTime());

	return s.toString();
    }

    public String toString() {
	return this.toString("\n");
    }

}
