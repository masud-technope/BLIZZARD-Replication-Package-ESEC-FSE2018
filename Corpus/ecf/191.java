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
package org.eclipse.ecf.ui.screencapture;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.swt.graphics.ImageData;

/**
 * Interface for sending image (represented by ImageData) to target receiver.
 */
public interface IImageSender {

    /**
	 * Send imageData to targetID.
	 * 
	 * @param targetID the target to send the image to.  May be <code>null</code>.
	 * @param imageData the imageData to send.  May not be <code>null</code>.
	 */
    public void sendImage(ID targetID, ImageData imageData);
}
