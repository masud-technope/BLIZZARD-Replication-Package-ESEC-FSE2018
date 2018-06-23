/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * SLPConfiguration object holds all configurable properties.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
class SLPConfiguration {

    private static final String USE_SCOPES_PROP = "net.slp.useScopes";

    private static final String USE_SCOPES_DEFAULT = "DEFAULT";

    private static final String DA_ADDRESSES_PROP = "net.slp.DAAddresses";

    private static final String DA_ADDRESSES_DEFAULT = null;

    private static final String WAIT_TIME_PROP = "net.slp.waitTime";

    private static final String WAIT_TIME_DEFAULT = "1000";

    private static final String TRACE_DATRAFFIC_PROP = "net.slp.traceDATraffic";

    private static final String TRACE_DATRAFFIC_DEFAULT = "false";

    private static final String TRACE_MESSAGE_PROP = "net.slp.traceMsg";

    private static final String TRACE_MESSAGE_DEFAULT = "false";

    private static final String TRACE_DROP_PROP = "net.slp.traceDrop";

    private static final String TRACE_DROP_DEFAULT = "false";

    private static final String TRACE_REG_PROP = "net.slp.traceReg";

    private static final String TRACE_REG_DEFAULT = "false";

    private static final String MCAST_TTL_PROP = "net.slp.multicastTTL";

    private static final String MCAST_TTL_DEFAULT = "255";

    private static final String MCAST_MAX_WAIT_PROP = "net.slp.multicastMaximumWait";

    private static final String MCAST_MAX_WAIT_DEFAULT = "15000";

    private static final String MCAST_TIMEOUTS_PROP = "net.slp.multicastTimeouts";

    private static final String MCAST_TIMEOUTS_DEFAULT = "3000,2000,1500,1000,750,500";

    private static final String DATAGRAM_MAX_WAIT_PROP = "net.slp.datagramMaximumWait";

    private static final String DATAGRAM_MAX_WAIT_DEFAULT = "5000";

    private static final String DATAGRAM_TIMEOUTS_PROP = "net.slp.datagramTimeouts";

    private static final String DATAGRAM_TIMEOUTS_DEFAULT = "3000,3000,3000,3000,3000";

    private static final String MTU_PROP = "net.slp.MTU";

    private static final String MTU_DEFAULT = "1400";

    private static final String SECURITY_ENABLED_PROP = "net.slp.securityEnabled";

    private static final String SECURITY_ENABLED_DEFAULT = "false";

    private static final String SPI_PROP = "net.slp.spi";

    private static final String SPI_DEFAULT = "";

    private static final String PRIVATE_KEY_PROP = "net.slp.privateKey.";

    private static final String PUBLIC_KEY_PROP = "net.slp.publicKey.";

    private static final String INTERFACES_PROP = "net.slp.interfaces";

    private static final String INTERFACES_DEFAULT = null;

    private static final String NO_DA_DISCOVERY_PROP = "net.slp.noDADiscovery";

    private static final String PORT_PROP = "net.slp.port";

    private static final String DEFAULT_PORT = "427";

    private static final String DEFAULT_CONVERGENCE_FAILERCOUNT = "2";

    private static final String CONVERGENCE_FAILERCOUNT_PROP = "net.slp.failercount";

    private static final String DEBUG_ENABLED_PROP = "ch.ethz.iks.slp.debug";

    private static String[] INTERFACES;

    private static int PORT;

    private static String SCOPES;

    private static boolean NO_DA_DISCOVERY;

    private static String[] DA_ADDRESSES;

    private static boolean TRACE_DA_TRAFFIC;

    private static boolean TRACE_MESSAGES;

    private static boolean TRACE_DROP;

    private static boolean TRACE_REG;

    private static int MCAST_TTL;

    private static int MCAST_MAX_WAIT;

    private static int[] MCAST_TIMEOUTS;

    private static int DATAGRAM_MAX_WAIT;

    private static int[] DATAGRAM_TIMEOUTS;

    private static int MTU;

    private static boolean SECURITY_ENABLED;

    private static String SPI;

    private static int WAIT_TIME;

    private static Map PUBLIC_KEY_CACHE;

    private static Map PRIVATE_KEY_CACHE;

    private static int CONVERGENCE_FAILERCOUNT;

    private static boolean DEBUG_ENABLED;

    /**
	 * create a new SLPConfiguration from properties.
	 * 
	 * @param properties
	 *            properties.
	 */
     SLPConfiguration() {
        processProperties(System.getProperties());
    }

    /**
	 * create a new SLPConfiguration from config file.
	 * 
	 * @param file
	 *            the file.
	 * @throws IOException
	 *             in case of IO errors.
	 */
     SLPConfiguration(final File file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        props.putAll(System.getProperties());
        processProperties(props);
    }

    private static void processProperties(final Properties props) {
        String ifaces = props.getProperty(INTERFACES_PROP, INTERFACES_DEFAULT);
        if (ifaces == null) {
            INTERFACES = null;
        } else {
            INTERFACES = (String[]) SLPMessage.stringToList(ifaces, ",").toArray(new String[0]);
        }
        PORT = Integer.parseInt(props.getProperty(PORT_PROP, DEFAULT_PORT));
        SCOPES = props.getProperty(USE_SCOPES_PROP, USE_SCOPES_DEFAULT);
        NO_DA_DISCOVERY = new Boolean(props.getProperty(NO_DA_DISCOVERY_PROP, "false")).booleanValue();
        final String dAs = props.getProperty(DA_ADDRESSES_PROP, DA_ADDRESSES_DEFAULT);
        if (dAs == null) {
            DA_ADDRESSES = null;
        } else {
            DA_ADDRESSES = (String[]) SLPMessage.stringToList(dAs, ",").toArray(new String[0]);
        }
        TRACE_DA_TRAFFIC = new Boolean(props.getProperty(TRACE_DATRAFFIC_PROP, TRACE_DATRAFFIC_DEFAULT)).booleanValue();
        TRACE_MESSAGES = new Boolean(props.getProperty(TRACE_MESSAGE_PROP, TRACE_MESSAGE_DEFAULT)).booleanValue();
        TRACE_DROP = new Boolean(props.getProperty(TRACE_DROP_PROP, TRACE_DROP_DEFAULT)).booleanValue();
        TRACE_REG = new Boolean(props.getProperty(TRACE_REG_PROP, TRACE_REG_DEFAULT)).booleanValue();
        MCAST_TTL = Integer.parseInt(props.getProperty(MCAST_TTL_PROP, MCAST_TTL_DEFAULT));
        MCAST_MAX_WAIT = Integer.parseInt(props.getProperty(MCAST_MAX_WAIT_PROP, MCAST_MAX_WAIT_DEFAULT));
        DATAGRAM_MAX_WAIT = Integer.parseInt(props.getProperty(DATAGRAM_MAX_WAIT_PROP, DATAGRAM_MAX_WAIT_DEFAULT));
        MCAST_TIMEOUTS = parseTimeouts(props.getProperty(MCAST_TIMEOUTS_PROP, MCAST_TIMEOUTS_DEFAULT));
        DATAGRAM_TIMEOUTS = parseTimeouts(props.getProperty(DATAGRAM_TIMEOUTS_PROP, DATAGRAM_TIMEOUTS_DEFAULT));
        MTU = Integer.parseInt(props.getProperty(MTU_PROP, MTU_DEFAULT));
        SECURITY_ENABLED = new Boolean(props.getProperty(SECURITY_ENABLED_PROP, SECURITY_ENABLED_DEFAULT)).booleanValue();
        SPI = props.getProperty(SPI_PROP, SPI_DEFAULT);
        WAIT_TIME = Integer.parseInt(props.getProperty(WAIT_TIME_PROP, WAIT_TIME_DEFAULT));
        CONVERGENCE_FAILERCOUNT = Integer.parseInt(props.getProperty(CONVERGENCE_FAILERCOUNT_PROP, DEFAULT_CONVERGENCE_FAILERCOUNT));
        DEBUG_ENABLED = new Boolean(props.getProperty(DEBUG_ENABLED_PROP, "false")).booleanValue();
        if (SECURITY_ENABLED) {
            PUBLIC_KEY_CACHE = new HashMap(0);
            PRIVATE_KEY_CACHE = new HashMap(0);
        }
    }

    /**
	 * returns the network interfaces.
	 * 
	 * @return interfaces
	 */
    String[] getInterfaces() {
        return INTERFACES;
    }

    /**
	 * get the port.
	 * 
	 * @return the port
	 */
    int getPort() {
        return PORT;
    }

    /**
	 * get the scopes.
	 * 
	 * @return the scopes.
	 */
    String getScopes() {
        return SCOPES;
    }

    /**
	 * get the predefined DA addresses.
	 * 
	 * @return the DA addresses.
	 */
    String[] getDaAddresses() {
        return DA_ADDRESSES;
    }

    /**
	 * get no DA discovery
	 * 
	 * @return true, if DA discovery is disabled
	 */
    boolean getNoDaDiscovery() {
        return NO_DA_DISCOVERY;
    }

    /**
	 * trace DA traffic ?
	 * 
	 * @return true if trace is enabled.
	 */
    boolean getTraceDaTraffic() {
        return TRACE_DA_TRAFFIC;
    }

    /**
	 * trace messages ?
	 * 
	 * @return true if trace is enabled.
	 */
    boolean getTraceMessage() {
        return TRACE_MESSAGES;
    }

    /**
	 * trace dropped messages ?
	 * 
	 * @return true if trace is enabled.
	 */
    boolean getTraceDrop() {
        return TRACE_DROP;
    }

    /**
	 * trace registrations ?
	 * 
	 * @return true if trace is enabled.
	 */
    boolean getTraceReg() {
        return TRACE_REG;
    }

    /**
	 * get the multicast TTL.
	 * 
	 * @return the multicast TTL.
	 */
    int getMcastTTL() {
        return MCAST_TTL;
    }

    /**
	 * get the multicast max wait time.
	 * 
	 * @return the max wait time.
	 */
    int getMcastMaxWait() {
        return MCAST_MAX_WAIT;
    }

    /**
	 * get the datagram max wait time.
	 * 
	 * @return the datagram max wait time.
	 */
    int getDatagramMaxWait() {
        return DATAGRAM_MAX_WAIT;
    }

    /**
	 * parse timeout lists.
	 * 
	 * @param s
	 *            a timeout list string.
	 * @return the timeout int array.
	 */
    private static int[] parseTimeouts(final String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        int[] timeouts = new int[st.countTokens()];
        for (int i = 0; i < timeouts.length; i++) {
            timeouts[i] = Integer.parseInt(st.nextToken());
        }
        return timeouts;
    }

    /**
	 * get the multicast timeouts.
	 * 
	 * @return the multicast timeouts.
	 */
    int[] getMcastTimeouts() {
        return MCAST_TIMEOUTS;
    }

    /**
	 * get the datagram timeouts.
	 * 
	 * @return the datagram timeouts.
	 */
    int[] getDatagramTimeouts() {
        return DATAGRAM_TIMEOUTS;
    }

    /**
	 * get the MTU.
	 * 
	 * @return the MTU.
	 */
    int getMTU() {
        return MTU;
    }

    /**
	 * is security enabled ?
	 * 
	 * @return true if security is enabled.
	 */
    boolean getSecurityEnabled() {
        return SECURITY_ENABLED;
    }

    /**
	 * get the SPIs.
	 * 
	 * @return the SPI string list.
	 */
    String getSPI() {
        return SPI;
    }

    /**
	 * get the public key for a certain SPI.
	 * 
	 * @param spi
	 *            the SPI.
	 * @return the location of the public key.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
    PublicKey getPublicKey(final String spi) throws IOException, GeneralSecurityException {
        PublicKey key = (PublicKey) PUBLIC_KEY_CACHE.get(spi);
        if (key != null) {
            return key;
        }
        FileInputStream keyfis = new FileInputStream(System.getProperty(PUBLIC_KEY_PROP + spi));
        byte[] encKey = new byte[keyfis.available()];
        keyfis.read(encKey);
        keyfis.close();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        key = keyFactory.generatePublic(pubKeySpec);
        PUBLIC_KEY_CACHE.put(spi, key);
        return key;
    }

    /**
	 * get the private key for a certain SPI.
	 * 
	 * @param spi
	 *            the SPI.
	 * @return the location of the private key.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
    PrivateKey getPrivateKey(final String spi) throws IOException, GeneralSecurityException {
        PrivateKey key = (PrivateKey) PRIVATE_KEY_CACHE.get(spi);
        if (key != null) {
            return key;
        }
        FileInputStream keyfis = new FileInputStream(System.getProperty(PRIVATE_KEY_PROP + spi));
        byte[] encKey = new byte[keyfis.available()];
        keyfis.read(encKey);
        keyfis.close();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        key = keyFactory.generatePrivate(privKeySpec);
        PRIVATE_KEY_CACHE.put(spi, key);
        return key;
    }

    /**
	 * get the default wait time.
	 * 
	 * @return the default wait time.
	 */
    int getWaitTime() {
        return WAIT_TIME;
    }

    /**
	 * Defines after how many inconclusive multicastConvergence cycles the attempt is aborted.
	 * It indirectly defines how many multicast convergence {@link SLPMessage}.SRVTYPERQST get send during an attempt.
	 * 
	 * This value can be seen as an heuristical optimization to stop multicast convergence early if inconclusive 
	 * @return  how many inconclusive cycles are allowed
	 */
    int getConvergenceFailerCount() {
        return CONVERGENCE_FAILERCOUNT;
    }

    /**
	 * Whether Debug mode is turned on or off
	 * @return true if debug mode is enabled
	 */
    boolean getDebugEnabled() {
        return DEBUG_ENABLED;
    }

    /**
	 * @return int defining when a TCP send should time out
	 */
    int getTCPTimeout() {
        // 5sec
        return 5000;
    }
}
