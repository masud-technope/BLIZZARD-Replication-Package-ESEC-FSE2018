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
package ch.ethz.iks.slp.impl;

import ch.ethz.iks.slp.ServiceLocationException;

/**
 * the SLPDeaemon interface. Factored out to make the daemon part optional as
 * part of the jSLP modularity.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich.
 */
public interface SLPDaemon {

    /**
	 * called, when a new DA has been discovered.
	 * 
	 * @param advert
	 *            the <code>DAAdvertisement</code> received from the new DA.
	 */
    void newDaDiscovered(DAAdvertisement advert);

    /**
	 * handle a message dispatched by SLPCore.
	 * 
	 * @param msg
	 *            the message.
	 * @return the reply message or <code>null</code>.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 */
    ReplyMessage handleMessage(final SLPMessage msg) throws ServiceLocationException;
}
