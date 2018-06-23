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
package org.eclipse.ecf.tests.provider.dnssd;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.provider.dnssd.DnsSdDiscoveryAdvertiser;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.ZoneTransferIn;

public class DnsSdAdvertiserServiceTest extends DnsSdAbstractDiscoveryTest {

    public  DnsSdAdvertiserServiceTest() {
        super();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.AbstractDiscoveryTest#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        // purge all SRV records from the test domain
        ZoneTransferIn xfr = ZoneTransferIn.newAXFR(new Name(DnsSdTestHelper.REG_DOMAIN), DnsSdTestHelper.DNS_SERVER, null);
        List records = xfr.run();
        for (Iterator itr = records.iterator(); itr.hasNext(); ) {
            Record record = (Record) itr.next();
            String name = record.getName().toString();
            if (name.startsWith("_" + DnsSdTestHelper.REG_SCHEME + "._" + DnsSdTestHelper.PROTO)) {
                Update update = new Update(Name.fromString(DnsSdTestHelper.REG_DOMAIN + "."));
                update.delete(record);
                SimpleResolver resolver = new SimpleResolver(DnsSdTestHelper.DNS_SERVER);
                resolver.setTCP(true);
                resolver.setTSIGKey(new TSIG(DnsSdTestHelper.TSIG_KEY_NAME, DnsSdTestHelper.TSIG_KEY));
                Message response = resolver.send(update);
                int rcode = response.getRcode();
                assertTrue("", rcode == 0);
            }
        }
    }

    private void createTXTRecords() throws TextParseException, IOException, UnknownHostException {
        final Name zone = Name.fromString(DnsSdTestHelper.REG_DOMAIN + ".");
        final Name name = Name.fromString("_" + DnsSdTestHelper.REG_SCHEME + "._" + DnsSdTestHelper.PROTO, zone);
        Update update = null;
        final IServiceProperties properties = serviceInfo.getServiceProperties();
        final Enumeration enumeration = properties.getPropertyNames();
        while (enumeration.hasMoreElements()) {
            final Object property = enumeration.nextElement();
            final String key = property.toString();
            final String value = (String) properties.getProperty(key).toString();
            final Record record = Record.fromString(name, Type.TXT, DClass.IN, serviceInfo.getTTL(), key + "=" + value, zone);
            update = new Update(zone);
            update.add(record);
        }
        final SimpleResolver resolver = new SimpleResolver(DnsSdTestHelper.DNS_SERVER);
        resolver.setTCP(true);
        resolver.setTSIGKey(new TSIG(DnsSdTestHelper.TSIG_KEY_NAME, DnsSdTestHelper.TSIG_KEY));
        final Message response = resolver.send(update);
        final int rcode = response.getRcode();
        assertTrue("", rcode == 0);
    }

    private void createSRVRecord() throws TextParseException, IOException, UnknownHostException {
        // create a service manually
        final Name zone = Name.fromString(DnsSdTestHelper.REG_DOMAIN + ".");
        final Name type = Name.fromString("_" + DnsSdTestHelper.REG_SCHEME + "._" + DnsSdTestHelper.PROTO, zone);
        final String s = serviceInfo.getPriority() + " " + serviceInfo.getWeight() + " " + serviceInfo.getLocation().getPort() + " " + serviceInfo.getLocation().getHost() + ".";
        final Record record = Record.fromString(type, Type.SRV, DClass.IN, DnsSdTestHelper.TTL, s, zone);
        final Update update = new Update(zone);
        update.add(record);
        final SimpleResolver resolver = new SimpleResolver(DnsSdTestHelper.DNS_SERVER);
        resolver.setTCP(true);
        resolver.setTSIGKey(new TSIG(DnsSdTestHelper.TSIG_KEY_NAME, DnsSdTestHelper.TSIG_KEY));
        final Message response = resolver.send(update);
        final int rcode = response.getRcode();
        assertTrue("", rcode == 0);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.provider.dnssd.DnsSdDiscoveryServiceTest#testRegisterService()
	 */
    public void testRegisterService() throws Exception {
        // actually register the service
        discoveryAdvertiser.registerService(serviceInfo);
        // check postcondition service is registered
        final ZoneTransferIn xfr = ZoneTransferIn.newAXFR(new Name(DnsSdTestHelper.REG_DOMAIN), DnsSdTestHelper.DNS_SERVER, null);
        assertTrue("Mismatch between DNS response and IServiceInfo", comparator.compare(serviceInfo, xfr.run()) == 0);
    }

    /**
	 * Tests that a register is handled correctly when no key is present
	 * for that domain and the underlying ddns call fails
	 * @throws ContainerConnectException 
	 */
    public void testRegisterServiceWithoutHostKey() throws ContainerConnectException {
        final DnsSdDiscoveryAdvertiser advertiser = new DnsSdDiscoveryAdvertiser();
        advertiser.connect(null, null);
        try {
            advertiser.registerService(serviceInfo);
        } catch (ECFRuntimeException e) {
            advertiser.disconnect();
            return;
        }
        fail("Register should have failed without a host key given.");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.provider.dnssd.DnsSdDiscoveryServiceTest#testUnregisterService()
	 */
    public void testUnregisterService() throws ContainerConnectException, IOException, ZoneTransferException {
        createSRVRecord();
        // unregister via ECF discovery
        discoveryAdvertiser.unregisterService(serviceInfo);
        // check SRV record is gone
        final ZoneTransferIn xfr = ZoneTransferIn.newAXFR(new Name(DnsSdTestHelper.REG_DOMAIN), DnsSdTestHelper.DNS_SERVER, null);
        final List records = xfr.run();
        for (final Iterator itr = records.iterator(); itr.hasNext(); ) {
            final Record record = (Record) itr.next();
            if (record instanceof SRVRecord) {
                if (comparator.compare(serviceInfo, record) >= 0) {
                    fail("Service not removed/unregisterd");
                }
            }
        }
    }

    /**
	 * Test that all service properties are removed along with the service
	 */
    public void testUnregisterServiceWithProperties() throws ContainerConnectException, IOException, ZoneTransferException {
        createSRVRecord();
        createTXTRecords();
        // unregister via ECF discovery
        discoveryAdvertiser.unregisterService(serviceInfo);
        // check SRV record is gone
        final ZoneTransferIn xfr = ZoneTransferIn.newAXFR(new Name(DnsSdTestHelper.REG_DOMAIN), DnsSdTestHelper.DNS_SERVER, null);
        final List records = xfr.run();
        for (final Iterator itr = records.iterator(); itr.hasNext(); ) {
            final Record record = (Record) itr.next();
            if (record instanceof SRVRecord) {
                if (comparator.compare(serviceInfo, record) >= 0) {
                    fail("Service not removed/unregisterd");
                }
            } else if (record instanceof TXTRecord) {
                if (comparator.compare(serviceInfo, record) >= 0) {
                    fail("TXT record not removed/unregisterd");
                }
            }
        }
    }

    /**
	 * Test that all service properties are removed along with the service
	 */
    public void testUnregisterServiceWithForeignProperties() throws ContainerConnectException, IOException, ZoneTransferException {
        fail("Not implemented yet");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.provider.dnssd.DnsSdDiscoveryServiceTest#testUnregisterAllServices()
	 */
    public void testUnregisterAllServices() throws ContainerConnectException {
        //TODO make sure not to accidentally remove pre existing SRV records
        fail("Not yet implemented");
    }
}
