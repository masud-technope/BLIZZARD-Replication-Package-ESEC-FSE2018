/****************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation, Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    IBM Corporation - initial API and implementation (non-distributed EventAdmin)
 *    Markus Alexander Kuppe - https://bugs.eclipse.org/412261
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.eventadmin;

import java.io.IOException;
import java.io.NotSerializableException;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectCreateResponseEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectMessageEvent;
import org.eclipse.ecf.internal.remoteservice.eventadmin.DefaultSerializationHandler;
import org.eclipse.ecf.internal.remoteservice.eventadmin.EventHandlerTracker;
import org.eclipse.ecf.internal.remoteservice.eventadmin.EventHandlerWrapper;
import org.eclipse.ecf.internal.remoteservice.eventadmin.LogTracker;
import org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler;
import org.eclipse.osgi.framework.eventmgr.CopyOnWriteIdentityMap;
import org.eclipse.osgi.framework.eventmgr.EventManager;
import org.eclipse.osgi.framework.eventmgr.ListenerQueue;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DistributedEventAdmin extends BaseSharedObject implements EventAdmin {

    /**
	 * @since 1.2
	 * @noreference
	 * protected non-final for unit tests only!!!
	 */
    protected static boolean ignoreSerializationExceptions = Boolean.getBoolean(DistributedEventAdmin.class.getName() + ".IgnoreSerialzationFailures");

    private LogTracker logTracker;

    private LogService log;

    private EventManager eventManager;

    private ServiceTracker etfServiceTracker;

    private ServiceTracker shServiceTracker;

    private final Set eventFilters = new HashSet();

    private final Map topic2serializationHandler = new HashMap();

    private static final String SHARED_OBJECT_MESSAGE_METHOD = "__handlePostEventSharedObjectMsg";

    /**
	 * @since 1.2
	 * @noreference This field is not intended to be referenced by clients.
	 */
    protected EventHandlerTracker eventHandlerTracker;

    /**
	 * @since 1.2
	 */
    protected BundleContext context;

    /**
	 * @noreference
	 */
    protected  DistributedEventAdmin() {
    // nop
    }

    /**
	 * Create a Distributed EventAdmin implementation.
	 * 
	 * @param context
	 *            the BundleContext to be used. Must not be <code>null</code>.
	 * @param log
	 *            the {@link LogService} to use. May be <code>null</code>. If
	 *            <code>null</code>, then a LogTracker is created and
	 *            used to find and use a {@link LogService}.
	 * 
	 * @since 1.1
	 */
    public  DistributedEventAdmin(BundleContext context, LogService log) {
        Assert.isNotNull(context);
        this.context = context;
        if (log == null) {
            // create log tracker and set the log to it
            this.logTracker = new LogTracker(context, System.out);
            this.log = this.logTracker;
        } else {
            this.logTracker = null;
            this.log = log;
        }
        // Now create eventHandler tracker
        this.eventHandlerTracker = new EventHandlerTracker(context, log);
    }

    /**
	 * Create a Distributed EventAdmin implementation.
	 * 
	 * @param context
	 *            the BundleContext to be used. Must not be <code>null</code>.
	 * 
	 * @since 1.1
	 */
    public  DistributedEventAdmin(BundleContext context) {
        this(context, null);
    }

    /**
	 * Start this distributed event admin instance. This method should be called
	 * prior to registering this object as an {@link EventAdmin} implementation
	 * with the OSGi service registry.
	 */
    public void start() {
        if (logTracker != null)
            logTracker.open();
        //$NON-NLS-1$
        ThreadGroup eventGroup = new ThreadGroup("Distributed EventAdmin");
        eventGroup.setDaemon(true);
        eventManager = new EventManager("Distributed EventAdmin Async Event Dispatcher Thread", eventGroup);
        eventHandlerTracker.open();
        // Other services can contribute Event topic filters which will be ignored
        // by the distribution part of DistributedEventAdmin. This is primarily useful
        // in cases where the Event data cannot be serialized or serialization is too
        // expensive.
        etfServiceTracker = new ServiceTracker(this.context, EventTopicFilter.class, new ServiceTrackerCustomizer() {

            public Object addingService(ServiceReference reference) {
                final EventTopicFilter etf = (EventTopicFilter) context.getService(reference);
                addEventTopicFilters(etf.getFilters());
                return etf;
            }

            public void modifiedService(ServiceReference reference, Object service) {
            // nop
            }

            public void removedService(ServiceReference reference, Object service) {
                final EventTopicFilter etf = (EventTopicFilter) service;
                removeEventTopicFilters(etf.getFilters());
            }
        });
        etfServiceTracker.open();
        // SerializationHandler are responsible to handle serialization of Event properties
        shServiceTracker = new ServiceTracker(this.context, SerializationHandler.class, new ServiceTrackerCustomizer() {

            public Object addingService(ServiceReference reference) {
                final SerializationHandler sh = (SerializationHandler) context.getService(reference);
                topic2serializationHandler.put(sh.getTopic(), sh);
                return sh;
            }

            public void modifiedService(ServiceReference reference, Object service) {
            // nop
            }

            public void removedService(ServiceReference reference, Object service) {
                final SerializationHandler sh = (SerializationHandler) service;
                topic2serializationHandler.remove(sh.getTopic());
            }
        });
        shServiceTracker.open();
    }

    /**
	 * Stop this distributed event admin instance. This method should be called
	 * after unregistering the {@link ServiceRegistration} created on
	 * registration with the OSGi service registry.
	 */
    public void stop() {
        eventHandlerTracker.close();
        if (eventManager != null) {
            eventManager.close();
            eventManager = null;
        }
        if (logTracker != null)
            logTracker.close();
        if (etfServiceTracker != null) {
            etfServiceTracker.close();
            etfServiceTracker = null;
        }
        if (shServiceTracker != null) {
            shServiceTracker.close();
            shServiceTracker = null;
        }
    }

    /**
	 * Send the given event synchronously. The default implementation of this
	 * method simply does a <b>local-only</b> dispatch to {@link EventHandler}s.
	 * It does <b>not</b> attempt to distribute the given event as does
	 * {@link #postEvent(Event)}.
	 * 
	 * @param event
	 *            the Event to send synchronously to local {@link EventHandler}s
	 *            (only). Must not be <code>null</code>.
	 */
    public void sendEvent(Event event) {
        localDispatch(event, false);
    // see bug https://bugs.eclipse.org/412303
    }

    /**
	 * Post an event for asynchronous delivery via this distributed event admin.
	 * This is the primary entry point for the distributed event admin
	 * implementation for asynchronous delivery to a set of receivers (known to
	 * the this shared object and it's enclosing {@link IContainer}. The event
	 * to post must not be <code>null</code>.
	 * <p>
	 * This implementation goes through the following steps
	 * <ol>
	 * <li>Call {@link #getEventToSend(Event)}. If the Event returned from
	 * getEventToSend is <code>null</code>, then the following three method
	 * calls do not occur, and postEvent returns immediately.</li>
	 * <li>Call {@link #sendMessage(Event)} with the non-<code>null</code>
	 * result of {@link #getEventToSend(Event)}</li>
	 * <li>Call {@link #notifyPostSendMessage(Event)}</li>
	 * <li>Call {@link #localDispatch(Event, boolean)}</li>
	 * </ol>
	 * 
	 * @param event
	 *            the Event to send asynchronously to matching
	 *            {@link EventHandler}s. Must not be <code>null</code>.
	 * @since 1.1
	 */
    public void postEvent(final Event event) {
        // First thing, we allow subclasses to decide whether the given event
        // should be translated before message
        // send into a new Event, or if it should not be sent at all
        Event eventToSend = getEventToSend(event);
        if (eventToSend != null) {
            if (!eventFilters.contains(event.getTopic())) {
                sendMessage(eventToSend);
                // sent successfully, so now dispatch to any appropriate local
                // EventHandlers
                notifyPostSendMessage(eventToSend);
            }
            // This does local dispatch asynchronously
            localDispatch(event, true);
        }
    }

    /**
	 * Send the event as a shared object message. The given event will be
	 * serialized and sent via
	 * {@link #sendSharedObjectMsgTo(ID, SharedObjectMsg)}.
	 * <p>
	 * Prior to actual sending, the {@link #getTarget(Event)} method will be
	 * called, to allow subclasses to determine the target receiver. Then the
	 * {@link #createMessageDataFromEvent(ID, Event)} method is called, to
	 * create an Object[] of data for sending in the message. The Object[]
	 * returned from {@link #createMessageDataFromEvent(ID, Event)} must be
	 * serializable. See {@link #createMessageDataFromEvent(ID, Event)}.
	 * <p>
	 * Subclasses may override this method to customize or replace this
	 * sendMessage behavior.
	 * <p>
	 * If an exception occurs on serialization or sending, the
	 * {@link #handleSendMessageException(String, Event, Object[], IOException)}
	 * method will be called to handle it.
	 * 
	 * @param eventToSend
	 *            the event to send. Will not be <code>null</code>.
	 * @since 1.1
	 */
    protected void sendMessage(Event eventToSend) {
        ID target = null;
        Object[] messageData = null;
        try {
            target = getTarget(eventToSend);
            messageData = createMessageDataFromEvent(target, eventToSend);
            sendSharedObjectMsgTo(target, SharedObjectMsg.createMsg(SHARED_OBJECT_MESSAGE_METHOD, messageData));
        } catch (IOException e) {
            handleSendMessageException("send exception to target=" + target, eventToSend, messageData, e);
        }
    }

    /**
	 * Create message data for deliver to a target (which could be
	 * <code>null</code> to designate multiple target receivers), The resulting
	 * Object[] must be Serializable and in a form that receivers can
	 * deserialize via {@link #createEventFromMessageData(ID, Object[])} on the
	 * receiver.
	 * <p>
	 * The default implementation creates a single {@link EventMessage} instance
	 * and adds it to an Object[] of length 1.
	 * <p>
	 * Subclasses may override as appropriate to customize the serialization of
	 * the given eventToSend.
	 * <p>
	 * Subclasses may override as appropriate. If this method is overridden,
	 * then {@link #createEventFromMessageData(ID, Object[])} should also be
	 * overridden as well on the receiver.
	 * 
	 * @param target
	 *            the target {@link ID} that is the intended receiver returned
	 *            from {@link #getTarget(Event)}.
	 * @param eventToSend
	 *            the event to send. Will not be <code>null</code>.
	 * @return Object[] the actual message data that will be serialized (must be
	 *         Serializable to use in {@link #sendMessage(Event)}). The default
	 *         implementation creates a single {@link EventMessage} instance and
	 *         adds it to an Object[] of length 1.
	 * @throws NotSerializableException
	 *             if the eventToSend cannot be serialized.
	 * @since 1.1
	 */
    protected Object[] createMessageDataFromEvent(ID target, Event eventToSend) throws NotSerializableException {
        final Object[] results = { new EventMessage(eventToSend, getSerializationHandler(eventToSend.getTopic())) };
        return results;
    }

    /**
	 * @param topic topic
	 * @return SerializationHandler the serialization handler associated with topic
	 * @since 1.2
	 */
    protected SerializationHandler getSerializationHandler(String topic) {
        final SerializationHandler sh = (SerializationHandler) topic2serializationHandler.get(topic);
        if (sh == null) {
            return DefaultSerializationHandler.INST;
        }
        return sh;
    }

    /**
	 * Create a local {@link Event} from deserialized messageData. The fromID
	 * will be a non-<code>null</code> {@link ID} instance that is the container
	 * ID of the sender DistributedEventAdmin. The default implementation
	 * assumes that a single {@link EventMessage} is in the first array element
	 * of the messageData, casts the messageData[0] to EventMessage, and then
	 * returns eventMessage.getEvent().
	 * <p>
	 * Subclasses can override as appropriate. If this method is overridden,
	 * then {@link #createMessageDataFromEvent(ID, Event)} should also be
	 * overridden as well on the sender.
	 * 
	 * @param fromID
	 *            the ID of the message sender. Will not be <code>null</code>.
	 * @param messageData
	 *            Object[] received from fromID. Will be a deserialized
	 *            local version of the Object[] from fromID.
	 * @return Event to be delivered to local {@link EventHandler}s. Should not
	 *         be <code>null</code>.
	 * 
	 * @since 1.1
	 */
    protected Event createEventFromMessageData(ID fromID, Object[] messageData) {
        final EventMessage eventMessage = (EventMessage) messageData[0];
        eventMessage.setSerializationHandler(getSerializationHandler(eventMessage.getTopic()));
        return eventMessage.getEvent();
    }

    /**
	 * Handle any exceptions occuring as part of Event serialization or message
	 * send. The default is to call {@link #logError(String, Throwable)} with
	 * the eventToSend and messageParams appended to the message parameter.
	 * 
	 * @param message
	 *            a message associated with the exception.
	 * @param eventToSend
	 *            the event that was to be sent.
	 * @param messageParams
	 *            the message params that were to be
	 * @param exception exception
	 * @since 1.1
	 */
    protected void handleSendMessageException(String message, Event eventToSend, Object[] messageParams, IOException exception) {
        String exceptionMessage = ((message == null) ? "" : message) + " eventToSend=" + eventToSend + " messageParams=" + ((messageParams == null) ? null : Arrays.asList(messageParams));
        // only throw an exception if not explicitly turned off
        if (!(exception instanceof NotSerializableException) || !ignoreSerializationExceptions) {
            logError(exceptionMessage, exception);
            throw new ServiceException(exceptionMessage, exception);
        } else {
            logWarning(exceptionMessage, exception);
        }
    }

    /**
	 * Get the target receiver for the eventToSend. The returned {@link ID} will
	 * be used to send a shared object message to either a single
	 * {@link IContainer}, or a group of {@link IContainer}s. To send to the
	 * entire group, this method should return <code>null</code>. The default
	 * implementation is to return <code>null</code>, meaning that the given
	 * eventToSend is to be sent to all receivers connected to this shared
	 * object's enclosing {@link IContainer}.
	 * 
	 * @param eventToSend
	 *            the eventToSend. Will not be <code>null</code>.
	 * @return ID the ID target for {@link #sendMessage(Event)} to send to. May
	 *         be <code>null</code>. <code>null</code> is the default
	 *         implementation, meaning that the Event will be delivered to all
	 *         members of the group known to this shared object's
	 *         {@link IContainer}.
	 * 
	 * @since 1.1
	 */
    protected ID getTarget(Event eventToSend) {
        return null;
    }

    /**
	 * Get the actual event to pass to {@link #sendMessage(Event)}. The default
	 * implementation of this method is to simply return the event passed in as
	 * the method argument.
	 * <p>
	 * Subclasses may override...to filter or transform the event prior to
	 * calling {@link #sendMessage(Event)}.
	 * 
	 * @param event
	 *            the event. Will not be <code>null</code>.
	 * @return Event to send. By default, the event provided as the argument is
	 *         returned.
	 * 
	 * @since 1.1
	 */
    protected Event getEventToSend(Event event) {
        // By default, we distribute the same event that is passed in
        return event;
    }

    /**
	 * Method called after {@link #sendMessage(Event)} is called (typically from
	 * within {@link #postEvent(Event)}), but prior to local dispatch. The
	 * default implementation is to do nothing.
	 * 
	 * @param eventSent
	 *            the event that was sent. Will not be <code>null</code>.
	 * @since 1.1
	 */
    protected void notifyPostSendMessage(Event eventSent) {
    }

    /**
	 * Method called from within {@link #localDispatch(Event, boolean)} prior to
	 * actual deliver to matching {@link EventHandler}s. The default
	 * implementation returns the given event. Subclasses may override as
	 * appropriate. If the returned Event is <code>null</code> then no local
	 * dispatch will occur for the given Event.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param event
	 *            the Event to dispatch. Will not be <code>null</code>.
	 * @return Event the event to actually dispatch. If <code>null</code>, no
	 *         local dispatch is done for this event.
	 * 
	 * @since 1.1
	 */
    protected Event notifyPreLocalDispatch(Event event) {
        return event;
    }

    /**
	 * Notification called after local dispatch has been done. The default
	 * implemenation does nothing. Note that this method is called by the thread
	 * that calls {@link #localDispatch(Event, boolean)}, and if the actual
	 * dispatch is done by another thread (i.e. second param to localDispatch is
	 * <code>true</code>), then the dispatch could occur before, after, or
	 * during the actual handling via the matching {@link EventHandler}s.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param event
	 *            the Event that was delivered to matching {@link EventHandler}
	 *            s. Will not be <code>null</code>.
	 * @since 1.1
	 */
    protected void notifyPostLocalDispatch(Event event) {
    }

    /**
	 * Locally dispatch an Event. This method is used to deliver an
	 * {@link Event} to matching {@link EventHandler}s that are registered in
	 * the local OSGi service registry.
	 * 
	 * @param dispatchedEvent
	 *            the Event to dispatch. Will not be <code>null</code>.
	 * @param isAsync
	 *            <code>true</code> if the dispatch should be done
	 *            asynchronously (non-blocking), <code>false</code> if the
	 *            dispatch should be done synchronously.
	 */
    protected void localDispatch(Event dispatchedEvent, boolean isAsync) {
        EventManager currentManager = eventManager;
        if (currentManager == null) {
            return;
        }
        if (dispatchedEvent == null) {
            log.log(LogService.LOG_ERROR, "Null event passed to EventAdmin was ignored.");
        }
        Event event = notifyPreLocalDispatch(dispatchedEvent);
        if (event != null) {
            String eventTopic = event.getTopic();
            try {
                SecurityManager sm = System.getSecurityManager();
                if (sm != null)
                    sm.checkPermission(new TopicPermission(eventTopic, TopicPermission.PUBLISH));
            } catch (SecurityException e) {
                logError("Caller bundle does not have TopicPermission to publish topic " + eventTopic, e);
                throw e;
            }
            Set eventHandlerWrappers = eventHandlerTracker.getHandlers(eventTopic);
            SecurityManager sm = System.getSecurityManager();
            Permission perm = (sm == null) ? null : new TopicPermission(eventTopic, TopicPermission.SUBSCRIBE);
            CopyOnWriteIdentityMap listeners = new CopyOnWriteIdentityMap();
            Iterator iter = eventHandlerWrappers.iterator();
            while (iter.hasNext()) {
                EventHandlerWrapper wrapper = (EventHandlerWrapper) iter.next();
                listeners.put(wrapper, perm);
            }
            ListenerQueue listenerQueue = new ListenerQueue(currentManager);
            listenerQueue.queueListeners(listeners.entrySet(), eventHandlerTracker);
            if (isAsync) {
                listenerQueue.dispatchEventAsynchronous(0, event);
            } else {
                listenerQueue.dispatchEventSynchronous(0, event);
            }
            notifyPostLocalDispatch(event);
        }
    }

    /**
	 * Handle the shared object message.  This method is called on receiver implementations of 
	 * the DistributedEventAdmin, so that they can deliver to locally registered {@link EventHandler}s.
	 * <p>
	 * This implementation does the following:
	 * <ol>
	 * <li>Verifies that the value {@link SharedObjectMsg#getMethod()} matches the appropriate String.</li>
	 * <li>Calls #createEventFromMessageData(ID, Object[]) to convert the message data (returned from #createMessageDataFromEvent(ID, Event)
	 * on the sender)</li>
	 * <li>If the Event returned from #createEventFromMessageData(ID, Object[]) is non-<code>null</code>, then call
	 * <ol>
	 * <li>#notifyReceivedEvent(ID, Event) to allow subclasses to be notified prior to local dispatch</li>
	 * <li>#localDispatch(Event, boolean) to actually do the local dispatch asynchronously</li>
	 * </ol></li>
	 * <li>
	 * </ol>
	 * @since 1.1
	 */
    protected boolean handleSharedObjectMsg(ID fromID, SharedObjectMsg msg) {
        String soMethod = msg.getMethod();
        if (SHARED_OBJECT_MESSAGE_METHOD.equals(soMethod)) {
            try {
                Object[] messageData = msg.getParameters();
                Event receivedEvent = createEventFromMessageData(fromID, messageData);
                if (receivedEvent != null) {
                    notifyReceivedEvent(fromID, receivedEvent);
                    localDispatch(receivedEvent, true);
                }
            } catch (Exception e) {
                logError("DistributedEventAdmin handleSharedObjectMsg error receiving msg=" + msg, e);
            }
            return true;
        } else {
            logError("DistributedEventAdmin received bad shared object msg=" + msg + " from=" + fromID);
        }
        return false;
    }

    /**
	 * @param fromID fromID
	 * @param receivedEvent received event
	 * @since 1.1
	 */
    protected void notifyReceivedEvent(ID fromID, Event receivedEvent) {
    }

    /**
	 * Override of BaseSharedObject.handleSharedObjectMsgEvent. Subclasses must
	 * not override this method.
	 * 
	 * @since 1.1
	 */
    protected final boolean handleSharedObjectMsgEvent(ISharedObjectMessageEvent event) {
        boolean result = false;
        if (event instanceof ISharedObjectCreateResponseEvent)
            result = handleSharedObjectCreateResponseEvent((ISharedObjectCreateResponseEvent) event);
        else {
            SharedObjectMsg msg = getSharedObjectMsgFromEvent(event);
            if (msg != null)
                result = handleSharedObjectMsg(event.getRemoteContainerID(), msg);
        }
        return result;
    }

    // log methods
    /**
	 * Log a warning.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param message
	 *            the message to include in the warning. Should not be
	 *            <code>null</code>.
	 * @since 1.1
	 */
    protected void logWarning(String message) {
        logWarning(message, null);
    }

    /**
	 * Log a warning.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param message
	 *            the message to include in the warning. Should not be
	 *            <code>null</code>.
	 * @param exception
	 *            the exception to include in the warning. May be
	 *            <code>null</code>. If non-<code>null</code> then exception
	 *            will be printed to System.out.
	 * 
	 * @since 1.1
	 */
    protected void logWarning(String message, Throwable exception) {
        if (log != null) {
            log.log(LogService.LOG_WARNING, message, exception);
        } else {
            System.out.println(message);
            if (exception != null)
                exception.printStackTrace(System.out);
        }
    }

    /**
	 * Log an error.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param message
	 *            the message to include in the error. Should not be
	 *            <code>null</code>.
	 * @since 1.1
	 */
    protected void logError(String message) {
        logError(message, null);
    }

    /**
	 * Log an error.
	 * <p>
	 * Subclasses may override as appropriate.
	 * 
	 * @param message
	 *            the message to include in the error. Should not be
	 *            <code>null</code>.
	 * @param exception
	 *            the exception to include in the warning. May be
	 *            <code>null</code>. If non-<code>null</code> then exception
	 *            will be printed to System.out.
	 */
    protected void logError(String message, Throwable exception) {
        if (log != null) {
            log.log(LogService.LOG_ERROR, message, exception);
        } else {
            System.err.println(message);
            if (exception != null)
                exception.printStackTrace(System.err);
        }
    }

    /**
	 * @param filters topic filters to add
	 * @return boolean true if given filters added, false otherwise
	 * @since 1.2
	 */
    public boolean addEventTopicFilters(String[] filters) {
        final List asList = Arrays.asList(filters);
        return eventFilters.addAll(asList);
    }

    /**
	 * @param filters topic filters to add
	 * @return boolean true if given filters added, false otherwise
	 * @since 1.2
	 */
    public boolean removeEventTopicFilters(String[] filters) {
        final List asList = Arrays.asList(filters);
        return eventFilters.removeAll(asList);
    }
}
