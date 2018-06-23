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
package org.eclipse.team.internal.ecf.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.team.internal.ecf.core.messages";

    public static String RemoteShare_FetchingVariant;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    // private constructor to prevent instantiation
    }
}
