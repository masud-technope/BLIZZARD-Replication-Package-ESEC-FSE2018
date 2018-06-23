/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.generic;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.*;
import org.eclipse.ecf.core.sharedobject.util.QueueEnqueueImpl;
import org.eclipse.ecf.core.sharedobject.util.SimpleFIFOQueue;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.generic.gmm.Member;

public class SOWrapper {

    protected ISharedObject sharedObject;

    private SOConfig sharedObjectConfig;

    ID sharedObjectID;

    private SOContainer container;

    private ID containerID;

    private Thread thread;

    SimpleFIFOQueue queue;

    protected  SOWrapper(SOContainer.LoadingSharedObject obj, SOContainer cont) {
        sharedObjectID = obj.getID();
        sharedObject = obj;
        container = cont;
        containerID = cont.getID();
        sharedObjectConfig = null;
        thread = null;
        queue = new SimpleFIFOQueue();
    }

    public  SOWrapper(SOConfig aConfig, ISharedObject obj, SOContainer cont) {
        sharedObjectConfig = aConfig;
        sharedObjectID = sharedObjectConfig.getSharedObjectID();
        sharedObject = obj;
        container = cont;
        containerID = cont.getID();
        thread = null;
        queue = new SimpleFIFOQueue();
    }

    protected void init() throws SharedObjectInitException {
        //$NON-NLS-1$
        debug("init()");
        sharedObjectConfig.makeActive(new QueueEnqueueImpl(queue));
        sharedObject.init(sharedObjectConfig);
    }

    protected ID getObjID() {
        return sharedObjectConfig.getSharedObjectID();
    }

    protected ID getHomeID() {
        return sharedObjectConfig.getHomeContainerID();
    }

    protected SOConfig getConfig() {
        return sharedObjectConfig;
    }

    protected void activated() {
        thread = (Thread) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                Thread aThread = getThread();
                return aThread;
            }
        });
        // Notify container and listeners
        container.notifySharedObjectActivated(sharedObjectID);
        // Start thread
        thread.start();
        // Send message
        send(new SharedObjectActivatedEvent(containerID, sharedObjectID));
    }

    protected void deactivated() {
        container.notifySharedObjectDeactivated(sharedObjectID);
        send(new SharedObjectDeactivatedEvent(containerID, sharedObjectID));
        destroyed();
    }

    protected void destroyed() {
        if (!queue.isStopped()) {
            if (thread != null)
                queue.enqueue(new DisposeEvent());
            queue.close();
        }
    }

    protected void otherChanged(ID otherID, boolean activated) {
        if (activated && thread != null) {
            send(new SharedObjectActivatedEvent(containerID, otherID));
        } else {
            send(new SharedObjectDeactivatedEvent(containerID, otherID));
        }
    }

    protected void memberChanged(Member m, boolean add) {
        if (thread != null) {
            if (add) {
                send(new ContainerConnectedEvent(containerID, m.getID()));
            } else {
                send(new ContainerDisconnectedEvent(containerID, m.getID()));
            }
        }
    }

    protected Thread getThread() {
        return container.getNewSharedObjectThread(sharedObjectID, new Runnable() {

            public void run() {
                //$NON-NLS-1$ //$NON-NLS-2$
                debug("runner(" + sharedObjectID + ")");
                Event evt = null;
                for (; ; ) {
                    if (Thread.currentThread().isInterrupted())
                        break;
                    evt = (Event) queue.dequeue();
                    if (Thread.currentThread().isInterrupted() || evt == null)
                        break;
                    try {
                        if (evt instanceof ProcEvent) {
                            SOWrapper.this.svc(((ProcEvent) evt).getEvent());
                        } else if (evt instanceof DisposeEvent) {
                            SOWrapper.this.doDestroy();
                        } else {
                            SOWrapper.this.svc(evt);
                        }
                    } catch (Throwable t) {
                        handleRuntimeException(t);
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    debug(//$NON-NLS-1$
                    "runner(" + //$NON-NLS-1$
                    sharedObjectID + ") terminating interrupted");
                } else {
                    debug(//$NON-NLS-1$
                    "runner(" + //$NON-NLS-1$
                    sharedObjectID + //$NON-NLS-1$
                    ") terminating normally");
                }
            }
        });
    }

    private void send(Event evt) {
        queue.enqueue(new ProcEvent(evt));
    }

    public static class ProcEvent implements Event {

        Event theEvent = null;

        public  ProcEvent(Event event) {
            theEvent = event;
        }

        Event getEvent() {
            return theEvent;
        }
    }

    protected static class DisposeEvent implements Event {

         DisposeEvent() {
        //
        }
    }

    protected void svc(Event evt) {
        sharedObject.handleEvent(evt);
    }

    protected void doDestroy() {
        sharedObject.dispose(containerID);
        // make config inactive
        sharedObjectConfig.makeInactive();
    }

    protected void deliverSharedObjectMessage(ID fromID, Serializable data) {
        send(new RemoteSharedObjectEvent(getObjID(), fromID, data));
    }

    protected void deliverCreateResponse(ID fromID, ContainerMessage.CreateResponseMessage resp) {
        send(new RemoteSharedObjectCreateResponseEvent(resp.getSharedObjectID(), fromID, resp.getSequence(), resp.getException()));
    }

    public void deliverEvent(Event evt) {
        send(evt);
    }

    protected void destroySelf() {
        //$NON-NLS-1$
        debug("destroySelf()");
        send(new DisposeEvent());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("SharedObjectWrapper[").append(getObjID()).append("]");
        return sb.toString();
    }

    protected void debug(String msg) {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.SHAREDOBJECTWRAPPER, msg + ":oID=" + sharedObjectID + ":cID=" + container.getID());
    }

    protected void traceStack(String msg, Throwable e) {
        //$NON-NLS-1$
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SOContainerGMM.class, container.getID() + ":" + msg, e);
    }

    protected void handleRuntimeException(Throwable except) {
        except.printStackTrace(System.err);
        traceStack(//$NON-NLS-1$ //$NON-NLS-2$
        "runner:handleRuntimeException(" + sharedObjectID.getName() + ")", except);
    }

    protected ISharedObject getSharedObject() {
        return sharedObject;
    }

    public SimpleFIFOQueue getQueue() {
        return queue;
    }
}
