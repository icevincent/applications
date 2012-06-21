package org.universAAL.AALApplication.health.motivation;

import java.util.Locale;

import org.universAAL.ontology.profile.User;
import org.universaal.ontology.health.owl.MotivationalStatusType;
import org.universaal.ontology.owl.MotivationalMessage;
import org.universaal.ontology.owl.MotivationalMessageClassification;

public interface MotivationInterface {
	
	//Users needed
	/*
	public User getAssistedPerson();
	public User getCaregiver(User assistedPerson);
	public HealthProfile getHealthProfile(User u);
	*/
	
	public void registerClassesNeeded();
	
	//Message context needed
	public String getMessageDatabaseRoute(Locale messageDatabaseLanguage);
	public String getMessageVariablesRoute(Locale variablesDatabaseLanguage);
	public String setMessageDatabaseRoute(String messageDatabaseRoute);
	public String setMessageVariablesRoute(String variablesDatabaseRoute);
	//public String sendMotivationalMessageToUser(MotivationalMessage mm);
	//In the real context the message should be sent to the user's inbox
	//public void sendMessageToCaregiver(String illness, String treatmentType, MotivationalStatusType motStatus, MotivationalMessageClassification messageType);
	//public void sendMessageToAssistedPerson(String illness, String treatmentType, MotivationalStatusType motStatus, MotivationalMessageClassification messageType);

}