/*******************************************************************************
 * Copyright (c) 2015 Composent and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.identity;

import java.net.URI;
import java.util.UUID;
import org.eclipse.core.runtime.Assert;

/**
 * @since 3.5
 */
public class UuID extends BaseID {

    private static final long serialVersionUID = -2586540125532542205L;

    public static class UuIDNamespace extends Namespace {

        private static final long serialVersionUID = -7708511830843215943L;

        public static final String SCHEME = "uuid";

        public  UuIDNamespace() {
            this(UuID.class.getName(), "UuID Namespace");
        }

        public  UuIDNamespace(String name, String description) {
            super(name, description);
        }

        @Override
        public ID createInstance(Object[] parameters) throws IDCreateException {
            try {
                String init = getInitStringFromExternalForm(parameters);
                if (init != null)
                    return new UuID(this, UUID.fromString(init));
                if (parameters != null && parameters.length > 0) {
                    if (parameters[0] instanceof String)
                        return new UuID(this, UUID.fromString((String) parameters[0]));
                    else if (parameters[0] instanceof URI)
                        return new UuID(this, UUID.fromString(((URI) parameters[0]).getSchemeSpecificPart()));
                    else if (parameters[0] instanceof UUID)
                        return new UuID(this, (UUID) parameters[0]);
                }
                // If we get here, then use random
                return new UuID(this, UUID.randomUUID());
            } catch (Exception e) {
                throw new IDCreateException(UuIDNamespace.this.getName() + " createInstance()", e);
            }
        }

        @Override
        public String getScheme() {
            return SCHEME;
        }

        @Override
        public Class<?>[][] getSupportedParameterTypes() {
            return new Class[][] { { String.class }, { UUID.class }, { URI.class } };
        }
    }

    protected final UUID uuid;

    protected  UuID(UuIDNamespace ns, UUID uuid) {
        super(ns);
        Assert.isNotNull(uuid);
        this.uuid = uuid;
    }

    protected int namespaceCompareTo(BaseID obj) {
        return getName().compareTo(obj.getName());
    }

    protected boolean namespaceEquals(BaseID obj) {
        if (!(obj instanceof UuID))
            return false;
        UuID o = (UuID) obj;
        return uuid.equals(o.uuid);
    }

    protected String namespaceGetName() {
        return uuid.toString();
    }

    protected int namespaceHashCode() {
        return uuid.hashCode() ^ getClass().hashCode();
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "UuID[uuid=" + uuid + "]";
    }
}
