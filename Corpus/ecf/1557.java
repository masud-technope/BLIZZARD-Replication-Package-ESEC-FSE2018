/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.docshare2.messages;

import org.eclipse.ecf.core.identity.ID;

/**
 * @since 2.1
 *
 */
public class StartMessage extends Message {

    private static final long serialVersionUID = 4712028336072890912L;

    private final ID peerID;

    private final String path;

    private final String documentContent;

    public  StartMessage(ID peerID, String content, String path) {
        this.peerID = peerID;
        this.path = path;
        this.documentContent = content;
    }

    public ID getPeerID() {
        return peerID;
    }

    public String getPath() {
        return path;
    }

    public String getDocumentContent() {
        return documentContent;
    }
}
