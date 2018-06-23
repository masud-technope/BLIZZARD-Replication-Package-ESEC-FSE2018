/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.ecf.remoteservice.util.ObjectSerializationUtil;

public class ObjectSerializationParameterDeserializer extends ObjectSerializationUtil implements IRemoteCallParameterDeserializer {

    public Object[] deserializeParameters(HttpServletRequest req) throws IOException, ServletException {
        byte[] inputStreamAsBytes = readToByteArray(req.getInputStream());
        Object object = deserializeFromBytes(inputStreamAsBytes);
        if (object instanceof Object[])
            return (Object[]) object;
        return new Object[] { object };
    }
}
