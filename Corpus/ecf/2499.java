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
 * Freeform draw tool.
 * @author kgilmer
 *
 */
public class Pencil extends AbstractTool {

    private static final long serialVersionUID = -111458978163259455L;

    private static int lastX;

    private static int lastY;

    public  Pencil() {
        isComplete = true;
    }

    public String getName() {
        return "Pencil";
    }

    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    public void draw(final Canvas canvas) {
        Display display = canvas.getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                GC gc = new GC(canvas);
                setupGC(gc);
                //gc.setForeground(new Color(display,new RGB(128,128,128)));
                gc.drawLine(endX, endY, startX, startY);
                gc.dispose();
            }
        });
    }

    public void handleUIEvent(Event event, Canvas canvas) {
        switch(event.type) {
            case SWT.MouseMove:
                if ((event.stateMask & SWT.BUTTON1) == 0)
                    break;
                startX = event.x;
                startY = event.y;
                endX = lastX;
                endY = lastY;
                isComplete = true;
                draw(canvas);
            case SWT.MouseDown:
                lastX = event.x;
                lastY = event.y;
                penDown = true;
                break;
            case SWT.MouseUp:
                penDown = false;
        }
    }
}
