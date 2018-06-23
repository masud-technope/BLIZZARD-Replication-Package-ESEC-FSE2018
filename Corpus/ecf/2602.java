/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.r_osgi;

/**
 * Constants for setting up an R-OSGi test environment.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public interface R_OSGi {

    public static final String CONSUMER_CONTAINER_TYPE = "ecf.r_osgi.peer";

    public static final String HOST_CONTAINER_TYPE = "ecf.r_osgi.peer";

    public static final String HOST_CONTAINER_ENDPOINT_ID = "r-osgi://localhost:9278";
}
