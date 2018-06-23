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
import org.eclipse.swt.graphics.RGB;

/**
 * Changable settings for the GC.  Used in the UI and AbstractTool to set the GC.
 * @author kgilmer
 *
 */
public class DrawSettings implements Serializable {

    private static final long serialVersionUID = -8547433052358403391L;

    private int penWidth;

    private RGB backgroundColor;

    private RGB forgroundColor;

    private boolean isAntialaised;

    public  DrawSettings() {
        penWidth = 1;
        backgroundColor = new RGB(255, 255, 255);
        forgroundColor = new RGB(0, 0, 0);
    }

    public RGB getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(RGB backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public RGB getForgroundColor() {
        return forgroundColor;
    }

    public void setForgroundColor(RGB forgroundColor) {
        this.forgroundColor = forgroundColor;
    }

    public int getPenWidth() {
        return penWidth;
    }

    public void setPenWidth(int penWidth) {
        this.penWidth = penWidth;
    }

    public boolean isAntialias() {
        return isAntialaised;
    }

    public void setAntialias(boolean isAntialaised) {
        this.isAntialaised = isAntialaised;
    }
}
