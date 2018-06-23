/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Activator;

/**
 *
 */
public abstract class AbstractCollabShare extends AbstractShare {

    /**
	 * @param adapter
	 * @throws ECFException
	 */
    public  AbstractCollabShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    /**
	 * @param adapter
	 * @param channelID
	 * @throws ECFException
	 */
    public  AbstractCollabShare(IChannelContainerAdapter adapter, ID channelID) throws ECFException {
        super(adapter, channelID);
    }

    /**
	 * @param adapter
	 * @param channelID
	 * @param options
	 * @throws ECFException
	 */
    public  AbstractCollabShare(IChannelContainerAdapter adapter, ID channelID, Map options) throws ECFException {
        super(adapter, channelID, options);
    }

    public byte[] serialize(Object o) throws Exception {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        return bos.toByteArray();
    }

    public Object deserialize(byte[] bytes) throws Exception {
        final ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
        final ObjectInputStream oins = new ObjectInputStream(bins);
        return oins.readObject();
    }

    public void logError(String exceptionString, Throwable e) {
        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, exceptionString, e));
    }

    public void logError(IStatus status) {
        Activator.getDefault().getLog().log(status);
    }
}
