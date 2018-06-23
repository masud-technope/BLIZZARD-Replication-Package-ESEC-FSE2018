/****************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.ui.wizards;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.internal.provider.xmpp.ui.Messages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public final class XMPPSConnectWizard extends XMPPConnectWizard {

    public  XMPPSConnectWizard() {
        super();
    }

    public  XMPPSConnectWizard(String uri) {
        super(uri);
    }

    public void addPages() {
        page = new XMPPSConnectWizardPage(usernameAtHost);
        addPage(page);
    }

    public void init(IWorkbench workbench, IContainer container) {
        super.init(workbench, container);
        setWindowTitle(Messages.XMPPSConnectWizard_WIZARD_TITLE);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        super.init(workbench, selection);
        try {
            this.container = ContainerFactory.getDefault().createContainer("ecf.xmpps.smack");
        } catch (ContainerCreateException e) {
        }
        setWindowTitle(Messages.XMPPSConnectWizard_WIZARD_TITLE);
    }

    protected IConnectContext createConnectContext() {
        // Extract passwords from page prior to widget disposal:
        final String password = page.getPassword();
        final String keystorePassword = ((XMPPSConnectWizardPage) page).getKeystorePassword();
        // Use a context capable of both ECF and javax.security Callback:
        return new IConnectContext() {

            public CallbackHandler getCallbackHandler() {
                return new DualCallbackHandler(password, keystorePassword);
            }
        };
    }

    /**
	 * <p>
	 * Wrapper class for handling both ECF and javax.security Callback needs.
	 * Will delegate ECF Callback to a handler created by
	 * {@link ConnectContextFactory}.
	 * </p>
	 * <p>
	 * Currently only handles
	 * {@link javax.security.auth.callback.PasswordCallback} for javax.security
	 * Callback for XMPPConnection class from org.jivesoftware.smack.
	 * </p>
	 * 
	 * @author T. Yueh
	 */
    class DualCallbackHandler implements CallbackHandler, javax.security.auth.callback.CallbackHandler {

        /**
		 * Handler for all ECF Callback.
		 */
        CallbackHandler delegate;

        /**
		 * User input for keystore password. Since keystore impl (PKI vs JKS vs
		 * etc) is hidden via interface, all that is necessary is a password.
		 */
        String keystorePassword;

        public  DualCallbackHandler(String password, String keystorePassword) {
            delegate = ConnectContextFactory.createPasswordConnectContext(password).getCallbackHandler();
            this.keystorePassword = keystorePassword;
        }

        public void handle(Callback[] acallback) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < acallback.length; i++) if (acallback[i] instanceof PasswordCallback)
                ((javax.security.auth.callback.PasswordCallback) acallback[i]).setPassword(keystorePassword.toCharArray());
        }

        public void handle(org.eclipse.ecf.core.security.Callback[] callbacks) throws IOException, org.eclipse.ecf.core.security.UnsupportedCallbackException {
            delegate.handle(callbacks);
        }
    }
}
