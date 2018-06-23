/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ken Gilmer <kgilmer@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare.toolbox;

import java.io.Serializable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;

/**
 * Common functionality for all tools.  Handles common variables and settings.
 * @author kgilmer
 *
 */
public abstract class AbstractTool implements Serializable {

    protected int startX, startY, endX, endY;

    protected boolean penDown = false;

    protected boolean isComplete = false;

    protected DrawSettings drawSettings;

    /**
	 * 
	 * @return Name of tool.
	 */
    public abstract String getName();

    /**
	 * @return Image of tool, for use in tool palette view.
	 */
    public abstract Image getImage();

    /**
	 * Causes to tool to create it's output.  May be called from remote clients.
	 * @param canvas
	 */
    public abstract void draw(final Canvas canvas);

    /**
	 * Have a tool handle a mouse event.  Used for local events only.
	 * @param event
	 * @param canvas
	 */
    public abstract void handleUIEvent(Event event, Canvas canvas);

    protected void setupGC(GC gc) {
        gc.setLineWidth(drawSettings.getPenWidth());
        if (drawSettings.isAntialias()) {
            gc.setAntialias(SWT.ON);
        } else {
            gc.setAntialias(SWT.OFF);
        }
    }

    /**
	 * Used to determine when an event should be sent to remote clients.
	 * @return
	 */
    public boolean isPenDown() {
        return penDown;
    }

    /**Used to determine when an event should be sent to remote clients.
	 * @return
	 */
    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean b) {
        isComplete = b;
    }

    /**
	 * Set the drawSettings.  Effects the GC and how shapes are drawn.
	 * @param drawSettings
	 */
    public void setDrawSettings(DrawSettings drawSettings) {
        this.drawSettings = drawSettings;
    }
}
