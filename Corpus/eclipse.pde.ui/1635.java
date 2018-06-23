/*******************************************************************************
 *  Copyright (c) 2007, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.product;

import org.eclipse.pde.internal.ui.PDEUIMessages;

/**
 * ISplashHandlerConstants
 *
 */
public interface ISplashHandlerConstants {

    public static final String[] F_SPLASH_SCREEN_CLASSES = { //$NON-NLS-1$
    "InteractiveSplashHandler", //$NON-NLS-1$
    "BrowserSplashHandler", //$NON-NLS-1$
    "ExtensibleSplashHandler" };

    //$NON-NLS-1$
    public static final String F_UNQUALIFIED_EXTENSION_ID = "splashHandlers";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_TOOLTIP = "tooltip";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_ICON = "icon";

    //$NON-NLS-1$
    public static final String F_ELEMENT_SPLASH = "splashExtension";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_SPLASH_ID = "splashId";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_PRODUCT_ID = "productId";

    //$NON-NLS-1$
    public static final String F_ELEMENT_PRODUCT_BINDING = "splashHandlerProductBinding";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_ID = "id";

    //$NON-NLS-1$
    public static final String F_ATTRIBUTE_CLASS = "class";

    //$NON-NLS-1$
    public static final String F_ELEMENT_SPLASH_HANDLER = "splashHandler";

    //$NON-NLS-1$
    public static final String F_SPLASH_EXTENSION_POINT = "splashExtension";

    //$NON-NLS-1$
    public static final String F_SPLASH_HANDLERS_EXTENSION = "org.eclipse.ui.splashHandlers";

    public static final String[][] F_SPLASH_SCREEN_TYPE_CHOICES = { //$NON-NLS-1$
    { "interactive", PDEUIMessages.UpdateSplashHandlerInModelAction_templateTypeInteractive }, //$NON-NLS-1$
    { "browser", PDEUIMessages.UpdateSplashHandlerInModelAction_templateTypeBrowser }, //$NON-NLS-1$
    { "extensible", PDEUIMessages.UpdateSplashHandlerInModelAction_templateTypeExtensible } };
}
