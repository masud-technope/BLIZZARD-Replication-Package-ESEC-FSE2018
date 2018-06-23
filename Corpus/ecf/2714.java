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

import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBBObject;

public abstract class PHPBBObject extends AbstractBBObject implements IBBObject {

    public  PHPBBObject(String name, int mode) {
        super(name, mode);
    }
}
