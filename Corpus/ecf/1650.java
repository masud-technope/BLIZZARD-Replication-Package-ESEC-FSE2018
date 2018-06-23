/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.DiscoveryContainerConfig;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

/**
 * @author markus
 *
 */
public class DnsSdDiscoveryLocator extends DnsSdDiscoveryContainerAdapter {

    //$NON-NLS-1$
    private static final String DNS_SD_PATH = "path";

    //$NON-NLS-1$
    private static final String DNS_SD_PTCL = "dns-sd.ptcl";

    public  DnsSdDiscoveryLocator() {
        super(DnsSdNamespace.NAME, new DiscoveryContainerConfig(IDFactory.getDefault().createStringID(DnsSdDiscoveryLocator.class.getName())));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryLocator#getServiceInfo(org.eclipse
	 * .ecf.discovery.identity.IServiceID)
	 */
    public IServiceInfo getServiceInfo(IServiceID aServiceId) {
        Assert.isNotNull(aServiceId);
        IServiceInfo[] services = getServices(aServiceId.getServiceTypeID());
        for (int i = 0; i < services.length; i++) {
            //TODO This can be a lot faster if done directly instead of via org.eclipse.ecf.provider.dnssrv.DnsSrvDisocoveryLocator.getServices(IServiceTypeID)
            IServiceInfo iServiceInfo = services[i];
            if (iServiceInfo.getServiceID().equals(aServiceId)) {
                return iServiceInfo;
            }
        }
        return null;
    }

    /**
	 * This always returns the service type found for our local domain
	 * Use org.eclipse.ecf.provider.dnssrv.DnsSrvDisocoveryLocator.getServices(IServiceTypeID) with a wildcard query instead.
	 * 
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServiceTypes()
	 */
    public IServiceTypeID[] getServiceTypes() {
        // technically can't do anything without a scope (domain) -> falling back to local domain (mDNS?)
        DnsSdServiceTypeID serviceTypeId = (DnsSdServiceTypeID) targetID;
        return getServiceTypes(serviceTypeId);
    }

    private IServiceTypeID[] getServiceTypes(final DnsSdServiceTypeID serviceTypeId) {
        List result = new ArrayList();
        Record[] queryResult = getRecords(serviceTypeId);
        for (int j = 0; j < queryResult.length; j++) {
            Record record = queryResult[j];
            if (record instanceof PTRRecord) {
                PTRRecord ptrRecord = (PTRRecord) record;
                result.add(new DnsSdServiceTypeID(getServicesNamespace(), ptrRecord.getTarget()));
            } else if (record instanceof SRVRecord) {
                SRVRecord srvRecord = (SRVRecord) record;
                result.add(new DnsSdServiceTypeID(getServicesNamespace(), srvRecord.getName()));
            }
        }
        return (IServiceTypeID[]) result.toArray(new IServiceTypeID[result.size()]);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServices()
	 */
    public IServiceInfo[] getServices() {
        // technically can't do anything without a scope (domain) -> falling back to local domain (mDNS?)
        return getServices(targetID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryLocator#getServices(org.eclipse.ecf
	 * .discovery.identity.IServiceTypeID)
	 */
    public IServiceInfo[] getServices(IServiceTypeID aServiceTypeId) {
        Assert.isNotNull(aServiceTypeId);
        DnsSdServiceTypeID serviceTypeId = (DnsSdServiceTypeID) aServiceTypeId;
        Collection srvRecords = getSRVRecords(serviceTypeId.getInternalQueries());
        List serviceInfos = getServiceInfos(srvRecords);
        return (IServiceInfo[]) serviceInfos.toArray(new IServiceInfo[serviceInfos.size()]);
    }

    private List getServiceInfos(Collection srvQueryResult) {
        List infos = new ArrayList();
        for (Iterator iterator = srvQueryResult.iterator(); iterator.hasNext(); ) {
            SRVRecord srvRecord = (SRVRecord) iterator.next();
            long ttl = srvRecord.getTTL();
            int priority = srvRecord.getPriority();
            int weight = srvRecord.getWeight();
            int port = srvRecord.getPort();
            Name target = srvRecord.getTarget();
            String host = target.toString();
            host = host.substring(0, host.length() - 1);
            IServiceTypeID aServiceTypeID = new DnsSdServiceTypeID(getConnectNamespace(), srvRecord.getName());
            // query for txt records (attributes)
            Properties props = new Properties();
            Lookup txtQuery = new Lookup(srvRecord.getName(), Type.TXT);
            txtQuery.setResolver(resolver);
            Record[] txtQueryResults = txtQuery.run();
            int length = txtQueryResults == null ? 0 : txtQueryResults.length;
            for (int l = 0; l < length; l++) {
                TXTRecord txtResult = (TXTRecord) txtQueryResults[l];
                List strings = txtResult.getStrings();
                for (Iterator itr = strings.iterator(); itr.hasNext(); ) {
                    String str = (String) itr.next();
                    String[] //$NON-NLS-1$
                    split = //$NON-NLS-1$
                    str.split("=");
                    props.put(split[0], split[1]);
                }
            }
            String path = props.getProperty(DNS_SD_PATH);
            String proto = props.getProperty(DNS_SD_PTCL) == null ? aServiceTypeID.getProtocols()[0] : props.getProperty(DNS_SD_PTCL);
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            URI uri = URI.create(proto + "://" + host + ":" + port + (path == null ? "" : path));
            IServiceInfo info = new ServiceInfo(uri, host, aServiceTypeID, priority, weight, new ServiceProperties(props), ttl);
            infos.add(info);
        }
        return infos;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID,
	 * org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(ID aTargetID, IConnectContext connectContext) throws ContainerConnectException {
        // connect can only be called once
        if (targetID != null || getConfig() == null) {
            throw new ContainerConnectException(Messages.DnsSdDiscoveryLocator_Container_Already_Connected);
        }
        // fall back to the search path as last resort 
        if (aTargetID == null || !(aTargetID instanceof DnsSdServiceTypeID)) {
            ResolverConfig config = new ResolverConfig();
            Name[] searchPaths = config.searchPath();
            if (searchPaths != null && searchPaths.length > 0) {
                targetID = new DnsSdServiceTypeID();
                targetID.setSearchPath(searchPaths);
            } else {
                throw new ContainerConnectException(Messages.DnsSdDiscoveryLocator_No_Target_ID);
            }
        } else {
            final Namespace ns = getConnectNamespace();
            try {
                targetID = (DnsSdServiceTypeID) ns.createInstance(new Object[] { aTargetID });
            } catch (IDCreateException e) {
                throw new ContainerConnectException(e);
            }
        }
        // instantiate a default resolver
        if (resolver == null) {
            try {
                resolver = new SimpleResolver();
            } catch (UnknownHostException e) {
                throw new ContainerConnectException(e);
            }
        }
        // read browsing domains for the given targetID/searchpath and merge with existing
        targetID.addSearchPath(getBrowsingDomains(targetID));
        // done setting up this provider, send event
        fireContainerEvent(new ContainerConnectingEvent(this.getID(), targetID, connectContext));
        fireContainerEvent(new ContainerConnectedEvent(this.getID(), targetID));
    }

    private String[] getBrowsingDomains(IServiceTypeID aServiceTypeId) {
        final String[] rrs = new String[] { BnRDnsSdServiceTypeID.BROWSE_DOMAINS, BnRDnsSdServiceTypeID.DEFAULT_BROWSE_DOMAIN };
        final Collection res = getBrowsingOrRegistrationDomains(aServiceTypeId, rrs);
        return (String[]) res.toArray(new String[res.size()]);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#getContainerName()
	 */
    public String getContainerName() {
        return Activator.DISCOVERY_CONTAINER_NAME_VALUE + Activator.LOCATOR;
    }
}
