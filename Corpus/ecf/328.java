/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rpc;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.tests.ECFAbstractTestCase;

public class RpcContainerInstantiatorTest extends ECFAbstractTestCase {

    private IContainerFactory containerFactory;

    private ContainerTypeDescription description;

    protected void setUp() throws Exception {
        containerFactory = getContainerFactory();
        description = containerFactory.getDescriptionByName(RpcConstants.RPC_CONTAINER_TYPE);
    }

    public void testSupportedParameterTypes() {
        Class<?>[][] types = description.getSupportedParameterTypes();
        boolean foundString = false;
        boolean foundInteger = false;
        boolean foundBoolean = false;
        boolean foundDouble = false;
        boolean foundDate = false;
        boolean foundByteArray = false;
        boolean foundObjectArray = false;
        boolean foundList = false;
        boolean foundMap = false;
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < types[i].length; j++) {
                if (types[i][j].equals(String.class))
                    foundString = true;
                if (types[i][j].equals(Integer.class))
                    foundInteger = true;
                if (types[i][j].equals(Boolean.class))
                    foundBoolean = true;
                if (types[i][j].equals(Double.class))
                    foundDouble = true;
                if (types[i][j].equals(Date.class))
                    foundDate = true;
                if (types[i][j].equals(byte[].class))
                    foundByteArray = true;
                if (types[i][j].equals(Object[].class))
                    foundObjectArray = true;
                if (types[i][j].equals(List.class))
                    foundList = true;
                if (types[i][j].equals(Map.class))
                    foundMap = true;
            }
        }
        assertTrue(foundString && foundInteger && foundBoolean && foundDouble && foundDate && foundByteArray && foundObjectArray && foundList && foundMap);
    }
}
