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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * A HTTP response message
 * 
 */
public class HttpResponse extends HttpMessage {

    // status codes:
    // informational
    public static final int CONTINUE = 100;

    public static final int SWITCH_PROTOCOLS = 101;

    // success
    public static final int OK = 200;

    public static final int CREATED = 201;

    public static final int ACCEPTED = 202;

    public static final int NON_AUTHORATIVE = 203;

    public static final int NO_CONTENT = 204;

    public static final int RESET_CONTENT = 205;

    public static final int PARTIAL_CONTENT = 206;

    // redirection
    public static final int MULTIPLE_CHOICES = 300;

    public static final int MOVED_PERMANENTLY = 301;

    public static final int MOVED_TEMPORARILY = 302;

    public static final int SEE_OTHER = 303;

    public static final int NOT_MODIFIED = 304;

    public static final int USE_PROXY = 305;

    // client error
    public static final int BAD_REQUEST = 400;

    public static final int UNAUTHORIZED = 401;

    public static final int PAYMENT_REQUIRED = 402;

    public static final int FORBIDDEN = 403;

    public static final int NOT_FOUND = 404;

    public static final int METHOD_NOT_ALLOWD = 405;

    public static final int NOT_ACCEPTABLE = 406;

    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;

    public static final int REQUEST_TIMEOUT = 408;

    public static final int CONFLICT = 409;

    public static final int GONE = 410;

    public static final int LENGTH_REQUIRED = 411;

    public static final int PRECONDITION_FAILED = 412;

    public static final int REQUEST_TOO_LARGE = 413;

    public static final int URI_TOO_LARGE = 414;

    public static final int UNSUPPORTED_MEDIA = 415;

    // server error
    public static final int SERVER_ERROR = 500;

    public static final int NOT_IMPLEMENTED = 501;

    public static final int BAD_GATEWAY = 502;

    public static final int UNAVAILABLE = 503;

    public static final int GATEWAY_TIMEOUT = 504;

    public static final int VERSION_NOT_SUPPORTED = 505;

    private String version;

    private int code;

    private String reason;

    /** Creates a new instance of HttpResponse 
	 * @param version 
	 * @param code 
	 * @param reason */
    public  HttpResponse(String version, int code, String reason) {
        this.version = version;
        this.code = code;
        this.reason = reason;
        setDateHeader("Date", new Date());
    }

    /**
	 * use default version HTTP/1.1
	 * @param code 
	 * @param body 
	 */
    public  HttpResponse(int code, String body) {
        this(DEFAULT_VERSION, code, type2String(code));
        this.setBody(body.getBytes());
    }

    /**
	 * response ok (200) with string as body default version
	 * @param body 
	 */
    public  HttpResponse(String body) {
        this(DEFAULT_VERSION, OK, type2String(OK));
        this.setBody(body.getBytes());
    }

    /**
	 * default version and code.
	 * @param code 
	 */
    public  HttpResponse(int code) {
        this(DEFAULT_VERSION, code, type2String(code));
    }

    /**
	 * create the response from the inputstream
	 * @param in 
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
    public  HttpResponse(InputStream in) throws MalformedURLException, IOException {
        super(in);
    }

    /**
	 * Reads the head of the message (startline and headers) from the
	 * InputStream, and parses the startline for version, return code and
	 * reason.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
    public void readHead() throws IOException, MalformedURLException {
        super.readHead();
        // parse requestline
        final int i = startLine.indexOf(' ');
        final int j = startLine.indexOf(' ', i + 1);
        version = startLine.substring(0, i);
        if (j > -1) {
            code = Integer.parseInt(startLine.substring(i + 1, j));
            reason = startLine.substring(j + 1);
        } else {
            code = Integer.parseInt(startLine.substring(i + 1));
            reason = "";
        }
        if ((code >= 100 && code < 200) || code == 204 || code == 304) {
            // - and responses to HEAD requests
            hasBody = false;
        } else {
            hasBody = true;
        }
        // read body in immediately if cunked transfer-encoding...
        if ((containsHeader("Transfer-Encoding")) && (getHeader("Transfer-Encoding").equals("chunked"))) {
            getBody();
        }
    }

    public String getVersion() {
        return version;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStartLine() {
        return version + " " + code + " " + reason;
    }

    public boolean isHTML() {
        if (this.containsHeader("content-type")) {
            if (this.getHeader("content-type").indexOf("text/html") > -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isCSS() {
        if (this.containsHeader("content-type")) {
            if (this.getHeader("content-type").indexOf("text/css") > -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isGIF() {
        if (this.containsHeader("content-type")) {
            if (this.getHeader("content-type").indexOf("image/gif") > -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isEncoded() {
        return this.containsHeader("content-encoding");
    }

    public boolean isGZIPEncoded() {
        if (this.containsHeader("content-encoding")) {
            if (this.getHeader("content-encoding").indexOf("gzip") > -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeflateEncoded() {
        if (this.containsHeader("content-encoding")) {
            if (this.getHeader("content-encoding").indexOf("deflate") > -1) {
                return true;
            }
        }
        return false;
    }

    public void appendToBody(String s) throws java.io.IOException {
        final String bs = new String(getBody()) + s;
        setBody(bs.getBytes());
    }

    public void wrapBody(String header, String trailer) throws java.io.IOException {
        final String bs = header + new String(getBody()) + trailer;
        setBody(bs.getBytes());
    }

    public void writeToStream(OutputStream stream) throws IOException {
        super.writeToStream(getStartLine(), stream);
    }

    private static String type2String(int type) {
        switch(type) {
            case CONTINUE:
                return "Continue";
            case SWITCH_PROTOCOLS:
                return "Switching Protocols";
            case OK:
                return "OK";
            case CREATED:
                return "Created";
            case ACCEPTED:
                return "Accepted";
            case NON_AUTHORATIVE:
                return "Non-Authorative Information";
            case NO_CONTENT:
                return "No Content";
            case RESET_CONTENT:
                return "Reset Content";
            case PARTIAL_CONTENT:
                return "Partial Content";
            case MULTIPLE_CHOICES:
                return "Multiple Choices";
            case MOVED_PERMANENTLY:
                return "Moved Permanently";
            case MOVED_TEMPORARILY:
                return "Moved Temporarily";
            case SEE_OTHER:
                return "See Other";
            case NOT_MODIFIED:
                return "Not Modified";
            case USE_PROXY:
                return "Use Proxy";
            case BAD_REQUEST:
                return "Bad Request";
            case UNAUTHORIZED:
                return "Unauthorized";
            case PAYMENT_REQUIRED:
                return "Payment Required";
            case FORBIDDEN:
                return "Forbidden";
            case NOT_FOUND:
                return "Not Found";
            case METHOD_NOT_ALLOWD:
                return "Method Not Allowed";
            case NOT_ACCEPTABLE:
                return "Not Acceptable";
            case PROXY_AUTHENTICATION_REQUIRED:
                return "Proxy Authentication required";
            case REQUEST_TIMEOUT:
                return "Request Time-out";
            case CONFLICT:
                return "Conflict";
            case GONE:
                return "Gone";
            case LENGTH_REQUIRED:
                return "Lenght Required";
            case PRECONDITION_FAILED:
                return "Precondition Failed";
            case REQUEST_TOO_LARGE:
                return "Request Entity Too Large";
            case URI_TOO_LARGE:
                return "Request-URI Too Large";
            case UNSUPPORTED_MEDIA:
                return "Unsupported Media Type";
            case SERVER_ERROR:
                return "Internal Server Error";
            case NOT_IMPLEMENTED:
                return "Not Implemented";
            case BAD_GATEWAY:
                return "Bad Gateway";
            case UNAVAILABLE:
                return "Service Unavailable";
            case GATEWAY_TIMEOUT:
                return "Gateway Time-out";
            case VERSION_NOT_SUPPORTED:
                return "HTTP Version not supported";
            default:
                return "No Reason Phrase";
        }
    }
}
