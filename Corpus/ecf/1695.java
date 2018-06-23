/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.presence.bot.application;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.ecf.internal.presence.bot.Activator;
import org.eclipse.ecf.presence.bot.IIMBotEntry;
import org.eclipse.ecf.presence.bot.impl.IMBot;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * Application for getting im bots defined in extension registry and running
 * them. This application will continue to run indefinitely. Subclasses may be
 * implemented as desired.
 */
public class IMBotApplication implements IApplication {

    protected Map getIMBotsFromExtensionRegistry() {
        return Activator.getDefault().getIMBots();
    }

    public Object start(IApplicationContext context) throws Exception {
        Map bots = getIMBotsFromExtensionRegistry();
        for (Iterator it = bots.values().iterator(); it.hasNext(); ) {
            IIMBotEntry entry = (IIMBotEntry) it.next();
            // Create default im bot
            IMBot bot = new IMBot(entry);
            // connect
            bot.connect();
        }
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                break;
            }
        }
        return IApplication.EXIT_OK;
    }

    public void stop() {
    }
}
