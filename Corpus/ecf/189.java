/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.identity;

import java.security.BasicPermission;
import java.security.Permission;

public class NamespacePermission extends BasicPermission {

    private static final long serialVersionUID = 3257004371500806969L;

    //$NON-NLS-1$
    public static final String ADD_NAMESPACE = "add";

    //$NON-NLS-1$
    public static final String ALL_NAMESPACE = "all";

    //$NON-NLS-1$
    public static final String CONTAINS_NAMESPACE = "contains";

    //$NON-NLS-1$
    public static final String GET_NAMESPACE = "get";

    //$NON-NLS-1$
    public static final String REMOVE_NAMESPACE = "remove";

    protected String actions;

    public  NamespacePermission(String s) {
        super(s);
    }

    public  NamespacePermission(String s, String s1) {
        super(s, s1);
        actions = s1;
    }

    public String getActions() {
        return actions;
    }

    public boolean implies(Permission p) {
        return false;
    }
}
