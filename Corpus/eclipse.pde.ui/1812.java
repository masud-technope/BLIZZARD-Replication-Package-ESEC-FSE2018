/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.environment.Constants;
import org.eclipse.pde.internal.core.iproduct.IConfigurationFileInfo;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.*;

public class ConfigurationFileInfo extends ProductObject implements IConfigurationFileInfo {

    private static final long serialVersionUID = 1L;

    private String fUse;

    private String fPath;

    private static final String LIN = Constants.OS_LINUX;

    private static final String MAC = Constants.OS_MACOSX;

    private static final String SOL = Constants.OS_SOLARIS;

    private static final String WIN = Constants.OS_WIN32;

    private String fLinPath, fLinUse;

    private String fMacPath, fMacUse;

    private String fSolPath, fSolUse;

    private String fWinPath, fWinUse;

    public  ConfigurationFileInfo(IProductModel model) {
        super(model);
    }

    public void setPath(String path) {
        String old = fPath;
        fPath = path;
        if (isEditable())
            firePropertyChanged(P_PATH, old, fPath);
    }

    public String getPath() {
        return fPath;
    }

    @Override
    public void parse(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            fPath = element.getAttribute(P_PATH);
            fUse = element.getAttribute(P_USE);
            NodeList list = element.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node child = list.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getNodeName().equals(LIN)) {
                        fLinPath = getText(child);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        fLinUse = fLinPath == null ? "default" : "custom";
                    } else if (child.getNodeName().equals(MAC)) {
                        fMacPath = getText(child);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        fMacUse = fMacPath == null ? "default" : "custom";
                    } else if (child.getNodeName().equals(SOL)) {
                        fSolPath = getText(child);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        fSolUse = fSolPath == null ? "default" : "custom";
                    } else if (child.getNodeName().equals(WIN)) {
                        fWinPath = getText(child);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        fWinUse = fWinPath == null ? "default" : "custom";
                    }
                }
            }
            // if we have an old path, we convert it to a platform specific path if it wasn't set
            if (//$NON-NLS-1$
            fPath != null && fUse.equals("custom")) {
                if (fLinUse == null) {
                    fLinPath = fLinPath == null ? fPath : null;
                    //$NON-NLS-1$
                    fLinUse = //$NON-NLS-1$
                    "custom";
                }
                if (fMacUse == null) {
                    fMacPath = fMacPath == null ? fPath : null;
                    //$NON-NLS-1$
                    fMacUse = //$NON-NLS-1$
                    "custom";
                }
                if (fSolUse == null) {
                    fSolPath = fSolPath == null ? fPath : null;
                    //$NON-NLS-1$
                    fSolUse = //$NON-NLS-1$
                    "custom";
                }
                if (fWinUse == null) {
                    fWinPath = fWinPath == null ? fPath : null;
                    //$NON-NLS-1$
                    fWinUse = //$NON-NLS-1$
                    "custom";
                }
                // null out things
                fPath = null;
                //$NON-NLS-1$
                fUse = //$NON-NLS-1$
                "default";
            }
        }
    }

    private String getText(Node node) {
        node.normalize();
        Node text = node.getFirstChild();
        if (text != null && text.getNodeType() == Node.TEXT_NODE) {
            return text.getNodeValue();
        }
        return null;
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        // the first entry here is for backwards compatibility
        //$NON-NLS-1$
        writer.print(indent + "<configIni");
        if (fUse != null)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            writer.print(" " + P_USE + "=\"" + fUse + "\"");
        if (fPath != null && fPath.trim().length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            writer.print(" " + P_PATH + "=\"" + getWritableString(fPath.trim()) + "\"");
        //$NON-NLS-1$
        writer.println(">");
        // write out the platform specific config.ini entries
        if (fLinPath != null) {
            writer.print(indent);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   <" + LIN + ">");
            writer.print(getWritableString(fLinPath.trim()));
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println("</" + LIN + ">");
        }
        if (fMacPath != null) {
            writer.print(indent);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   <" + MAC + ">");
            writer.print(getWritableString(fMacPath.trim()));
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println("</" + MAC + ">");
        }
        if (fSolPath != null) {
            writer.print(indent);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   <" + SOL + ">");
            writer.print(getWritableString(fSolPath.trim()));
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println("</" + SOL + ">");
        }
        if (fWinPath != null) {
            writer.print(indent);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   <" + WIN + ">");
            writer.print(getWritableString(fWinPath.trim()));
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println("</" + WIN + ">");
        }
        //$NON-NLS-1$
        writer.print(indent + "</configIni>");
        writer.println();
    }

    @Override
    public void setUse(String os, String use) {
        if (os == null) {
            String old = fUse;
            fUse = use;
            if (isEditable())
                firePropertyChanged(P_USE, old, fUse);
        }
        if (Platform.OS_WIN32.equals(os)) {
            String old = fWinUse;
            fWinUse = use;
            if (isEditable())
                firePropertyChanged(WIN, old, fWinUse);
        } else if (Platform.OS_LINUX.equals(os)) {
            String old = fLinUse;
            fLinUse = use;
            if (isEditable())
                firePropertyChanged(LIN, old, fLinUse);
        } else if (Platform.OS_MACOSX.equals(os)) {
            String old = fMacUse;
            fMacUse = use;
            if (isEditable())
                firePropertyChanged(MAC, old, fMacUse);
        } else if (Platform.OS_SOLARIS.equals(os)) {
            String old = fSolUse;
            fSolUse = use;
            if (isEditable())
                firePropertyChanged(SOL, old, fSolUse);
        }
    }

    @Override
    public String getUse(String os) {
        if (os == null)
            return fUse;
        if (Platform.OS_WIN32.equals(os)) {
            return fWinUse;
        } else if (Platform.OS_LINUX.equals(os)) {
            return fLinUse;
        } else if (Platform.OS_MACOSX.equals(os)) {
            return fMacUse;
        } else if (Platform.OS_SOLARIS.equals(os)) {
            return fSolUse;
        }
        return null;
    }

    @Override
    public void setPath(String os, String path) {
        if (os == null) {
            String old = fPath;
            fPath = path;
            if (isEditable())
                firePropertyChanged(P_PATH, old, fPath);
        }
        if (Platform.OS_WIN32.equals(os)) {
            String old = fWinPath;
            fWinPath = path;
            if (isEditable())
                firePropertyChanged(WIN, old, fWinPath);
        } else if (Platform.OS_LINUX.equals(os)) {
            String old = fLinPath;
            fLinPath = path;
            if (isEditable())
                firePropertyChanged(LIN, old, fLinPath);
        } else if (Platform.OS_MACOSX.equals(os)) {
            String old = fMacPath;
            fMacPath = path;
            if (isEditable())
                firePropertyChanged(MAC, old, fMacPath);
        } else if (Platform.OS_SOLARIS.equals(os)) {
            String old = fSolPath;
            fSolPath = path;
            if (isEditable())
                firePropertyChanged(SOL, old, fSolPath);
        }
    }

    @Override
    public String getPath(String os) {
        if (os == null)
            return fPath;
        if (Platform.OS_WIN32.equals(os)) {
            return fWinPath;
        } else if (Platform.OS_LINUX.equals(os)) {
            return fLinPath;
        } else if (Platform.OS_MACOSX.equals(os)) {
            return fMacPath;
        } else if (Platform.OS_SOLARIS.equals(os)) {
            return fSolPath;
        }
        return null;
    }
}
