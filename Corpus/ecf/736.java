/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.protocol.msn.internal;

import junit.framework.TestCase;
import org.eclipse.ecf.protocol.msn.internal.encode.Encryption;

public class EncryptionTest extends TestCase {

    public void testSHA() {
        String object = //$NON-NLS-1$
        "Creatorbuddy1@hotmail.comSize24539Type3Location" + "TFR2C.tmpFriendlyAAA=SHA1DtrC8SlFx2sWQxZMIBAWSEnXc8oQ=";
        assertEquals(Encryption.computeSHA(object.getBytes()), //$NON-NLS-1$
        "U32o6bosZzluJq82eAtMpx5dIEI=");
    }
}
