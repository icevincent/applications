package mainclasses;

import org.universAAL.middleware.input.InputEvent;
import org.universAAL.middleware.io.rdf.Form;
import org.universAAL.middleware.io.rdf.InputField;
import org.universAAL.middleware.io.rdf.Label;
import org.universAAL.middleware.io.rdf.Submit;
import org.universAAL.middleware.rdf.Resource;

public class InputSubscriber {
	static final String INPUT_NAMESPACE = SCalleeProvidedService.SERVICE_START_UI+"InputSubscriber#";
	static final String CONFIGURATION_MENU = INPUT_NAMESPACE+"ConfigurationMenu";
	
	
	public void handleInputEvent(InputEvent ie) {
		
		
		
		  if (ie != null) {
		   if (mainclasses.OutputPublisher.SUBMISSION_ON.equals(ie.getSubmissionID())){
			     
			   Activator.opublisher.initMainDialog();
			   return;
			   
		   }
			   
		   else if (mainclasses.OutputPublisher.CONFIGURATION.equals(ie.getSubmissionID())){
			   
			   Activator.opublisher.showConfigurationMenu();
		   
		    return;
		  }
		   // Cancel Dialog, go back to main dialog
	
		  }
}

}