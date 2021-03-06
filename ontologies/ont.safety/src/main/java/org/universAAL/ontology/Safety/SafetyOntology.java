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

import org.universAAL.middleware.owl.DataRepOntology;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntClassInfoSetup;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.service.owl.Service;
import org.universAAL.middleware.service.owl.ServiceBusOntology;
import org.universAAL.ontology.SafetyFactory;
import org.universAAL.ontology.lighting.LightSource;
import org.universAAL.ontology.lighting.LightingOntology;
import org.universAAL.ontology.location.LocationOntology;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.phThing.PhThingOntology;
import org.universAAL.ontology.profile.ProfileOntology;

/**
 * @author dimokas
 * 
 */

public final class SafetyOntology extends Ontology {

    private static SafetyFactory factory = new SafetyFactory();

    public static final String NAMESPACE = Resource.uAAL_NAMESPACE_PREFIX + "Safety.owl#";

    public SafetyOntology() {
    	super(NAMESPACE);
    }

    public SafetyOntology(String ontURI) {
    	super(ontURI);
    }


    public void create() {
	Resource r = getInfo();
	r.setResourceComment("Ontology for safety at home.");
	r.setResourceLabel("SafetyManagement");
	addImport(DataRepOntology.NAMESPACE);
	addImport(PhThingOntology.NAMESPACE);
	addImport(ServiceBusOntology.NAMESPACE);
	addImport(LocationOntology.NAMESPACE);
	addImport(ProfileOntology.NAMESPACE);
	addImport(LightingOntology.NAMESPACE);

	OntClassInfoSetup oci;

	// load Door Bell device
	oci = createNewOntClassInfo(DoorBell.MY_URI, factory, 0);
	oci.setResourceComment("The class of Door Bell device");
	oci.setResourceLabel("DoorBell");
	oci.addSuperClass(Device.MY_URI);
	oci.addObjectProperty(DoorBell.PROP_IS_ENABLED).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			DoorBell.PROP_IS_ENABLED, DoorBell.MY_URI, 1, 1));

	// load Door
	oci = createNewOntClassInfo(Door.MY_URI, factory, 1);
	oci.setResourceComment("The class of Door");
	oci.setResourceLabel("Door");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(Door.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(Door.PROP_DEVICE_RFID).setFunctional();
	oci.addObjectProperty(Door.PROP_HAS_DOORBELL).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			Door.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//Door.PROP_DEVICE_STATUS, Door.MY_URI, 1, 1));
			//.addRestriction( new BoundedValueRestriction(Door.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			Door.PROP_DEVICE_RFID, Door.MY_URI, 1, 1));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			Door.PROP_HAS_DOORBELL, DoorBell.MY_URI, 1, -1));
	
	// load Humidity Sensor
	oci = createNewOntClassInfo(HumiditySensor.MY_URI, factory, 2);
	oci.setResourceComment("The class of Humidity Sensor");
	oci.setResourceLabel("Humidity");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(HumiditySensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(HumiditySensor.PROP_HUMIDITY).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			HumiditySensor.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction( new BoundingValueRestriction(HumiditySensor.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			HumiditySensor.PROP_HUMIDITY, HumiditySensor.MY_URI, 1, 1));
    
	// load Light Sensor
	oci = createNewOntClassInfo(LightSensor.MY_URI, factory, 3);
	oci.setResourceComment("The class of Light Sensor");
	oci.setResourceLabel("Light");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(LightSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(LightSensor.PROP_SENSOR_STATUS).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			LightSensor.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction(new BoundingValueRestriction(LightSensor.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			LightSensor.PROP_SENSOR_STATUS, LightSensor.MY_URI, 1, 1));

	// load Motion Sensor
	oci = createNewOntClassInfo(MotionSensor.MY_URI, factory, 4);
	oci.setResourceComment("The class of Motion Sensor");
	oci.setResourceLabel("Motion");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(MotionSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(MotionSensor.PROP_MOTION).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			MotionSensor.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction(new BoundingValueRestriction(MotionSensor.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			MotionSensor.PROP_MOTION, MotionSensor.MY_URI, 1, 1));

	// load Smoke Sensor
	oci = createNewOntClassInfo(SmokeSensor.MY_URI, factory, 5);
	oci.setResourceComment("The class of Smoke Sensor");
	oci.setResourceLabel("Smoke");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(SmokeSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(SmokeSensor.PROP_SMOKE).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			SmokeSensor.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction(new BoundingValueRestriction(SmokeSensor.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			SmokeSensor.PROP_SMOKE, SmokeSensor.MY_URI, 1, 1));

	// load Temperature Sensor
	oci = createNewOntClassInfo(TemperatureSensor.MY_URI, factory, 6);
	oci.setResourceComment("The class of Temperature Sensor");
	oci.setResourceLabel("Temperature");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(TemperatureSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(TemperatureSensor.PROP_TEMPERATURE).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			TemperatureSensor.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction(new BoundingValueRestriction(TemperatureSensor.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			TemperatureSensor.PROP_TEMPERATURE, TemperatureSensor.MY_URI, 1, 1));
	
	// load Window Sensor
	oci = createNewOntClassInfo(Window.MY_URI, factory, 7);
	oci.setResourceComment("The class of Window Sensor");
	oci.setResourceLabel("Window");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(Window.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(Window.PROP_SENSOR_STATUS).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			Window.PROP_DEVICE_STATUS, TypeMapper.getDatatypeURI(Integer.class), 1, 1));
			//.addRestriction(new BoundingValueRestriction(Window.PROP_DEVICE_STATUS,new Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			Window.PROP_SENSOR_STATUS, Window.MY_URI, 1, 1));

	// load Fan Heater device
	oci = createNewOntClassInfo(FanHeater.MY_URI, factory, 8);
	oci.setResourceComment("The class of Fan Heater device");
	oci.setResourceLabel("FanHeater");
	oci.addSuperClass(Device.MY_URI);
	oci.addObjectProperty(FanHeater.PROP_IS_ENABLED).setFunctional();
	oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
			FanHeater.PROP_IS_ENABLED, FanHeater.MY_URI, 1, 1));

	// load FoodItem
	oci = createNewOntClassInfo(Notification.MY_URI, factory, 9);
	oci.setResourceComment("The class of notifications");
	oci.setResourceLabel("Notification");
	oci.addSuperClass(ManagedIndividual.MY_URI);
	oci.addDatatypeProperty(Notification.PROP_MESSAGE).setFunctional();
	oci.addDatatypeProperty(Notification.PROP_DATE).setFunctional();
	oci.addDatatypeProperty(Notification.PROP_TIME).setFunctional();
	oci.addRestriction(MergedRestriction.getCardinalityRestriction(
			Notification.PROP_MESSAGE, 1, 1));
	oci.addRestriction(MergedRestriction.getCardinalityRestriction(
			Notification.PROP_DATE, 1, 1));
	oci.addRestriction(MergedRestriction.getCardinalityRestriction(
			Notification.PROP_TIME, 1, 1));

	// load SafetyManagement
	oci = createNewOntClassInfo(SafetyManagement.MY_URI, factory, 10);
	oci.setResourceComment("The class of services controling safety items");
	oci.setResourceLabel("SafetyManagement");
	oci.addSuperClass(Service.MY_URI);
	oci.addObjectProperty(SafetyManagement.PROP_CONTROLS);
	oci.addObjectProperty(SafetyManagement.PROP_LAMP_CONTROLS);
	oci.addObjectProperty(SafetyManagement.PROP_HEATING_CONTROLS);
	oci.addRestriction(MergedRestriction.getAllValuesRestriction(
		SafetyManagement.PROP_CONTROLS, Door.MY_URI));
	oci.addRestriction(MergedRestriction.getAllValuesRestriction(
			SafetyManagement.PROP_LAMP_CONTROLS, LightSource.MY_URI));	
	oci.addRestriction(MergedRestriction.getAllValuesRestriction(
			SafetyManagement.PROP_HEATING_CONTROLS, FanHeater.MY_URI));	

    }
}




/*
public final class SafetyOntology extends Ontology {

    private static SafetyFactory factory = new SafetyFactory();

    public static final String NAMESPACE = Resource.uAAL_NAMESPACE_PREFIX
	    + "Safety.owl#";

    public SafetyOntology() {
	super(NAMESPACE);
    }

    public SafetyOntology(String ontURI) {
	super(ontURI);
    }

    public void create() {
	Resource r = getInfo();
	r.setResourceComment("Ontology for safety at home.");
	r.setResourceLabel("SafetyManagement");
	addImport(DataRepOntology.NAMESPACE);
	addImport(PhThingOntology.NAMESPACE);
	addImport(ServiceBusOntology.NAMESPACE);
	addImport(LocationOntology.NAMESPACE);
	addImport(ProfileOntology.NAMESPACE);

	OntClassInfoSetup oci;

	// load Door
	oci = createNewOntClassInfo(Door.MY_URI, factory, 0);
	oci.setResourceComment("The class of Door");
	oci.setResourceLabel("Door");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(Door.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(Door.PROP_DEVICE_RFID).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			Door.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction( new
	// BoundedValueRestriction(Door.PROP_DEVICE_STATUS,new Integer(0), true,
	// new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(Door.PROP_DEVICE_RFID,
			Door.MY_URI, 1, 1));

	// load Humidity Sensor
	oci = createNewOntClassInfo(HumiditySensor.MY_URI, factory, 1);
	oci.setResourceComment("The class of Humidity Sensor");
	oci.setResourceLabel("Humidity");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(HumiditySensor.PROP_DEVICE_STATUS)
		.setFunctional();
	oci.addObjectProperty(HumiditySensor.PROP_HUMIDITY).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			HumiditySensor.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction( new
	// BoundingValueRestriction(HumiditySensor.PROP_DEVICE_STATUS,new
	// Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			HumiditySensor.PROP_HUMIDITY, HumiditySensor.MY_URI, 1,
			1));

	// load Light Sensor
	oci = createNewOntClassInfo(LightSensor.MY_URI, factory, 2);
	oci.setResourceComment("The class of Light Sensor");
	oci.setResourceLabel("Light");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(LightSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(LightSensor.PROP_SENSOR_STATUS).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			LightSensor.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction(new
	// BoundingValueRestriction(LightSensor.PROP_DEVICE_STATUS,new
	// Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			LightSensor.PROP_SENSOR_STATUS, LightSensor.MY_URI, 1,
			1));

	// load Motion Sensor
	oci = createNewOntClassInfo(MotionSensor.MY_URI, factory, 3);
	oci.setResourceComment("The class of Motion Sensor");
	oci.setResourceLabel("Motion");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(MotionSensor.PROP_DEVICE_STATUS)
		.setFunctional();
	oci.addObjectProperty(MotionSensor.PROP_MOTION).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			MotionSensor.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction(new
	// BoundingValueRestriction(MotionSensor.PROP_DEVICE_STATUS,new
	// Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			MotionSensor.PROP_MOTION, MotionSensor.MY_URI, 1, 1));

	// load Smoke Sensor
	oci = createNewOntClassInfo(SmokeSensor.MY_URI, factory, 4);
	oci.setResourceComment("The class of Smoke Sensor");
	oci.setResourceLabel("Smoke");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(SmokeSensor.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(SmokeSensor.PROP_SMOKE).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			SmokeSensor.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction(new
	// BoundingValueRestriction(SmokeSensor.PROP_DEVICE_STATUS,new
	// Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(SmokeSensor.PROP_SMOKE,
			SmokeSensor.MY_URI, 1, 1));

	// load Temperature Sensor
	oci = createNewOntClassInfo(TemperatureSensor.MY_URI, factory, 5);
	oci.setResourceComment("The class of Temperature Sensor");
	oci.setResourceLabel("Temperature");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(TemperatureSensor.PROP_DEVICE_STATUS)
		.setFunctional();
	oci.addObjectProperty(TemperatureSensor.PROP_TEMPERATURE)
		.setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			TemperatureSensor.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction(new
	// BoundingValueRestriction(TemperatureSensor.PROP_DEVICE_STATUS,new
	// Integer(0), true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			TemperatureSensor.PROP_TEMPERATURE,
			TemperatureSensor.MY_URI, 1, 1));

	// load Window Sensor
	oci = createNewOntClassInfo(Window.MY_URI, factory, 6);
	oci.setResourceComment("The class of Window Sensor");
	oci.setResourceLabel("Window");
	oci.addSuperClass(Device.MY_URI);
	oci.addDatatypeProperty(Window.PROP_DEVICE_STATUS).setFunctional();
	oci.addObjectProperty(Window.PROP_SENSOR_STATUS).setFunctional();
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			Window.PROP_DEVICE_STATUS, TypeMapper
				.getDatatypeURI(Integer.class), 1, 1));
	// .addRestriction(new
	// BoundingValueRestriction(Window.PROP_DEVICE_STATUS,new Integer(0),
	// true, new Integer(100), true)));
	oci.addRestriction(MergedRestriction
		.getAllValuesRestrictionWithCardinality(
			Window.PROP_SENSOR_STATUS, Window.MY_URI, 1, 1));

	// load SafetyManagement
	oci = createNewOntClassInfo(SafetyManagement.MY_URI, factory, 7);
	oci.setResourceComment("The class of services controling safety items");
	oci.setResourceLabel("SafetyManagement");
	oci.addSuperClass(Service.MY_URI);
	oci.addObjectProperty(SafetyManagement.PROP_CONTROLS);
	oci.addRestriction(MergedRestriction.getAllValuesRestriction(
		SafetyManagement.PROP_CONTROLS, Door.MY_URI));

    }

}
*/