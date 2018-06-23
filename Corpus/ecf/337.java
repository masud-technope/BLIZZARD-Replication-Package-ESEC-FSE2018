/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.provider;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * Service interface for registering distribution providers.   When IRemoteServiceDistributionProvider implementations
 * are registered, they result in the two methods below being called by the org.eclipse.ecf.remoteservice bundle, 
 * with the BundleContext from the org.eclipse.ecf.remoteservice bundle.  Intended to be implemented by remote 
 * service distribution provider implementations.  When instance of this service interface is registered, the 
 * methods below will be called in order to register the ContainerTypeDescription, Namespace, and AdapterConfig 
 * for this distribution provider.
 * <p>
 * <b>Please NOTE</b>:  IRemoteServiceDistributionProviders should be registered (and therefore the bundles containing implementations started) <b>before</b>
 * any remote services using these distribution providers are exported.  In other words, if you create and register
 * a IRemoteServiceDistributionProvider with name 'com.myproject.myprovider' the provider implementation bundle should 
 * be started and the IRemoteServiceDistributionProvider service must be registered prior to registering the service
 * that is to be exported using that provider.  For example
 * <p>
 * <pre>
 * #Must first register the com.myproject.myprovider distribution provider, so it's available
 * providerBuilder.setName('com.myproject.myprovider')...
 * bundleContext.registerService(IRemoteServiceDistributionProvider.class,providerBuilder.build(),null);
 * 
 * ...
 * 
 * #Then may register a remote service that uses com.myproject.myprovider distribution provider
 * props.put("service.exported.interfaces","*");
 * 
 * #This specifies that com.myproject.myprovider is to be used to export the service, but the above registration
 * #must take place before MyService registration so it can be active for exporting this service
 * props.put("service.exported.configs","com.myproject.myprovider");
 * 
 * #With usual topology manager the following will export MyService using com.myproject.myprovider
 * #distribution provider
 * bundleContext.registerService(MyService.class,new MyServiceImpl(),props);
 * </pre>
 * @since 8.7
 */
public interface IRemoteServiceDistributionProvider {

    /**
	 * Return the ContainerTypeDescription to register for this distribution provider.
	 * The returned ContainerTypeDescription must not be <code>null</code> and 
	 * should be unique identified via it's name (obtained via {@link ContainerTypeDescription#getName()}.
	 * 
	 * @return ContainerTypeDescription.  Must not be <code>null</code>.
	 */
    ContainerTypeDescription getContainerTypeDescription();

    /**
	 * Return any properties that are to be used when registering the ContainerTypeDescription
	 * returned by above method.  <code>Null</code> may be returned.
	 * @return Dictionary to use when registering the ContainerTypeDescription  
	 * May be <code>null</code>.
	 */
    Dictionary<String, ?> getContainerTypeDescriptionProperties();

    /**
	 * Return the Namespace (or subclass) to register for this distribution provider.
	 * The returned Namespace may be <code>null</code>.  In that case, no 
	 * new Namespace will be registered.  If the returned Namespace is non-null, 
	 * It should be uniquely identified via it's name (obtained via {@link Namespace#getName()}.
	 * 
	 * @return ContainerTypeDescription.  May be <code>null</code>.
	 */
    Namespace getNamespace();

    /**
	 * Return any properties that are to be used when registering the Namespace
	 * returned by above method.  <code>Null</code> may be returned.
	 * @return Dictionary to use when registering this provider's Namespace.  
	 * May be <code>null</code>.
	 */
    Dictionary<String, ?> getNamespaceProperties();

    /**
	 * Return any AdapterConfigs to register with the IAdapterManager
	 * @return AdapterConfig[] holding any AdapterConfigs to be registered
	 * with the system-wide adaptermanager.
	 */
    AdapterConfig[] getAdapterConfigs();
}
