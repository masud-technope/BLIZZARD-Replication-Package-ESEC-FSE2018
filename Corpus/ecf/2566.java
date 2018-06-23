/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    Nick Boldt <codeslave@ca.ibm.com> - bug 206528
 *    Dominik Goepel <dominik.goepel@gmx.de> - bug 216644
 *******************************************************************************/
package org.eclipse.ecf.internal.presence.bot.kosmos;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CustomMessages {

    //$NON-NLS-1$
    static final String No_Operation_Privileges = "No_Operation_Privileges";

    //$NON-NLS-1$
    static final String Learn_Failure = "Learn_Failure";

    //$NON-NLS-1$
    static final String Learn_Reply = "Learn_Reply";

    //$NON-NLS-1$
    static final String Learn_Conflict = "Learn_Conflict";

    //$NON-NLS-1$
    static final String Learn_Update = "Learn_Update";

    //$NON-NLS-1$
    static final String Learn_Remove = "Learn_Remove";

    //$NON-NLS-1$
    static final String Bug = "Bug";

    //$NON-NLS-1$
    static final String Bug_Not_Found = "Bug_Not_Found";

    //$NON-NLS-1$
    static final String BugContent = "BugContent";

    //$NON-NLS-1$
    static final String BugContent2 = "BugContent2";

    //$NON-NLS-1$
    static final String Javadoc_NotFound = "Javadoc_NotFound";

    //$NON-NLS-1$
    static final String Javadoc_ResultsUnknown = "Javadoc_ResultsUnknown";

    //$NON-NLS-1$
    static final String NewsgroupSearch = "NewsgroupSearch";

    //$NON-NLS-1$
    static final String NewsgroupSearch_Invalid = "NewsgroupSearch_Invalid";

    //$NON-NLS-1$
    static final String NewsgroupSearch_InvalidGroup = "NewsgroupSearch_InvalidGroup";

    //$NON-NLS-1$
    static final String Google = "Google";

    //$NON-NLS-1$
    static final String Wiki = "Wiki";

    //$NON-NLS-1$
    static final String EclipseHelp = "EclipseHelp";

    //$NON-NLS-1$
    static final String MessageList = "MessageList";

    //$NON-NLS-1$
    static final String SearchPlugins = "SearchPlugins";

    //$NON-NLS-1$
    static final String CQ = "CQ";

    //$NON-NLS-1$
    private static final String RESOURCE_BUNDLE = "org.eclipse.ecf.internal.presence.bot.kosmos.custom";

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE);

    /**
	 * A private constructor to prevent instantiation.
	 */
    private  CustomMessages() {
    // do nothing
    }

    public static String getString(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return "! " + key + " !";
        }
    }

    public static ResourceBundle getResourceBundle() {
        return BUNDLE;
    }
}
