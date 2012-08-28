package org.universAAL.AALApplication.health.motivation.motivatonalMessageManagement;

import java.util.Map;
import java.util.TreeMap;

import org.universAAL.AALApplication.health.motivation.MotivationServiceRequirementsIface;

//llamar a un m�todo que obtenga del sistema estas variables
public class MessageVariables {
	
	public static Map<String, String> mapOfVariables = new TreeMap<String, String>();
	
	public static MotivationServiceRequirementsIface requirements;
	
	public static void setMotivationServiceRequirementsIface(MotivationServiceRequirementsIface iface){
		requirements = iface;
	}
	
	public static void buildInitialMapOfVariables(){
		
		String username = requirements.getAssistedPersonName();
		mapOfVariables.put("userName", username);
		
		String caregivername = requirements.getCaregiverName(requirements.getAssistedPerson());
		mapOfVariables.put("caregiverName", caregivername);
		
		String partOfDay = requirements.getPartOfDay();
		mapOfVariables.put("partOfDay", partOfDay);
		
		String apGenderArticle = requirements.getAPGenderArticle();
		mapOfVariables.put("userGender", apGenderArticle);
		
		String apPosesiveGenderArticle = requirements.getAPPosesiveGenderArticle();
		mapOfVariables.put("userPosesiveGender", apPosesiveGenderArticle);
		
		String caregiverGenderArticle = requirements.getCaregiverGenderArticle();
		mapOfVariables.put("caregiverGender", caregiverGenderArticle);
		
		String caregiverPosesiveGenderArticle = requirements.getCaregiverPosesiveGenderArticle();
		mapOfVariables.put("caregiverPosesiveGender", caregiverPosesiveGenderArticle);
	}
	
	public static void addToMapOfVariables(String key, String value){
		mapOfVariables.put(key, value);
	}
	
	public static String[] replaceVariables (String[] variable){
		String[] values = new String[variable.length];
		for (int i=0;i<variable.length;i++){
			if (mapOfVariables.containsKey(variable[i])){
				values[i] = (String) mapOfVariables.get(variable[i]);
			}
			else
				values[i] = null;
		}
		return values;
	}
	
	/*
public static String[] replaceVariables(String[] variable){
		
	String[] values = new String[variable.length];
	
	for (int i=0;i<variable.length;i++){
			
		if (variable[i].equals("caregiverRol"))
			values[i] = "Doctor";
			
		else if(variable[i].equals("treatmentName"))
			values[i] = "Nombredeltratamiento";
			
		else if(variable[i].equals("username"))
			values[i] = "Pepe";
			
		else if(variable[i].equals("treatmentDescription"))
			values[i] = "Definici�n del tratamiento";
			
		else if(variable[i].equals("treatmentPuropose"))
			values[i] = "Prop�sito del tratamiento";
		else if(variable[i].equals("prueba"))
			values[i] = "Marina para saber si esto funciona";	
		else if(variable[i].equals("partOfDay"))
			values[i] = "morning";
		else if(variable[i].equals("userPetName"))
			values[i] = "Bobby";
		else if(variable[i].equals("veterinarianDate"))
			values[i] = "16 h";
		else if(variable[i].equals("veterinarianPlace"))
			values[i] = "City Mall";
		else{
			System.out.println("Par�metro no definido");
			values[i] = null;
		}
	}
		return values;
}
*/
}