/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Thomas Joiner - HttpClient 4 implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.httpclient4;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.core.util.ProxyAddress;
import org.eclipse.ecf.core.util.Trace;

public abstract class HttpClientProxyCredentialProvider implements CredentialsProvider {

    protected abstract Proxy getECFProxy();

    protected abstract Credentials getNTLMCredentials(Proxy proxy);

    private Map cachedCredentials;

    public  HttpClientProxyCredentialProvider() {
        cachedCredentials = new ConcurrentHashMap<AuthScope, Credentials>();
    }

    public void setCredentials(AuthScope authscope, Credentials credentials) {
        if (authscope == null)
            //$NON-NLS-1$
            throw new IllegalArgumentException("Authentication scope may not be null");
        this.cachedCredentials.put(authscope, credentials);
    }

    public Credentials getCredentials(AuthScope authscope) {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, DebugOptions.METHODS_ENTERING, HttpClientProxyCredentialProvider.class, "getCredentials " + authscope);
        // First check to see whether given authscope matches any authscope 
        // already cached.
        Credentials result = matchCredentials(this.cachedCredentials, authscope);
        // If we have a match, return credentials
        if (result != null)
            return result;
        // If we don't have a match, first get ECF proxy, if any
        Proxy proxy = getECFProxy();
        if (proxy == null)
            return null;
        // Make sure that authscope and proxy host and port match
        if (!matchAuthScopeAndProxy(authscope, proxy))
            return null;
        // Then match scheme, and get credentials from proxy (if it's scheme we know about)
        Credentials credentials = null;
        if (//$NON-NLS-1$
        "ntlm".equalsIgnoreCase(authscope.getScheme())) {
            credentials = getNTLMCredentials(proxy);
        } else if (//$NON-NLS-1$
        "basic".equalsIgnoreCase(authscope.getScheme()) || //$NON-NLS-1$
        "digest".equalsIgnoreCase(authscope.getScheme())) {
            final String proxyUsername = proxy.getUsername();
            final String proxyPassword = proxy.getPassword();
            // If credentials present for proxy then we're done
            if (proxyUsername != null) {
                credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
            }
        } else if (//$NON-NLS-1$
        "negotiate".equalsIgnoreCase(authscope.getScheme())) {
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "SPNEGO is not supported, if you can contribute support, please do so.");
        } else {
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "Unrecognized authentication scheme.");
        }
        // Put found credentials in cache for next time
        if (credentials != null)
            cachedCredentials.put(authscope, credentials);
        return credentials;
    }

    private boolean matchAuthScopeAndProxy(AuthScope authscope, Proxy proxy) {
        ProxyAddress proxyAddress = proxy.getAddress();
        return (authscope.getHost().equals(proxyAddress.getHostName()) && (authscope.getPort() == proxyAddress.getPort()));
    }

    private static Credentials matchCredentials(final Map<AuthScope, Credentials> map, final AuthScope authscope) {
        // see if we get a direct hit
        Credentials creds = map.get(authscope);
        if (creds == null) {
            // Nope.
            // Do a full scan
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (AuthScope current : map.keySet()) {
                int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }

    public void clear() {
        this.cachedCredentials.clear();
    }
}
