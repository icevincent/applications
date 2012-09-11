package na.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FoodInventory {

//	format = [0] => Food ID
//	format = [1] => Food Name
	public static int ID_col 		= 0;
	public static int NAME_col 		= 1;
	
	private static Log log = LogFactory.getLog(FoodInventory.class);
	private static final String filename = na.utils.ServiceInterface.PATH_FOOD_INVENTORY;
	private static final String code = "foods";
	
	/*
	 * return 0 --> Error
	 * return 1 --> Added succesfuly
	 * return 2 --> Already existed
	 */
	public static int addFood(int foodID, String foodName) {
		// load file
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(filename));
		    String foods_list = properties.getProperty(code);
		    if (foods_list==null) {
		    	properties.setProperty(code, foodID+"@"+foodName+";");
			    try {
			        properties.store(new FileOutputStream(filename), null);
			        log.info("Success! File: " + filename + " edited.");
			        return 1;
			    } catch (IOException e) {
			    	log.info("Error, couldn't write file: "+e.toString());
			    	return 0;
			    }
		    } else {
		    	String[] foods = foods_list.split(";");
		    	for (String food : foods) {
		    		String[] elem = food.split("@");
					if (elem[0].compareTo(String.valueOf(foodID))==0) {
						log.info("The food is already stored");
						return 2;
					} 
				}
		    	log.info("Storing new food...");
				properties.setProperty(code, foods_list + foodID+"@"+foodName+";");
			    try {
			        properties.store(new FileOutputStream(filename), null);
			        log.info("Success! File: " + filename + " has one more food.");
			        return 1;
			    } catch (IOException e) {
			    	log.info("Error, couldn't write file: "+e.toString());
			    	return 0;
			    }
		    }
		    
		} catch (IOException e) {
			log.info("Error, couldn't read file: "+e.toString());
		}
		
		return 0;
	}
	
	public static boolean removeFood(int foodID, String name) {
		// load file
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(filename));
		    String favorites = properties.getProperty("foods");
		    if (favorites==null) {
		    	return false;
		    } else {
		    	String[] favoriteArray = favorites.split(";");
		    	String res = "";
		    	boolean found = false;
		    	for (String fav : favoriteArray) {
		    		if (fav!=null && fav.contains("@")) {
		    			String[] parts = fav.split("@");
		    			int id = new Integer(parts[0]).intValue();
		    			if (id == foodID) {
		    				found = true;
		    			} else {
		    				res += fav;
		    			}
		    		}
				}
		    	
//		    	String search = ""+foodID+"@"+name+";";
//				
//				Pattern pattern = Pattern.compile(search);
//				Matcher matcher = pattern.matcher(favorites);
//		        String output = matcher.replaceAll("");
		        properties.setProperty("foods", res);
			    try {
			        properties.store(new FileOutputStream(filename), null);
			        if (found)
			        	log.info("Success! File: " + filename + " has one food less.");
			        else
			        	log.info("No changes on FoodInventory file.");
			        return true;
			    } catch (IOException e) {
			    	log.info("Error, couldn't write file: "+e.toString());
			    	return false;
			    }
		    }
		    
		} catch (IOException e) {
			log.info("Error, couldn't read file: "+e.toString());
		}
		
		return false;
	}
	
	/**
	 * Gets an array of foods from the food inventory.
	 * String = ID@FoodName
	 * 
	 * @return the food inventory
	 */
	static public String[] getFoodInventory() {
		// load file
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(filename));
		    String foods = properties.getProperty(code);
		    if (foods==null) {
		    	return null;
		    } else {
		    	return foods.split(";");
		    }
		} catch (IOException e) {
			log.info("Error, couldn't read file: "+e.toString());
		}
		
		return null;
	}
	
	
//	/*
//	 * return 0 --> Error
//	 * return 1 --> Added succesfuly
//	 * return 2 --> Already existed
//	 */
//	public static int addFood(int foodID) {
//		// load file
//		Properties properties = new Properties();
//		try {
//		    properties.load(new FileInputStream(filename));
//		    String foods_list = properties.getProperty(code);
//		    if (foods_list==null) {
////		    	log.info("No hay favoritos");
//		    	properties.setProperty(code, foodID+";");
//			    try {
//			        properties.store(new FileOutputStream(filename), null);
//			        log.info("Success! File: " + filename + " edited.");
//			        return 1;
//			    } catch (IOException e) {
//			    	log.info("Error, couldn't write file: "+e.toString());
//			    	return 0;
//			    }
//		    } else {
//		    	String[] foods = foods_list.split(";");
//		    	for (String food : foods) {
//					if (food.compareTo(String.valueOf(foodID))==0) {
//						log.info("The food is already stored");
//						return 2;
//					} 
//				}
//		    	log.info("Storing new food...");
//				properties.setProperty(code, foods_list + foodID+";");
//			    try {
//			        properties.store(new FileOutputStream(filename), null);
//			        log.info("Success! File: " + filename + " has one more food.");
//			        return 1;
//			    } catch (IOException e) {
//			    	log.info("Error, couldn't write file: "+e.toString());
//			    	return 0;
//			    }
//		    }
//		    
//		} catch (IOException e) {
//			log.info("Error, couldn't read file: "+e.toString());
//		}
//		
//		return 0;
//	}
	
	
//	// Write properties file.
//	static private boolean CreateFile() {
//		Properties properties = new Properties();
//		try {
//		    properties.store(new FileOutputStream(filename), null);
//		    log.info("File: "+ filename + " written succesfuly");
//		    return true;
//		} catch (IOException e) {
//			log.info("Error, couldn't write file: "+e.toString());
//			return false;
//		}
//	}
}