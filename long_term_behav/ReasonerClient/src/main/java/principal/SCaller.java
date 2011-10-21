package principal;

import org.osgi.framework.BundleContext;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;




public class SCaller extends ServiceCaller{

	private ServiceCaller caller; // to call a service
	
	protected SCaller(BundleContext context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		System.out.println("IM CALLING REASONER-ADDRULE");
		
		ServiceResponse sr;

		// I create a object service request

		ServiceRequest req = new ServiceRequest(new ServiceReasoner(null), null);

		// I configure the request for the call.
		req.addTypeFilter(new String[] { ServiceReasoner.PROPERTY_CONTROLS }, ServiceReasoner.MY_URI);

		// output_temp id of the uri.

		req.addRequiredOutput(SCalleeProvidedService.SERVER_NAMESPACE
				+ "output_temp",
				new String[] { ServiceReasoner.PROPERTY_CONTROLS,
				ServiceReasoner.MY_URI });

		// I call the service

		sr = caller.call(req);
		
		
		
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}

	public void handleResponse(String reqID, ServiceResponse response) {
		// TODO Auto-generated method stub
		
	}

}