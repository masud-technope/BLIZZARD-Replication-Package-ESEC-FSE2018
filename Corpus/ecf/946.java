/*******************************************************************************
 * Copyright (c) 2009 Versant Corp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.userinput.handler;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.userinput.Activator;
import org.eclipse.ecf.discovery.ui.userinput.Messages;
import org.eclipse.ecf.discovery.ui.userinput.UserInputDiscoveryContainerInstantiator;
import org.eclipse.ecf.discovery.ui.userinput.UserInputDiscoveryLocator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class LookupHandler extends AbstractHandler {

    //$NON-NLS-1$
    private static final String MANUAL_LOOKUP_HANDLER = "ManualLookupHandler";

    //$NON-NLS-1$
    private static final String HOST_DOMAIN_TLD_PORT = "scheme://host.domain.tld:port";

    private static IInputValidator VALIDATOR = new MyInputValidator();

    /**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IDialogSettings dialogSettings = Activator.getDefault().getDialogSettings();
        String[] inputs = dialogSettings.getArray(MANUAL_LOOKUP_HANDLER);
        InputDialogWithComboHistory dialog = new InputDialogWithComboHistory(window.getShell(), Messages.LookupHandler_DIALOG_TITLE, Messages.LookupHandler_DIALOG_LABEL, HOST_DOMAIN_TLD_PORT, inputs, VALIDATOR);
        if (dialog.open() == InputDialog.OK) {
            String input = dialog.getValue();
            try {
                URI uri = URI.create(input);
                // verify the hostname to be resolveable
                InetAddress.getByName(uri.getHost());
                Job job = new LookupJob(uri);
                job.setUser(false);
                job.schedule();
                // add the newly added input to the preference store
                String[] copyOfPreviousInput;
                if (inputs != null) {
                    copyOfPreviousInput = new String[inputs.length + 1];
                    copyOfPreviousInput[0] = input;
                    for (int i = 0; i < inputs.length; i++) {
                        copyOfPreviousInput[i + 1] = inputs[i];
                    }
                } else {
                    copyOfPreviousInput = new String[] { input };
                }
                //  but we don't want dups
                Set aSet = new LinkedHashSet(Arrays.asList(copyOfPreviousInput));
                dialogSettings.put(MANUAL_LOOKUP_HANDLER, (String[]) aSet.toArray(new String[aSet.size()]));
            } catch (ContainerCreateException e) {
                throw new ExecutionException(Messages.LookupHandler_EXEC_FAILED, e);
            } catch (IDCreateException e) {
                throw new ExecutionException(Messages.LookupHandler_EXEC_FAILED, e);
            } catch (UnknownHostException e) {
                IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.LookupHandler_UNKNOWN_HOSTNAME);
                ErrorDialog.openError(null, Messages.LookupHandler_UNKNOWN_HOSTNAME, NLS.bind(Messages.LookupHandler_HOSTNAME_UNABLE_TO_RESOLVE, input), status);
            }
        }
        return null;
    }

    private class LookupJob extends Job {

        private UserInputDiscoveryLocator container;

        private IServiceTypeID serviceTypeID;

        private URI uri;

        public  LookupJob(URI anURI) throws ContainerCreateException, IDCreateException {
            super(Messages.LookupHandler_RESOLVING);
            uri = anURI;
            container = (UserInputDiscoveryLocator) ContainerFactory.getDefault().createContainer(UserInputDiscoveryContainerInstantiator.NAME);
            Namespace namespace = IDFactory.getDefault().getNamespaceByName(uri.getScheme());
            if (namespace != null) {
                serviceTypeID = (IServiceTypeID) namespace.createInstance(new Object[] { uri });
            } else {
            //TODO throw ex and catch in execute with proper user notification
            }
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
        protected IStatus run(IProgressMonitor arg0) {
            container.fireServiceResolved(uri, serviceTypeID);
            return Status.OK_STATUS;
        }
    }

    private static class MyInputValidator implements IInputValidator {

        /*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
		 */
        public String isValid(String newText) {
            URI uri = null;
            // use the uri logic to validate hostname and port
            try {
                uri = new URI(newText);
            } catch (URISyntaxException e) {
                return Messages.LookupHandler_INVALID_HOSTNAME;
            }
            // check the port
            int p = uri.getPort();
            if (p > 65565 || p < 1) {
                return Messages.LookupHandler_INVALID_PORT;
            }
            return null;
        }
    }
}
