/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.text.build;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.pde.core.build.*;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.pde.internal.core.text.IDocumentKey;
import org.eclipse.pde.internal.core.text.IEditingModel;
import org.eclipse.pde.internal.core.util.PropertiesUtil;

public class BuildEntry implements IBuildEntry, IDocumentKey {

    private int fLength = -1;

    private int fOffset = -1;

    private IBuildModel fModel;

    private String fName;

    private ArrayList<Object> fTokens = new ArrayList();

    private String fLineDelimiter;

    public  BuildEntry(String name, IBuildModel model) {
        fName = name;
        fModel = model;
        setLineDelimiter();
    }

    private void setLineDelimiter() {
        if (fModel instanceof IEditingModel) {
            IDocument document = ((IEditingModel) fModel).getDocument();
            fLineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
        } else {
            //$NON-NLS-1$
            fLineDelimiter = System.getProperty("line.separator");
        }
    }

    @Override
    public void addToken(String token) throws CoreException {
        if (fTokens.contains(token))
            return;
        if (fTokens.add(token))
            getModel().fireModelObjectChanged(this, getName(), null, token);
    }

    @Override
    public String getName() {
        return fName;
    }

    @Override
    public String[] getTokens() {
        return fTokens.toArray(new String[fTokens.size()]);
    }

    @Override
    public boolean contains(String token) {
        return fTokens.contains(token);
    }

    @Override
    public void removeToken(String token) throws CoreException {
        if (fTokens.remove(token))
            getModel().fireModelObjectChanged(this, getName(), token, null);
    }

    @Override
    public void renameToken(String oldToken, String newToken) throws CoreException {
        int index = fTokens.indexOf(oldToken);
        if (index != -1) {
            fTokens.set(index, newToken);
            getModel().fireModelObjectChanged(this, getName(), oldToken, newToken);
        }
    }

    @Override
    public void setName(String name) {
        String oldName = fName;
        if (getModel() != null) {
            try {
                IBuild build = getModel().getBuild();
                build.remove(this);
                fName = name;
                build.add(this);
            } catch (CoreException e) {
                PDECore.logException(e);
            }
            getModel().fireModelObjectChanged(this, getName(), oldName, name);
        } else
            fName = name;
    }

    @Override
    public int getOffset() {
        return fOffset;
    }

    @Override
    public void setOffset(int offset) {
        fOffset = offset;
    }

    @Override
    public int getLength() {
        return fLength;
    }

    @Override
    public void setLength(int length) {
        fLength = length;
    }

    @Override
    public void write(String indent, PrintWriter writer) {
    }

    @Override
    public IBuildModel getModel() {
        return fModel;
    }

    public void processEntry(String value) {
        //$NON-NLS-1$
        StringTokenizer stok = new StringTokenizer(value, ",");
        IPath root = getRootPath();
        while (stok.hasMoreTokens()) {
            String token = stok.nextToken().trim();
            token = fromRelative(token, root);
            fTokens.add(token);
        }
    }

    @Override
    public String write() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(PropertiesUtil.createWritableName(fName));
        //$NON-NLS-1$
        buffer.append(" = ");
        int indentLength = fName.length() + 3;
        IPath rootPath = getRootPath();
        for (int i = 0; i < fTokens.size(); i++) {
            String token = fTokens.get(i).toString();
            token = toRelative(token, rootPath);
            buffer.append(PropertiesUtil.createEscapedValue(token));
            if (i < fTokens.size() - 1) {
                buffer.append(",\\");
                buffer.append(fLineDelimiter);
                for (int j = 0; j < indentLength; j++) {
                    buffer.append(" ");
                }
            }
        }
        buffer.append(fLineDelimiter);
        return buffer.toString();
    }

    public void swap(int index1, int index2) {
        Object obj1 = fTokens.get(index1);
        Object obj2 = fTokens.set(index2, obj1);
        fTokens.set(index1, obj2);
        getModel().fireModelObjectChanged(this, getName(), new Object[] { obj1, obj2 }, new Object[] { obj2, obj1 });
    }

    public String getPreviousToken(String targetToken) {
        if (fTokens.size() <= 1) {
            return null;
        }
        int targetIndex = fTokens.indexOf(targetToken);
        if (targetIndex < 0) {
            return null;
        } else if (targetIndex == 0) {
            return null;
        }
        String previousToken = (String) fTokens.get(targetIndex - 1);
        return previousToken;
    }

    public String getNextToken(String targetToken) {
        if (fTokens.size() <= 1) {
            return null;
        }
        int targetIndex = fTokens.indexOf(targetToken);
        int lastIndex = fTokens.size() - 1;
        if (targetIndex < 0) {
            return null;
        } else if (targetIndex >= lastIndex) {
            return null;
        }
        String nextToken = (String) fTokens.get(targetIndex + 1);
        return nextToken;
    }

    public int getIndexOf(String targetToken) {
        return fTokens.indexOf(targetToken);
    }

    public void addToken(String token, int position) {
        if (position < 0) {
            return;
        } else if (position > fTokens.size()) {
            return;
        }
        if (fTokens.contains(token)) {
            return;
        }
        fTokens.add(position, token);
        getModel().fireModelObjectChanged(this, getName(), null, token);
    }

    IPath getRootPath() {
        if (fName.startsWith(IBuildEntry.JAR_PREFIX) || fName.startsWith(IBuildEntry.OUTPUT_PREFIX)) {
            IResource resource = getModel().getUnderlyingResource();
            if (resource != null) {
                IProject project = resource.getProject();
                if (project != null) {
                    IContainer root = PDEProject.getBundleRoot(project);
                    if (root != null && !root.equals(project)) {
                        return root.getProjectRelativePath();
                    }
                }
            }
        }
        return null;
    }

    String toRelative(String token, IPath root) {
        if (root == null) {
            return token;
        }
        return (new Path(token)).makeRelativeTo(root).toPortableString();
    }

    String fromRelative(String token, IPath root) {
        if (root == null) {
            return token;
        }
        return root.append(new Path(token)).toPortableString();
    }
}
