/*
	Copyright 2011-2012 TSB, http://www.tsbtecnologias.es
	TSB - Tecnologías para la Salud y el Bienestar
	
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
 */package org.universAAL.EnergyReader;

 

/* More on how to use this class at: 
 * http://forge.universaal.org/wiki/support:Developer_Handbook_6#Publishing_context_events */
import java.util.TimerTask;

import javax.swing.Timer;

import org.osgi.framework.BundleContext;
import org.universAAL.EnergyReader.database.EnergyReaderDBInterface;
import org.universAAL.EnergyReader.model.MeasurementModel;
import org.universAAL.EnergyReader.model.ReadEnergyModel;
import org.universAAL.EnergyReader.utils.PowerReader;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.energy.reader.EnergyMeasurement;
import org.universAAL.ontology.energy.reader.ReadEnergy;
import org.universAAL.ontology.energy.reader.ReadEnergyDevice;

public class MinutePublisher extends TimerTask{ 
	
	private ContextPublisher cp;
	ContextProvider info = new ContextProvider();
	ModuleContext mc;
	public final static String NAMESPACE = "http://tsbtecnologias.es/ReadEnergy#";
	private PowerReader reader; 
	private ReadEnergyModel[] consumptions;
	   
    public MinutePublisher(BundleContext context) {
    	System.out.print("New Publisher\n");
		info = new ContextProvider("http://tsbtecnologias.es#MyNewContext");
		mc = uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context });
		info.setType(ContextProviderType.controller);
		info
				.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
	}
    
    public void publishAnEvent(ReadEnergyModel consumption) {
    	
    	System.out.print("Publishing in the context bus\n");
    	
    	ReadEnergyDevice device = new ReadEnergyDevice(NAMESPACE+consumption.getDevice().getName());
    	device.setName(consumption.getDevice().getName());
    	device.setDeviceType("Plug");
    	device.setPlace("Office");
    	
    	EnergyMeasurement con = new EnergyMeasurement(NAMESPACE+"Measurement"+consumption.getMeasure().getMeasurement());
    	con.setUnit("w");
    	con.setValue(consumption.getMeasure().getMeasurement());
    	
    	ReadEnergy energy = new ReadEnergy(NAMESPACE+"Energy"+consumption.getDevice().getName());
    	energy.setMeasurement(con);
    	energy.setDevice(device);
    	energy.setDaily("false");
    	
    	cp.publish(new ContextEvent(energy, ReadEnergy.PROP_HAS_MEASUREMENT));
    	
	}

	@Override
	public void run() {
		reader = new PowerReader();
		consumptions = reader.readEnergyConsumption();
		EnergyReaderDBInterface db = new EnergyReaderDBInterface();
		
		try {
			
			for (int i=0;i<consumptions.length;i++){
				db.insertDailyMeasurement(consumptions[i].getDevice().getName(), consumptions[i].getMeasure().getMeasurement());
				publishAnEvent(consumptions[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
