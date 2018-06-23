/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import ch.ethz.iks.r_osgi.RemoteOSGiService;
import ch.ethz.iks.r_osgi.Remoting;
import ch.ethz.iks.r_osgi.channels.NetworkChannelFactory;

/**
 * the OSGi BundleActivator.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class RemoteOSGiActivator implements BundleActivator {

    /**
	 * the Remote OSGi service instance.
	 */
    private RemoteOSGiServiceImpl remoting;

    private static RemoteOSGiActivator instance;

    private BundleContext context;

    static RemoteOSGiActivator getActivator() {
        return instance;
    }

    BundleContext getContext() {
        return context;
    }

    /**
	 * called when the bundle is started.
	 * 
	 * @param context
	 *            the bundle context.
	 * @throws Exception
	 *             not thrown.
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(final BundleContext context) throws Exception {
        instance = this;
        this.context = context;
        // get the log service, if present
        final ServiceReference logRef = context.getServiceReference(//$NON-NLS-1$
        "org.osgi.service.log.LogService");
        if (logRef != null) {
            RemoteOSGiServiceImpl.log = (LogService) context.getService(logRef);
        }
        if (remoting == null) {
            // get the instance of RemoteOSGiServiceImpl
            remoting = new RemoteOSGiServiceImpl();
        }
        // and register the service
        context.registerService(new String[] { RemoteOSGiService.class.getName(), Remoting.class.getName() }, remoting, null);
        // register the default tcp channel
        if (!//$NON-NLS-1$
        "false".equals(context.getProperty(RemoteOSGiServiceImpl.REGISTER_DEFAULT_TCP_CHANNEL))) {
            final Dictionary properties = new Hashtable();
            properties.put(NetworkChannelFactory.PROTOCOL_PROPERTY, TCPChannelFactory.PROTOCOL);
            context.registerService(NetworkChannelFactory.class.getName(), new TCPChannelFactory(), properties);
        // TODO: add default transport supported intents
        }
    }

    /**
	 * called when the bundle is stopped.
	 * 
	 * @param context
	 *            the bundle context.
	 * @throws Exception
	 *             if something goes wrong.
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(final BundleContext context) throws Exception {
        // unregister and clean up
        remoting.cleanup();
        instance = null;
        this.context = null;
    }
}
