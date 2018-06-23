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
import org.eclipse.core.runtime.Assert;
import org.osgi.framework.Bundle;

/**
 * @since 3.7
 */
public class BundleClassResolver implements IClassResolver {

    private final Bundle bundle;

    public  BundleClassResolver(Bundle b) {
        Assert.isNotNull(b);
        this.bundle = b;
    }

    @SuppressWarnings("unused")
    protected void verifyClass(ObjectStreamClass desc) throws ClassNotFoundException {
    // do nothing by default
    }

    public Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
        verifyClass(desc);
        try {
            return this.bundle.loadClass(desc.getName());
        } catch (IllegalStateException e) {
            throw new ClassNotFoundException("Cannot load class=" + desc + " because bundle=" + this.bundle.getSymbolicName() + " has been uninstalled");
        } catch (ClassNotFoundException e) {
            return ClassResolverObjectInputStream.resolvePrimitiveClass(desc, e);
        }
    }
}
