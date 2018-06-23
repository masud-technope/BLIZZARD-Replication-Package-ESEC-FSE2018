/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class ChatWindow extends ApplicationWindow {

    private static final long FLASH_INTERVAL = 600;

    private final LineChatClientView view;

    private final TableViewer table;

    private final String initText;

    private ChatComposite chat;

    private Image image;

    private Image blank;

    private boolean flashing;

    private final Runnable flipImage = new Runnable() {

        public void run() {
            final Shell shell = getShell();
            if (shell != null && !shell.isDisposed())
                if (blank == shell.getImage()) {
                    if (image != null && !image.isDisposed())
                        shell.setImage(image);
                } else {
                    if (blank != null && !blank.isDisposed())
                        shell.setImage(blank);
                }
        }
    };

    private Flash flash;

    public  ChatWindow(LineChatClientView view, TableViewer table, String initText) {
        super(null);
        this.view = view;
        this.table = table;
        this.initText = initText;
        addStatusLine();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(NLS.bind(Messages.ChatWindow_SHELL_TEXT, view.name));
        image = SharedImages.getImage(SharedImages.IMG_USER_AVAILABLE);
        newShell.setImage(image);
        final RGB[] colors = new RGB[2];
        colors[0] = new RGB(0, 0, 0);
        colors[1] = new RGB(255, 255, 255);
        final ImageData data = new ImageData(16, 16, 1, new PaletteData(colors));
        data.transparentPixel = 0;
        blank = new Image(newShell.getDisplay(), data);
        flash = new Flash(newShell.getDisplay());
        new Thread(flash).start();
        newShell.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                if (flash != null)
                    flash.dispose();
                if (blank != null)
                    blank.dispose();
            }
        });
        newShell.addShellListener(new ShellAdapter() {

            public void shellActivated(ShellEvent e) {
                stopFlashing();
                if (!chat.isDisposed())
                    chat.textinput.setFocus();
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
    protected Control createContents(Composite parent) {
        chat = new ChatComposite(view, parent, table, initText, this);
        chat.setLayoutData(new GridData(GridData.FILL_BOTH));
        chat.setFont(parent.getFont());
        return chat;
    }

    ChatComposite getChat() {
        return chat;
    }

    boolean hasFocus() {
        if (getShell().isDisposed())
            return false;
        else
            return hasFocus(getShell());
    }

    private boolean hasFocus(Composite composite) {
        if (composite.isFocusControl())
            return true;
        else {
            final Control[] children = composite.getChildren();
            for (int i = 0; i < children.length; ++i) if (children[i] instanceof Composite && hasFocus((Composite) children[i]))
                return true;
            else if (children[i].isFocusControl())
                return true;
        }
        return false;
    }

    void flash() {
        synchronized (flash) {
            if (!flashing) {
                flashing = true;
                flash.notify();
            }
        }
    }

    private void stopFlashing() {
        synchronized (flash) {
            if (flashing) {
                flashing = false;
                if (!getShell().isDisposed() && image != null && !image.isDisposed())
                    getShell().setImage(image);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
    protected void handleShellCloseEvent() {
        if (!getShell().isDisposed())
            getShell().setVisible(false);
    }

    private class Flash implements Runnable {

        private final Display display;

        private boolean disposed;

        public  Flash(Display display) {
            this.display = display;
        }

        public synchronized void dispose() {
            if (!disposed) {
                disposed = true;
                notify();
            }
        }

        public void run() {
            while (true) {
                synchronized (this) {
                    try {
                        while (!flashing && !disposed) wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
                if (disposed && display.isDisposed())
                    break;
                display.syncExec(flipImage);
                synchronized (this) {
                    try {
                        wait(FLASH_INTERVAL);
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }
}
