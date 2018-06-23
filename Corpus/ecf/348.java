/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.datashare.nio;

import java.net.InetSocketAddress;
import junit.framework.TestCase;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelConnectEvent;
import org.eclipse.ecf.datashare.events.IChannelDisconnectEvent;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;

public class NIODatashareTest extends TestCase {

    //$NON-NLS-1$
    private static final String LOCALHOST = "127.0.0.1";

    // private static final String LOCALHOST = "localhost"; //$NON-NLS-1$
    // private static final String LOCALHOST = "0.0.0.0"; //$NON-NLS-1$
    //$NON-NLS-1$
    private static final String CHANNEL_NAME = "channel";

    private Object waitObject = new Object();

    private Exception exception;

    private IContainer containerA = new ContainerImpl();

    private IContainer containerB = new ContainerImpl();

    private ConcreteNIODatashareContainer channelContainerA;

    private ConcreteNIODatashareContainer channelContainerB;

    private ConcreteNIOChannel channelA;

    private ConcreteNIOChannel channelB;

    private static void assertEquals(byte[] expected, byte[] actual) {
        // don't use Arrays.equals(byte[], byte[]) to get more accurate failure
        // information
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    private static ConcreteNIOChannel createChannel(IChannelContainerAdapter channelContainer) throws ECFException {
        return createChannel(channelContainer, null);
    }

    private static ConcreteNIOChannel createChannel(IChannelContainerAdapter channelContainer, IChannelListener listener) throws ECFException {
        return (ConcreteNIOChannel) channelContainer.createChannel(IDFactory.getDefault().createStringID(CHANNEL_NAME), listener, null);
    }

    protected void setUp() throws Exception {
        super.setUp();
        channelContainerA = new ConcreteNIODatashareContainer(containerA);
        channelContainerB = new ConcreteNIODatashareContainer(containerB);
        exception = null;
    }

    protected void tearDown() throws Exception {
        containerA.disconnect();
        containerB.disconnect();
        channelContainerA = null;
        channelContainerB = null;
        channelA = null;
        channelB = null;
        // just to make sure we don't have any pending threads
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
        super.tearDown();
    }

    private void waitForCompletion(long timeout) throws Exception {
        synchronized (waitObject) {
            waitObject.wait(timeout);
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void send(IChannel channel, ID receiver, byte[] data) {
        try {
            channel.sendMessage(receiver, data);
        } catch (ECFException e) {
            exception = e;
            synchronized (waitObject) {
                waitObject.notify();
            }
        }
    }

    public void testChannelConnectEvent() throws Exception {
        final ID[] eventIds = new ID[2];
        channelA = createChannel(channelContainerA, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent e) {
                if (e instanceof IChannelConnectEvent) {
                    IChannelConnectEvent event = (IChannelConnectEvent) e;
                    eventIds[0] = event.getChannelID();
                    eventIds[1] = event.getTargetID();
                }
            }
        });
        containerA.connect(null, null);
        assertEquals(channelA.getID(), eventIds[0]);
        assertEquals(containerA.getConnectedID(), eventIds[1]);
    }

    /**
	 * Test that while the container fires events, the datashare implementation
	 * is ignoring them successfully as it does not have a listener and is not
	 * throwing runtime exceptions.
	 */
    public void testChannelConnectEventWithoutListeners() throws Exception {
        channelA = createChannel(channelContainerA);
        containerA.connect(null, null);
    }

    public void testChannelDisconnectEvent() throws Exception {
        final ID[] eventIds = new ID[2];
        channelA = createChannel(channelContainerA, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent e) {
                if (e instanceof IChannelDisconnectEvent) {
                    IChannelDisconnectEvent event = (IChannelDisconnectEvent) e;
                    eventIds[0] = event.getChannelID();
                    eventIds[1] = event.getTargetID();
                }
            }
        });
        containerA.disconnect();
        assertEquals(channelA.getID(), eventIds[0]);
        // technically, getConnectedID() should return null when a container has
        // disconnected, but anyway...
        assertEquals(containerA.getConnectedID(), eventIds[1]);
    }

    /**
	 * Test that while the container fires events, the datashare implementation
	 * is ignoring them successfully as it does not have a listener and is not
	 * throwing runtime exceptions.
	 */
    public void testChannelDisconnectEventWithoutListeners() throws Exception {
        channelA = createChannel(channelContainerA, null);
        containerA.disconnect();
    }

    public void testOneWaySend() throws Exception {
        final byte[][] actual = new byte[1][];
        channelA = createChannel(channelContainerA);
        int targetPort = channelA.getPort();
        channelB = createChannel(channelContainerB, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    actual[0] = ((IChannelMessageEvent) event).getData();
                    synchronized (waitObject) {
                        waitObject.notify();
                    }
                }
            }
        });
        byte[] expected = { 1, 2, 3 };
        channelA.sendMessage(containerB.getConnectedID(), expected);
        channelContainerB.enqueue(new InetSocketAddress(LOCALHOST, targetPort));
        waitForCompletion(5000);
        assertEquals(expected, actual[0]);
    }

    public void testOneWaySend16k() throws Exception {
        final byte[][] actual = new byte[1][];
        channelA = createChannel(channelContainerA);
        int targetPort = channelA.getPort();
        channelB = createChannel(channelContainerB, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    actual[0] = ((IChannelMessageEvent) event).getData();
                    synchronized (waitObject) {
                        waitObject.notify();
                    }
                }
            }
        });
        byte[] expected = new byte[16384];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = (byte) (i % 128);
        }
        channelA.sendMessage(containerB.getConnectedID(), expected);
        channelContainerB.enqueue(new InetSocketAddress(LOCALHOST, targetPort));
        waitForCompletion(10000);
        assertEquals(expected, actual[0]);
    }

    public void testSendAndReply() throws Exception {
        final byte[] expected1 = { 1, 2, 3 };
        final byte[] expected2 = { 4, 5, 6 };
        final byte[][] actual = new byte[2][];
        channelA = createChannel(channelContainerA, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    actual[1] = ((IChannelMessageEvent) event).getData();
                    synchronized (waitObject) {
                        waitObject.notify();
                    }
                }
            }
        });
        int targetPort = channelA.getPort();
        channelB = createChannel(channelContainerB, new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    actual[0] = ((IChannelMessageEvent) event).getData();
                    send(channelB, containerA.getConnectedID(), expected2);
                }
            }
        });
        channelA.sendMessage(containerB.getConnectedID(), expected1);
        channelContainerB.enqueue(new InetSocketAddress(LOCALHOST, targetPort));
        waitForCompletion(5000);
        assertEquals(expected1, actual[0]);
        assertEquals(expected2, actual[1]);
    }
}
