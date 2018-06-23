/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.osgi.util.NLS;
import org.xbill.DNS.Rcode;

public class DnsSdDiscoveryException extends ECFRuntimeException {

    public static DnsSdDiscoveryException getException(int rcode) {
        switch(rcode) {
            case Rcode.REFUSED:
                return new DnsSdDiscoveryException(NLS.bind(Messages.DnsSdDiscoveryException_DynDns_Update_Denied, Integer.toString(rcode)));
            case Rcode.NOTAUTH:
                return new DnsSdDiscoveryException(NLS.bind(Messages.DnsSdDiscoveryException_TSIG_Verify_Failed, Integer.toString(rcode)));
            default:
                return new DnsSdDiscoveryException(NLS.bind(Messages.DnsSdDiscoveryException_DynDNS_Updated_Failed, Integer.toString(rcode)));
        }
    }

    private static final long serialVersionUID = 415901701737755102L;

    public  DnsSdDiscoveryException(Throwable e) {
        super(e);
    }

    public  DnsSdDiscoveryException(String message) {
        super(message);
    }
}
