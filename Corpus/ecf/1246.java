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
package org.eclipse.ecf.internal.provider.phpbb.container;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBBContainer;
import org.eclipse.ecf.internal.provider.phpbb.PHPBBFactory;
import org.eclipse.ecf.internal.provider.phpbb.PHPBBPlugin;

public class PHPBBContainer extends AbstractBBContainer {

    public  PHPBBContainer(ID id) {
        super(id);
        this.bb = PHPBBFactory.getDefault().createPHPBB(this);
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(PHPBBPlugin.getNamespaceIdentifier());
    }
}
