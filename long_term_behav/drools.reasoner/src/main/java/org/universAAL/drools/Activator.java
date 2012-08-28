package org.universAAL.drools;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.persistence.EntityManagerFactory;

//import org.dynamicjava.osgi.dynamic_jpa.DynamicJpaConstants;
//import org.dynamicjava.osgi.service_binding_utils.OsgiServiceBinder;
//import org.dynamicjava.osgi.service_binding_utils.ServiceFilter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
//import org.universAAL.drools.engine.DynamicJpaTestScenario;
import org.universAAL.drools.engine.RulesEngine;
import org.universAAL.drools.engine.Suscriber;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;

/**
 * Activator for the rules engine.
 * 
 * @author mllorente TSB Technologies for Health and Well-being
 */
public class Activator implements BundleActivator {

	private DroolsReasonerProvider provider = null;
	public static ModuleContext mc;
	RulesEngine rules = null;

	public void start(final BundleContext context) throws Exception {

		mc = uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context });

		// provider = new DroolsReasonerProvider(mc);
		provider = new DroolsReasonerProvider(mc);
		rules = RulesEngine.getInstance(context);
		LogUtils.logInfo(mc, getClass(), "start",
				new String[] { "UUID of the RulesEngine instance: "
						+ rules.getUUID() }, null);
		if (!rules.isWellFormed())
			LogUtils.logError(mc, getClass(), "start",
					new String[] { "ERROR with Rules Engine" }, null);

		RulesEngine re = RulesEngine.getInstance(context);

		new Suscriber(context, rules);
		
//		OsgiServiceBinder osgiServiceBinder = new OsgiServiceBinder(context);
//		
//		
//		System.out.println("New");
//		DynamicJpaTestScenario dynamicJpaTestScenario = new DynamicJpaTestScenario(context);
//		System.out.println("New executed. Binding...");
//		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
//		Thread.currentThread().setContextClassLoader(org.hibernate.ejb.HibernatePersistence.class.getClassLoader());
//		Thread.currentThread().setContextClassLoader(javax.persistence.spi.ProviderUtil.class.getClassLoader());
//		Thread.currentThread().setContextClassLoader(org.hibernate.HibernateException.class.getClassLoader());
//		//Thread.currentThread().setContextClassLoader(org.hibernate.internal.util.xml.Origin.class.getClassLoader());
//		Class.forName("org.hibernate.internal.util.xml.Origin");
//		Thread.currentThread().setContextClassLoader(tccl);
//		osgiServiceBinder.bind(dynamicJpaTestScenario, "setEntityManagerFactory",
//				ServiceFilter.forInterfaceAndServiceProperties(
//						EntityManagerFactory.class.getName(),
//						getEntityManagerFactoryServiceProperties()));
//		System.out.println("Bind exectuted");
		
	}

	public void stop(BundleContext arg0) throws Exception {
		if (provider != null) {
			provider.close();
			provider = null;
		}

	}
//	
//	protected Dictionary<String, String> getEntityManagerFactoryServiceProperties() {
//		Dictionary<String, String> result = new Hashtable<String, String>();
//		/// You can use the string "persistenceUnit" if you don't want to have references to 
//		/// Dynamic-JPA from your code.
//		result.put(DynamicJpaConstants.PERSISTENCE_UNIT_PROPERTY, "OrmTestDbPersistenceUnit");
//		
//		/// Since we are interested in the Dynamic Factory (Factories which produce updatable Entity
//		/// Managers and look for Entity Manager Factories in the OSGi Environment) not ordinary ones
//		/// for this persistence unit we added this line of code:
//		/// You can use the string "isDynamicFactory" if you don't want to have references to 
//		/// Dynamic-JPA from your code.
//		result.put(DynamicJpaConstants.IS_DYNAMIC_FACTORY_PROPERTY, "true");
//		return result;
//	}

}