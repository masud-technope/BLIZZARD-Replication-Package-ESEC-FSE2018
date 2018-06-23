/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.events;

import org.eclipse.ecf.core.util.Event;
import org.jivesoftware.smack.packet.IQ;

public class IQEvent implements Event {

    protected IQ iq = null;

    public  IQEvent(IQ iq) {
        this.iq = iq;
    }

    public IQ getIQ() {
        return iq;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("IQEvent[]");
        return buf.toString();
    }
}
