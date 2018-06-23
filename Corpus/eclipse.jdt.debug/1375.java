/*******************************************************************************
 *  Copyright (c) 2004, 2015 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests.console;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

/**
 * Test the multiple stream delegate for the console
 */
public class IOConsoleMultipleStreamActionDelegate implements IActionDelegate2, IWorkbenchWindowActionDelegate {

    private boolean ended = false;

    /**
     * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
     */
    @Override
    public void init(IAction action) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate2#dispose()
     */
    @Override
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
     */
    @Override
    public void runWithEvent(IAction action, Event event) {
        run(action);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        runTest();
    }

    /**
     * Actually runs the test 
     */
    public void runTest() {
        ended = false;
        //$NON-NLS-1$
        final IOConsole console = new IOConsole("IO Test Console", null, DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_ACT_RUN));
        //        console.setWaterMarks(5, 10);
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        manager.addConsoles(new IConsole[] { console });
        final Display display = Display.getDefault();
        final IOConsoleInputStream in = console.getInputStream();
        final IOConsoleOutputStream echo = console.newOutputStream();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                in.setColor(display.getSystemColor(SWT.COLOR_BLUE));
                echo.setColor(display.getSystemColor(SWT.COLOR_RED));
            }
        });
        startInputReadThread(in, echo);
        IOConsoleOutputStream out = console.newOutputStream();
        startOutputThread(out);
    }

    private void startOutputThread(final IOConsoleOutputStream out) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                int i = 1;
                PrintStream ps = new PrintStream(out);
                //$NON-NLS-1$
                ps.println("Enter 'XXX' to stop");
                while (!ended) {
                    try {
                        //$NON-NLS-1$//$NON-NLS-2$
                        ps.println("TESTING(" + i + ")");
                        Thread.sleep(1000);
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, //$NON-NLS-1$
        "OUTPUT").start();
    }

    private void startInputReadThread(final InputStream in, final OutputStream out) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    byte b[] = new byte[1024];
                    while (!ended) {
                        int read = in.read(b);
                        String string = new String(b, 0, read);
                        //$NON-NLS-1$
                        ended = string.startsWith("XXX") ? true : false;
                        if (ended) {
                            //$NON-NLS-1$
                            out.write("Threads stopped".getBytes());
                            continue;
                        }
                        //$NON-NLS-1$
                        out.write("ECHO:".getBytes());
                        out.write(b, 0, read);
                    }
                } catch (Exception e) {
                }
            }
        }, //$NON-NLS-1$
        "INPUT").start();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    public void init(IWorkbenchWindow window) {
    }
}
