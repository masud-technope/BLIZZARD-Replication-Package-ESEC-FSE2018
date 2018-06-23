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
package org.eclipse.ecf.internal.examples.updatesite;

import java.util.Properties;
import org.eclipse.ecf.discovery.IServiceProperties;

public class UpdateSiteProperties {

    String name;

    //$NON-NLS-1$
    public static final String NAME_PROPERTY = "name";

    public  UpdateSiteProperties(String name) {
        this.name = name;
    }

    public  UpdateSiteProperties(IServiceProperties serviceProperties) {
        this.name = serviceProperties.getPropertyString(NAME_PROPERTY);
    }

    public String getName() {
        return name;
    }

    public Properties toProperties() {
        final Properties props = new Properties();
        props.put(NAME_PROPERTY, this.name);
        return props;
    }
}
