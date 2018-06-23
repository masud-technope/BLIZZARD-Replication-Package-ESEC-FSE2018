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

final class StopMessage extends Message implements Serializable {

    private static final long serialVersionUID = -7843915658263321577L;

    private String projectName;

     StopMessage(String projectName) {
        this.projectName = projectName;
    }

    String getProjectName() {
        return projectName;
    }
}
