/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis (slewis@composent.com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.examples.raspberrypi.management.host;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.ecf.examples.raspberrypi.management.IRaspberryPi;

/**
 * Implementation of IRaspberryPi service interface.
 */
public class RaspberryPi implements IRaspberryPi {

    @Override
    public Map<String, String> getSystemProperties() {
        Properties props = System.getProperties();
        Map<String, String> result = new HashMap<String, String>();
        for (final String name : props.stringPropertyNames()) result.put(name, props.getProperty(name));
        System.out.println("REMOTE CALL: getSystemProperties()");
        return result;
    }
}
