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

import java.util.Dictionary;
import ch.ethz.iks.slp.ServiceURL;

/**
 * encapsulates the internal information about registered services.
 * 
 * @author Jan S. Rellermeyer, IKS, ETH Zurich
 * @since 0.6
 */
class Service {

    /**
	 * the service URL.
	 */
    ServiceURL url;

    /**
	 * the service attributes.
	 */
    Dictionary attributes;

    /**
	 * creates a new Service instance.
	 * 
	 * @param sreg
	 *            the service registration message.
	 */
     Service(final ServiceRegistration sreg) {
        // TODO: support localized registrations ...
        url = sreg.url;
        attributes = SLPUtils.attrListToDict(sreg.attList);
    }

    /**
	 * @param obj
	 *            Object to compare.
	 * @return <code>true</code> if the object is of type <code>Service</code>
	 *         and the two services have a matching serviceURL and equal
	 *         properties.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(final Object obj) {
        if (obj instanceof Service) {
            Service service = (Service) obj;
            return attributes.equals(service.attributes) && url.equals(service.url);
        }
        return false;
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return url.hashCode();
    }

    /**
	 * get a string representation.
	 * 
	 * @return a string.
	 */
    public String toString() {
        return url.toString();
    }
}
