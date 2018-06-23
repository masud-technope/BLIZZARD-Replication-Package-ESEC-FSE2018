/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.vbulletin.container;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBBContainer;
import org.eclipse.ecf.internal.provider.vbulletin.Activator;
import org.eclipse.ecf.internal.provider.vbulletin.VBFactory;

public class VBContainer extends AbstractBBContainer {

    public  VBContainer(ID id) {
        super(id);
        bb = VBFactory.getDefault().createVB(this);
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(Activator.getNamespaceIdentifier());
    }
}
