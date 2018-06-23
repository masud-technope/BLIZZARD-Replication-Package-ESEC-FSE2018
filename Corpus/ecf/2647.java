/****************************************************************************
 * Copyright (c) 2008, 2010 Composent, Inc., IBM and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Henrich Kraemer - bug 263869, testHttpsReceiveFile fails using HTTP proxy
 *    Thomas Joiner - HttpClient 4 implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.httpclient4;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.NameCallback;
import org.eclipse.ecf.core.security.ObjectCallback;
import org.eclipse.ecf.core.security.UnsupportedCallbackException;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.core.util.ProxyAddress;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.filetransfer.BrowseFileTransferException;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemListener;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemRequest;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.Activator;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.ConnectingSocketMonitor;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.DebugOptions;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.HttpClientProxyCredentialProvider;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.Messages;
import org.eclipse.ecf.provider.filetransfer.browse.AbstractFileSystemBrowser;
import org.eclipse.ecf.provider.filetransfer.browse.URLRemoteFile;
import org.eclipse.ecf.provider.filetransfer.events.socket.SocketEventSource;
import org.eclipse.ecf.provider.filetransfer.util.JREProxyHelper;
import org.eclipse.ecf.provider.filetransfer.util.ProxySetupHelper;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class HttpClientFileSystemBrowser extends AbstractFileSystemBrowser {

    //$NON-NLS-1$
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    // changing to 2 minutes (120000) as per bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=266246
    // 10/26/2009:  Added being able to set with system property with name org.eclipse.ecf.provider.filetransfer.httpclient4.browse.connectTimeout
    // for https://bugs.eclipse.org/bugs/show_bug.cgi?id=292995
    protected static final int DEFAULT_CONNECTION_TIMEOUT = HttpClientOptions.BROWSE_DEFAULT_CONNECTION_TIMEOUT;

    //$NON-NLS-1$
    private static final String USERNAME_PREFIX = "Username:";

    private JREProxyHelper proxyHelper = null;

    private ConnectingSocketMonitor connectingSockets;

    protected String username = null;

    protected String password = null;

    protected DefaultHttpClient httpClient = null;

    protected volatile HttpHead headMethod;

    /**
	 * This is the response returned by {@link HttpClient} when it executes
	 * {@link #headMethod}.
	 * @since 5.0
	 */
    protected volatile HttpResponse httpResponse;

    /**
	 * This is the context used to retain information about the request that
	 * the {@link HttpClient} gathers during the request.
	 * @since 5.0
	 */
    protected volatile HttpContext httpContext;

    /**
	 * @param httpClient http client
	 * @param directoryOrFileID directory or file id
	 * @param listener listener
	 * @param directoryOrFileURL directory or file id
	 * @param connectContext connect context
	 * @param proxy proxy
	 * @since 5.0
	 */
    public  HttpClientFileSystemBrowser(DefaultHttpClient httpClient, IFileID directoryOrFileID, IRemoteFileSystemListener listener, URL directoryOrFileURL, IConnectContext connectContext, Proxy proxy) {
        super(directoryOrFileID, listener, directoryOrFileURL, connectContext, proxy);
        Assert.isNotNull(httpClient);
        this.httpClient = httpClient;
        this.httpClient.setCredentialsProvider(new HttpClientProxyCredentialProvider() {

            protected Proxy getECFProxy() {
                return getProxy();
            }

            protected Credentials getNTLMCredentials(Proxy lp) {
                if (hasForceNTLMProxyOption())
                    return HttpClientRetrieveFileTransfer.createNTLMCredentials(lp);
                return null;
            }
        });
        this.proxyHelper = new JREProxyHelper();
        this.connectingSockets = new ConnectingSocketMonitor(1);
        prepareAuth();
    }

    private void prepareAuth() {
        // SPNEGO is not supported, so remove it from the list
        List authpref = new ArrayList(3);
        authpref.add(AuthPolicy.NTLM);
        authpref.add(AuthPolicy.DIGEST);
        authpref.add(AuthPolicy.BASIC);
        httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
        httpClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
    }

    class HttpClientRemoteFileSystemRequest extends RemoteFileSystemRequest {

        protected SocketEventSource socketEventSource;

         HttpClientRemoteFileSystemRequest() {
            this.socketEventSource = new SocketEventSource() {

                public Object getAdapter(Class adapter) {
                    if (adapter == null) {
                        return null;
                    }
                    if (adapter.isInstance(this)) {
                        return this;
                    }
                    if (adapter.isInstance(HttpClientRemoteFileSystemRequest.this)) {
                        return HttpClientRemoteFileSystemRequest.this;
                    }
                    return null;
                }
            };
        }

        public Object getAdapter(Class adapter) {
            if (adapter == null) {
                return null;
            }
            return socketEventSource.getAdapter(adapter);
        }

        public void cancel() {
            HttpClientFileSystemBrowser.this.cancel();
        }
    }

    protected IRemoteFileSystemRequest createRemoteFileSystemRequest() {
        return new HttpClientRemoteFileSystemRequest();
    }

    protected void cancel() {
        if (isCanceled()) {
            // break job cancel recursion
            return;
        }
        setCanceled(getException());
        super.cancel();
        if (headMethod != null) {
            if (!headMethod.isAborted()) {
                headMethod.abort();
            }
        }
        if (connectingSockets != null) {
            // Change for preventing CME from bug
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=430704
            connectingSockets.closeSockets();
        }
    }

    protected boolean hasForceNTLMProxyOption() {
        return (System.getProperties().getProperty(HttpClientOptions.FORCE_NTLM_PROP) != null);
    }

    protected void setupProxies() {
        // If it's been set directly (via ECF API) then this overrides platform settings
        if (proxy == null) {
            try {
                // give SOCKS priority see https://bugs.eclipse.org/bugs/show_bug.cgi?id=295030#c61
                proxy = ProxySetupHelper.getSocksProxy(directoryOrFile);
                if (proxy == null) {
                    proxy = ProxySetupHelper.getProxy(directoryOrFile.toExternalForm());
                }
            } catch (NoClassDefFoundError e) {
                Activator.logNoProxyWarning(e);
            }
        }
        if (proxy != null)
            setupProxy(proxy);
    }

    protected void cleanUp() {
        clearProxy();
        super.cleanUp();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.filetransfer.browse.AbstractFileSystemBrowser#runRequest()
	 */
    protected void runRequest() throws Exception {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, DebugOptions.METHODS_ENTERING, this.getClass(), "runRequest");
        setupProxies();
        // set timeout
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        String urlString = directoryOrFile.toString();
        // setup authentication
        setupAuthentication(urlString);
        headMethod = new HttpHead(urlString);
        //$NON-NLS-1$
        int maxAge = Integer.getInteger("org.eclipse.ecf.http.cache.max-age", 0).intValue();
        // fix the fix for bug 249990 with bug 410813
        if (maxAge == 0) {
            //$NON-NLS-1$//$NON-NLS-2$
            headMethod.addHeader("Cache-Control", "max-age=0");
        } else if (maxAge > 0) {
            //$NON-NLS-1$//$NON-NLS-2$
            headMethod.addHeader("Cache-Control", "max-age=" + maxAge);
        }
        long lastModified = 0;
        long fileLength = -1;
        connectingSockets.clear();
        int code = -1;
        try {
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "browse=" + urlString);
            httpContext = new BasicHttpContext();
            httpResponse = httpClient.execute(headMethod, httpContext);
            code = httpResponse.getStatusLine().getStatusCode();
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "browse resp=" + code);
            // Check for NTLM proxy in response headers 
            // This check is to deal with bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=252002
            boolean ntlmProxyFound = NTLMProxyDetector.detectNTLMProxy(httpContext);
            if (ntlmProxyFound && !hasForceNTLMProxyOption())
                throw new //$NON-NLS-1$
                BrowseFileTransferException(//$NON-NLS-1$
                "HttpClient Provider is not configured to support NTLM proxy authentication.", //$NON-NLS-1$
                HttpClientOptions.NTLM_PROXY_RESPONSE_CODE);
            if (NTLMProxyDetector.detectSPNEGOProxy(httpContext))
                throw new //$NON-NLS-1$
                BrowseFileTransferException(//$NON-NLS-1$
                "HttpClient Provider does not support the use of SPNEGO proxy authentication.");
            if (code == HttpURLConnection.HTTP_OK) {
                Header contentLength = httpResponse.getLastHeader(CONTENT_LENGTH_HEADER);
                if (contentLength != null) {
                    fileLength = Integer.parseInt(contentLength.getValue());
                }
                lastModified = getLastModifiedTimeFromHeader();
            } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new //$NON-NLS-1$
                BrowseFileTransferException(//$NON-NLS-1$
                NLS.bind("File not found: {0}", urlString), //$NON-NLS-1$
                code);
            } else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new BrowseFileTransferException(Messages.HttpClientRetrieveFileTransfer_Unauthorized, code);
            } else if (code == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new //$NON-NLS-1$
                BrowseFileTransferException(//$NON-NLS-1$
                "Forbidden", //$NON-NLS-1$
                code);
            } else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
                throw new BrowseFileTransferException(Messages.HttpClientRetrieveFileTransfer_Proxy_Auth_Required, code);
            } else {
                throw new BrowseFileTransferException(NLS.bind(Messages.HttpClientRetrieveFileTransfer_ERROR_GENERAL_RESPONSE_CODE, new Integer(code)), code);
            }
            remoteFiles = new IRemoteFile[1];
            remoteFiles[0] = new URLRemoteFile(lastModified, fileLength, fileID);
        } catch (Exception e) {
            Trace.throwing(Activator.PLUGIN_ID, DebugOptions.EXCEPTIONS_THROWING, this.getClass(), "runRequest", e);
            BrowseFileTransferException ex = (BrowseFileTransferException) ((e instanceof BrowseFileTransferException) ? e : new BrowseFileTransferException(NLS.bind(Messages.HttpClientRetrieveFileTransfer_EXCEPTION_COULD_NOT_CONNECT, urlString), e, code));
            throw ex;
        }
    }

    private long getLastModifiedTimeFromHeader() throws IOException {
        //$NON-NLS-1$
        Header lastModifiedHeader = httpResponse.getLastHeader("Last-Modified");
        if (lastModifiedHeader == null)
            return 0L;
        String lastModifiedString = lastModifiedHeader.getValue();
        long lastModified = 0;
        if (lastModifiedString != null) {
            try {
                lastModified = DateUtils.parseDate(lastModifiedString).getTime();
            } catch (Exception e) {
                throw new IOException(Messages.HttpClientRetrieveFileTransfer_EXCEPITION_INVALID_LAST_MODIFIED_FROM_SERVER);
            }
        }
        return lastModified;
    }

    Proxy getProxy() {
        return proxy;
    }

    /**
	 * Retrieves the credentials for requesting the file.
	 * @return the {@link Credentials} necessary to retrieve the file
	 * @throws UnsupportedCallbackException if the callback fails
	 * @throws IOException if IO fails
	 * @since 5.0
	 */
    protected Credentials getFileRequestCredentials() throws UnsupportedCallbackException, IOException {
        if (connectContext == null)
            return null;
        final CallbackHandler callbackHandler = connectContext.getCallbackHandler();
        if (callbackHandler == null)
            return null;
        final NameCallback usernameCallback = new NameCallback(USERNAME_PREFIX);
        final ObjectCallback passwordCallback = new ObjectCallback();
        callbackHandler.handle(new Callback[] { usernameCallback, passwordCallback });
        username = usernameCallback.getName();
        password = (String) passwordCallback.getObject();
        return new UsernamePasswordCredentials(username, password);
    }

    protected void setupAuthentication(String urlString) throws UnsupportedCallbackException, IOException {
        Credentials credentials = null;
        if (username == null) {
            credentials = getFileRequestCredentials();
        }
        if (credentials != null && username != null) {
            final AuthScope authScope = new AuthScope(HttpClientRetrieveFileTransfer.getHostFromURL(urlString), HttpClientRetrieveFileTransfer.getPortFromURL(urlString), AuthScope.ANY_REALM);
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "browse credentials=" + credentials);
            httpClient.getCredentialsProvider().setCredentials(authScope, credentials);
        }
    }

    protected void setupProxy(Proxy proxy) {
        if (proxy.getType().equals(Proxy.Type.HTTP)) {
            final ProxyAddress address = proxy.getAddress();
            ConnRouteParams.setDefaultProxy(httpClient.getParams(), new HttpHost(address.getHostName(), address.getPort()));
        } else if (proxy.getType().equals(Proxy.Type.SOCKS)) {
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "brows socksproxy=" + proxy.getAddress());
            proxyHelper.setupProxy(proxy);
        }
    }

    /**
	 * This method will clear out the proxy information (so that if this is
	 * reused for a request without a proxy, it will work correctly).
	 * @since 5.0
	 */
    protected void clearProxy() {
        ConnRouteParams.setDefaultProxy(httpClient.getParams(), null);
    }
}
