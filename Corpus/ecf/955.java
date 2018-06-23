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
package org.eclipse.ecf.example.collab.share;

import org.eclipse.ecf.core.identity.ID;

public interface SharedObjectEventListener {

    public void memberRemoved(ID member);

    public void memberAdded(ID member);

    public void otherActivated(ID other);

    public void otherDeactivated(ID other);

    public void windowClosing();
}
