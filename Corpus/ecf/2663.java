//Copyright 2003-2005 Arthur van Hoff, Rick Blair
package javax.jmdns.impl;

/**
 * DNS constants.
 *
 * @version %I%, %G%
 * @author	Arthur van Hoff, Jeff Sonstein, Werner Randelshofer, Pierre Frisch, Rick Blair
 */
public final class DNSConstants {

    // changed to final class - jeffs
    public static final String MDNS_GROUP = "224.0.0.251";

    public static final String MDNS_GROUP_IPV6 = "FF02::FB";

    public static final int MDNS_PORT = Integer.parseInt(System.getProperty("net.mdns.port", "5353"));

    public static final int DNS_PORT = 53;

    // default one hour TTL
    public static final int DNS_TTL = 60 * 60;

    // public final static int DNS_TTL		    = 120 * 60;	// two hour TTL (draft-cheshire-dnsext-multicastdns.txt ch 13)
    public static final int MAX_MSG_TYPICAL = 1460;

    public static final int MAX_MSG_ABSOLUTE = 8972;

    // Query response mask
    public static final int FLAGS_QR_MASK = 0x8000;

    // Query
    public static final int FLAGS_QR_QUERY = 0x0000;

    // Response
    public static final int FLAGS_QR_RESPONSE = 0x8000;

    // Authorative answer
    public static final int FLAGS_AA = 0x0400;

    // Truncated
    public static final int FLAGS_TC = 0x0200;

    // Recursion desired
    public static final int FLAGS_RD = 0x0100;

    // Recursion available
    public static final int FLAGS_RA = 0x8000;

    // Zero
    public static final int FLAGS_Z = 0x0040;

    // Authentic data
    public static final int FLAGS_AD = 0x0020;

    // Checking disabled
    public static final int FLAGS_CD = 0x0010;

    // public final static Internet
    public static final int CLASS_IN = 1;

    // CSNET
    public static final int CLASS_CS = 2;

    // CHAOS
    public static final int CLASS_CH = 3;

    // Hesiod
    public static final int CLASS_HS = 4;

    // Used in DNS UPDATE [RFC 2136]
    public static final int CLASS_NONE = 254;

    // Not a DNS class, but a DNS query class, meaning "all classes"
    public static final int CLASS_ANY = 255;

    // Multicast DNS uses the bottom 15 bits to identify the record class...
    public static final int CLASS_MASK = 0x7FFF;

    // ... and the top bit indicates that all other cached records are now invalid
    public static final int CLASS_UNIQUE = 0x8000;

    // This is a hack to stop further processing
    public static final int TYPE_IGNORE = 0;

    // Address
    public static final int TYPE_A = 1;

    // Name Server
    public static final int TYPE_NS = 2;

    // Mail Destination
    public static final int TYPE_MD = 3;

    // Mail Forwarder
    public static final int TYPE_MF = 4;

    // Canonical Name
    public static final int TYPE_CNAME = 5;

    // Start of Authority
    public static final int TYPE_SOA = 6;

    // Mailbox
    public static final int TYPE_MB = 7;

    // Mail Group
    public static final int TYPE_MG = 8;

    // Mail Rename
    public static final int TYPE_MR = 9;

    // NULL RR
    public static final int TYPE_NULL = 10;

    // Well-known-service
    public static final int TYPE_WKS = 11;

    // Domain Name popublic final static inter
    public static final int TYPE_PTR = 12;

    // Host information
    public static final int TYPE_HINFO = 13;

    // Mailbox information
    public static final int TYPE_MINFO = 14;

    // Mail exchanger
    public static final int TYPE_MX = 15;

    // Arbitrary text string
    public static final int TYPE_TXT = 16;

    // for Responsible Person                 [RFC1183]
    public static final int TYPE_RP = 17;

    // for AFS Data Base location             [RFC1183]
    public static final int TYPE_AFSDB = 18;

    // for X.25 PSDN address                  [RFC1183]
    public static final int TYPE_X25 = 19;

    // for ISDN address                       [RFC1183]
    public static final int TYPE_ISDN = 20;

    // for Route Through                      [RFC1183]
    public static final int TYPE_RT = 21;

    // for NSAP address, NSAP style A record  [RFC1706]
    public static final int TYPE_NSAP = 22;

    //
    public static final int TYPE_NSAP_PTR = 23;

    // for security signature                 [RFC2931]
    public static final int TYPE_SIG = 24;

    // for security key                       [RFC2535]
    public static final int TYPE_KEY = 25;

    // X.400 mail mapping information         [RFC2163]
    public static final int TYPE_PX = 26;

    // Geographical Position                  [RFC1712]
    public static final int TYPE_GPOS = 27;

    // IP6 Address                            [Thomson]
    public static final int TYPE_AAAA = 28;

    // Location Information                   [Vixie]
    public static final int TYPE_LOC = 29;

    // Next Domain - OBSOLETE                 [RFC2535, RFC3755]
    public static final int TYPE_NXT = 30;

    // Endpoint Identifier                    [Patton]
    public static final int TYPE_EID = 31;

    // Nimrod Locator                         [Patton]
    public static final int TYPE_NIMLOC = 32;

    // Server Selection                       [RFC2782]
    public static final int TYPE_SRV = 33;

    // ATM Address                            [Dobrowski]
    public static final int TYPE_ATMA = 34;

    // Naming Authority Pointer               [RFC2168, RFC2915]
    public static final int TYPE_NAPTR = 35;

    // Key Exchanger                          [RFC2230]
    public static final int TYPE_KX = 36;

    // CERT                                   [RFC2538]
    public static final int TYPE_CERT = 37;

    // A6                                     [RFC2874]
    public static final int TYPE_A6 = 38;

    // DNAME                                  [RFC2672]
    public static final int TYPE_DNAME = 39;

    // SINK                                   [Eastlake]
    public static final int TYPE_SINK = 40;

    // OPT                                    [RFC2671]
    public static final int TYPE_OPT = 41;

    // APL                                    [RFC3123]
    public static final int TYPE_APL = 42;

    // Delegation Signer                      [RFC3658]
    public static final int TYPE_DS = 43;

    // SSH Key Fingerprint                    [RFC-ietf-secsh-dns-05.txt]
    public static final int TYPE_SSHFP = 44;

    // RRSIG                                  [RFC3755]
    public static final int TYPE_RRSIG = 46;

    // NSEC                                   [RFC3755]
    public static final int TYPE_NSEC = 47;

    // DNSKEY                                 [RFC3755]
    public static final int TYPE_DNSKEY = 48;

    //									      [IANA-Reserved]
    public static final int TYPE_UINFO = 100;

    //                                        [IANA-Reserved]
    public static final int TYPE_UID = 101;

    //                                        [IANA-Reserved]
    public static final int TYPE_GID = 102;

    //                                        [IANA-Reserved]
    public static final int TYPE_UNSPEC = 103;

    // Transaction Key                        [RFC2930]
    public static final int TYPE_TKEY = 249;

    // Transaction Signature                  [RFC2845]
    public static final int TYPE_TSIG = 250;

    // Incremental transfer                   [RFC1995]
    public static final int TYPE_IXFR = 251;

    // Transfer of an entire zone             [RFC1035]
    public static final int TYPE_AXFR = 252;

    // Mailbox-related records (MB, MG or MR) [RFC1035]
    public static final int TYPE_MAILA = 253;

    // Mail agent RRs (Obsolete - see MX)     [RFC1035]
    public static final int TYPE_MAILB = 254;

    // Request for all records	        	  [RFC1035]
    public static final int TYPE_ANY = 255;

    //Time Intervals for various functions
    //milliseconds before send shared query
    public static final int SHARED_QUERY_TIME = 20;

    //milliseconds between query loops.
    public static final int QUERY_WAIT_INTERVAL = 225;

    //milliseconds between probe loops.
    public static final int PROBE_WAIT_INTERVAL = 250;

    //minimal wait interval for response.
    public static final int RESPONSE_MIN_WAIT_INTERVAL = 20;

    //maximal wait interval for response
    public static final int RESPONSE_MAX_WAIT_INTERVAL = 115;

    //milliseconds to wait after conflict.
    public static final int PROBE_CONFLICT_INTERVAL = 1000;

    //After x tries go 1 time a sec. on probes.
    public static final int PROBE_THROTTLE_COUNT = 10;

    //We only increment the throttle count, if
    public static final int PROBE_THROTTLE_COUNT_INTERVAL = 5000;

    // the previous increment is inside this interval.
    //milliseconds between Announce loops.
    public static final int ANNOUNCE_WAIT_INTERVAL = 1000;

    //milliseconds between cache cleanups.
    public static final int RECORD_REAPER_INTERVAL = 10000;

    public static final int KNOWN_ANSWER_TTL = 120;

    // 50% of the TTL in milliseconds
    public static final int ANNOUNCED_RENEWAL_TTL_INTERVAL = DNS_TTL * 500;

    // PTR records, where the rdata of each PTR record is the two-label name of a service type, e.g. "_http._tcp.
    public static final String DNS_META_QUERY = "._dns-sd._udp.";
}
