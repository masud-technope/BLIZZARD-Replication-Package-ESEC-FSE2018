/****************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.examples.provider.remoteservice.identity;

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class RSExampleID extends StringID {

    private static final long serialVersionUID = -3656862045346223983L;

    protected  RSExampleID(Namespace n, String s) {
        super(n, s);
    }
}
