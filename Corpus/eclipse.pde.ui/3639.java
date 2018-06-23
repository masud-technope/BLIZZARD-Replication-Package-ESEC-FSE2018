/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@gmail.com> - Bug 424111
 *******************************************************************************/
package org.eclipse.ui.internal.views.log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * Displays the error log in non-Win32 platforms - see bug 55314.
 */
public final class OpenLogDialog extends TrayDialog {

    // input log file
    private File logFile;

    // location/size configuration
    private IDialogSettings dialogSettings;

    private Point dialogLocation;

    private Point dialogSize;

    private int DEFAULT_WIDTH = 750;

    private int DEFAULT_HEIGHT = 800;

    public  OpenLogDialog(Shell parentShell, File logFile) {
        super(parentShell);
        this.logFile = logFile;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.MODELESS);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.OpenLogDialog_title);
        readConfiguration();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
    }

    @Override
    public void create() {
        super.create();
        // dialog location
        if (dialogLocation != null)
            getShell().setLocation(dialogLocation);
        // dialog size
        if (dialogSize != null)
            getShell().setSize(dialogSize);
        else
            getShell().setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        getButton(IDialogConstants.CLOSE_ID).setFocus();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite outer = (Composite) super.createDialogArea(parent);
        Text text = new Text(outer, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.NO_FOCUS | SWT.H_SCROLL);
        text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        text.setLayoutData(gridData);
        text.setText(getLogSummary());
        return outer;
    }

    private String getLogSummary() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        if (logFile.length() > LogReader.MAX_FILE_LENGTH) {
            readLargeFileWithMonitor(writer);
        } else {
            readFileWithMonitor(writer);
        }
        writer.close();
        return out.toString();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID) {
            storeSettings();
            close();
        }
        super.buttonPressed(buttonId);
    }

    //--------------- configuration handling --------------
    /**
	 * Stores the current state in the dialog settings.
	 *
	 * @since 2.0
	 */
    private void storeSettings() {
        writeConfiguration();
    }

    /**
	 * Returns the dialog settings object used to share state between several
	 * event detail dialogs.
	 *
	 * @return the dialog settings to be used
	 */
    private IDialogSettings getDialogSettings() {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();
        dialogSettings = settings.getSection(getClass().getName());
        if (dialogSettings == null)
            dialogSettings = settings.addNewSection(getClass().getName());
        return dialogSettings;
    }

    /**
	 * Initializes itself from the dialog settings with the same state as at the
	 * previous invocation.
	 */
    private void readConfiguration() {
        IDialogSettings s = getDialogSettings();
        try {
            //$NON-NLS-1$
            int x = s.getInt("x");
            //$NON-NLS-1$
            int y = s.getInt("y");
            dialogLocation = new Point(x, y);
            //$NON-NLS-1$
            x = s.getInt("width");
            //$NON-NLS-1$
            y = s.getInt("height");
            dialogSize = new Point(x, y);
        } catch (NumberFormatException e) {
            dialogLocation = null;
            dialogSize = null;
        }
    }

    private void writeConfiguration() {
        IDialogSettings s = getDialogSettings();
        Point location = getShell().getLocation();
        //$NON-NLS-1$
        s.put("x", location.x);
        //$NON-NLS-1$
        s.put("y", location.y);
        Point size = getShell().getSize();
        //$NON-NLS-1$
        s.put("width", size.x);
        //$NON-NLS-1$
        s.put("height", size.y);
    }

    // reading file within MAX_FILE_LENGTH size
    void readFile(PrintWriter writer) throws FileNotFoundException, IOException {
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new FileReader(logFile));
            while (bReader.ready()) {
                writer.println(bReader.readLine());
            }
        } finally {
            try {
                if (bReader != null)
                    bReader.close();
            } catch (IOException // do nothing
            e1) {
            }
        }
    }

    // reading large files
    void readLargeFile(PrintWriter writer) throws FileNotFoundException, IOException {
        RandomAccessFile random = null;
        boolean hasStarted = false;
        try {
            //$NON-NLS-1$
            random = new RandomAccessFile(logFile, "r");
            random.seek(logFile.length() - LogReader.MAX_FILE_LENGTH);
            for (; ; ) {
                String line = random.readLine();
                if (line == null)
                    break;
                line = line.trim();
                if (line.length() == 0)
                    continue;
                if (!hasStarted && (//$NON-NLS-1$
                line.startsWith("!ENTRY") || line.startsWith(LogSession.SESSION)))
                    hasStarted = true;
                if (hasStarted)
                    writer.println(line);
                continue;
            }
        } finally {
            try {
                if (random != null)
                    random.close();
            } catch (IOException // do nothing
            e1) {
            }
        }
    }

    private void readLargeFileWithMonitor(final PrintWriter writer) {
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.OpenLogDialog_message, IProgressMonitor.UNKNOWN);
                try {
                    readLargeFile(writer);
                } catch (IOException e) {
                    writer.println(Messages.OpenLogDialog_cannotDisplay);
                }
            }
        };
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getParentShell());
        try {
            dialog.run(true, true, runnable);
        } catch (InvocationTargetException // do nothing
        e) {
        } catch (InterruptedException // do nothing
        e) {
        }
    }

    private void readFileWithMonitor(final PrintWriter writer) {
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.OpenLogDialog_message, IProgressMonitor.UNKNOWN);
                try {
                    readFile(writer);
                } catch (IOException e) {
                    writer.println(Messages.OpenLogDialog_cannotDisplay);
                }
            }
        };
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getParentShell());
        try {
            dialog.run(true, true, runnable);
        } catch (InvocationTargetException // do nothing
        e) {
        } catch (InterruptedException // do nothing
        e) {
        }
    }
}
