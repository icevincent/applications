package org.universAAL.AALapplication.safety_home.service.humiditySensorProvider;

import org.osgi.framework.BundleContext;
import org.universAAL.AALapplication.safety_home.service.humiditySensorSoapClient.SOAPClient;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.safetyDevices.HumiditySensor;

public class CPublisher extends ContextPublisher{
	public static final String SAFETY_HUMIDITY_PROVIDER_NAMESPACE = "http://ontology.universaal.org/SafetyHumidityProvider.owl#";
	public static final String MY_URI = SAFETY_HUMIDITY_PROVIDER_NAMESPACE + "Humidity";
	static final String DEVICE_URI_PREFIX = CPublisher.SAFETY_HUMIDITY_PROVIDER_NAMESPACE
			+ "controlledDevice";
	static final String LOCATION_URI_PREFIX = "urn:aal_space:myHome#";
	
	private ContextPublisher cp;
	
	protected CPublisher(BundleContext context, ContextProvider providerInfo) {
		super(context, providerInfo);
	}
	
	protected CPublisher(BundleContext context) {
		super(context, getProviderInfo());
		try{
			ContextProvider info = new ContextProvider(SAFETY_HUMIDITY_PROVIDER_NAMESPACE + "HumidityContextProvider");
			info.setType(ContextProviderType.controller);
			cp = new DefaultContextPublisher(context, info);
			invoke();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	public void invoke() throws InterruptedException{
		//getUsers();
		while (true){
			Thread.sleep(60000);
			publishHumidity(0);
		}
	}
	
	private void publishHumidity(int deviceID){
		Device device=null;
		if(deviceID==0){
			HumiditySensor humidity = new HumiditySensor(CPublisher.DEVICE_URI_PREFIX + deviceID);
			device=(Device)humidity;
			humidity.setDeviceLocation(new Room(CPublisher.LOCATION_URI_PREFIX + "humidity"));
			humidity.setHumidity(SOAPClient.getHumidity());
			
			System.out.println("############### PUBLISHING EVENT ###############");
			cp.publish(new ContextEvent(humidity, HumiditySensor.PROP_HUMIDITY));
			System.out.println("################################################");
		}
	}

	
	private static ContextProvider getProviderInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}
}