package org.universAAL.ontology.drools;

import java.util.ArrayList;
import java.util.List;

import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.ontology.drools.FactProperty;
import org.universAAL.ontology.phThing.Sensor;




//If you are making a concept that does not inherit from any other you just extend ManagedIndividual
//Otherwise you extend the concept class you inherit from
public class Fact extends ManagedIndividual {
  // Make sure you use the same namespace in all your domain ontology
  // You can declare the namespace in your root concept and later reuse it in
  // the rest of classes
  public static final String MY_NAMESPACE;
  // MY URI is the URI of this concept. It is mandatory for all.
  public static final String MY_URI;
  // Now declare ALL properties that this concept defines
  public static final String PROP_ID;
  public static final String PROP_FactProperty;
   
  
  // In this static block you set the URIs of your concept and its properties
  static {
	// Namespaces must follow this format
	MY_NAMESPACE = "http://ontology.universAAL.org/Drool.owl#";
	// The URI of your concept, which is the same name than the class
	MY_URI = Fact.MY_NAMESPACE + "Fact";
	// Now declare the URIs of the properties. They must start with lower
	// case.
	PROP_ID = Fact.MY_NAMESPACE + "hasFactID";
	
	PROP_FactProperty = FactProperty.MY_NAMESPACE + "hasFactProperty";
	
	
	// This line registers the ontology concept in the platform
	register(Fact.class);
  }

  // In this method you must return the restrictions on the property you are
  // asked for
  public static Restriction getClassRestrictionsOnProperty(String propURI) {
	// For each property you have declared, return the appropriate
	// restrictions
	if (PROP_ID.equals(propURI))
	    // This restriction means that in this property there must be a
	    // Device concept, with cardinality 1:1 (a mandatory single Device)
		//ID: String Cardinality 1:1
	    return Restriction.getAllValuesRestrictionWithCardinality(propURI,
	    		TypeMapper.getDatatypeURI(String.class), 1, 1);
	
		if (PROP_FactProperty.equals(propURI))
		    // This restriction means that in this property there can be none,
		    // one or many Sensor concepts
		    return Restriction.getAllValuesRestriction(propURI, FactProperty.MY_URI);
	
	// There are other methods to declare restrictions, and even construct
	// more complex ones, but these are the most commonly used.
	// You can also return restrictions on properties you inherit from
	// parent concepts.
	// Finally, you must return the default restrictions of your parent
	// concept, with this
	return ManagedIndividual.getClassRestrictionsOnProperty(propURI);
	// In this case we have no parent concept so we use ManagedIndividual.
	// If you inherited from other concept, then use it instead.
  }

  // This method is used by the system to handle the ontologies. It returns
  // the URIs of all properties used in this concept.
  public static String[] getStandardPropertyURIs() {
	// First get property URIs of your parent concept (in this case we have
	// none, so we use ManagedIndividual)
	String[] inherited = ManagedIndividual.getStandardPropertyURIs();
	String[] toReturn = new String[inherited.length + 2];// Make sure you
	// increase the size by the number of properties declared in your
	// concept!
	int i = 0;
	// With this we copy the properties of the parent...
	while (i < inherited.length) {
	    toReturn[i] = inherited[i];
	    i++;
	}
	// ...and with this we add the properties declared in this concept
	toReturn[i++] = PROP_ID;
	toReturn[i] = PROP_FactProperty;
	
	
	// Now we have all the property URIs of the concept, both inherited and
	// declared by it.
	return toReturn;
  }

  public Fact() {
	// Basic constructor. In general it is empty.
  }

  public Fact(String uri) {
	super(uri);
	// This is the commonly used constructor. In general it is like this,
	// just a call to super.
  }

  public static String getRDFSComment() {
	return "RDFs of comments of Rule:";
  }

  public static String getRDFSLabel() {
	return "Rule ontology properties ID, FactPropery and Body'";
  }

  // This method is used for serialization purposes, to restrict the amount of
  // information to serialize when forwarding it among nodes.
  // For each property you must return one of PROP_SERIALIZATION_FULL,
  // REDUCED, OPTIONAL or UNDEFINED.
  // Refer to their javadoc to see what they mean.
  public int getPropSerializationType(String propURI) {
	// In this case we serialize everything. It is up to you to define what
	// is important to be serialized and what is expendable in your concept.
	return PROP_SERIALIZATION_FULL;
  }

  // In this method you evaluate if an instance of your concept is properly
  // built, e.g. if all mandatory fields are present.
  public boolean isWellFormed() {
	// In this case we say it is well formed if the property X, that we
	// declared as mandatory, is present.
	// While you test your concept it is easier to return always true.
	return true;
  }

  // From here onwards we declare the getter and setters and other helper
  // methods for our declared properties
  // These are NOT MANDATORY, but are helpful for those who will use the
  // ontology.
  public String getID() {
	return (String) props.get(PROP_ID);
  }

  public void setID(String str) {
	props.put(PROP_ID, str);
  }
  
  public FactProperty[] getFactProperty() {
		Object propList = props.get(PROP_FactProperty);
		if (propList instanceof List) {
		    return (FactProperty[]) ((List) propList).toArray(new FactProperty[0]);
		} else {
		    List returnList = new ArrayList();
		    if (propList != null)
			returnList.add((FactProperty) propList);
		    return (FactProperty[]) returnList.toArray(new FactProperty[0]);
		}
	  }

  public void setFactProperty(FactProperty[] factProperty) {
	List propList = new ArrayList(factProperty.length);
	for (int i = 0; i < factProperty.length; i++) {
	    propList.add(factProperty[i]);
	}
	props.put(PROP_FactProperty, factProperty);
  }

  // Getters and setters are the most common, but you can add as many other
  // helper methods as you want, such as remove. Take into account that all
  // properties can always be handled with the methods of ManagedIndividual
  // and Resource, which all concepts inherit. The helper methods just make
  // developers life easier.


  // In the case of properties with multiple values, take into account that
  // they are handled internally (in the "props" element of the object) as a
  // List if there is more than one value.
  // In the helper method itself you can return or accept whatever format you
  // want (List or array) although it is recommended (it seems) to return and
  // accept arrays.
  // Although using List would be easier...
  
}