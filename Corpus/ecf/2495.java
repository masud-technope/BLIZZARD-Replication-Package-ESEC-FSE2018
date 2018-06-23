/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.telephony.call.ui.actions;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.ecf.internal.telephony.call.ui.Activator;
import org.eclipse.ecf.internal.telephony.call.ui.Messages;
import org.eclipse.ecf.telephony.call.*;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * Abstract class to implement calling via ECF Call API. Subclasses may be
 * created.
 */
public abstract class AbstractCallAction extends Action {

    protected ID callReceiver = null;

    protected IExceptionHandler exceptionHandler = null;

    public  AbstractCallAction() {
    // nothing to do
    }

    /**
	 * @param text
	 */
    public  AbstractCallAction(String text) {
        super(text);
    }

    /**
	 * @param text
	 * @param image
	 */
    public  AbstractCallAction(String text, ImageDescriptor image) {
        super(text, image);
    }

    /**
	 * @param text
	 * @param style
	 */
    public  AbstractCallAction(String text, int style) {
        super(text, style);
    }

    public void setCallExceptionHandler(IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public IExceptionHandler getCallExceptionHandler() {
        return this.exceptionHandler;
    }

    public void setCallReceiver(ID callReceiver) {
        this.callReceiver = callReceiver;
    }

    public ID getCallReceiver() {
        return this.callReceiver;
    }

    protected ICallSessionContainerAdapter getCallContainerAdapter() {
        IContainer c = getContainer();
        if (c == null)
            return null;
        return (ICallSessionContainerAdapter) c.getAdapter(ICallSessionContainerAdapter.class);
    }

    protected abstract IContainer getContainer();

    protected ICallSessionListener createCallSessionListener() {
        return new ICallSessionListener() {

            public void handleCallSessionEvent(final ICallSessionEvent event) {
                final ICallSession callSession = event.getCallSession();
                if (callSession.getState().equals(CallSessionState.FAILED))
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openInformation(null, Messages.CallAction_Title_Call_Failed, NLS.bind(Messages.CallAction_Message_Call_Failed, callSession.getReceiver().getName(), callSession.getFailureReason().getReason()));
                        }
                    });
            }
        };
    }

    protected ID getReceiverFromInputDialog(ICallSessionContainerAdapter adapter) throws IDCreateException {
        //$NON-NLS-1$
        InputDialog id = new InputDialog(Display.getDefault().getActiveShell(), Messages.CallAction_Initiate_Call_Title, Messages.CallAction_Initiate_Call_Message, "", null);
        id.setBlockOnOpen(true);
        int res = id.open();
        String receiver = null;
        if (res == Window.OK)
            receiver = id.getValue();
        if (//$NON-NLS-1$
        receiver == null || receiver.equals(""))
            return null;
        return IDFactory.getDefault().createID(adapter.getReceiverNamespace(), receiver);
    }

    protected Map createOptions() {
        return null;
    }

    protected void makeCall() throws CallException, IDCreateException {
        ICallSessionContainerAdapter adapter = getCallContainerAdapter();
        if (adapter == null)
            throw new CallException(Messages.CallAction_Exception_Container_Not_Call_API);
        // If we haven't been given a skypeReceiver then show input dialog
        if (callReceiver == null)
            callReceiver = getReceiverFromInputDialog(adapter);
        // If the skypeReceiver now has a value...ring them up
        if (callReceiver != null)
            adapter.sendCallRequest(callReceiver, createCallSessionListener(), createOptions());
    }

    /**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 */
    public void run() {
        try {
            makeCall();
        } catch (Exception e) {
            if (exceptionHandler != null)
                exceptionHandler.handleException(e);
            else
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.CallAction_Exception_CallAction_Run, e));
        }
    }
}
