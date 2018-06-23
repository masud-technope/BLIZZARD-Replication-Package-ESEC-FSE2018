/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.eventadmin;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.ecf.internal.remoteservice.eventadmin.DefaultSerializationHandler;
import org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler;
import org.osgi.service.event.Event;

/**
 * @since 1.1
 */
public class EventMessage implements Serializable {

    private static final long serialVersionUID = 2743430591605178391L;

    private String topic;

    private Map properties;

    private transient SerializationHandler sh = new DefaultSerializationHandler();

    private transient Event localEvent;

    public  EventMessage(Event event) throws NotSerializableException {
        this.topic = event.getTopic();
        this.properties = createPropertiesFromEvent(event);
    }

    /**
	 * @param event event
	 * @param serializationHandler serialization handler
	 * @throws NotSerializableException if event can't be serialized
	 * @since 1.2
	 */
    public  EventMessage(Event event, SerializationHandler serializationHandler) throws NotSerializableException {
        this.sh = serializationHandler;
        this.topic = event.getTopic();
        this.properties = createPropertiesFromEvent(event);
    }

    public  EventMessage(String topic, Map properties) {
        this.topic = topic;
        this.properties = properties;
    }

    protected Map createPropertiesFromEvent(Event event) throws NotSerializableException {
        String[] propertyNames = event.getPropertyNames();
        Hashtable ht = (propertyNames == null) ? new Hashtable(1) : new Hashtable(propertyNames.length);
        for (int i = 0; i < propertyNames.length; i++) {
            Object val = event.getProperty(propertyNames[i]);
            ht.put(propertyNames[i], sh.serialize(val));
        }
        return ht;
    }

    protected String getTopic() {
        return topic;
    }

    protected Map getProperties() {
        final Set keySet = properties.keySet();
        for (final Iterator itr = keySet.iterator(); itr.hasNext(); ) {
            final Object key = (Object) itr.next();
            final Object val = properties.get(key);
            properties.put(key, sh.deserialize(val));
        }
        return properties;
    }

    protected Event createLocalEvent() {
        return new Event(getTopic(), getProperties());
    }

    public synchronized Event getEvent() {
        if (localEvent == null) {
            localEvent = createLocalEvent();
        }
        return localEvent;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("EventMessage[topic=");
        buffer.append(topic);
        buffer.append(", properties=");
        buffer.append(properties);
        buffer.append("]");
        return buffer.toString();
    }

    /**
	 * @param serializationHandler serialization handler
	 * @since 1.2
	 */
    public void setSerializationHandler(SerializationHandler serializationHandler) {
        this.sh = serializationHandler;
    }
}
