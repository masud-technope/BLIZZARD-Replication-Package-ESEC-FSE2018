/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ltk.core.refactoring.tests.history;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class MockRefactoringDescriptor extends RefactoringDescriptor {

    public static final String ID = "org.eclipse.ltk.core.mock";

    private final Map<String, String> fArguments = new HashMap();

    public  MockRefactoringDescriptor(String project, String description, String comment, int flags) {
        super(ID, project, description, comment, flags);
    }

    public  MockRefactoringDescriptor(String project, String description, String comment, Map<String, String> arguments, int flags) {
        this(project, description, comment, flags);
        fArguments.putAll(arguments);
    }

    @Override
    public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
        return new MockRefactoring();
    }

    public Map<String, String> getArguments() {
        return fArguments;
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(128);
        buffer.append(getClass().getName());
        if (getID().equals(ID_UNKNOWN))
            //$NON-NLS-1$
            buffer.append("[unknown refactoring]");
        else {
            //$NON-NLS-1$
            buffer.append("[timeStamp=");
            buffer.append(getTimeStamp());
            //$NON-NLS-1$
            buffer.append(",id=");
            buffer.append(getID());
            //$NON-NLS-1$
            buffer.append(",description=");
            buffer.append(getDescription());
            //$NON-NLS-1$
            buffer.append(",project=");
            buffer.append(getProject());
            //$NON-NLS-1$
            buffer.append(",comment=");
            buffer.append(getComment());
            //$NON-NLS-1$
            buffer.append(",arguments=");
            buffer.append(new TreeMap(getArguments()));
            //$NON-NLS-1$
            buffer.append(",flags=");
            buffer.append(getFlags());
            //$NON-NLS-1$
            buffer.append("]");
        }
        return buffer.toString();
    }
}
