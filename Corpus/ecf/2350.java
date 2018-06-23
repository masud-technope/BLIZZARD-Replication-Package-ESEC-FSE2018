/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Markus Alexander Kuppe (Versant GmbH) - https://bugs.eclipse.org/259041
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.updatesite.client;

import org.eclipse.core.commands.*;

public class UpdateSiteServiceAccessHandler extends AbstractHandler {

    /* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        //$NON-NLS-1$
        throw new ExecutionException("UpdateSiteServiceAccessHandler no longer supported");
    }
}
