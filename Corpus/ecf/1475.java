/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.DiscoveryContainerConfig;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;

public class DnsSdDiscoveryAdvertiser extends DnsSdDiscoveryContainerAdapter {

    //$NON-NLS-1$
    private static final String _DNS_UPDATE = "_dns-update._udp.";

    private static final boolean ADD = true;

    private static final boolean REMOVE = false;

    public  DnsSdDiscoveryAdvertiser() {
        super(DnsSdNamespace.NAME, new DiscoveryContainerConfig(IDFactory.getDefault().createStringID(DnsSdDiscoveryAdvertiser.class.getName())));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#registerService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void registerService(final IServiceInfo serviceInfo) {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "registerService(IServiceInfo serviceInfo)", "Registering service");
        sendToServer(serviceInfo, ADD);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void unregisterService(final IServiceInfo serviceInfo) {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "unregisterService(IServiceInfo serviceInfo)", "Unregistering service");
        sendToServer(serviceInfo, REMOVE);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#unregisterAllServices()
	 */
    public void unregisterAllServices() {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "unregisterAllServices()", "Unregistering all services");
        //$NON-NLS-1$
        throw new UnsupportedOperationException("Not yet implemented, see http://bugs.eclipse.org/321959");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#purgeCache()
	 */
    public IServiceInfo[] purgeCache() {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("Not yet implemented, see http://bugs.eclipse.org/");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(final ID aTargetID, final IConnectContext connectContext) throws ContainerConnectException {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "connect(ID aTargetID, IConnectContext connectContext)", "connecting container");
        // connect can only be called once
        if (targetID != null || getConfig() == null) {
            throw new ContainerConnectException(Messages.DnsSdDiscoveryAdvertiser_Container_Already_Connected);
        }
        //TODO convert non DnsSdServiceTypeIDs into DSTIDs
        if (aTargetID == null) {
            targetID = new DnsSdServiceTypeID();
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
                resolver.setTCP(true);
            } catch (UnknownHostException e) {
                throw new ContainerConnectException(e);
            }
        }
        // done setting up this provider, send event
        fireContainerEvent(new ContainerConnectingEvent(this.getID(), targetID, connectContext));
        fireContainerEvent(new ContainerConnectedEvent(this.getID(), targetID));
    }

    protected void sendToServer(final IServiceInfo serviceInfo, final boolean mode) {
        Assert.isNotNull(serviceInfo);
        Assert.isLegal(serviceInfo.getServiceID() instanceof DnsSdServiceID);
        final DnsSdServiceID serviceID = (DnsSdServiceID) serviceInfo.getServiceID();
        try {
            // TYPE.SRV
            final Record srvRecord = serviceID.toSRVRecord();
            // TYPE.TXT
            final Record[] txtRecords = serviceID.toTXTRecords(srvRecord);
            final Name name = serviceID.getDnsName();
            final String[] registrationDomains = getRegistrationDomains(serviceID.getServiceTypeID());
            for (int i = 0; i < registrationDomains.length; i++) {
                final Name zone = new Name(registrationDomains[i]);
                final Name fqdn = new //$NON-NLS-1$
                Name(//$NON-NLS-1$
                name.toString() + "." + zone.toString());
                final Update update = new Update(zone);
                //TYPE.SRV
                if (mode == ADD) {
                    //TODO add absent/present condition checks
                    update.replace(srvRecord.withName(fqdn));
                } else {
                    update.delete(srvRecord.withName(fqdn));
                }
                //TYPE.TXT
                for (int j = 0; j < txtRecords.length; j++) {
                    if (mode == ADD) {
                        update.add(txtRecords[j].withName(fqdn));
                    } else {
                        update.delete(txtRecords[j].withName(fqdn));
                    }
                }
                // set up a the resolver for the given domain (a scope might use different domains)
                final Collection dnsServers = getUpdateDomain(zone);
                if (dnsServers.size() == 0) {
                    throw new DnsSdDiscoveryException(Messages.DnsSdDiscoveryAdvertiser_No_DynDns_Servers_Found);
                }
                for (final Iterator iterator = dnsServers.iterator(); iterator.hasNext(); ) {
                    final SRVRecord dnsServer = (SRVRecord) iterator.next();
                    // try to send msg and fail gracefully if more dns servers are available
                    final Name target = dnsServer.getTarget();
                    final Message response;
                    final InetAddress byName;
                    try {
                        byName = InetAddress.getByName(target.toString());
                        ((SimpleResolver) resolver).setAddress(byName);
                        ((SimpleResolver) resolver).setPort(dnsServer.getPort());
                        response = resolver.send(update);
                    } catch (UnknownHostException uhe) {
                        if (iterator.hasNext()) {
                            continue;
                        } else {
                            throw new DnsSdDiscoveryException(uhe);
                        }
                    } catch (EOFException eof) {
                        if (iterator.hasNext()) {
                            continue;
                        } else {
                            throw new DnsSdDiscoveryException(eof);
                        }
                    }
                    // catch some errors and fall back to the next dnsServer
                    if (response.getRcode() != Rcode.NOERROR) {
                        if (iterator.hasNext()) {
                            continue;
                        } else {
                            throw DnsSdDiscoveryException.getException(response.getRcode());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DnsSdDiscoveryException(e);
        }
    }

    protected Collection getUpdateDomain(final Name zone) throws TextParseException {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "getUpdateDomain(Name zone)", "Getting update domain");
        // query for special "_dns-update" SRV records which mark the server to use for dyndns
        final Lookup query = new Lookup(_DNS_UPDATE + zone, Type.SRV);
        // use the SRV record with the lowest priority/weight first
        final SortedSet srvRecords = getSRVRecord(query, new SRVRecordComparator());
        // if no dedicated "_dns-update" server is configured, fall back to regular authoritative server
        if (srvRecords.size() == 0) {
            //$NON-NLS-1$ //$NON-NLS-2$
            Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "getUpdateDomain(Name zone)", "Found no _dns-update SRV records in zone");
            return getAuthoritativeNameServer(zone);
        }
        return srvRecords;
    }

    protected Collection getAuthoritativeNameServer(final Name zone) throws TextParseException {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "getAuthoritativeNameServer(Name zone)", "Trying to find authoritative name server");
        final Set result = new HashSet();
        final Name name = new Name(_DNS_UPDATE + zone);
        //query for NS records
        Lookup query = new Lookup(zone, Type.NS);
        query.setResolver(resolver);
        Record[] queryResult = query.run();
        //TODO file bug upstream that queryResult may never be null
        int length = queryResult == null ? 0 : queryResult.length;
        for (int j = 0; j < length; j++) {
            final Record record = queryResult[j];
            if (record instanceof NSRecord) {
                final NSRecord nsRecord = (NSRecord) record;
                final Name target = nsRecord.getTarget();
                result.add(new SRVRecord(name, DClass.IN, nsRecord.getTTL(), 0, 0, SimpleResolver.DEFAULT_PORT, target));
            }
        }
        //query for primary ns in SOA record (may overwrite/be equal to one of the ns records)
        query = new Lookup(zone, Type.SOA);
        query.setResolver(resolver);
        queryResult = query.run();
        //TODO file bug upstream that queryResult may never be null
        length = queryResult == null ? 0 : queryResult.length;
        for (int j = 0; j < length; j++) {
            final Record record = queryResult[j];
            if (record instanceof SOARecord) {
                final SOARecord soaRecord = (SOARecord) record;
                result.add(new SRVRecord(name, DClass.IN, soaRecord.getTTL(), 0, 0, SimpleResolver.DEFAULT_PORT, soaRecord.getHost()));
            }
        }
        return result;
    }

    protected String[] getRegistrationDomains(final IServiceTypeID aServiceTypeId) {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, DnsSdDebugOptions.METHODS_TRACING, this.getClass(), "getRegistrationDomains(IServiceTypeID aServiceTypeId)", "Getting registration domains");
        final String[] rrs = new String[] { BnRDnsSdServiceTypeID.REG_DOMAINS, BnRDnsSdServiceTypeID.DEFAULT_REG_DOMAIN };
        final Collection registrationDomains = getBrowsingOrRegistrationDomains(aServiceTypeId, rrs);
        final String[] scopes = aServiceTypeId.getScopes();
        for (int i = 0; i < scopes.length; i++) {
            //$NON-NLS-1$
            scopes[i] = scopes[i].concat(".");
        }
        return registrationDomains.size() == 0 ? scopes : (String[]) registrationDomains.toArray(new String[registrationDomains.size()]);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#getContainerName()
	 */
    public String getContainerName() {
        return Activator.DISCOVERY_CONTAINER_NAME_VALUE + Activator.ADVERTISER;
    }
}
