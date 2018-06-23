/*******************************************************************************
* Copyright (c) 2009 IBM, and others. 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM Corporation - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.tests.filetransfer.httpserver;

import org.eclipse.core.runtime.IAdaptable;

/** 
 * Represents a test server involved in a junit test case.
 * <p.
 * The server may be external. In this case only the server address and 
 * port are available. Otherwise if the server is implemented by in process
 * code one may be able to cast or adapt the test server reference to a more
 * specific class.
 */
public interface ITestServer extends IAdaptable {

    String getServerURL();

    int getServerPort();

    String getServerHost();

    // should do nothing for external servers.
    void shutdown();
}
