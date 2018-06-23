/****************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Alexander Kuppe - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.eventadmin;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceException;
import org.osgi.service.event.Event;

public class DistributedEventAdminTest {

    private TestDistributedEventAdmin dea;

    private Event eventToSend;

    @Before
    public void setup() {
        final String aTopic = "non/serializable";
        final Object nonSerializable = new Object();
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", nonSerializable);
        eventToSend = new Event(aTopic, map);
        dea = new TestDistributedEventAdmin();
    }

    /**
	 * Default behavior where non-serializable objects throw an exception
	 */
    @Test
    public void testSendMessage() {
        try {
            dea.sendMessage(eventToSend);
        } catch (ServiceException e) {
            return;
        }
        Assert.fail("Should throw a service exception");
    }

    /**
	 * @see https://bugs.eclipse.org/412261
	 */
    @Test
    public void testSendMessageIgnore() {
        // turn on ignoring of serialization exceptions explicitly
        dea.setIgnoreSerializationFailures(true);
        try {
            dea.sendMessage(eventToSend);
        // will still cause a warning to be printed on the console
        } catch (ServiceException e) {
            Assert.fail("Should not throw a service exception");
        }
    }
}
