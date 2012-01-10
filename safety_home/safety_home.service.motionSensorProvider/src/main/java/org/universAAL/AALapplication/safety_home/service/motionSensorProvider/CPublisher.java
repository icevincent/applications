package org.universAAL.AALapplication.safety_home.service.motionSensorProvider;

import org.osgi.framework.BundleContext;
import org.universAAL.AALapplication.safety_home.service.motionSensorSoapClient.SOAPClient;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.safetyDevices.MotionSensor;

public class CPublisher extends ContextPublisher{
	public static final String SAFETY_MOTION_PROVIDER_NAMESPACE = "http://ontology.universaal.org/SafetyMotionProvider.owl#";
	public static final String MY_URI = SAFETY_MOTION_PROVIDER_NAMESPACE + "Motion";
	static final String DEVICE_URI_PREFIX = CPublisher.SAFETY_MOTION_PROVIDER_NAMESPACE
			+ "controlledDevice";
	static final String LOCATION_URI_PREFIX = "urn:aal_space:myHome#";
	
	private ContextPublisher cp;
	
	protected CPublisher(BundleContext context, ContextProvider providerInfo) {
		super(context, providerInfo);
	}
	
	protected CPublisher(BundleContext context) {
		super(context, getProviderInfo());
		try{
			ContextProvider info = new ContextProvider(SAFETY_MOTION_PROVIDER_NAMESPACE + "MotionContextProvider");
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
		for (int i=0; i<5; i++){
			Thread.sleep(20000);
			publishMotionDetection(0);
		}
	}
	
	private void publishMotionDetection(int deviceID){
		Device device=null;
		if(deviceID==0){
			MotionSensor motion = new MotionSensor(CPublisher.DEVICE_URI_PREFIX + deviceID);
			device=(Device)motion;
			motion.setDeviceLocation(new Room(CPublisher.LOCATION_URI_PREFIX + "humidity"));
			motion.setMotion(SOAPClient.getMotionDetection());
			
			System.out.println("############### PUBLISHING EVENT ###############");
			cp.publish(new ContextEvent(motion, MotionSensor.PROP_MOTION));
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