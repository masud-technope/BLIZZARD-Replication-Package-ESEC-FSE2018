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
import org.eclipse.ecf.protocol.msn.internal.encode.Challenge;

public class ChallengeTest extends TestCase {

    public void testChallenge() {
        assertEquals("85ecb0db8f32113df79ce0892b9a102c", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "22210219642164014968"));
        assertEquals("e7ad3cb09d3e9e4e7c720175984809e9", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "36819795137093047918"));
        assertEquals("59bcf63ed21f44906c3d3e121ddbed65", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "21948129323261853323"));
        assertEquals("dcb8ff529e4dd12cc43389851128d2db", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "41525959199453244913"));
        assertEquals("d15553d0ea89c9f63bbb98a208fa4235", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "31744216663023315951"));
        assertEquals("4e11d4cd56a65bdf04f60aa133db7ebc", //$NON-NLS-1$
        Challenge.createQuery(//$NON-NLS-1$
        "14494180082586329971"));
    }
}
