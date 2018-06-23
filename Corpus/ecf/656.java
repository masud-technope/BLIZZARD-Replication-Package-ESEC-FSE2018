/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.dosgi.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.ui.DiscoveryHandlerUtil;
import org.eclipse.ecf.internal.remoteservices.ui.Activator;
import org.eclipse.ecf.internal.remoteservices.ui.handlers.ReflectiveRemoteServiceHandler;
import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class DOSGiReflectiveRemoteServiceHandler extends ReflectiveRemoteServiceHandler {

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IServiceInfo serviceInfo = DiscoveryHandlerUtil.getActiveIServiceInfoChecked(event);
        final IServiceProperties serviceProperties = serviceInfo.getServiceProperties();
        final String clazz = serviceProperties.getPropertyString(Constants.OBJECTCLASS);
        final String serviceId = new String(serviceProperties.getPropertyString(RemoteConstants.SERVICE_IMPORTED_ENDPOINT_SERVICE_ID));
        final String containerId = new String(serviceProperties.getPropertyString(RemoteConstants.SERVICE_IMPORTED_ENDPOINT_ID));
        // get the service via the osgi service registry
        final BundleContext context = Activator.getDefault().getBundle().getBundleContext();
        final Filter filter;
        try {
            filter = context.createFilter(//$NON-NLS-1$ //$NON-NLS-2$
            "(&(" + Constants.OBJECTCLASS + "=" + clazz + ")" + "(" + IDistributionConstants.SERVICE_IMPORTED + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "=*" + ")" + "(" + RemoteConstants.SERVICE_IMPORTED_ENDPOINT_ID + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "=" + containerId + ")(" + RemoteConstants.SERVICE_IMPORTED_ENDPOINT_SERVICE_ID + "=" + serviceId + //$NON-NLS-1$ //$NON-NLS-2$
            "))");
        } catch (InvalidSyntaxException e1) {
            MessageDialog.openError(null, Messages.DOSGiReflectiveRemoteServiceHandler_HandlerInvocationFailed, NLS.bind(Messages.DOSGiReflectiveRemoteServiceHandler_FilterCreationFailed, e1.getMessage()));
            return null;
        }
        final ServiceTracker serviceTracker = new ServiceTracker(context, filter, null);
        serviceTracker.open();
        final ServiceReference serviceReference = serviceTracker.getServiceReference();
        if (serviceReference == null) {
            MessageDialog.openError(null, Messages.DOSGiReflectiveRemoteServiceHandler_HandlerInvocationFailed, NLS.bind(Messages.DOSGiReflectiveRemoteServiceHandler_NoServiceMatch, filter.toString()));
            return null;
        }
        // obtain the remote service reference from the local service ref (cool
        // ECF feature, huh?)
        final IRemoteService remoteService = (IRemoteService) serviceReference.getProperty(IDistributionConstants.SERVICE_IMPORTED);
        if (remoteService == null) {
            MessageDialog.openError(null, Messages.DOSGiReflectiveRemoteServiceHandler_HandlerInvocationFailed, Messages.DOSGiReflectiveRemoteServiceHandler_RemoteServiceUnresolveable);
            return null;
        }
        try {
            executeMethodInvocationDialog(Class.forName(clazz), remoteService);
        } catch (ClassNotFoundException e) {
            MessageDialog.openError(null, Messages.DOSGiReflectiveRemoteServiceHandler_HandlerInvocationFailed, e.getLocalizedMessage());
            throw new ExecutionException(e.getMessage(), e);
        }
        return null;
    }
}
