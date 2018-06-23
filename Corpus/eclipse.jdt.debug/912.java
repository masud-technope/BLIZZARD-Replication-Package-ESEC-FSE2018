/*******************************************************************************
 *  Copyright (c) 2000, 2012 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests.breakpoints;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;
import org.eclipse.ui.IEditorPart;

/**
 * Tests deferred breakpoints.
 */
public class DeferredBreakpointTests extends AbstractDebugTest {

    /**
	 * Constructor
	 * @param name
	 */
    public  DeferredBreakpointTests(String name) {
        super(name);
    }

    /**
	 * Tests deferring several breakpoints
	 * @throws Exception
	 */
    public void testDeferredBreakpoints() throws Exception {
        String typeName = "Breakpoints";
        List<IBreakpoint> bps = new ArrayList<IBreakpoint>();
        int[] lines = new int[] { // anonymous class
        43, // blocks
        102, // constructor
        77, // else
        88, // finally after catch
        120, // finally after try
        128, // for loop
        93, // if
        81, // initializer
        17, // inner class
        22, // return true
        72, // instance method
        107, // static method 
        53, // case statement
        133, // default statement
        140, // synchronized blocks
        146, // try
        125, // catch
        118, // while
        97 };
        createBreakpoints(typeName, bps, lines);
        IJavaThread thread = null;
        try {
            // do not register launch - see bug 130911
            thread = launchToBreakpoint(typeName, false);
            assertNotNull("Breakpoint not hit within timeout period", thread);
            while (!bps.isEmpty()) {
                IBreakpoint hit = getBreakpoint(thread);
                assertNotNull("suspended, but not by breakpoint", hit);
                assertTrue("hit un-registered breakpoint", bps.contains(hit));
                assertTrue("suspended, but not by line breakpoint", hit instanceof ILineBreakpoint);
                ILineBreakpoint breakpoint = (ILineBreakpoint) hit;
                int lineNumber = breakpoint.getLineNumber();
                int stackLine = thread.getTopStackFrame().getLineNumber();
                assertEquals("line numbers of breakpoint and stack frame do not match", lineNumber, stackLine);
                bps.remove(breakpoint);
                breakpoint.delete();
                if (!bps.isEmpty()) {
                    thread = resume(thread);
                }
            }
        } finally {
            terminateAndRemove(thread);
            removeAllBreakpoints();
        }
    }

    private void createBreakpoints(String typeName, List<IBreakpoint> breakpoints, int[] lineNumbers) throws Exception {
        IType type = get14Project().findType(typeName);
        assertNotNull(type);
        IResource resource = type.getResource();
        assertTrue(resource instanceof IFile);
        IEditorPart editor = openEditor((IFile) resource);
        for (int i = 0; i < lineNumbers.length; i++) {
            IBreakpoint breakpoint = toggleBreakpoint(editor, lineNumbers[i]);
            assertTrue("Wrong kind of breakpoint", breakpoint instanceof IJavaLineBreakpoint);
            breakpoints.add(breakpoint);
        }
    }

    /**
	 * Tests disabling several breakpoints
	 * @throws Exception
	 */
    public void testDisabledBreakpoint() throws Exception {
        String typeName = "Breakpoints";
        ILineBreakpoint bp = createLineBreakpoint(52, typeName);
        bp.setEnabled(false);
        IJavaDebugTarget debugTarget = null;
        try {
            debugTarget = launchAndTerminate(typeName);
        } finally {
            terminateAndRemove(debugTarget);
            removeAllBreakpoints();
        }
    }

    /**
	 * Tests a cycle of enable/disable breakpoints
	 * @throws Exception
	 */
    public void testEnableDisableBreakpoint() throws Exception {
        String typeName = "HitCountLooper";
        ILineBreakpoint bp = createLineBreakpoint(16, typeName);
        bp.setEnabled(true);
        IJavaThread thread = null;
        try {
            thread = launchToLineBreakpoint(typeName, bp);
            bp.setEnabled(false);
            resumeAndExit(thread);
        } finally {
            terminateAndRemove(thread);
            removeAllBreakpoints();
        }
    }

    /**
	 * Tests skipping a single line breakpoint
	 * @throws Exception
	 */
    public void testSkipLineBreakpoint() throws Exception {
        String typeName = "Breakpoints";
        ILineBreakpoint bp = createLineBreakpoint(52, typeName);
        createLineBreakpoint(54, typeName);
        IJavaThread thread = null;
        try {
            thread = launchToLineBreakpoint(typeName, bp);
            getBreakpointManager().setEnabled(false);
            resumeAndExit(thread);
        } finally {
            terminateAndRemove(thread);
            removeAllBreakpoints();
            getBreakpointManager().setEnabled(true);
        }
    }
}
