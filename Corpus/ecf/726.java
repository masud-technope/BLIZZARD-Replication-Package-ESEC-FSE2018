/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.timeservice.internal.provider.rest.host;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.servlet.HttpServiceComponent;
import org.eclipse.ecf.remoteservice.servlet.RemoteServiceHttpServlet;
import org.eclipse.ecf.remoteservice.servlet.ServletServerContainer;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.http.NamespaceException;
import com.mycorp.examples.timeservice.ITimeService;

public class TimeServiceServerContainer extends ServletServerContainer {

    public static final String TIMESERVICE_HOST_CONFIG_NAME = "com.mycorp.examples.timeservice.rest.host";

    public static final String TIMESERVICE_SERVLET_NAME = "/" + ITimeService.class.getName();

     TimeServiceServerContainer(ID id) throws ServletException, NamespaceException {
        super(id);
        // Register our servlet with the given httpService with the
        // TIMESERVICE_SERVLET_NAME
        // which is "/com.mycorp.examples.timeservice.ITimeService"
        TimeServiceHttpServiceComponent.getDefault().registerServlet(TIMESERVICE_SERVLET_NAME, new TimeRemoteServiceHttpServlet(), null, null);
    }

    @Override
    public void dispose() {
        TimeServiceHttpServiceComponent.getDefault().unregisterServlet(TIMESERVICE_SERVLET_NAME);
        super.dispose();
    }

    @Override
    public Namespace getConnectNamespace() {
        return RestNamespace.INSTANCE;
    }

    class TimeRemoteServiceHttpServlet extends RemoteServiceHttpServlet {

        private static final long serialVersionUID = 3906126401901826462L;

        // Handle remote time service get call here.
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            // Get local OSGi ITimeService
            ITimeService timeService = HttpServiceComponent.getDefault().getService(ITimeService.class);
            // Call local service to get the current time
            Long currentTime = timeService.getCurrentTime();
            // Serialize response and send as http response
            try {
                resp.getOutputStream().print(new JSONObject().put("time", currentTime).toString());
            } catch (JSONException e) {
                throw new ServletException("json response object could not be created for time service", e);
            }
        }
    }

    public static class Instantiator extends RemoteServiceContainerInstantiator {

        @Override
        public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters) throws ContainerCreateException {
            try {
                return new TimeServiceServerContainer(RestNamespace.INSTANCE.createInstance(new Object[] { (String) parameters.get("id") }));
            } catch (Exception e) {
                throw new ContainerCreateException("Could not create time service server", e);
            }
        }

        public String[] getSupportedConfigs(ContainerTypeDescription description) {
            return new String[] { TIMESERVICE_HOST_CONFIG_NAME };
        }
    }
}
