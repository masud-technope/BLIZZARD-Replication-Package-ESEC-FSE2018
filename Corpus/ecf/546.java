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

import java.util.List;

/**
 * Abstract base class for all request messages.
 *
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.1
 */
abstract class RequestMessage extends SLPMessage {

    /**
     * the list of previous responders. If a peer receives a request message and
     * is already in the previous responder list, it will silently drop the
     * message.
     */
    List prevRespList;

    /**
     * a list of scopes that will be included.
     */
    List scopeList;
}
