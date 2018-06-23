/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

public class BasicScribbleView extends ViewPart {

    private Display display;

    private Canvas canvas;

    public void createPartControl(Composite parent) {
        canvas = new Canvas(parent, SWT.NONE);
        display = parent.getDisplay();
        canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        Listener listener = new Listener() {

            int lastX = 0, lastY = 0;

            public void handleEvent(Event event) {
                switch(event.type) {
                    case SWT.MouseMove:
                        if ((event.stateMask & SWT.BUTTON1) == 0)
                            break;
                        GC gc = new GC(canvas);
                        gc.drawLine(lastX, lastY, event.x, event.y);
                        gc.dispose();
                    case SWT.MouseDown:
                        lastX = event.x;
                        lastY = event.y;
                        break;
                }
            }
        };
        canvas.addListener(SWT.MouseDown, listener);
        canvas.addListener(SWT.MouseMove, listener);
    }

    public void setFocus() {
        canvas.setFocus();
    }
}
