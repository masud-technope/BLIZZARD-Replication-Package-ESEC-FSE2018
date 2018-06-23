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
package org.eclipse.ecf.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.ui.messages";

    public static String AbstractConnectWizardPage_CONNECT_ID_LABEL;

    public static String AbstractConnectWizardPage_CONNECTID_ERROR_STATUS;

    public static String AbstractConnectWizardPage_LOGIN_AUTOMATICALLY_LABEL;

    public static String AbstractConnectWizardPage_PASSWORD_LABEL;

    public static String AbstractConnectWizardPage_PROVIDER_TITLE;

    public static String AbstractConnectWizardPage_USERNAME_LABEL;

    public static String AsynchContainerConnectAction_JOB_NAME;

    public static String AsynchContainerConnectAction_MONITOR_BEGIN_TASK;

    public static String ConfigurationWizardSelectionPage_CONFIGRATION_ERROR_TITLE;

    public static String ConfigurationWizardSelectionPage_CONFIGURATION_ERROR_MESSAGE;

    public static String ConfigurationWizardSelectionPage_CONFIGURATION_WIZARD_PAGE_NAME;

    public static String ConfigurationWizardSelectionPage_ERROR_MESSAGE;

    public static String ConfigurationWizardSelectionPage_SELECT;

    public static String ConfigurationWizardSelectionPage_UNCATEGORIZED_WIZARD_CATEGORY;

    public static String ConfigurationWizardSelectionPage_WIZARD_CATEGORY;

    public static String ContainerConnectErrorDialog_ERROR_MESSAGE;

    public static String ContainerConnectErrorDialog_ERROR_TITLE;

    public static String ContainerCreateErrorDialog_CREATE_CONTAINER_ERROR_MESSAGE;

    public static String ContainerCreateErrorDialog_CREATE_CONTAINER_ERROR_TITLE;

    public static String Select;

    public static String ConfigurationWizard_title;

    public static String AbstractURIHyperlink_EXCEPTION_HYPERLINK;

    public static String SelectProviderAction_WARNING_CONTAINER_TYPE_DESCRIPTION_NOT_FOUND;

    public static String SelectProviderAction_selectProviderDialog_title;

    public static String SelectProviderAction_selectProviderDialog_message;

    public static String ScreenCaptureScreenCaptureConfirmationDialog;

    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
