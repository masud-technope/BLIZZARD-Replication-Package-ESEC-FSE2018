/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservices.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.remoteservices.ui.RemoteServiceHandlerUtil;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.ecf.remoteservices.ui.MethodInvocationDialog;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ReflectiveRemoteServiceHandler extends AbstractHandler implements IHandler {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.
	 * commands .ExecutionEvent)
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final String clazz = event.getParameter(//$NON-NLS-1$
        "org.eclipse.ecf.remoteservices.ui.commands.reflectiveMethodDialogParameter");
        final IRemoteServiceContainerAdapter adapter = RemoteServiceHandlerUtil.getActiveIRemoteServiceContainerAdapterChecked(event);
        if (adapter == null) {
            //$NON-NLS-1$
            MessageDialog.openError(//$NON-NLS-1$
            null, //$NON-NLS-1$
            "Handler invocation failed", "No container found");
            return null;
        }
        final IRemoteServiceReference[] references = RemoteServiceHandlerUtil.getActiveIRemoteServiceReferencesChecked(event);
        if (references == null || references.length == 0) {
            //$NON-NLS-1$
            MessageDialog.openError(//$NON-NLS-1$
            null, //$NON-NLS-1$
            "Handler invocation failed", "No remote service reference found");
            return null;
        }
        final IRemoteService remoteService = adapter.getRemoteService(references[0]);
        if (remoteService == null) {
            //$NON-NLS-1$
            MessageDialog.openError(//$NON-NLS-1$
            null, //$NON-NLS-1$
            "Handler invocation failed", "No remote service found");
            return null;
        }
        try {
            executeMethodInvocationDialog(Class.forName(clazz), remoteService);
        } catch (ClassNotFoundException e) {
            MessageDialog.openError(null, "Handler invocation failed", e.getLocalizedMessage());
            throw new ExecutionException(e.getMessage(), e);
        }
        return null;
    }

    protected void executeMethodInvocationDialog(final Class cls, final IRemoteService remoteService) {
        final MethodInvocationDialog mid = new MethodInvocationDialog((Shell) null, cls);
        if (mid.open() == Window.OK) {
            final int timeout = (mid.getTimeout() > 0) ? mid.getTimeout() : 30000;
            final String methodName = mid.getMethod().getName();
            final Object[] methodArgs = mid.getMethodArguments();
            final IRemoteCall remoteCall = new IRemoteCall() {

                public String getMethod() {
                    return methodName;
                }

                public Object[] getParameters() {
                    return methodArgs;
                }

                public long getTimeout() {
                    return timeout;
                }
            };
            final int invokeType = mid.getInvocationType();
            try {
                switch(invokeType) {
                    case MethodInvocationDialog.ASYNC_FIRE_AND_GO:
                        remoteService.callAsync(remoteCall);
                        break;
                    case MethodInvocationDialog.ASYNC_FUTURE_RESULT:
                        invokeFuture(cls, remoteService, remoteCall);
                        break;
                    case MethodInvocationDialog.ASYNC_LISTENER:
                        invokeAsyncListener(cls, remoteService, remoteCall);
                        break;
                    case MethodInvocationDialog.OSGI_SERVICE_PROXY:
                        throw new UnsupportedOperationException();
                    // break;
                    case MethodInvocationDialog.REMOTE_SERVICE_PROXY:
                        throw new UnsupportedOperationException();
                    // break;
                    case MethodInvocationDialog.SYNCHRONOUS:
                        invokeSync(cls, remoteService, remoteCall);
                        break;
                    default:
                        break;
                }
            } catch (final Exception e) {
                showException(e);
            }
        }
    }

    protected void showException(final Throwable t, final IContainer container, ID targetID) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                String msg = t.toString();
                if (t.getCause() != null) {
                    msg += t.getCause().toString();
                }
                MessageDialog.openInformation(null, "Received Exception", NLS.bind("Exception: {0}", msg));
                container.disconnect();
            }
        });
    }

    protected void invokeFuture(Class cls, IRemoteService remoteService, IRemoteCall remoteCall) throws InterruptedException, InvocationTargetException, OperationCanceledException {
        // Make async call with future result
        final IFuture asyncResult = remoteService.callAsync(remoteCall);
        // Call blocking get and show result
        showResult(cls.getName(), remoteCall, asyncResult.get());
    }

    private void invokeAsyncListener(final Class interfaceClass, final IRemoteService remoteService, final IRemoteCall remoteCall) {
        // Make async call
        remoteService.callAsync(remoteCall, new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                if (event instanceof IRemoteCallCompleteEvent) {
                    final IRemoteCallCompleteEvent complete = (IRemoteCallCompleteEvent) event;
                    if (complete.hadException()) {
                        showException(complete.getException());
                    } else
                        showResult(interfaceClass.getName(), remoteCall, complete.getResponse());
                }
            }
        });
    }

    private void invokeSync(final Class interfaceClass, final IRemoteService remoteService, final IRemoteCall remoteCall) {
        try {
            Object callSync = remoteService.callSync(remoteCall);
            showResult(interfaceClass.getName(), remoteCall, callSync);
        } catch (ECFException e) {
            showException(e);
        }
    }

    protected void showException(final Throwable t) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                String msg = t.toString();
                if (t.getCause() != null) {
                    msg += t.getCause().toString();
                }
                MessageDialog.openInformation(null, "Received Exception", NLS.bind("Exception: {0}", msg));
            }
        });
    }

    protected void showResult(final String serviceInterface, final IRemoteCall remoteCall, final Object result) {
        final Object display = (result != null && result.getClass().isArray()) ? Arrays.asList((Object[]) result) : result;
        final Object[] bindings = new Object[] { serviceInterface, remoteCall.getMethod(), Arrays.asList(remoteCall.getParameters()), display };
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                MessageDialog.openInformation(null, "Received Response", NLS.bind("Service Interface:\n{0}\n\nMethod: {1}\nParameters: {2}\n\nResult:  {3}", bindings));
            }
        });
    }
}
