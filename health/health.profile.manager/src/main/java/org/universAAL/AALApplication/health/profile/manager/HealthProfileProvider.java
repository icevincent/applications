package org.universAAL.AALApplication.health.profile.manager;

import org.universaal.ontology.health.owl.HealthProfile;

public interface HealthProfileProvider {

	/**
	 * Returns the {org.universAAL.ontology.profile.health.HealthProfile} of the
	 * given user.
	 * 
	 * @param userURI The URI of the user
	 * 
	 * @return The health profile of the user
	 */
	/*
	public HealthProfile getHealthProfile(String userURI) {
	
		ServiceRequest req = new ServiceRequest(new ProfilingService(null), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, userURI);
		req.addRequiredOutput(OUTPUT_SUB_PROFILE, new String[] { 
				ProfilingService.PROP_CONTROLS, 
				Profilable.PROP_HAS_PROFILE, 
				Profile.PROP_HAS_SUB_PROFILE });
	
		ServiceResponse sr = caller.call(req);
		if(sr.getCallStatus() == CallStatus.succeeded) {
			try {
		    	List<?> subProfiles = sr.getOutput(OUTPUT_SUB_PROFILE, true);
		    	if(subProfiles == null || subProfiles.size() == 0) {
		    		moduleContext.logInfo("there are no sub profiles, creating default health subprofile", null);
		    		return newHealthProfile(userURI);
		    	}
		    	Iterator<?> iter = subProfiles.iterator();
		    	while(iter.hasNext()) {
		    		SubProfile subProfile = (SubProfile)iter.next();
		    		if(subProfile.getClassURI().equals(HealthProfile.MY_URI)) {
		    			return (HealthProfile)subProfile;
		    		}
		    	}
	    		moduleContext.logInfo("there is no health profile, creating default one", null);
	    		return newHealthProfile(userURI);
			} catch(Exception e) {
				moduleContext.logError( "got exception", e);
				return null;
		    }
		} else {
			moduleContext.logWarn("callstatus is not succeeded", null);
			return null;
		}
	}
	 */
	public abstract HealthProfile getHealthProfile(String userURI);

	/**
	 * Updates the {org.universAAL.ontology.profile.health.HealthProfile}.
	 *  
	 * @param healthProfile The updated health profile
	 */
	/*
	public void updateHealthProfile(HealthProfile healthProfile) {
	
		ServiceRequest req = new ServiceRequest(new ProfilingService(null), null);
		req.addAddEffect(new String[] { 
				ProfilingService.PROP_CONTROLS, 
				Profilable.PROP_HAS_PROFILE, 
				Profile.PROP_HAS_SUB_PROFILE }, healthProfile);
	
		 ServiceResponse sr = caller.call(req);
		 if(sr.getCallStatus() != CallStatus.succeeded) {
			 moduleContext.logWarn("callstatus is not succeeded", null);
		 }
	}
	 */
	public abstract void updateHealthProfile(HealthProfile healthProfile);

}