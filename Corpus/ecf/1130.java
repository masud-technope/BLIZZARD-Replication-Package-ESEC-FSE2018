/**
 * Copyright (c) 2006 Parity Communications, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Yakovlev - initial API and implementation
 */
package org.eclipse.ecf.internal.provider.rss.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;

/**
 * A HTTP message. It can be read from an InputStream or constructed from
 * scratch. The body of the message is only read on demand and cached in a
 * byte[] for later use. Provides functions for getting and setting headers
 * 
 */
public class HttpMessage {

    protected static String DEFAULT_VERSION = "HTTP/1.1";

    protected String startLine = null;

    protected Hashtable headers = new Hashtable(20);

    protected boolean hasBody = false;

    protected byte[] body = null;

    protected int length = 0;

    private InputStream in = null;

    /** Creates a new instance of HttpMessage */
    public  HttpMessage() {
        super();
    }

    public  HttpMessage(InputStream in) throws MalformedURLException, IOException {
        this.in = in;
        readHead();
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    /**
	 * Reads the head of the message (startline and headers) from the
	 * InputStream
	 * @throws IOException 
	 */
    public void readHead() throws IOException {
        trace("readHead start");
        String line;
        // read startline & ignore empty lines before startline
        for (int i = 0; i < 100; i++) {
            if (!((startLine = readLine(in)).equals(""))) {
                break;
            }
        }
        if (startLine.equals("")) {
            trace("startLine empty");
            throw new IOException();
        }
        // Logs startLine
        trace("startLine: " + startLine);
        // read headers until CRLF
        String actHeader = null;
        while (!(line = readLine(in)).equals("")) {
            if (line.startsWith(" ") || line.startsWith("\t")) {
                // multiline header
                if (actHeader != null) {
                    headers.put(actHeader, ((String) headers.get(actHeader)) + "\n" + line.trim());
                }
            } else {
                // header
                actHeader = (line.substring(0, line.indexOf(":"))).toLowerCase();
                if (!headers.containsKey(actHeader)) {
                    // normal case
                    headers.put(actHeader, ((line.substring(line.indexOf(":") + 1)).trim()));
                } else {
                    // header is already defined -> make comma seperated list
                    // RFC2086 page 31
                    String list = (String) headers.get(actHeader);
                    list += "," + (line.substring(line.indexOf(":") + 1)).trim();
                    headers.put(actHeader, list);
                }
            }
            // Logs header
            trace(line);
        }
    }

    public String getHeader(String name) {
        return (String) headers.get(name.toLowerCase());
    }

    public Date getDateHeader(String name) {
        Date date;
        SimpleDateFormat dateFormatter;
        final String header = this.getHeader(name);
        // try rfc 1123 date first
        dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            date = dateFormatter.parse(header);
            return date;
        } catch (final java.text.ParseException e1) {
            dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.ENGLISH);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                date = dateFormatter.parse(header);
                return date;
            } catch (final java.text.ParseException e2) {
                dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    date = dateFormatter.parse(header);
                    return date;
                } catch (final java.text.ParseException e3) {
                    trace("Could not parse date: " + e3.getMessage());
                }
            }
        }
        return null;
    }

    public Enumeration getHeaders() {
        return headers.keys();
    }

    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), value);
    }

    public String setDateHeader(String name, Date value) {
        // make rfc 1123 (and rfc 2068) compliant date
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String strDate = dateFormatter.format(value).toString();
        setHeader(name, strDate);
        return strDate;
    }

    public void removeHeader(String name) {
        headers.remove(name.toLowerCase());
    }

    public boolean containsHeader(String name) {
        return headers.containsKey(name.toLowerCase());
    }

    public boolean hasBody() {
        return hasBody;
    }

    /**
	 * retrieves the body of the message as a byte[]. If the body is not read in
	 * yet, it is read from the InputStream and cached in an internal array
	 * @return byte []
	 * @throws IOException 
	 */
    public byte[] getBody() throws IOException {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        readBody(b);
        body = b.toByteArray();
        return body;
    }

    /**
	 * sets the body of the message and therefore overrides a received body
	 * @param body 
	 */
    public void setBody(byte[] body) {
        this.body = body;
        length = body.length;
        hasBody = true;
    }

    /**
	 * overridden by HttpRequest and HttpResponse
	 */
    protected String getStartLine() {
        return startLine;
    }

    /**
	 * writes the message (startline, headers, body) out to the specified
	 * OutputStream
	 */
    protected void writeToStream(String startLine, OutputStream out) throws IOException {
        out.write((startLine + "\n").getBytes());
        final Enumeration e = headers.keys();
        if (body != null) {
            setHeader("content-length", String.valueOf(body.length));
        }
        // * we should not change the order, although it should not matter...
        while (e.hasMoreElements()) {
            final String hName = (String) e.nextElement();
            out.write((hName + ": " + (String) headers.get(hName) + "\n").getBytes());
        }
        out.write(("\n").getBytes());
        // write body out
        if (hasBody) {
            readBody(out);
        }
    }

    /**
	 * change default version
	 * @param version 
	 */
    public void setDefaultVersion(String version) {
        DEFAULT_VERSION = version;
    }

    /**
	 * utility function: reads a header line from the stream according to rfc
	 * (taken from muffin)
	 */
    protected String readLine(InputStream in) throws IOException {
        char buf[] = new char[128];
        int offset = 0;
        int ch;
        while ((ch = in.read()) > -1) {
            if (ch == '\n') {
                break;
            } else if (ch == '\r') {
                final int tmpch = in.read();
                if (tmpch != '\n') {
                    if (!(in instanceof PushbackInputStream)) {
                        trace("creating PushbackInputStream");
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream) in).unread(tmpch);
                }
                break;
            } else {
                if (offset == buf.length) {
                    final char tmpbuf[] = buf;
                    buf = new char[tmpbuf.length * 2];
                    System.arraycopy(tmpbuf, 0, buf, 0, offset);
                }
                buf[offset++] = (char) ch;
            }
        }
        return String.copyValueOf(buf, 0, offset);
    }

    /**
	 * read body of message in and write it to specified OutputStream.
	 * content-length is determined according to RFC 2068 page 31 1.) if a body
	 * must not be present (hasBody == false), ignore body content. 2.) if
	 * chunked transfer-ecoding, this defines the length 3.) content-length
	 * header 4.) multipart/byteranges -> ignore, we can't handle them (todo!!)
	 * 5.) read all content in until server closes connection
	 */
    private void readBody(OutputStream out) throws IOException {
        if (hasBody && body == null) {
            if ((containsHeader("Transfer-Encoding")) && (getHeader("Transfer-Encoding").equals("chunked"))) {
                // decoding-routine for chunked transfer-encoding
                // (according to rfc 2616 section 19.4.6.)
                int n;
                int chunk_size;
                String line;
                final byte[] buffer = new byte[8192];
                final ByteArrayOutputStream byte_out = new ByteArrayOutputStream(8192);
                length = 0;
                line = readLine(in);
                chunk_size = Integer.parseInt(line.substring(0, line.indexOf(" ") > -1 ? line.indexOf(" ") : line.length()), 16);
                while (chunk_size > 0) {
                    for (int len = 0; len < chunk_size; ) {
                        n = in.read(buffer, 0, Math.min(chunk_size - len, buffer.length));
                        if (n == -1) {
                            break;
                        }
                        len += n;
                        byte_out.write(buffer, 0, n);
                    }
                    // trailing crlf
                    line = // trailing crlf
                    readLine(in);
                    length += chunk_size;
                    line = readLine(in);
                    chunk_size = Integer.parseInt(line.substring(0, line.indexOf(" ") > -1 ? line.indexOf(" ") : line.length()), 16);
                }
                // headers come last...
                String actHeader = null;
                while (!(line = readLine(in)).equals("")) {
                    if (line.startsWith(" ") || line.startsWith("\t")) {
                        // multiline header
                        headers.put(actHeader, ((String) headers.get(actHeader)) + "\n" + line);
                    } else {
                        actHeader = (line.substring(0, line.indexOf(":"))).toLowerCase();
                        headers.put(actHeader, ((line.substring(line.indexOf(":") + 1)).trim()));
                    }
                }
                setHeader("content-length", "" + length);
                // Proxies/gateways MUST remove any transfer coding prior to
                // forwarding a message via a MIME-compliant protocol (RFC 2068,
                // sec.19.4.6).
                removeHeader("transfer-encoding");
                out.write(byte_out.toByteArray());
            } else if (headers.containsKey("content-length")) {
                // header defines length of content
                length = Integer.parseInt(getHeader("Content-Length"));
                int n;
                int len = 0;
                final byte buffer[] = new byte[8192];
                while (len < length) {
                    n = in.read(buffer, 0, Math.min(length - len, buffer.length));
                    if (n == -1) {
                        break;
                    }
                    len += n;
                    out.write(buffer, 0, n);
                    out.flush();
                }
            } else {
                // just read until server closes connection
                int n;
                final byte buffer[] = new byte[8192];
                while (true) {
                    n = in.read(buffer, 0, buffer.length);
                    if (n == -1) {
                        break;
                    }
                    out.write(buffer, 0, n);
                    out.flush();
                }
            }
        } else if (// body != null
        hasBody) {
            out.write(body);
        }
    }
}
