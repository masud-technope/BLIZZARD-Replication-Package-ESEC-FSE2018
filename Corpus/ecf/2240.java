/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui.screencapture;

import java.io.ByteArrayOutputStream;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.collab.ui.AbstractCollabShare;
import org.eclipse.ecf.ui.screencapture.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Send/receive requests to share a specific view (identified by view ID).
 */
public class ScreenCaptureShare extends AbstractCollabShare {

    private static final int MAX_MESSAGE_SIZE = 8096;

    private static final Map screenCaptureSharechannels = new Hashtable();

    public static ScreenCaptureShare getScreenCaptureShare(ID containerID) {
        return (ScreenCaptureShare) screenCaptureSharechannels.get(containerID);
    }

    public static ScreenCaptureShare addScreenCaptureShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        return (ScreenCaptureShare) screenCaptureSharechannels.put(containerID, new ScreenCaptureShare(channelAdapter));
    }

    public static ScreenCaptureShare removeScreenCaptureShare(ID containerID) {
        return (ScreenCaptureShare) screenCaptureSharechannels.remove(containerID);
    }

    public  ScreenCaptureShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.datashare.AbstractShare#dispose()
	 */
    public synchronized void dispose() {
        super.dispose();
        shells.clear();
    }

    Map shells = new HashMap();

    protected void handleScreenCaptureStart(final ID id, final String fromUser, final ImageWrapper imageWrapper) {
        final Display defaultDisplay = Display.getDefault();
        defaultDisplay.asyncExec(new Runnable() {

            public void run() {
                ShowImageShell showImageShell = (ShowImageShell) shells.get(id);
                if (showImageShell == null) {
                    showImageShell = new ShowImageShell(defaultDisplay, id, new DisposeListener() {

                        public void widgetDisposed(DisposeEvent e) {
                            shells.remove(id);
                        }
                    });
                    shells.put(id, showImageShell);
                }
                showImageShell.initialize(NLS.bind(Messages.ScreenCaptureShare_SCREEN_CAPTURE_RECEIVE_TITLE, fromUser), imageWrapper);
                showImageShell.open();
            }
        });
    }

    protected void handleScreenCaptureData(final ID id, final byte[] data, final Boolean done) {
        final ShowImageShell showImageShell = (ShowImageShell) shells.get(id);
        if (showImageShell != null) {
            final Display display = showImageShell.getDisplay();
            if (display != null) {
                display.asyncExec(new Runnable() {

                    public void run() {
                        showImageShell.addData(data);
                        if (done.booleanValue())
                            showImageShell.showImage();
                    }
                });
            }
        }
    }

    public void sendImage(final ID senderID, final String senderuser, final ID toID, ImageData imageData) {
        try {
            sendMessage(toID, serialize(new ScreenCaptureStartMessage(senderID, senderuser, new ImageWrapper(imageData))));
            final byte[] compressedData = ScreenCaptureUtil.compress(imageData.data);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream(MAX_MESSAGE_SIZE);
            int startPos = 0;
            while (startPos <= compressedData.length) {
                bos.reset();
                final int length = Math.min(compressedData.length - startPos, MAX_MESSAGE_SIZE);
                bos.write(compressedData, startPos, length);
                startPos += MAX_MESSAGE_SIZE;
                bos.flush();
                sendMessage(toID, serialize(new ScreenCaptureDataMessage(senderID, bos.toByteArray(), new Boolean((compressedData.length - startPos) <= 0))));
            }
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_SEND, e);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.AbstractShare#handleChannelData(byte[])
	 */
    protected void handleMessage(ID fromContainerID, byte[] data) {
        try {
            final Object msg = deserialize(data);
            if (msg instanceof ScreenCaptureStartMessage) {
                handleScreenCaptureStart(((ScreenCaptureStartMessage) msg).getSenderID(), ((ScreenCaptureStartMessage) msg).getSenderUser(), ((ScreenCaptureStartMessage) msg).getImageWrapper());
            } else if (msg instanceof ScreenCaptureDataMessage) {
                handleScreenCaptureData(((ScreenCaptureDataMessage) msg).getSenderID(), ((ScreenCaptureDataMessage) msg).getData(), ((ScreenCaptureDataMessage) msg).getIsDone());
            }
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_MESSAGE, e);
        }
    }
}
