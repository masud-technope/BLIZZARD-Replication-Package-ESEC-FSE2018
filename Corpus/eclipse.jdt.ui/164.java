/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.examples;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.jdt.core.IType;

public class MyRenameTypeParticipant extends RenameParticipant {

    private IType fType;

    @Override
    protected boolean initialize(Object element) {
        fType = (IType) element;
        return true;
    }

    @Override
    public String getName() {
        //$NON-NLS-1$
        return "My special file participant";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException {
        final HashMap<IFile, TextFileChange> changes = new HashMap();
        final String newName = getArguments().getNewName();
        // use the text search engine to find matches in my special files
        // in a real world implementation, clients would use their own, more precise search engine
        // limit to the current project
        IResource[] roots = { fType.getJavaProject().getProject() };
        //$NON-NLS-1$ // all files with file suffix 'special'
        String[] fileNamePatterns = { "*.special" };
        FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
        // only find the simple name of the type
        Pattern pattern = Pattern.compile(fType.getElementName());
        TextSearchRequestor collector = new TextSearchRequestor() {

            @Override
            public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
                IFile file = matchAccess.getFile();
                TextFileChange change = changes.get(file);
                if (change == null) {
                    // an other participant already modified that file?
                    TextChange textChange = getTextChange(file);
                    if (textChange != null) {
                        // don't try to merge changes
                        return false;
                    }
                    change = new TextFileChange(file.getName(), file);
                    change.setEdit(new MultiTextEdit());
                    changes.put(file, change);
                }
                ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), newName);
                change.addEdit(edit);
                //$NON-NLS-1$
                change.addTextEditGroup(//$NON-NLS-1$
                new TextEditGroup("Update type reference", edit));
                return true;
            }
        };
        TextSearchEngine.create().search(scope, collector, pattern, pm);
        if (changes.isEmpty())
            return null;
        //$NON-NLS-1$
        CompositeChange result = new CompositeChange("My special file updates");
        for (Iterator<TextFileChange> iter = changes.values().iterator(); iter.hasNext(); ) {
            result.add(iter.next());
        }
        return result;
    }
}
