/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer;

import java.net.URLConnection;
import org.osgi.framework.BundleContext;

/**
 *
 */
public interface IURLConnectionModifier {

    public void init(BundleContext context);

    public void setSocketFactoryForConnection(URLConnection urlConnection);

    public void dispose();
}
