/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui.screencapture;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;

/**
 *
 */
public class ScreenCaptureDataMessage implements Serializable {

    private static final long serialVersionUID = 6036044167371073951L;

    ID senderID;

    byte[] data;

    Boolean isDone;

    public  ScreenCaptureDataMessage(ID senderID, byte[] data, Boolean isDone) {
        this.senderID = senderID;
        this.data = data;
        this.isDone = isDone;
    }

    /**
	 * @return the senderID
	 */
    public ID getSenderID() {
        return senderID;
    }

    /**
	 * @return the data
	 */
    public byte[] getData() {
        return data;
    }

    /**
	 * @return the isDone
	 */
    public Boolean getIsDone() {
        return isDone;
    }
}
