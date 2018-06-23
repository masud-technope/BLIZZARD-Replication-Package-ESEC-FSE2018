/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jmdns;

public interface JMDNSDebugOptions {

    //$NON-NLS-1$
    public static final String DEBUG = JMDNSPlugin.getDefault().getBundle().getSymbolicName() + "/debug";

    //$NON-NLS-1$
    public static final String EXCEPTIONS_CATCHING = DEBUG + "/exceptions/catching";

    //$NON-NLS-1$
    public static final String EXCEPTIONS_THROWING = DEBUG + "/exceptions/throwing";

    //$NON-NLS-1$
    public static final String METHODS_ENTERING = DEBUG + "/methods/entering";

    //$NON-NLS-1$
    public static final String METHODS_EXITING = DEBUG + "/methods/exiting";
}
