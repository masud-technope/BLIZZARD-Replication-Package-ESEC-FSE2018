/****************************************************************************
 * Copyright (c) 2013 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.remoteservice.generic;

/**
 *
 */
public interface SSLGeneric {

    public static final String CONSUMER_CONTAINER_TYPE = "ecf.generic.ssl.client";

    public static final String HOST_CONTAINER_TYPE = "ecf.generic.ssl.server";

    public static final String HOST_CONTAINER_ENDPOINT_ID = "ecfssl://localhost:{0}/secureserver";
}
