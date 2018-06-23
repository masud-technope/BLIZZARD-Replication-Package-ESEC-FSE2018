//Copyright 2003-2005 Arthur van Hoff, Rick Blair
package javax.jmdns.impl;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Listen for multicast packets.
 */
class SocketListener implements Runnable {

    //    static Logger logger = Logger.getLogger(SocketListener.class.getName());
    /**
     * 
     */
    private final JmDNSImpl jmDNSImpl;

    /**
     * @param jmDNSImpl
     */
     SocketListener(JmDNSImpl jmDNSImpl) {
        this.jmDNSImpl = jmDNSImpl;
    }

    public void run() {
        try {
            byte buf[] = new byte[DNSConstants.MAX_MSG_ABSOLUTE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (this.jmDNSImpl.getState() != DNSState.CANCELED) {
                packet.setLength(buf.length);
                this.jmDNSImpl.getSocket().receive(packet);
                if (this.jmDNSImpl.getState() == DNSState.CANCELED) {
                    break;
                }
                try {
                    if (this.jmDNSImpl.getLocalHost().shouldIgnorePacket(packet)) {
                        continue;
                    }
                    DNSIncoming msg = new DNSIncoming(packet);
                    synchronized (this.jmDNSImpl.getIoLock()) {
                        if (msg.isQuery()) {
                            if (packet.getPort() != DNSConstants.MDNS_PORT) {
                                this.jmDNSImpl.handleQuery(msg, packet.getAddress(), packet.getPort());
                            }
                            this.jmDNSImpl.handleQuery(msg, this.jmDNSImpl.getGroup(), DNSConstants.MDNS_PORT);
                        } else {
                            this.jmDNSImpl.handleResponse(msg);
                        }
                    }
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            if (this.jmDNSImpl.getState() != DNSState.CANCELED) {
                this.jmDNSImpl.recover();
            }
        }
    }
}
