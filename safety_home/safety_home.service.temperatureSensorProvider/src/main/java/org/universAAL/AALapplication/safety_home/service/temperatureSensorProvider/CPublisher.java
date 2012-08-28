package org.universAAL.AALapplication.safety_home.service.temperatureSensorProvider;

import org.osgi.framework.BundleContext;
import org.universAAL.AALapplication.safety_home.service.temperatureSensorSoapClient.SOAPClient;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.safetyDevices.HumiditySensor;
import org.universAAL.ontology.safetyDevices.TemperatureSensor;

public class CPublisher extends ContextPublisher{
	public static final String SAFETY_TEMPERATURE_PROVIDER_NAMESPACE = "http://ontology.universaal.org/SafetyTemperatureProvider.owl#";
	public static final String MY_URI = SAFETY_TEMPERATURE_PROVIDER_NAMESPACE + "Temperature";
	static final String DEVICE_URI_PREFIX = CPublisher.SAFETY_TEMPERATURE_PROVIDER_NAMESPACE
			+ "controlledDevice";
	static final String LOCATION_URI_PREFIX = "urn:aal_space:myHome#";
	
	private ContextPublisher cp;
	
	protected CPublisher(ModuleContext context, ContextProvider providerInfo) {
		super(context, providerInfo);
	}
	
	protected CPublisher(ModuleContext context) {
		super(context, getProviderInfo());
		try{
			cp = new DefaultContextPublisher(context, getProviderInfo());
			invoke();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	public CPublisher(ModuleContext context, ContextProvider providerInfo, ContextPublisher cp) {
		super(context, providerInfo);
		try{
			this.cp = cp;
			invoke();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	public void invoke() throws InterruptedException{
		//getUsers();
		while(true){
			Thread.sleep(15000);
			publishTemperature(0);
		}
	}
	
	private void publishTemperature(int deviceID){
		Device device=null;
		if(deviceID==0){
			TemperatureSensor temperature = new TemperatureSensor(CPublisher.DEVICE_URI_PREFIX + deviceID);
			device=(Device)temperature;
			temperature.setDeviceLocation(new Room(CPublisher.LOCATION_URI_PREFIX + "temperature"));
			temperature.setTemperature(SOAPClient.getTemperature());
			
			System.out.println("############### PUBLISHING EVENT ###############");
			cp.publish(new ContextEvent(temperature, TemperatureSensor.PROP_TEMPERATURE));
			System.out.println("################################################");
		}
	}

	private static ContextProvider getProviderInfo() {
		ContextEventPattern cep4 = new ContextEventPattern();
		cep4.addRestriction(MergedRestriction.getAllValuesRestriction(ContextEvent.PROP_RDF_SUBJECT, TemperatureSensor.MY_URI));
		ContextProvider info = new ContextProvider(SAFETY_TEMPERATURE_PROVIDER_NAMESPACE + "TemperatureContextProvider");
		info.setType(ContextProviderType.controller);
		info.setProvidedEvents(new ContextEventPattern[]{cep4});

		return info;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}
}