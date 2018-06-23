/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

/**
 * @since 3.3
 */
public class NameValuePropertyNode extends AbstractRSANode {

    private final String propertyName;

    private String propertyAlias;

    //$NON-NLS-1$
    private String nameValueSeparator = ": ";

    private Object propertyValue;

    public  NameValuePropertyNode(String propertyName, Object propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyAlias() {
        return this.propertyAlias;
    }

    public void setPropertyAlias(String propertyAlias) {
        this.propertyAlias = propertyAlias;
    }

    public String getNameValueSeparator() {
        return nameValueSeparator;
    }

    public void setNameValueSeparator(String nameValueSeparator) {
        this.nameValueSeparator = nameValueSeparator;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }
}
