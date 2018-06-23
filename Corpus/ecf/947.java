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
package org.eclipse.ecf.internal.provider.phpbb;

import org.eclipse.ecf.internal.provider.phpbb.container.PHPBBContainer;

public class PHPBBFactory {

    private static PHPBBFactory instance;

    private  PHPBBFactory() {
        super();
    }

    public static PHPBBFactory getDefault() {
        if (instance == null) {
            instance = new PHPBBFactory();
        }
        return instance;
    }

    public PHPBB createPHPBB(PHPBBContainer mainContainer) {
        return new PHPBB(mainContainer);
    }
}
