/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.irc.datashare;

import java.net.SocketAddress;

/**
 * An interface for interacting with the actual datashare container. This allows
 * the IRC provider to work even if the optional dependencies are not present.
 */
public interface IIRCDatashareContainer {

    public void enqueue(SocketAddress address);

    public void setIP(String ip);
}
