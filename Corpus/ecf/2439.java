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
package org.eclipse.ecf.internal.provider.xmpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.xmpp.XMPPSContainer;

public class XMPPSContainerInstantiator extends XMPPContainerInstantiator {

    public  XMPPSContainerInstantiator() {
        super();
    }

    public IContainer createInstance(ContainerTypeDescription description, Object[] args) throws ContainerCreateException {
        try {
            Integer ka = new Integer(XMPPSContainer.DEFAULT_KEEPALIVE);
            String name = null;
            if (args != null) {
                if (args.length > 0) {
                    name = (String) args[0];
                    if (args.length > 1) {
                        ka = getIntegerFromArg(args[1]);
                    }
                }
            }
            if (name == null) {
                if (ka == null) {
                    return new XMPPSContainer();
                } else {
                    return new XMPPSContainer(ka.intValue());
                }
            } else {
                if (ka == null) {
                    ka = new Integer(XMPPSContainer.DEFAULT_KEEPALIVE);
                }
                return new XMPPSContainer(name, ka.intValue());
            }
        } catch (Exception e) {
            throw new ContainerCreateException("Exception creating generic container", e);
        }
    }

    //$NON-NLS-1$
    private static final String XMPPS_CONFIG = "ecf.xmpps.smack";

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        if (exporterSupportedConfigs == null)
            return null;
        List results = new ArrayList();
        List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
        if (XMPPS_CONFIG.equals(description.getName())) {
            if (supportedConfigs.contains(XMPPS_CONFIG))
                results.add(XMPPS_CONFIG);
        }
        if (supportedConfigs.size() == 0)
            return null;
        return (String[]) results.toArray(new String[] {});
    }

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        return new String[] { XMPPS_CONFIG };
    }
}
