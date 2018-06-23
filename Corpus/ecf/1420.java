/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.team.internal.ecf.ui.messages";

    public static String WorkbenchAwareRemoteShare_PromptMessage;

    public static String OverrideWithRemoteAction_ActionLabel;

    public static String OverrideWithRemoteOperation_SubTaskName;

    public static String OverrideWithRemoteOperation_CreatingResource;

    public static String OverrideWithRemoteOperation_ReplacingResource;

    public static String OverrideWithRemoteOperation_DeletingResource;

    public static String CompareWithHandler_FileNotSelectedError;

    public static String CompareWithHandler_ResourceComparisonJobTitle;

    public static String CompareWithHandler_CompareInputDescription;

    public static String CompareWithHandler_CompareEditorWorkbenchJobTitle;

    public static String CompareWithMenuContributionItem_MenuTitle;

    public static String SynchronizeWithHandler_SynchronizeResourceTaskName;

    public static String SynchronizeWithHandler_SynchronizeResourcesTaskName;

    public static String SynchronizeWithHandler_SynchronizeRequestDenial;

    public static String SynchronizeWithHandler_SynchronizeRequestError;

    public static String SynchronizeWithHandler_SynchronizeRequestInterrupted;

    public static String SynchronizeWithHandler_RemoteSynchronizationTaskName;

    public static String SynchronizeWithHandler_RemoteSynchronizationResourceDescription;

    public static String SynchronizeWithHandler_RemoteSynchronizationResourcesDescription;

    public static String SynchronizeWithMenuContributionItem_MenuTitle;

    public static String RemoteSubscriberParticipant_PageDescription;

    public static String RemotePeerSynchronizeWizard_WindowTitle;

    public static String RemotePeerSynchronizeWizardPage_Title;

    public static String RemotePeerSynchronizeWizardPage_Description;

    public static String RemotePeerSynchronizeWizardPage_NoRemotePeerSelectedError;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    // private constructor to prevent instantiation
    }
}
