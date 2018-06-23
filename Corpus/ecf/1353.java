/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rpc;

public interface RpcConstants {

    //$NON-NLS-1$
    public static final String RPC_CONTAINER_TYPE = "ecf.xmlrpc.client";

    //$NON-NLS-1$	
    public static final String NAMESPACE = "ecf.xmlrpc.namespace";

    public static final int HTTP_PORT = 8085;

    //$NON-NLS-1$
    public static final String TEST_SERVLETS_PATH = "/xmlrpc";

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String TEST_ECHO_TARGET = "http://localhost:" + HTTP_PORT + TEST_SERVLETS_PATH;

    //$NON-NLS-1$
    public static final String TEST_ECHO_METHOD = "Echo.echo";

    //$NON-NLS-1$
    public static final String TEST_ECHO_METHOD_NAME = "echo";

    //$NON-NLS-1$
    public static final String TEST_ECHO_METHOD_PARAM = "text";

    //$NON-NLS-1$
    public static final String TEST_CALC_PLUS_METHOD = "Calc.add";

    //$NON-NLS-1$
    public static final String TEST_CALC_PLUS_METHOD_NAME = "plus";

    //$NON-NLS-1$
    public static final String TEST_CALC_PLUS_METHOD_PARAM1 = "val1";

    //$NON-NLS-1$
    public static final String TEST_CALC_PLUS_METHOD_PARAM2 = "val2";
}
