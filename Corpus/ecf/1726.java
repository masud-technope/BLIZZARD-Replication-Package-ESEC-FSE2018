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

public class ComplexTypesImpl implements IComplexTypes {

    private MyComplexData data = new MyComplexData("host1", "host2");

    public MyComplexData get() {
        return data;
    }

    public void set(MyComplexData complex) {
        data = complex;
    }
}
