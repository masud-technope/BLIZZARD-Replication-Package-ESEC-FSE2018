package com.mycorp.examples.timeservice.internal.provider.rest.host;

import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(final BundleContext context) throws Exception {
        context.registerService(IRemoteServiceDistributionProvider.class, new RemoteServiceDistributionProvider.Builder().setName(TimeServiceServerContainer.TIMESERVICE_HOST_CONFIG_NAME).setInstantiator(new TimeServiceServerContainer.Instantiator()).build(), null);
    }

    public void stop(BundleContext context) throws Exception {
    }
}
