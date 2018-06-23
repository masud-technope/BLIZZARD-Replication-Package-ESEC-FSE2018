package org.eclipse.ecf.provider.dnssd;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.provider.dnssd.messages";

    public static String DnsSdDiscoveryAdvertiser_Container_Already_Connected;

    public static String DnsSdDiscoveryAdvertiser_No_DynDns_Servers_Found;

    public static String DnsSdDiscoveryContainerAdapter_Comparator_SRV_Records;

    public static String DnsSdDiscoveryContainerAdapter_No_IDiscovery_Advertiser;

    public static String DnsSdDiscoveryContainerAdapter_No_IDiscovery_Locator;

    public static String DnsSdDiscoveryException_DynDns_Update_Denied;

    public static String DnsSdDiscoveryException_DynDNS_Updated_Failed;

    public static String DnsSdDiscoveryException_TSIG_Verify_Failed;

    public static String DnsSdDiscoveryLocator_Container_Already_Connected;

    public static String DnsSdDiscoveryLocator_No_Target_ID;

    public static String DnsSdNamespace_Wrong_Parameters;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    }
}
