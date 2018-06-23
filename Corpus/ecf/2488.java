/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ken Gilmer <kgilmer@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare.toolbox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * Creates a box shape.
 * 
 * @author kgilmer
 *
 */
public class Box extends AbstractTool {

    private static final long serialVersionUID = -165859440014182966L;

    //private boolean dragging = false;
    private boolean dragging = false;

    public String getName() {
        return "Box";
    }

    public Image getImage() {
        return null;
    }

    public void draw(final Canvas canvas) {
        Display display = canvas.getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                GC gc = new GC(canvas);
                setupGC(gc);
                // gc.setForeground(new Color(display,new RGB(128,128,128)));
                gc.drawRectangle(startX, startY, endX - startX, endY - startY);
                gc.dispose();
            }
        });
    }

    public void handleUIEvent(Event event, Canvas canvas) {
        switch(event.type) {
            case SWT.MouseUp:
                draw(canvas);
                penDown = false;
                dragging = false;
                isComplete = true;
                break;
            case SWT.MouseMove:
                if (dragging) {
                    endX = event.x;
                    endY = event.y;
                    penDown = true;
                }
                break;
            case SWT.MouseDown:
                if (!dragging) {
                    startX = event.x;
                    startY = event.y;
                    dragging = true;
                    isComplete = false;
                }
                break;
        }
    }
}
