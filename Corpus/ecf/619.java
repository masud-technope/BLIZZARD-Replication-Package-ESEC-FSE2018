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
import java.net.URI;
import java.net.URL;

/**
 * A HTTP request message
 * 
 */
public class HttpRequest extends HttpMessage {

    private String method;

    private URL url;

    private String version;

    private static final int DEFAULT_PORT = 80;

    /** Creates a new instance of HttpRequest 
	 * @param method 
	 * @param url 
	 * @param version */
    public  HttpRequest(String method, URL url, String version) {
        this.method = method;
        this.version = version;
        setURL(url);
        hasBody = false;
    }

    public  HttpRequest(String method, String urlstring, String version) throws MalformedURLException {
        this(method, new URL(urlstring), version);
    }

    public  HttpRequest(String method, String urlstring) throws MalformedURLException {
        this(method, new URL(urlstring), DEFAULT_VERSION);
    }

    public  HttpRequest(String method, URL url) {
        this(method, url, DEFAULT_VERSION);
    }

    /**
	 * generate a Request Message which will be read from the specified
	 * InputStream
	 * @param in 
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
    public  HttpRequest(InputStream in) throws MalformedURLException, IOException {
        super(in);
    }

    /**
	 * Reads the head of the message (startline and headers) from the
	 * InputStream, and parses the startline for command, url and protocol.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
    public void readHead() throws IOException, MalformedURLException {
        super.readHead();
        String strURI;
        // parse requestline
        final int i = startLine.indexOf(' ');
        final int j = startLine.indexOf(' ', i + 1);
        method = startLine.substring(0, i);
        strURI = startLine.substring(i + 1, j);
        version = startLine.substring(j + 1);
        setURL(URI.create("http://localhost").resolve(strURI).toURL());
        if (method.equals("POST")) {
            hasBody = true;
        } else {
            hasBody = false;
        }
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        if (url.getPort() == -1) {
            return DEFAULT_PORT;
        } else {
            return url.getPort();
        }
    }

    public String getURLString() {
        return url.toString();
    }

    public URL getURL() {
        return url;
    }

    public String getPath() {
        return url.getPath();
    }

    public String getAbsPath() {
        String ref;
        String absPath;
        if (url.getRef() != null) {
            ref = "#" + url.getRef();
        } else {
            ref = "";
        }
        absPath = url.getFile() + ref;
        if (absPath == "/" && method.equalsIgnoreCase("OPTIONS")) {
            absPath = "*";
        }
        return absPath;
    }

    public void setHost(String host) {
    }

    public void setURL(URL url) {
        this.url = url;
        setHeader("Host", url.getHost());
    }

    public void setVersion(String s) {
        version = s;
    }

    public String getStartLine() {
        return method + " " + this.getAbsPath() + " " + version;
    }

    public String getAbsoluteStartLine() {
        return method + " " + this.getURLString() + " " + version;
    }

    /**
	 * write to stream using the default startline (absulute URI)
	 * @param stream 
	 * @throws IOException 
	 */
    public void writeToStream(OutputStream stream) throws IOException {
        super.writeToStream(getAbsoluteStartLine(), stream);
    }
    /*
	 * // Detects browser public boolean isIEBrowser() { if
	 * (containsHeader("user-agent")) { if
	 * (getHeader("user-agent").indexOf("MSIE 4") > -1) { return true; } else
	 * if(getHeader("user-agent").indexOf("MSIE 5") > -1) { return true; } else
	 * if(getHeader("user-agent").indexOf("MSIE 6") > -1) { return true; } }
	 * return false; }
	 * 
	 * public boolean isNNBrowser() { if (containsHeader("user-agent")) { if
	 * (getHeader("user-agent").indexOf("Mozilla/4") > -1) { return true; } else
	 * if(getHeader("user-agent").indexOf("Mozilla/5") > -1) { return true; } }
	 * return false; }
	 */
}
