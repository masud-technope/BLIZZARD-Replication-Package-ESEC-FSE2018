/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.ui.model;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

/**
 * This class is the superclass for all ItemProviderAdapter It provides
 * additional support for status line by implementing IItemStatusLineProvider
 */
public class ItemProviderWithStatusLineAdapter extends ItemProviderAdapter implements IItemStatusLineProvider {

    public  ItemProviderWithStatusLineAdapter(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
	 * default implementation just delegates to getText(..)
	 */
    public String getStatusLineText(Object object) {
        return getText(object);
    }
}
