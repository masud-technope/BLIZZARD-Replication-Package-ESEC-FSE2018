/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tutorial.distribution.common.hello;

public class HelloImpl implements IHello {

    public void hello() {
        System.out.println("Somebody just called hello.");
    }

    public String helloThere(String from) {
        String response = from + " just called helloThere";
        System.out.println(response);
        return response;
    }
}
