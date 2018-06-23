/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.sync.resources.core;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;

public final class StartMessage extends Message implements Serializable {

    private static final long serialVersionUID = -5609339410051661940L;

    private String projectName;

    private ID fromId;

    private ID localId;

     StartMessage(String projectName, ID fromId, ID localId) {
        this.projectName = projectName;
        this.fromId = fromId;
        this.localId = localId;
    }

    public String getProjectName() {
        return projectName;
    }

    public ID getFromId() {
        return fromId;
    }

    ID getLocalId() {
        return localId;
    }
}
