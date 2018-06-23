/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Assert;

/**
 * Implementation of {@link IRemoteCallParameter}.
 * 
 * @since 4.0
 */
public class RemoteCallParameter implements IRemoteCallParameter {

    private String name;

    private Object value;

    public  RemoteCallParameter(String name, Object value) {
        this.name = name;
        Assert.isNotNull(this.name);
        this.value = value;
    }

    public  RemoteCallParameter(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("RemoteCallParameter[name=");
        buffer.append(name);
        //$NON-NLS-1$
        buffer.append(", value=");
        buffer.append(value);
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }

    /**
	 * @since 8.5
	 */
    public static class Builder {

        private final List<IRemoteCallParameter> parameters;

        public  Builder() {
            this.parameters = new ArrayList<IRemoteCallParameter>();
        }

        public Builder addParameter(String name, Object defaultValue) {
            if (name == null)
                return this;
            this.parameters.add(new RemoteCallParameter(name, defaultValue));
            return this;
        }

        public Builder addParameter(String name) {
            return addParameter(name, null);
        }

        /**
		 * @param param remote call parameter to add
		 * @return Builder the builder
		 * @since 8.8
		 */
        public Builder addParameter(IRemoteCallParameter param) {
            if (param == null)
                return this;
            this.parameters.add(param);
            return this;
        }

        public IRemoteCallParameter[] build() {
            return this.parameters.toArray(new IRemoteCallParameter[this.parameters.size()]);
        }
    }
}
