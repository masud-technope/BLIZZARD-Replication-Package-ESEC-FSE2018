/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.provider;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.provider.IContainerInstantiator;

/**
 * Basic implementation of IRemoteServiceDistributionProvider.  Intended to be subclassed by distribution
 * provider implementations and or use Builder static inner class to create/build instances.
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
public class RemoteServiceDistributionProvider implements IRemoteServiceDistributionProvider {

    private String name;

    private IContainerInstantiator instantiator;

    private String description;

    private boolean server;

    private boolean hidden;

    private Dictionary<String, ?> ctdProperties;

    private Namespace namespace;

    private Dictionary<String, ?> nsProperties;

    private List<AdapterConfig> adapterConfigs = new ArrayList<AdapterConfig>();

    /**
	 * Builder for RemoteServiceDistributionProvider instances
	 *
	 */
    public static class Builder {

        private final RemoteServiceDistributionProvider instance;

        public  Builder() {
            this.instance = new RemoteServiceDistributionProvider();
        }

        public Builder setName(String name) {
            this.instance.setName(name);
            return this;
        }

        public Builder setInstantiator(IContainerInstantiator instantiator) {
            this.instance.setInstantiator(instantiator);
            return this;
        }

        public Builder setDescription(String desc) {
            this.instance.setDescription(desc);
            return this;
        }

        public Builder setServer(boolean server) {
            this.instance.setServer(server);
            return this;
        }

        public Builder setHidden(boolean hidden) {
            this.instance.setHidden(hidden);
            return this;
        }

        public Builder setNamespace(Namespace ns) {
            this.instance.setNamespace(ns);
            return this;
        }

        public Builder setContainerTypeDescriptionProperties(Dictionary<String, ?> props) {
            this.instance.setContainerTypeDescriptionProperties(props);
            return this;
        }

        public Builder setNamespaceProperties(Dictionary<String, ?> props) {
            this.instance.setNamespaceProperties(props);
            return this;
        }

        public Builder setAdapterConfig(AdapterConfig adapterConfig) {
            this.instance.setAdapterConfig(adapterConfig);
            return this;
        }

        public Builder addAdapterConfig(AdapterConfig adapterConfig) {
            this.instance.addAdapterConfig(adapterConfig);
            return this;
        }

        public RemoteServiceDistributionProvider build() {
            this.instance.validateComplete();
            return this.instance;
        }
    }

    protected  RemoteServiceDistributionProvider() {
    }

    protected  RemoteServiceDistributionProvider(String name) {
        setName(name);
    }

    protected  RemoteServiceDistributionProvider(String name, IContainerInstantiator instantiator) {
        setName(name).setInstantiator(instantiator);
    }

    protected  RemoteServiceDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
        setName(name).setInstantiator(instantiator).setDescription(description);
    }

    protected  RemoteServiceDistributionProvider(String name, IContainerInstantiator instantiator, String description, boolean server) {
        setName(name).setInstantiator(instantiator).setDescription(description).setServer(server);
    }

    protected String getName() {
        return this.name;
    }

    protected RemoteServiceDistributionProvider setName(String name) {
        Assert.isNotNull(name);
        this.name = name;
        return this;
    }

    protected IContainerInstantiator getInstantiator() {
        return instantiator;
    }

    protected RemoteServiceDistributionProvider setInstantiator(IContainerInstantiator instantiator) {
        Assert.isNotNull(instantiator);
        this.instantiator = instantiator;
        return this;
    }

    protected String getDescription() {
        return this.description;
    }

    protected RemoteServiceDistributionProvider setDescription(String desc) {
        this.description = desc;
        return this;
    }

    protected boolean isServer() {
        return this.server;
    }

    protected RemoteServiceDistributionProvider setServer(boolean server) {
        this.server = server;
        return this;
    }

    protected boolean isHidden() {
        return this.hidden;
    }

    protected RemoteServiceDistributionProvider setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    protected RemoteServiceDistributionProvider setNamespace(Namespace ns) {
        this.namespace = ns;
        Assert.isNotNull(ns);
        return this;
    }

    protected RemoteServiceDistributionProvider setContainerTypeDescriptionProperties(Dictionary<String, ?> props) {
        this.ctdProperties = props;
        Assert.isNotNull(this.ctdProperties);
        return this;
    }

    protected RemoteServiceDistributionProvider setNamespaceProperties(Dictionary<String, ?> props) {
        this.nsProperties = props;
        Assert.isNotNull(this.nsProperties);
        return this;
    }

    protected RemoteServiceDistributionProvider addAdapterConfig(AdapterConfig adapterConfig) {
        Assert.isNotNull(adapterConfig);
        this.adapterConfigs.add(adapterConfig);
        return this;
    }

    protected RemoteServiceDistributionProvider setAdapterConfig(AdapterConfig adapterConfig) {
        addAdapterConfig(adapterConfig);
        return this;
    }

    protected void validateComplete() throws NullPointerException {
        String ctdName = getName();
        if (ctdName == null)
            //$NON-NLS-1$
            throw new NullPointerException("Container type description name cannot be null");
        IContainerInstantiator ctdInstantiator = getInstantiator();
        if (ctdInstantiator == null)
            //$NON-NLS-1$
            throw new NullPointerException("Container type description instantiator cannot be null");
    }

    public ContainerTypeDescription getContainerTypeDescription() {
        validateComplete();
        return new ContainerTypeDescription(getName(), getInstantiator(), getDescription(), isServer(), isHidden());
    }

    public Dictionary<String, ?> getContainerTypeDescriptionProperties() {
        return ctdProperties;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public Dictionary<String, ?> getNamespaceProperties() {
        return nsProperties;
    }

    public AdapterConfig[] getAdapterConfigs() {
        return adapterConfigs.toArray(new AdapterConfig[adapterConfigs.size()]);
    }
}
