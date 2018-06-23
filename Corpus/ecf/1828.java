/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bug 192762
 ******************************************************************************/
package org.eclipse.ecf.internal.irc.ui.actions;

public class DevoiceAction extends AbstractActionDelegate {

    protected String getMessage(String username) {
        //$NON-NLS-1$
        return "/mode -v " + username;
    }
}
