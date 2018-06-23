//Copyright 2003-2005 Arthur van Hoff, Rick Blair
package javax.jmdns.impl;

/**
 * DNSListener.
 * Listener for record updates.
 *
 * @author Werner Randelshofer, Rick Blair
 * @version 1.0  May 22, 2004  Created.
 */
interface DNSListener {

    /**
     * Update a DNS record.
     */
    void updateRecord(JmDNSImpl jmdns, long now, DNSRecord record);
}
