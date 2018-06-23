/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.client;

import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.IRemoteServiceClientContainerAdapter;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;

/**
 * A container for REST services. 
 */
public class RestClientContainer extends AbstractRestClientContainer implements IRemoteServiceClientContainerAdapter {

    public  RestClientContainer(RestID id) {
        super(id);
    }

    protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
        return new RestClientService(this, registration);
    }
}
