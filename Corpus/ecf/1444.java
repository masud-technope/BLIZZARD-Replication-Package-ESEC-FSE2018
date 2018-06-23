/****************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.msn.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.msn.ui.messages";

    public static String MSNConnectWizardPage_Title;

    public static String MSNConnectWizardPage_EmailAddressRequired;

    public static String MSNConnectWizardPage_EmailAddressInvalid;

    public static String MSNConnectWizardPage_PasswordRequired;

    public static String MSNConnectWizardPage_EmailAddressLabel;

    public static String MSNConnectWizardPage_PasswordLabel;

    public static String MSNConnectWizardPage_WIZARD_PAGE_DESCRIPTION;

    public static String MSNConnectWizard_Title;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
