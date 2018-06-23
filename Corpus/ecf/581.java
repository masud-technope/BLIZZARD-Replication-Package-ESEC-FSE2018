/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tutorial.distribution.common.complextypes;

import java.io.Serializable;

public class MyComplexData implements Serializable {

    private static final long serialVersionUID = -7599059139426924939L;

    private String str1;

    private String str2;

    public  MyComplexData(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

    public String getStr1() {
        return str1;
    }

    public String getStr2() {
        return str2;
    }

    public String toString() {
        return "MyComplexData [str1=" + str1 + ", str2=" + str2 + "]";
    }
}
