/*******************************************************************************
 *  Copyright (c) 2005, 2012 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.pde.internal.core.iproduct.ILauncherInfo;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.*;

public class LauncherInfo extends ProductObject implements ILauncherInfo {

    private static final long serialVersionUID = 1L;

    private boolean fUseIcoFile;

    private Map<String, String> fIcons = new HashMap();

    private String fLauncherName;

    public  LauncherInfo(IProductModel model) {
        super(model);
    }

    @Override
    public String getLauncherName() {
        return fLauncherName;
    }

    @Override
    public void setLauncherName(String name) {
        String old = fLauncherName;
        fLauncherName = name;
        if (isEditable())
            firePropertyChanged(P_LAUNCHER, old, fLauncherName);
    }

    @Override
    public void setIconPath(String iconId, String path) {
        if (path == null)
            //$NON-NLS-1$
            path = "";
        String old = fIcons.get(iconId);
        fIcons.put(iconId, path);
        if (isEditable())
            firePropertyChanged(iconId, old, path);
    }

    @Override
    public String getIconPath(String iconId) {
        return fIcons.get(iconId);
    }

    @Override
    public boolean usesWinIcoFile() {
        return fUseIcoFile;
    }

    @Override
    public void setUseWinIcoFile(boolean use) {
        boolean old = fUseIcoFile;
        fUseIcoFile = use;
        if (isEditable())
            firePropertyChanged(P_USE_ICO, Boolean.toString(old), Boolean.toString(fUseIcoFile));
    }

    @Override
    public void parse(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            //$NON-NLS-1$
            fLauncherName = ((Element) node).getAttribute("name");
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String name = child.getNodeName();
                    if (//$NON-NLS-1$
                    name.equals(//$NON-NLS-1$
                    "linux")) {
                        parseLinux((Element) child);
                    } else if (//$NON-NLS-1$
                    name.equals(//$NON-NLS-1$
                    "macosx")) {
                        parseMac((Element) child);
                    } else if (//$NON-NLS-1$
                    name.equals(//$NON-NLS-1$
                    "solaris")) {
                        parseSolaris((Element) child);
                    } else if (//$NON-NLS-1$
                    name.equals(//$NON-NLS-1$
                    "win")) {
                        parseWin((Element) child);
                    }
                }
            }
        }
    }

    private void parseWin(Element element) {
        //$NON-NLS-1$
        fUseIcoFile = "true".equals(element.getAttribute(P_USE_ICO));
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) children.item(i);
                String name = child.getNodeName();
                if (//$NON-NLS-1$
                name.equals("ico")) {
                    fIcons.put(P_ICO_PATH, //$NON-NLS-1$
                    child.getAttribute(//$NON-NLS-1$
                    "path"));
                } else if (//$NON-NLS-1$
                name.equals("bmp")) {
                    fIcons.put(WIN32_16_HIGH, child.getAttribute(WIN32_16_HIGH));
                    fIcons.put(WIN32_16_LOW, child.getAttribute(WIN32_16_LOW));
                    fIcons.put(WIN32_32_HIGH, child.getAttribute(WIN32_32_HIGH));
                    fIcons.put(WIN32_32_LOW, child.getAttribute(WIN32_32_LOW));
                    fIcons.put(WIN32_48_HIGH, child.getAttribute(WIN32_48_HIGH));
                    fIcons.put(WIN32_48_LOW, child.getAttribute(WIN32_48_LOW));
                    fIcons.put(WIN32_256_HIGH, child.getAttribute(WIN32_256_HIGH));
                }
            }
        }
    }

    private void parseSolaris(Element element) {
        fIcons.put(SOLARIS_LARGE, element.getAttribute(SOLARIS_LARGE));
        fIcons.put(SOLARIS_MEDIUM, element.getAttribute(SOLARIS_MEDIUM));
        fIcons.put(SOLARIS_SMALL, element.getAttribute(SOLARIS_SMALL));
        fIcons.put(SOLARIS_TINY, element.getAttribute(SOLARIS_TINY));
    }

    private void parseMac(Element element) {
        //$NON-NLS-1$
        fIcons.put(MACOSX_ICON, element.getAttribute("icon"));
    }

    private void parseLinux(Element element) {
        //$NON-NLS-1$
        fIcons.put(LINUX_ICON, element.getAttribute("icon"));
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$
        writer.print(indent + "<launcher");
        if (fLauncherName != null && fLauncherName.length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" name=\"" + fLauncherName + "\"");
        //$NON-NLS-1$
        writer.println(">");
        //$NON-NLS-1$
        writeLinux(indent + "   ", writer);
        //$NON-NLS-1$
        writeMac(indent + "   ", writer);
        //$NON-NLS-1$
        writeSolaris(indent + "   ", writer);
        //$NON-NLS-1$
        writerWin(indent + "   ", writer);
        //$NON-NLS-1$
        writer.println(indent + "</launcher>");
    }

    private void writerWin(String indent, PrintWriter writer) {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        writer.println(indent + "<win " + P_USE_ICO + "=\"" + Boolean.toString(fUseIcoFile) + "\">");
        String path = fIcons.get(P_ICO_PATH);
        if (path != null && path.length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(indent + "   <ico path=\"" + getWritableString(path) + "\"/>");
        //$NON-NLS-1$
        writer.print(indent + "   <bmp");
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_16_HIGH, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_16_LOW, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_32_HIGH, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_32_LOW, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_48_HIGH, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_48_LOW, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", WIN32_256_HIGH, writer);
        //$NON-NLS-1$
        writer.println("/>");
        //$NON-NLS-1$
        writer.println(indent + "</win>");
    }

    private void writeSolaris(String indent, PrintWriter writer) {
        //$NON-NLS-1$
        writer.print(indent + "<solaris");
        //$NON-NLS-1$
        writeIcon(indent + "   ", SOLARIS_LARGE, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", SOLARIS_MEDIUM, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", SOLARIS_SMALL, writer);
        //$NON-NLS-1$
        writeIcon(indent + "   ", SOLARIS_TINY, writer);
        //$NON-NLS-1$
        writer.println("/>");
    }

    private void writeIcon(String indent, String iconId, PrintWriter writer) {
        String icon = fIcons.get(iconId);
        if (icon != null && icon.length() > 0) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            writer.print(indent + "   " + iconId + "=\"" + getWritableString(icon) + "\"");
        }
    }

    private void writeMac(String indent, PrintWriter writer) {
        String icon = fIcons.get(MACOSX_ICON);
        if (icon != null && icon.length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(indent + "<macosx icon=\"" + getWritableString(icon) + "\"/>");
    }

    private void writeLinux(String indent, PrintWriter writer) {
        String icon = fIcons.get(LINUX_ICON);
        if (icon != null && icon.length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(indent + "<linux icon=\"" + getWritableString(icon) + "\"/>");
    }
}
