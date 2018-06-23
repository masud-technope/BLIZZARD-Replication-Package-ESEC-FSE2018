/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.internal.remoteservice.java8;

import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.provider.generic.SSLClientSOContainer;

public class J8SSLClientSOContainer extends SSLClientSOContainer {

    public  J8SSLClientSOContainer(ISharedObjectContainerConfig config, int ka) {
        super(config, ka);
    }
}
