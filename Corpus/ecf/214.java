/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.datashare.service;

import org.eclipse.ecf.datashare.IChannelContainerAdapter;

/**
 * OSGI datashare service interface.  This interface should be registered
 * by providers when they wish to expose datashare services to OSGI
 * service clients.
 */
public interface IDatashareService extends IChannelContainerAdapter {
    //
}
