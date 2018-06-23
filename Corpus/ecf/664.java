/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.OptimisticSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;

/**
 * @since 2.4
 */
public class SharedModel extends OptimisticSharedObject {

    private List<ISharedModelListener> listeners = new ArrayList<ISharedModelListener>();

    public boolean addListener(ISharedModelListener listener) {
        synchronized (listeners) {
            return listeners.add(listener);
        }
    }

    public boolean removeListener(ISharedModelListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    protected void handleListenerException(ISharedModelListener l, Throwable exception) {
        //$NON-NLS-1$
        log(0, "Exception in listener " + l, exception);
    }

    protected void fireListeners(final ISharedModelEvent event) {
        List<ISharedModelListener> localCopy = null;
        synchronized (listeners) {
            localCopy = new ArrayList<ISharedModelListener>(listeners);
        }
        for (ISharedModelListener l : localCopy) {
            final ISharedModelListener list = l;
            SafeRunner.run(new ISafeRunnable() {

                public void handleException(Throwable exception) {
                    handleListenerException(list, exception);
                }

                public void run() throws Exception {
                    list.handleEvent(event);
                }
            });
        }
    }

    @Override
    public void dispose(ID containerID) {
        super.dispose(containerID);
        listeners.clear();
        removeAllProperties();
    }

    private Map<String, Property> properties = new HashMap<String, Property>();

    protected Collection<Property> copyProperties() {
        List<Property> results = new ArrayList<Property>();
        synchronized (properties) {
            for (String name : properties.keySet()) results.add(properties.get(name));
        }
        return results;
    }

    protected Property addProperty(String name) {
        return addProperty(name, null);
    }

    protected Property addProperty(String name, Object value) {
        synchronized (properties) {
            Property p = getProperty(name);
            if (p != null)
                return null;
            p = new Property(this, name, value);
            addProperty(p);
            return p;
        }
    }

    protected Property addProperty(final Property property) {
        if (property == null)
            //$NON-NLS-1$
            throw new NullPointerException("property cannot be null");
        Property oldProperty = null;
        synchronized (properties) {
            oldProperty = properties.put(property.getName(), property);
        }
        final Property op = oldProperty;
        fireListeners(new ISharedModelPropertyAddEvent() {

            public SharedModel getSource() {
                return SharedModel.this;
            }

            public Property getAddedProperty() {
                return property;
            }

            public Property getPreAddedProperty() {
                return op;
            }
        });
        return op;
    }

    protected Property removeProperty(String propertyName) {
        if (propertyName == null)
            //$NON-NLS-1$
            throw new NullPointerException("propertyName cannot be null");
        Property oldProperty = null;
        synchronized (properties) {
            oldProperty = properties.remove(propertyName);
        }
        final Property op = oldProperty;
        fireListeners(new ISharedModelPropertyRemoveEvent() {

            public SharedModel getSource() {
                return SharedModel.this;
            }

            public Property getRemovedProperty() {
                return op;
            }
        });
        return op;
    }

    protected Property getProperty(String propertyName) {
        if (propertyName == null)
            //$NON-NLS-1$
            throw new NullPointerException("propertyName cannot be null");
        synchronized (properties) {
            return properties.get(propertyName);
        }
    }

    protected Property getOrAddProperty(String propertyName) {
        if (propertyName == null)
            //$NON-NLS-1$
            throw new NullPointerException("propertyName cannot be null");
        synchronized (properties) {
            Property p = getProperty(propertyName);
            if (p != null)
                return p;
            return addProperty(propertyName);
        }
    }

    protected Property setOrAddProperty(String propertyName, Object value) {
        if (propertyName == null)
            //$NON-NLS-1$
            throw new NullPointerException("propertyName cannot be null");
        synchronized (properties) {
            Property p = getProperty(propertyName);
            if (p != null) {
                p.setValue(value);
                return p;
            }
            return addProperty(propertyName, value);
        }
    }

    protected void removeAllProperties() {
        synchronized (properties) {
            properties.clear();
        }
    }

    protected Map<String, ?> getMapFromProperties() {
        Map<String, Object> result = new HashMap<String, Object>();
        synchronized (properties) {
            for (String key : properties.keySet()) {
                Property p = getProperty(key);
                if (p != null)
                    result.put(key, p.getValue());
                else
                    result.put(key, null);
            }
        }
        return result;
    }

    protected void setPropertiesFromMap(Map<String, ?> map) {
        if (map == null)
            return;
        synchronized (properties) {
            for (String key : map.keySet()) {
                Property p = getProperty(key);
                // If the property already exists, change/set it's value
                Object val = map.get(key);
                if (p != null) {
                    p.setValue(val);
                } else {
                    // add property
                    addProperty(key, val);
                }
            }
        }
    }

    //$NON-NLS-1$
    protected final String SEND_PROPERTY_TO_MSG = ".sendPropertyTo.";

    public class Property implements Serializable {

        private static final long serialVersionUID = -716933143243026805L;

        private SharedModel model;

        private String name;

        private Object value;

        public  Property(SharedModel model, String name, Object value) {
            Assert.isNotNull(model);
            this.model = model;
            Assert.isNotNull(name);
            this.name = name;
            this.value = value;
        }

        public  Property(SharedModel model, String name) {
            this(model, name, null);
        }

        public SharedModel getModel() {
            return model;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newValue) {
            final Object previousValue = this.value;
            this.value = newValue;
            getModel().fireListeners(new ISharedModelPropertyValueChangeEvent() {

                public SharedModel getSource() {
                    return getModel();
                }

                public Property getProperty() {
                    return SharedModel.Property.this;
                }

                public Object getPreviousValue() {
                    return previousValue;
                }

                public Object getValue() {
                    return SharedModel.Property.this.getValue();
                }
            });
            return previousValue;
        }

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("SharedModel.Property[");
            //$NON-NLS-1$
            buf.append("modelid=" + getModel().getID());
            //$NON-NLS-1$
            buf.append(";name=" + getName());
            //$NON-NLS-1$
            buf.append(";value=" + getValue());
            return buf.toString();
        }
    }

    protected final void sendPropertyTo(ID target, String msg, Property property) throws IOException {
        if (property == null)
            //$NON-NLS-1$
            throw new IOException("property to send cannot be null");
        sendSharedObjectMsgTo(target, SharedObjectMsg.createMsg(SharedModel.class.getName(), SEND_PROPERTY_TO_MSG + msg, property));
    }

    protected final void sendPropertyTo(ID target, Property property) throws IOException {
        sendPropertyTo(target, property.getName(), property);
    }

    protected boolean handleSharedObjectMsg(ID fromID, SharedObjectMsg msg) {
        if (SharedModel.class.getName().equals(msg.getClassName())) {
            String methodName = msg.getMethod();
            if (methodName != null && methodName.startsWith(SEND_PROPERTY_TO_MSG))
                return handlePropertyTo(fromID, methodName.substring(0, SEND_PROPERTY_TO_MSG.length() - 1), (Property) msg.getParameters()[0]);
        }
        return super.handleSharedObjectMsg(fromID, msg);
    }

    protected boolean handlePropertyTo(ID fromID, String msg, Property property) {
        return false;
    }
}
