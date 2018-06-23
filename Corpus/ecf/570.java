/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp;

import java.util.Locale;
import ch.ethz.iks.slp.impl.SLPCore;
import ch.ethz.iks.slp.impl.StandalonePlatformAbstraction;

/**
 * The central manager for SLP interaction. Application can get a Locator for UA
 * functionality and a Advertiser for SA functionality.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class ServiceLocationManager extends SLPCore {

    /**
	 * hidden default constructor.
	 */
    private  ServiceLocationManager() {
    }

    public static void init() {
        if (SLPCore.platform == null) {
            SLPCore.platform = new StandalonePlatformAbstraction();
            SLPCore.init();
        }
    }

    /**
	 * get the refresh interval, that is the maximum over all DA's minimum
	 * update intervals.
	 * 
	 * @return the refresh interval.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
    public static int getRefreshInterval() throws ServiceLocationException {
        return -1;
    }

    /**
	 * get a Locator to have access to the UA functionalities.
	 * 
	 * @param locale
	 *            the <code>Locale</code> for all requests.
	 * @return a <code>Locator</code> instance.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
    public static Locator getLocator(final Locale locale) throws ServiceLocationException {
        init();
        if (locator != null) {
            try {
                return (Locator) locator.newInstance(new Object[] { locale });
            } catch (Exception e) {
                throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, e.getMessage());
            }
        } else {
            throw new ServiceLocationException(ServiceLocationException.NOT_IMPLEMENTED, "The current configuration does not support UA functionalities.");
        }
    }

    /**
	 * get a Advertiser to have access to the SA functionalities.
	 * 
	 * @param locale
	 *            the <code>Locale</code> for all messages.
	 * @return an <code>Advertiser</code> instance.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
    public static Advertiser getAdvertiser(final Locale locale) throws ServiceLocationException {
        init();
        SLPCore.initMulticastSocket();
        if (advertiser != null) {
            try {
                return (Advertiser) advertiser.newInstance(new Object[] { locale });
            } catch (Exception e) {
                throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, e.getMessage());
            }
        } else {
            throw new ServiceLocationException(ServiceLocationException.NOT_IMPLEMENTED, "The current configuration does not support SA functionalities.");
        }
    }
}
