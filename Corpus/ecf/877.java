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
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.ecf.remoteservice.util.ObjectSerializationUtil;

public class ObjectSerializationResponseSerializer extends ObjectSerializationUtil implements IRemoteCallResponseSerializer {

    public void serializeResponse(HttpServletResponse resp, Object responseObject) throws IOException, ServletException {
        if (responseObject == null)
            return;
        byte[] bytes = serializeToBytes(responseObject);
        OutputStream outs = resp.getOutputStream();
        writeByteArray(outs, bytes);
    }
}
