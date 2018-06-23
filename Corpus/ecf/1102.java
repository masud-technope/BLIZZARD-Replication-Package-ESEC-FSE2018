/*******************************************************************************
 * Copyright (c) 2010 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.synd;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.NotSerializableException;
import java.io.StringReader;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.internal.remoteservice.rest.synd.Activator;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteResponseDeserializer;

public class SyndFeedResponseDeserializer implements IRemoteResponseDeserializer {

    public Object deserializeResponse(String endpoint, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException {
        try {
            return new SyndFeedInput().build(new StringReader(new String(responseBody)));
        } catch (IllegalArgumentException e) {
            Activator.getDefault().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage()));
            throw new NotSerializableException(e.getMessage());
        } catch (FeedException e) {
            Activator.getDefault().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage()));
            throw new NotSerializableException(e.getMessage());
        }
    }
}
