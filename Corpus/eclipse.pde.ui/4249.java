/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource Corporation - initial API and implementation
 *     IBM Corporation - additional enhancements
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import org.eclipse.pde.internal.core.iproduct.IConfigurationProperty;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConfigurationProperty extends ProductObject implements IConfigurationProperty {

    private static final long serialVersionUID = -3549668957352554826L;

    private String fName;

    private String fValue;

    private String fOS;

    private String fArch;

    /**
	 * Only for parsing usage
	 * @param model
	 */
     ConfigurationProperty(IProductModel model) {
        super(model);
    }

    @Override
    public void parse(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            //$NON-NLS-1$
            fName = element.getAttribute("name");
            //$NON-NLS-1$
            fValue = element.getAttribute("value");
            //$NON-NLS-1$
            fOS = element.getAttribute("os");
            //$NON-NLS-1$
            fArch = element.getAttribute("arch");
        }
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print(indent + "<property name=\"" + fName + "\"");
        //$NON-NLS-1$//$NON-NLS-2$
        writer.print(" value=\"" + fValue + "\"");
        if (fOS.length() > 0) {
            //$NON-NLS-1$//$NON-NLS-2$
            writer.print(" os=\"" + fOS + "\"");
        }
        if (fArch.length() > 0) {
            //$NON-NLS-1$//$NON-NLS-2$
            writer.print(" arch=\"" + fArch + "\"");
        }
        //$NON-NLS-1$
        writer.println(" />");
    }

    @Override
    public String getName() {
        return fName;
    }

    @Override
    public String getValue() {
        return fValue;
    }

    @Override
    public void setName(String name) {
        String oldValue = fName;
        fName = name;
        if (isEditable() && !fName.equals(oldValue)) {
            firePropertyChanged(P_NAME, oldValue, fName);
        }
    }

    @Override
    public void setValue(String value) {
        String oldValue = fValue;
        fValue = value;
        if (isEditable() && !fValue.equals(oldValue)) {
            firePropertyChanged(P_VALUE, oldValue, fValue);
        }
    }

    @Override
    public String toString() {
        //$NON-NLS-1$
        return fName + " : " + fValue;
    }

    @Override
    public String getOs() {
        return fOS;
    }

    @Override
    public void setOs(String os) {
        String oldValue = fOS;
        fOS = os;
        if (isEditable() && !fOS.equals(oldValue)) {
            firePropertyChanged(P_OS, oldValue, fOS);
        }
    }

    @Override
    public String getArch() {
        return fArch;
    }

    @Override
    public void setArch(String arch) {
        String oldValue = fArch;
        fArch = arch;
        if (isEditable() && !fArch.equals(oldValue)) {
            firePropertyChanged(P_ARCH, oldValue, fArch);
        }
    }
}
