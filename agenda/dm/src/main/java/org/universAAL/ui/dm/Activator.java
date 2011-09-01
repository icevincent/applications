/*
	Copyright 2008-2010 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute of Computer Graphics Research 
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.ui.dm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.universAAL.context.conversion.jena.JenaConverter;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.util.LogUtils;
import org.universAAL.middleware.util.Messages;
import org.universAAL.middleware.io.owl.AccessImpairment;
import org.universAAL.middleware.io.owl.Gender;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.middleware.io.owl.Modality;
import org.universAAL.middleware.io.owl.PrivacyLevel;
import org.universAAL.ontology.profile.ElderlyProfile;
import org.universAAL.ontology.profile.ElderlyUser;
import org.universAAL.ontology.profile.HealthProfile;
import org.universAAL.ontology.profile.HearingImpairment;
import org.universAAL.ontology.profile.PersonalPreferenceProfile;
import org.universAAL.ontology.profile.UserIdentificationProfile;
import org.universAAL.middleware.sodapop.msg.MessageContentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * The bundle activator.
 * @author mtazari
 */
public class Activator extends Thread implements BundleActivator {

	private static BundleContext context = null;
	static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private static JenaConverter mc = null;
	private static MessageContentSerializer serializer = null;

	private static ContextSubscriber contextSubscriber = null;
	private static InputSubscriber inputSubscriber = null;
	private static OutputPublisher outputPublisher = null;
	private static ServiceCaller serviceCaller = null;
	
	/**
	 * The configuration file with translated strings to show to the user. 
	 */
	private static Messages messages;

	/**
	 * URL of the database.
	 */
	static final String JENA_DB_URL = System.getProperty(
			"org.persona.platform.jena_db.url",
			"jdbc:mysql://localhost:3306/persona_aal_space");
	
	/**
	 * User name for accessing the database.
	 */
	//SC2011: bilo je ui_dm
	static final String JENA_DB_USER = System.getProperty(
			"org.persona.platform.ui.dm.db_user", "root");
	
	/**
	 * Password for accessing the database.
	 */
	//SC2011: bilo je ui_dm
	static final String JENA_DB_PASSWORD = System.getProperty(
			"org.persona.platform.ui.dm.db_passwd", "sc2011");
	
	/**
	 * Model name of the database.
	 */
	static final String JENA_MODEL_NAME = System.getProperty(
			"org.persona.platform.jena_db.model_name", "PERSONA_AAL_Space");


	// static void changeTestData() {
	// Resource b = new Resource(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX +
	// "boiler1");
	// b.addType("http://ontology.persona.ratio.it/DummyServiceProvider.owl#Boiler",
	// true);
	// Location pl = new Location(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX +
	// "livingRoom");
	// b.setProperty(PhysicalThing.PROP_PHYSICAL_LOCATION, pl);
	// insert(b);
	// Temperature t = new Temperature();
	// t.setValue(21);
	// pl.setTargetTemperature(t);
	// new DefaultContextPublisher(context, null).publish(new ContextEvent(pl,
	// Location.PROP_TARGET_TEMPERATURE));
	// }

	/**
	 * Get the bundle context
	 * @return The bundle context.
	 */
	static BundleContext getBundleContext() {
		return context;
	}

	/**
	 * Get a connection to the database.
	 * @return The database connection.
	 */
	static DBConnection getConnection() {
		return new DBConnection(JENA_DB_URL, JENA_DB_USER, JENA_DB_PASSWORD,
				"MySQL");
	}

	/**
	 * Get the context subscriber which is responsible for realizing system
	 * reactivity.
	 * @return The context subscriber.
	 */
	static ContextSubscriber getContextSubscriber() {
		return contextSubscriber;
	}

	/**
	 * Get the input subscriber which handles input events from the input bus.
	 * @return The input subscriber.
	 */
	static InputSubscriber getInputSubscriber() {
		return inputSubscriber;
	}

	/**
	 * Get the model converter for accessing the database.
	 * @return The model converter.
	 */
	static JenaConverter getModelConverter() {
		return mc;
	}

	/**
	 * Get output publisher which handles dialogs and messages and interacts
	 * with the output bus.
	 * @return The output publisher.
	 */
	static OutputPublisher getOutputPublisher() {
		return outputPublisher;
	}

	/**
	 * Get the message serializer which can be used to (de-)serialize
	 * RDF messages.
	 * @return The message serializer.
	 */
	static MessageContentSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Get service caller.
	 * @return The service caller.
	 */
	static ServiceCaller getServiceCaller() {
		return serviceCaller;
	}

	/**
	 * Create a new user for testing purposes with some default values. The
	 * new user is then uploaded to the database.
	 * @param uri The URI of the user.
	 * @param name The name of the user.
	 */
	static void loadTestData(String uri, String name, Modality mod) {
		
		System.err.println("ucitavanje TestData: +" + uri + ", "+name);
		
		UserIdentificationProfile uip = new UserIdentificationProfile();
		uip.setName(name);
		ElderlyProfile ep = new ElderlyProfile();
		ep.setUserIdentificationProfile(uip);
		HealthProfile hp = new HealthProfile();
		hp.setDisability(new AccessImpairment[] { new HearingImpairment(
				LevelRating.middle) });
		ep.setHealthProfile(hp);
		PersonalPreferenceProfile ppp = new PersonalPreferenceProfile();
		ppp.setInsensibleMaxX(1024);
		ppp.setInsensibleMaxY(768);
		ppp.setInsensibleVolumeLevel(85);
		ppp.setPersonalMinX(176);
		ppp.setPersonalMinY(320);
		ppp.setPersonalVolumeLevel(60);
		ppp.setPLsMappedToInsensible(new PrivacyLevel[] { PrivacyLevel.knownPeopleOnly });
		ppp.setPLsMappedToPersonal(new PrivacyLevel[] {
				PrivacyLevel.intimatesOnly, PrivacyLevel.homeMatesOnly });
		ppp.setVoiceGender(Gender.female);
		ppp.setXactionModality(mod);
		
		ep.setPersonalPreferenceProfile(ppp);
		ElderlyUser eu = new ElderlyUser(uri);
		eu.setProfile(ep);
		insert(eu);
		
		System.err.println("nakon ucitavanje TestData");
	}

	/**
	 * Insert a new user into the database, e.g. a self-defined test user. 
	 * @param pr
	 */
	private static void insert(Resource pr) {
		try {
			DBConnection conn = getConnection();
			if (conn.containsModel(JENA_MODEL_NAME)) {
				ModelRDB CHModel = ModelRDB.open(conn, JENA_MODEL_NAME);
				Model m = mc.toJenaResource(pr).getModel();
				m.write(System.out, "RDF/XML-ABBREV");
				// CHModel.difference(m).write(System.out, "RDF/XML-ABBREV");
				CHModel.setDoDuplicateCheck(true);
				CHModel.add(m);
				CHModel.close();
			}
			conn.close();
		} catch (Exception e) {
			LogUtils.logWarning(logger, "Activator", "insert", null, e);
		}
	}

	/**
	 * Get a string from the configuration file.
	 * @param key Get the translated string for this key.
	 * @return The translated string.
	 */
	static String getString(String key) {
		return messages.getString(key);
	}

	/**
	 * Get the configuration file reader which contains the translated strings
	 * of all messages shown to the user. The configuration file reader is
	 * initialized with the correct path to the file system.
	 * @return The configuration file reader.
	 */
	static Messages getConfFileReader() {
		return messages;
	}

	/**
	 * The bundle start method externalized in a separate thread for non-
	 * blocking initialization of this bundle.
	 */
	public void run() {
		try {
			messages = new Messages(context.getBundle().getSymbolicName());
		} catch (Exception e) {
			LogUtils.logError(
					logger,
					"Activator",
					"start",
					new Object[] { "Cannot initialize Dialog Manager externalized strings!" },
					e);
			return;
		}

		contextSubscriber = new ContextSubscriber(context);
		// it is important to instantiate the input subscriber before the output
		// publisher
		inputSubscriber = new InputSubscriber(context);
		// loadTestData();
		outputPublisher = new OutputPublisher(context);
		serviceCaller = new ServiceCaller(context);
		// changeTestData();
	}

	/**
	 * Method for OSGi bundle: start this bundle.
	 */
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		ServiceReference sref = context
				.getServiceReference(MessageContentSerializer.class.getName());
		serializer = (sref == null) ? null : (MessageContentSerializer) context
				.getService(sref);
		sref = context.getServiceReference(JenaConverter.class.getName());
		mc = (sref == null) ? null : (JenaConverter) context.getService(sref);
		start();
	}

	/**
	 * Method for OSGi bundle: stop this bundle.
	 */
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
	}
}