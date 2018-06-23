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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter;
import org.eclipse.ecf.discovery.DiscoveryContainerConfig;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.Type;

public abstract class DnsSdDiscoveryContainerAdapter extends AbstractDiscoveryContainerAdapter {

    protected Resolver resolver;

    protected DnsSdServiceTypeID targetID;

    public  DnsSdDiscoveryContainerAdapter(String aNamespaceName, DiscoveryContainerConfig aConfig) {
        super(aNamespaceName, aConfig);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IServiceInfo getServiceInfo(IServiceID aServiceId) {
        Assert.isNotNull(aServiceId);
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Locator);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#getServiceTypes()
	 */
    public IServiceTypeID[] getServiceTypes() {
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Locator);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#getServices()
	 */
    public IServiceInfo[] getServices() {
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Locator);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryLocator#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IServiceInfo[] getServices(IServiceTypeID aServiceTypeId) {
        Assert.isNotNull(aServiceTypeId);
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Locator);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#registerService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void registerService(IServiceInfo serviceInfo) {
        Assert.isNotNull(serviceInfo);
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Advertiser);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void unregisterService(IServiceInfo serviceInfo) {
        Assert.isNotNull(serviceInfo);
        // doesn't support this yet
        throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_No_IDiscovery_Advertiser);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
    public abstract void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return targetID;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public void disconnect() {
        fireContainerEvent(new ContainerDisconnectingEvent(this.getID(), getConnectedID()));
        targetID = null;
        fireContainerEvent(new ContainerDisconnectedEvent(this.getID(), getConnectedID()));
    }

    protected Collection getBrowsingOrRegistrationDomains(final IServiceTypeID aServiceTypeId, final String[] rrs) {
        final Set res = new HashSet();
        getBrowsingOrRegistrationDomains(aServiceTypeId, rrs, res);
        return res;
    }

    private void getBrowsingOrRegistrationDomains(final IServiceTypeID aServiceTypeId, final String[] rrs, final Set res) {
        for (int i = 0; i < rrs.length; i++) {
            final BnRDnsSdServiceTypeID serviceType = new BnRDnsSdServiceTypeID(aServiceTypeId, rrs[i]);
            final Record[] records = getRecords(serviceType);
            if (records.length == 0) {
                res.addAll(serviceType.getScopesAsZones());
            } else {
                for (int j = 0; j < records.length; j++) {
                    final PTRRecord record = (PTRRecord) records[j];
                    final String target = record.getTarget().toString();
                    if (isFinal(record)) {
                        res.add(target.toString());
                    } else {
                        if (// guard against cycles
                        !res.contains(target)) {
                            final BnRDnsSdServiceTypeID newServiceType = new BnRDnsSdServiceTypeID(serviceType, rrs[i]);
                            newServiceType.setScope(target);
                            getBrowsingOrRegistrationDomains(newServiceType, rrs, res);
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
    }

    private boolean isFinal(PTRRecord record) {
        Name name = record.getName();
        Name target = record.getTarget();
        return name.subdomain(target);
    }

    protected Record[] getRecords(final DnsSdServiceTypeID serviceTypeId) {
        final List result = new ArrayList();
        final Lookup[] queries = serviceTypeId.getInternalQueries();
        for (int i = 0; i < queries.length; i++) {
            final Lookup query = queries[i];
            query.setResolver(resolver);
            final Record[] queryResult = query.run();
            if (queryResult != null) {
                result.addAll(Arrays.asList(queryResult));
            }
        }
        return (Record[]) result.toArray(new Record[result.size()]);
    }

    protected SortedSet getSRVRecords(Lookup[] queries) {
        return getSRVRecords(queries, null);
    }

    protected SortedSet getSRVRecords(Lookup[] queries, Comparator aComparator) {
        final SortedSet srvRecords = new TreeSet(aComparator);
        for (int i = 0; i < queries.length; i++) {
            srvRecords.addAll(getSRVRecord(queries[i], aComparator));
        }
        return srvRecords;
    }

    protected SortedSet getSRVRecord(Lookup query, Comparator aComparator) {
        final SortedSet srvRecords = new TreeSet(aComparator);
        query.setResolver(resolver);
        final Record[] queryResult = query.run();
        //TODO file bug upstream that queryResult may never be null
        final int length = queryResult == null ? 0 : queryResult.length;
        for (int j = 0; j < length; j++) {
            Record[] srvQueryResult = null;
            final Record record = queryResult[j];
            if (record instanceof PTRRecord) {
                final PTRRecord ptrRecord = (PTRRecord) record;
                final Name target = ptrRecord.getTarget();
                final Lookup srvQuery = new Lookup(target, Type.SRV);
                srvQuery.setResolver(resolver);
                srvQueryResult = srvQuery.run();
            } else if (record instanceof SRVRecord) {
                srvQueryResult = new SRVRecord[] { (SRVRecord) record };
            } else {
                // avoid NPE
                srvQueryResult = new SRVRecord[0];
            }
            srvRecords.addAll(Arrays.asList(srvQueryResult));
        }
        return srvRecords;
    }

    // compares SRV records based on priority and weight
    protected class SRVRecordComparator implements Comparator {

        /* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
        public int compare(Object arg0, Object arg1) {
            if (arg0 instanceof SRVRecord && arg1 instanceof SRVRecord) {
                SRVRecord srv1 = (SRVRecord) arg0;
                SRVRecord srv2 = (SRVRecord) arg1;
                if (srv1.getPriority() > srv2.getPriority()) {
                    return 1;
                } else if (srv1.getPriority() == srv2.getPriority()) {
                    if (srv1.getWeight() > srv2.getWeight()) {
                        return 1;
                    }
                    return -1;
                } else {
                    return -1;
                }
            }
            throw new UnsupportedOperationException(Messages.DnsSdDiscoveryContainerAdapter_Comparator_SRV_Records);
        }
    }

    /**
	 * @param searchPaths The default search path used for discovery 
	 */
    public void setSearchPath(String[] searchPaths) {
        targetID.setSearchPath(searchPaths);
    }

    /**
	 * @return The default search path used by this discovery provider
	 */
    public String[] getSearchPath() {
        return targetID.getSearchPath();
    }

    /**
	 * @param aResolver The resolver to use
	 * @throws DnsSdDiscoveryException if hostname cannot be resolved
	 */
    public void setResolver(String aResolver) {
        try {
            resolver = new SimpleResolver(aResolver);
        } catch (UnknownHostException e) {
            throw new DnsSdDiscoveryException(e);
        }
    }

    /**
	 * @param tsigKeyName A key name/user name for dns dynamic update
	 * @param tsigKey A string representation of the shared key
	 */
    public void setTsigKey(String tsigKeyName, String tsigKey) {
        resolver.setTSIGKey(new TSIG(tsigKeyName, tsigKey));
    }
}
