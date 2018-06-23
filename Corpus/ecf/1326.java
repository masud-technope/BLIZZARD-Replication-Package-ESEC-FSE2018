/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.provider;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * An adapter config is used to setup an adaptable.   
 * @since 8.7
 */
public class AdapterConfig {

    private final IAdapterFactory adapterFactory;

    private final Class<?> adaptable;

    /**
	 * 
	 * @param adapterFactory the adapter factory to use for the given adaptable.  Must not be <code>null</code>
	 * @param adaptable the Class that the adapterFactory is to use as the adaptable.
	 * Must not be <code>null</code>.
	 */
    public  AdapterConfig(IAdapterFactory adapterFactory, Class<?> adaptable) {
        this.adapterFactory = adapterFactory;
        Assert.isNotNull(this.adapterFactory);
        this.adaptable = adaptable;
        Assert.isNotNull(this.adaptable);
    }

    public IAdapterFactory getAdapterFactory() {
        return this.adapterFactory;
    }

    public Class<?> getAdaptable() {
        return this.adaptable;
    }
}
