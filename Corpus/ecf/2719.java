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
package org.eclipse.ecf.ui.screencapture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public class ShowImageShell {

    Shell shell;

    ID senderID;

    ImageWrapper imageWrapper;

    Image image;

    List imageData;

    public  ShowImageShell(Display display, ID senderID, final DisposeListener disposeListener) {
        this.shell = new Shell(display);
        this.senderID = senderID;
        this.shell.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                disposeListener.widgetDisposed(e);
                ShowImageShell.this.senderID = null;
                ShowImageShell.this.imageWrapper = null;
                ShowImageShell.this.imageData = null;
                if (ShowImageShell.this.image != null) {
                    ShowImageShell.this.image.dispose();
                    ShowImageShell.this.image = null;
                }
            }
        });
    }

    public void initialize(String text, ImageWrapper iw) {
        if (shell != null) {
            shell.setText(text);
            this.imageWrapper = iw;
            Rectangle shellBounds = shell.getBounds();
            this.shell.setBounds(shell.computeTrim(shellBounds.x, shellBounds.y, imageWrapper.width, imageWrapper.height));
            this.imageData = new ArrayList();
        }
    }

    public void open() {
        if (shell != null)
            shell.open();
    }

    public Display getDisplay() {
        if (shell == null)
            return null;
        return shell.getDisplay();
    }

    public void close() {
        if (shell != null) {
            shell.getDisplay().asyncExec(new Runnable() {

                public void run() {
                    try {
                        if (!shell.isDisposed())
                            shell.close();
                        shell = null;
                    } catch (final Exception e) {
                    }
                }
            });
        }
    }

    public ID getSenderID() {
        return senderID;
    }

    public void addData(byte[] bytes) {
        this.imageData.add(bytes);
    }

    public void showImage() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (imageData != null) {
                for (final Iterator i = imageData.iterator(); i.hasNext(); ) {
                    bos.write((byte[]) i.next());
                }
            }
            bos.flush();
        } catch (final IOException e) {
        }
        imageData.clear();
        final byte[] uncompressedData = ScreenCaptureUtil.uncompress(bos.toByteArray());
        shell.getDisplay().asyncExec(new Runnable() {

            public void run() {
                image = new Image(shell.getDisplay(), imageWrapper.createImageData(uncompressedData));
                ShowImageShell.this.shell.addPaintListener(new PaintListener() {

                    public void paintControl(PaintEvent e) {
                        e.gc.drawImage(image, 0, 0);
                    }
                });
                ShowImageShell.this.shell.redraw();
            }
        });
    }
}
