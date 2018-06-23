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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.impl.attr.AttributeListVisitor;
import ch.ethz.iks.slp.impl.attr.gen.Parser;
import ch.ethz.iks.slp.impl.attr.gen.ParserException;
import ch.ethz.iks.slp.impl.attr.gen.Rule;

/**
 * base class for all messages that the SLP framework uses.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public abstract class SLPMessage {

    /**
	 * the <code>Locale</code> of the message.
	 */
    Locale locale;

    /**
	 * the funcID encodes the message type.
	 */
    byte funcID;

    /**
	 * the transaction ID.
	 */
    short xid;

    /**
	 * the sender or receiver address.
	 */
    InetAddress address;

    /**
	 * the sender or receiver port.
	 */
    int port;

    /**
	 * true if the message was processed or will be sent via TCP
	 */
    boolean tcp;

    /**
	 * true if the message came in or will go out by multicast.
	 */
    boolean multicast;

    /**
	 * the message version according to RFC 2608, Version = 2.
	 */
    public static final byte VERSION = 2;

    /**
	 * the message funcID values according to RFC 2608, Service Request = 1.
	 */
    public static final byte SRVRQST = 1;

    /**
	 * the message funcID values according to RFC 2608, Service Reply = 2.
	 */
    public static final byte SRVRPLY = 2;

    /**
	 * the message funcID values according to RFC 2608, Service Registration =
	 * 3.
	 */
    public static final byte SRVREG = 3;

    /**
	 * the message funcID values according to RFC 2608, Service Deregistration =
	 * 4.
	 */
    public static final byte SRVDEREG = 4;

    /**
	 * the message funcID values according to RFC 2608, Service Acknowledgement =
	 * 5.
	 */
    public static final byte SRVACK = 5;

    /**
	 * the message funcID values according to RFC 2608, Attribute Request = 6.
	 */
    public static final byte ATTRRQST = 6;

    /**
	 * the message funcID values according to RFC 2608, Attribute Reply = 7.
	 */
    public static final byte ATTRRPLY = 7;

    /**
	 * the message funcID values according to RFC 2608, DA Advertisement = 8.
	 */
    public static final byte DAADVERT = 8;

    /**
	 * the message funcID values according to RFC 2608, Service Type Request =
	 * 9.
	 */
    public static final byte SRVTYPERQST = 9;

    /**
	 * the message funcID values according to RFC 2608, Service Type Reply = 10.
	 */
    public static final byte SRVTYPERPLY = 10;

    /**
	 * the message funcID values according to RFC 2608, SA Advertisement = 11.
	 */
    public static final byte SAADVERT = 11;

    /**
	 * used for reverse lookup of funcID values to have nicer debug messages.
	 */
    private static final String[] TYPES = { "NULL", "SRVRQST", "SRVPLY", "SRVREG", "SRVDEREG", "SRVACK", "ATTRRQST", "ATTRRPLY", "DAADVERT", "SRVTYPERQST", "SRVTYPERPLY", "SAADVERT" };

    /**
	 * get the bytes from a SLPMessage. Processes the header and then calls the
	 * getBody() method of the implementing subclass.
	 * 
	 * @return an array of bytes encoding the SLPMessage.
	 * @throws IOException
	 * @throws ServiceLocationException
	 *             in case of IOExceptions.
	 */
    private void writeHeader(final DataOutputStream out) throws IOException {
        byte flags = 0;
        if (funcID == SRVREG) {
            flags |= 0x40;
        }
        if (multicast) {
            flags |= 0x20;
        }
        int msgSize = getSize();
        if (!tcp && msgSize > SLPCore.CONFIG.getMTU()) {
            flags |= 0x80;
        }
        out.write(VERSION);
        out.write(funcID);
        out.write((byte) ((msgSize) >> 16));
        out.write((byte) (((msgSize) >> 8) & 0xFF));
        out.write((byte) ((msgSize) & 0xFF));
        out.write(flags);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(0);
        out.writeShort(xid);
        out.writeUTF(locale.getLanguage());
    }

    /**
	 * 
	 */
    protected abstract void writeTo(final DataOutputStream out) throws IOException;

    /**
	 * 
	 */
    byte[] getBytes() throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeHeader(out);
        writeTo(out);
        return bytes.toByteArray();
    }

    /**
	 * The RFC 2608 SLP message header:
	 * 
	 * <pre>
	 *                   0                   1                   2                   3
	 *                   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |    Version    |  Function-ID  |            Length             |
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  | Length, contd.|O|F|R|       reserved          |Next Ext Offset|
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |  Next Extension Offset, contd.|              XID              |
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |      Language Tag Length      |         Language Tag          \
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * This method parses the header and then delegates the creation of the
	 * corresponding SLPMessage to the subclass that matches the funcID.
	 * 
	 * @param senderAddr
	 *            the address of the message sender.
	 * @param senderPort
	 *            the port of the message sender.
	 * @param data
	 *            the raw bytes of the message
	 * @param len
	 *            the length of the byte array.
	 * @param tcp
	 *            true if the message was received via TCP, false otherwise.
	 * @return a SLPMessage of the matching subtype.
	 * @throws ServiceLocationException
	 *             in case of any parsing errors.
	 */
    static SLPMessage parse(final InetAddress senderAddr, final int senderPort, final DataInputStream in, final boolean tcp) throws ServiceLocationException, ProtocolException {
        try {
            // version
            final int version = in.readByte();
            if (version != VERSION) {
                // funcID
                in.readByte();
                final int length = in.readShort();
                byte[] drop = new byte[length - 4];
                in.readFully(drop);
                SLPCore.platform.logWarning("Dropped SLPv" + version + " message from " + senderAddr + ":" + senderPort);
            }
            final byte funcID = // funcID
            in.readByte();
            final int length = readInt(in, 3);
            // slpFlags
            final byte flags = (byte) (in.readShort() >> 8);
            if (!tcp && (flags & 0x80) != 0) {
                throw new ProtocolException();
            }
            // we don't process extensions, we simply ignore them
            // extOffset
            readInt(in, 3);
            final short xid = // XID
            in.readShort();
            final Locale locale = new // Locale
            Locale(// Locale
            in.readUTF(), // Locale
            "");
            final SLPMessage msg;
            // decide on the type of the message
            switch(funcID) {
                case DAADVERT:
                    msg = new DAAdvertisement(in);
                    break;
                case SRVRQST:
                    msg = new ServiceRequest(in);
                    break;
                case SRVRPLY:
                    msg = new ServiceReply(in);
                    break;
                case ATTRRQST:
                    msg = new AttributeRequest(in);
                    break;
                case ATTRRPLY:
                    msg = new AttributeReply(in);
                    break;
                case SRVREG:
                    msg = new ServiceRegistration(in);
                    break;
                case SRVDEREG:
                    msg = new ServiceDeregistration(in);
                    break;
                case SRVACK:
                    msg = new ServiceAcknowledgement(in);
                    break;
                case SRVTYPERQST:
                    msg = new ServiceTypeRequest(in);
                    break;
                case SRVTYPERPLY:
                    msg = new ServiceTypeReply(in);
                    break;
                default:
                    throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, "Message type " + getType(funcID) + " not supported");
            }
            // set the fields
            msg.address = senderAddr;
            msg.port = senderPort;
            msg.tcp = tcp;
            msg.multicast = ((flags & 0x2000) >> 13) == 1 ? true : false;
            msg.xid = xid;
            msg.funcID = funcID;
            msg.locale = locale;
            if (msg.getSize() != length) {
                SLPCore.platform.logError("Length of " + msg + " should be " + length + ", read " + msg.getSize());
            //				throw new ServiceLocationException(
            //						ServiceLocationException.INTERNAL_SYSTEM_ERROR,
            //						"Length of " + msg + " should be " + length + ", read "
            //								+ msg.getSize());
            }
            return msg;
        } catch (ProtocolException pe) {
            throw pe;
        } catch (IOException ioe) {
            SLPCore.platform.logError("Network Error", ioe);
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, ioe.getMessage());
        }
    }

    /**
	 * 
	 * @return
	 */
    protected int getHeaderSize() {
        return 14 + locale.getLanguage().length();
    }

    /**
	 * 
	 * @return
	 */
    protected abstract int getSize();

    /**
	 * Get a string representation of the message. Overridden by message
	 * subtypes.
	 * 
	 * @return a String.
	 */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getType(funcID) + " - ");
        buffer.append("xid=" + xid);
        buffer.append(", locale=" + locale);
        return buffer.toString();
    }

    /**
	 * returns the string value of the message type, catches the case where an
	 * unsupported message has been received.
	 * 
	 * @param type
	 *            the type.
	 * @return the type as String.
	 */
    static String getType(final int type) {
        if (type > -1 && type < 12) {
            return TYPES[type];
        }
        return String.valueOf(type + " - UNSUPPORTED");
    }

    /**
	 * parse a numerical value that can be spanned over multiple bytes.
	 * 
	 * @param input
	 *            the data input stream.
	 * @param len
	 *            the number of bytes to read.
	 * @return the int value.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    private static int readInt(final DataInputStream input, final int len) throws ServiceLocationException {
        try {
            int value = 0;
            for (int i = 0; i < len; i++) {
                value <<= 8;
                value += input.readByte() & 0xff;
            }
            return value;
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, ioe.getMessage());
        }
    }

    /**
	 * transforms a Java list to string list.
	 * 
	 * @param list
	 *            the list
	 * @param delim
	 *            the delimiter
	 * @return the String list.
	 */
    static String listToString(final List list, final String delim) {
        if (list == null || list.size() == 0) {
            return "";
        } else if (list.size() == 1) {
            return list.get(0).toString();
        } else {
            final StringBuffer buffer = new StringBuffer();
            final Object[] elements = list.toArray();
            for (int i = 0; i < elements.length - 1; i++) {
                buffer.append(elements[i]);
                buffer.append(delim);
            }
            buffer.append(elements[elements.length - 1]);
            return buffer.toString();
        }
    }

    /**
	 * transforms a string list to Java List.
	 * 
	 * @param str
	 *            the String list
	 * @param delim
	 *            the delimiter
	 * @return the List.
	 */
    static List stringToList(final String str, final String delim) {
        List result = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(str, delim);
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    /**
	 * 
	 * @param input
	 * @return
	 * @throws ServiceLocationException
	 * 
	 * @author Markus Alexander Kuppe
	 * @since 1.1
	 */
    protected List attributeStringToList(String input) throws ServiceLocationException {
        if ("".equals(input)) {
            return new ArrayList();
        }
        Parser parser = new Parser();
        try {
            Rule parse = parser.parse("attr-list", input);
            AttributeListVisitor visitor = new AttributeListVisitor();
            parse.visit(visitor);
            return visitor.getAttributes();
        } catch (IllegalArgumentException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, e.getMessage());
        } catch (ParserException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, e.getMessage());
        }
    }

    /**
	 * 
	 * @param input
	 * @return
	 * @throws ServiceLocationException
	 * 
	 * @author Markus Alexander Kuppe
	 * @since 1.1
	 */
    protected List attributeStringToListLiberal(String input) {
        if ("".equals(input)) {
            return new ArrayList();
        }
        Parser parser = new Parser();
        Rule rule = null;
        try {
            rule = parser.parse("attr-list", input);
        } catch (IllegalArgumentException e) {
            SLPCore.platform.logError(e.getMessage(), e);
            return new ArrayList();
        } catch (ParserException e) {
            SLPCore.platform.logTraceDrop(e.getMessage());
            rule = e.getRule();
        }
        AttributeListVisitor visitor = new AttributeListVisitor();
        rule.visit(visitor);
        return visitor.getAttributes();
    }
}
