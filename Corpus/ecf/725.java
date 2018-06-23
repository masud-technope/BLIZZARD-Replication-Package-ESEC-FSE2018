/*******************************************************************************
  * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.remoteservice.generic;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.*;

public class RemoteServiceImpl extends AbstractRemoteService {

    //$NON-NLS-1$ //$NON-NLS-2$
    protected static final long DEFAULT_TIMEOUT = new Long(System.getProperty("ecf.remotecall.timeout", "30000")).longValue();

    protected RemoteServiceRegistrationImpl registration = null;

    protected RegistrySharedObject sharedObject = null;

    public  RemoteServiceImpl(RegistrySharedObject sharedObject, RemoteServiceRegistrationImpl registration) {
        this.sharedObject = sharedObject;
        this.registration = registration;
    }

    protected IRemoteServiceID getRemoteServiceID() {
        return registration.getID();
    }

    protected IRemoteServiceReference getRemoteServiceReference() {
        return registration.getReference();
    }

    protected String[] getInterfaceClassNames() {
        return registration.getClasses();
    }

    protected long getDefaultTimeout() {
        return DEFAULT_TIMEOUT;
    }

    /**
	 * @since 3.0
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callAsync(org.eclipse.ecf.remoteservice.IRemoteCall, org.eclipse.ecf.remoteservice.IRemoteCallListener)
	 */
    public void callAsync(IRemoteCall call, IRemoteCallListener listener) {
        sharedObject.sendCallRequestWithListener(registration, call, listener);
    }

    /**
	 * @since 3.0
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callSync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
    public Object callSync(IRemoteCall call) throws ECFException {
        return sharedObject.callSynch(registration, call);
    }

    /**
	 * @since 3.0
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#fireAsync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
    public void fireAsync(IRemoteCall call) throws ECFException {
        sharedObject.sendFireRequest(registration, call);
    }
}
