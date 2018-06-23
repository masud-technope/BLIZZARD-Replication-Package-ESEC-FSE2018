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
package org.eclipse.ecf.provider.irc.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ecf.provider.irc.bot.handler.ICommandHandler;
import org.eclipse.ecf.provider.irc.internal.bot.BotEntry;
import org.eclipse.ecf.provider.irc.internal.bot.CommandEntry;
import org.eclipse.ecf.provider.irc.internal.bot.IBotEntry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.irc.bot";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker extensionRegistryTracker = null;

    private Map bots = new HashMap();

    private Map commands = new HashMap();

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    public IExtensionRegistry getExtensionRegistry() {
        return (IExtensionRegistry) extensionRegistryTracker.getService();
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        plugin = this;
        this.extensionRegistryTracker = new ServiceTracker(context, IExtensionRegistry.class.getName(), null);
        this.extensionRegistryTracker.open();
        loadExtensions();
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (extensionRegistryTracker != null) {
            extensionRegistryTracker.close();
            extensionRegistryTracker = null;
        }
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public Map getBots() {
        return this.bots;
    }

    private void loadExtensions() throws CoreException {
        // load the command handlers
        IExtensionRegistry reg = getExtensionRegistry();
        if (reg != null) {
            IConfigurationElement[] elements = reg.getConfigurationElementsFor("org.eclipse.ecf.provider.irc.bot.commandHandler");
            for (int i = 0; i < elements.length; i++) {
                String id = elements[i].getAttribute("botId");
                String expression = elements[i].getAttribute("expression");
                ICommandHandler handler = (ICommandHandler) elements[i].createExecutableExtension("class");
                List c = (List) commands.get(id);
                if (c == null) {
                    c = new ArrayList();
                    c.add(new CommandEntry(expression, handler));
                    commands.put(id, c);
                } else {
                    c.add(new CommandEntry(expression, handler));
                    commands.put(id, c);
                }
            }
            // load the bots
            elements = reg.getConfigurationElementsFor("org.eclipse.ecf.provider.irc.bot.ircbot");
            for (int i = 0; i < elements.length; i++) {
                String id = elements[i].getAttribute("id");
                String name = elements[i].getAttribute("name");
                String server = elements[i].getAttribute("server");
                String channel = elements[i].getAttribute("channel");
                List c = (List) commands.get(id);
                if (c == null)
                    c = new ArrayList();
                IBotEntry bot = new BotEntry(id, name, server, channel, c);
                bots.put(id, bot);
            }
        }
    }
}
