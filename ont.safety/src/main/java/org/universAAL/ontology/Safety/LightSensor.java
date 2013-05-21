/*****************************************************************************************
 * Copyright 2012 CERTH, http://www.certh.gr - Center for Research and Technology Hellas
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
 *****************************************************************************************/

package org.universAAL.ontology.Safety;

import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.phThing.Device;

/**
 * @author dimokas
 * 
 *         Light Ontology
 */
public class LightSensor extends Device {
    public static final String MY_URI = SafetyOntology.NAMESPACE + "Light";
    // public static final String PROP_DEVICE_LOCATION;
    public static final String PROP_DEVICE_STATUS = SafetyOntology.NAMESPACE
	    + "deviceStatus";
    public static final String PROP_SENSOR_STATUS = SafetyOntology.NAMESPACE
	    + "homeLight";

    public LightSensor() {
	super();
    }

    public LightSensor(String uri) {
	super(uri);
    }

    public String getClassURI() {
	return MY_URI;
    }

    public static String[] getStandardPropertyURIs() {
	// return new String[] {PROP_DEVICE_LOCATION, PROP_DEVICE_STATUS};
	return new String[] { PROP_PHYSICAL_LOCATION, PROP_DEVICE_STATUS,
		PROP_SENSOR_STATUS };
    }

    /*
     * public static String getRDFSComment() { return "A light sensor"; }
     * 
     * public static String getRDFSLabel() { return "Light"; }
     */

    public LightSensor(String uri, Location loc) {
	super(uri);
	if (loc == null)
	    throw new IllegalArgumentException();

	props.put(PROP_DEVICE_STATUS, new Integer(0));
	props.put(PROP_PHYSICAL_LOCATION, loc);
    }

    public LightSensor(String uri, Location loc, int sensor_status) {
	super(uri);
	if (loc == null)
	    throw new IllegalArgumentException();

	props.put(PROP_DEVICE_STATUS, new Integer(0));
	props.put(PROP_PHYSICAL_LOCATION, loc);
	props.put(PROP_SENSOR_STATUS, new Integer(sensor_status));
    }

    public int getStatus() {
	Integer i = (Integer) props.get(PROP_DEVICE_STATUS);
	return (i == null) ? -1 : i.intValue();
    }

    public Location getDeviceLocation() {
	return (Location) props.get(PROP_PHYSICAL_LOCATION);
    }

    public void setStatus(int state) {
	if (state > -1 && state < 101)
	    props.put(PROP_DEVICE_STATUS, new Integer(state));
    }

    public void setSensorStatus(int status) {
	props.put(PROP_SENSOR_STATUS, new Integer(status));
    }

    public int getSensorStatus() {
	Integer i = (Integer) props.get(PROP_SENSOR_STATUS);
	return (i == null) ? -1 : i.intValue();
    }

    public void setDeviceLocation(Location l) {
	if (l != null)
	    props.put(PROP_PHYSICAL_LOCATION, l);
    }

    public int getPropSerializationType(String propURI) {
	return PROP_SERIALIZATION_FULL;
	// return (PROP_PHYSICAL_LOCATION.equals(propURI)) ?
	// PROP_SERIALIZATION_REDUCED
	// : PROP_SERIALIZATION_FULL;
    }

    public boolean isWellFormed() {
	return true;
	// return props.containsKey(PROP_DEVICE_STATUS)
	// && props.containsKey(PROP_PHYSICAL_LOCATION);
    }

}
