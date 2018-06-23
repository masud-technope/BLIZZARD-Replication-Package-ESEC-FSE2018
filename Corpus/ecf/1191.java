/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import java.io.ObjectStreamClass;

/**
 * @since 3.7
 */
public interface IClassResolver {

    //$NON-NLS-1$
    public static final String BUNDLE_PROP_NAME = "org.eclipse.ecf.core.util.classresolver.bundleSymbolicName";

    public Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException;
}
