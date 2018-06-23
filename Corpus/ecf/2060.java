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
package org.eclipse.ecf.internal.provider.vbulletin;

import org.eclipse.ecf.internal.provider.vbulletin.container.VBContainer;

public class VBFactory {

    private static VBFactory instance;

    private  VBFactory() {
        super();
    }

    public static VBFactory getDefault() {
        if (instance == null) {
            instance = new VBFactory();
        }
        return instance;
    }

    public VBulletin createVB(VBContainer mainContainer) {
        return new VBulletin(mainContainer);
    }
}
