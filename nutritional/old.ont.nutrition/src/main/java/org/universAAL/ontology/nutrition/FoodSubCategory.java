package org.universAAL.ontology.nutrition;

import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.ontology.Activator;

public class FoodSubCategory extends ManagedIndividual {

	public static final String NAMESPACE = Activator.MY_NAMESPACE;
	public static final String MY_URI = FoodSubCategory.NAMESPACE + "FoodSubCategory";

	//property list
	public static final String P_ID;				// Integer
    public static final String P_NAME;				// String
    public static final String P_FOODCATEGORY;		// FoodCagegory
    
    
    static {
		// property names
    	P_ID 				= FoodSubCategory.NAMESPACE + "id";
    	P_NAME				= FoodSubCategory.NAMESPACE + "name";
    	P_FOODCATEGORY		= FoodSubCategory.NAMESPACE + "hasFoodCategory";
		
		register(FoodSubCategory.class);
    }

    public FoodSubCategory() { }
    public FoodSubCategory(String uri) { super(uri); }

	public static Restriction getClassRestrictionsOnProperty(String propURI) {
		if (propURI.equals(P_ID))
         return Restriction.getAllValuesRestrictionWithCardinality(propURI,
	         TypeMapper.getDatatypeURI(Integer.class), 1, 1);
        if (propURI.equals(P_NAME))
            return Restriction.getAllValuesRestrictionWithCardinality(propURI,
   	         TypeMapper.getDatatypeURI(String.class), 0, 1);
        if (propURI.equals(P_FOODCATEGORY))
            return Restriction.getAllValuesRestrictionWithCardinality(propURI,
   	         FoodCategory.MY_URI, 0, 1);
	    return ManagedIndividual.getClassRestrictionsOnProperty(propURI);
	}

	public static String[] getStandardPropertyURIs() {
		String[] proper = {P_ID, P_NAME};
		
		String[] inherited = ManagedIndividual.getStandardPropertyURIs();
		String[] merged = new String[inherited.length + proper.length];
		System.arraycopy(inherited, 0, merged, 0, inherited.length);
		System.arraycopy(proper, 0, merged, inherited.length, proper.length);
		return merged;
	}

	public static String getRDFSComment() {
		return "comment of the food ontology";
	}

	public static String getRDFSLabel() {
		return "food";
	}

	public int getPropSerializationType(String propURI) {
		return ManagedIndividual.PROP_SERIALIZATION_FULL;
	}

	public boolean isWellFormed() {
		return true;
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	
	// ID
	public int getID() {
		Integer v = (Integer) props.get(P_ID);
		return v.intValue();
	}
	
	public void setID(int id) {
		this.props.put(P_ID, new Integer(id));
	}
	
	// NAME
	public String getName() {
		String v = (String) props.get(P_NAME);
		return v;
	}
	
	public void setName(String name) {
		this.props.put(P_NAME, new String(name));
	}
	
	// FOOD CATEGORY
	public FoodCategory getFoodCategory() {
		FoodCategory v = (FoodCategory) props.get(P_FOODCATEGORY);
		return v;
	}
	
	public void setFoodCategory(FoodCategory foodCategory) {
		this.props.put(P_FOODCATEGORY, foodCategory);
	}
}