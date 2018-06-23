/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation 
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core;

import java.util.Map;

/**
 * Configuration values of this ZooKeeper-based discovery provider.
 */
public interface IDiscoveryConfig {

    /**
	 * @return Map of properties used for configuration. All properties
	 *         understood by Apache ZooKeeper (v3.1.1) might be included as
	 *         well.
	 * @see <a href=
	 *      "http://hadoop.apache.org/zookeeper/docs/r3.2.1/zookeeperAdmin.html"
	 *      > ZooKeeper Administrator's Guide</a>
	 */
    Map<String, Object> getConfigProperties();

    /*
	 * ====================================================================
	 * Constants defining keys
	 * ====================================================================
	 */
    //$NON-NLS-1$
    String ZOODISCOVERY_FLAVOR_STANDALONE = "zoodiscovery.flavor.standalone";

    //$NON-NLS-1$	
    String ZOODISCOVERY_FLAVOR_CENTRALIZED = "zoodiscovery.flavor.centralized";

    //$NON-NLS-1$
    String ZOODISCOVERY_FLAVOR_REPLICATED = "zoodiscovery.flavor.replicated";

    //$NON-NLS-1$
    String ZOODISCOVERY_CONSOLELOG = "consoleLog";

    /** The number of milliseconds of each tick. OPTIONAL **/
    //$NON-NLS-1$
    String ZOOKEEPER_TICKTIME = "tickTime";

    /**
	 * If found and the zookeeper discovery bundle is started then the zookeeper
	 * server will be started automatically. Value is not relevant, only the
	 * definition.
	 * 
	 * @since 1.0.0
	 **/
    String ZOOKEEPER_AUTOSTART = "autoStart";

    /** The directory where zookeeper can work. OPTIONAL **/
    //$NON-NLS-1$
    String ZOOKEEPER_TEMPDIR = "tempDir";

    /**
	 * The single name of the directory in {@link #ZOOKEEPER_TEMPDIR} where the
	 * snapshot is stored. OPIONAL
	 **/
    //$NON-NLS-1$
    String ZOOKEEPER_DATADIR = "dataDir";

    /**
	 * The single name of the directory in {@link #ZOOKEEPER_TEMPDIR} where the
	 * log is stored. It may be the same as {@link #ZOOKEEPER_DATADIR} but
	 * better if separate. OPTIONAL
	 **/
    //$NON-NLS-1$
    String ZOOKEEPER_DATALOGDIR = "dataLogDir";

    /**
	 * The number of ticks that the initial synchronization phase can take.
	 * OPTIONAL
	 **/
    //$NON-NLS-1$
    String ZOOKEEPER_INITLIMIT = "initLimit";

    /**
	 * The number of ticks that can pass between sending a request and getting
	 * an acknowledgment. OPTIONAL
	 **/
    //$NON-NLS-1$
    String ZOOKEEPER_SYNCLIMIT = "syncLimit";

    /** The port at which the clients will connect. OPTIONAL **/
    //$NON-NLS-1$
    String ZOOKEEPER_CLIENTPORT = "clientPort";

    /** Server to server port. OPTIONAL **/
    //$NON-NLS-1$
    String ZOOKEEPER_SERVER_PORT = "serverPort";

    /** Leader election port. OPTIONAL **/
    //$NON-NLS-1$
    String ZOOKEEPER_ELECTION_PORT = "electionPort";
}
