/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;

/**
 * @since 2.1
 */
public interface IStartableServer {

    public void start() throws ECFException;

    public void stop();

    public IContainer getContainer();
}
