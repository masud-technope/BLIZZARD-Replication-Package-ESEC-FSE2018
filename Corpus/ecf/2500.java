/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointPropertySource;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.framework.ServiceReference;

/**
 * @since 3.3
 */
public abstract class AbstractRegistrationNode extends AbstractRSANode {

    public static final String ERROR = Messages.AbstractRegistrationNode_ErrorName;

    private final Throwable error;

    /**
	 * @since 3.4
	 */
    public  AbstractRegistrationNode(Throwable t, boolean showStack) {
        this.error = t;
        if (this.error != null)
            addChild(new ExceptionNode(this.error, showStack));
    }

    public  AbstractRegistrationNode(Throwable t) {
        this(t, false);
    }

    protected Throwable getError() {
        return error;
    }

    protected boolean hasError() {
        return getError() != null;
    }

    public abstract boolean isClosed();

    protected abstract String getValidName();

    /**
	 * @since 3.4
	 */
    protected String getErrorName() {
        return ERROR;
    }

    /**
	 * @since 3.4
	 */
    protected String getClosedName() {
        return CLOSED;
    }

    public abstract ServiceReference getServiceReference();

    public String getName() {
        return hasError() ? getErrorName() : (isClosed() ? getClosedName() : getValidName());
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (adapter == IPropertySource.class) {
            ServiceReference sr = getServiceReference();
            if (sr != null)
                return new EndpointPropertySource(PropertyUtils.convertServicePropsToMap(sr));
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public abstract void close();
}
