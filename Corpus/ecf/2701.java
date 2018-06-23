/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.scp;

import java.net.URL;
import java.util.Map;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;

/**
 *
 */
public interface IScpFileTransfer {

    public IConnectContext getConnectContext();

    public String getUsername();

    public void setUsername(String username);

    public Proxy getProxy();

    public URL getTargetURL();

    public Map getOptions();
}
