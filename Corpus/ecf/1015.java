//Copyright 2003-2005 Arthur van Hoff, Rick Blair
package javax.jmdns.impl;

/**
 * A DNS question.
 *
 * @version %I%, %G%
 * @author	Arthur van Hoff
 */
public final class DNSQuestion extends DNSEntry {

    /**
     * Create a question.
     */
    public  DNSQuestion(String name, int type, int clazz) {
        super(name, type, clazz);
    }

    /**
     * Check if this question is answered by a given DNS record.
     */
    boolean answeredBy(DNSRecord rec) {
        return (clazz == rec.clazz) && ((type == rec.type) || (type == DNSConstants.TYPE_ANY)) && name.equals(rec.name);
    }

    /**
     * For debugging only.
     */
    public String toString() {
        return toString("question", null);
    }
}
