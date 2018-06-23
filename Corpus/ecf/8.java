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
import org.eclipse.ecf.ui.screencapture.ImageWrapper;

/**
 *
 */
public class ScreenCaptureStartMessage implements Serializable {

    private static final long serialVersionUID = 8305404092939645129L;

    ID senderID;

    String senderUser;

    ImageWrapper imageWrapper;

    public  ScreenCaptureStartMessage(ID senderID, String senderUser, ImageWrapper imageWrapper) {
        this.senderID = senderID;
        this.senderUser = senderUser;
        this.imageWrapper = imageWrapper;
    }

    public ID getSenderID() {
        return senderID;
    }

    public String getSenderUser() {
        return senderUser;
    }

    public ImageWrapper getImageWrapper() {
        return imageWrapper;
    }
}
