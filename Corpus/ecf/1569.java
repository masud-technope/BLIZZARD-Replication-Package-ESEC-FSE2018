/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.client;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.rest.IRestCall;
import org.eclipse.ecf.remoteservice.rest.RestException;

/**
 * This class represents a REST service from the client side of view. So a
 * RESTful web service can be accessed via the methods provided by this class.
 * Mostly the methods are inherited from {@link IRemoteService}.
 */
public class RestClientService extends AbstractRestClientService {

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final int socketTimeout = Integer.valueOf(System.getProperty("org.eclipse.ecf.remoteservice.rest.RestClientService.socketTimeout", "-1")).intValue();

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final int connectRequestTimeout = Integer.valueOf(System.getProperty("org.eclipse.ecf.remoteservice.rest.RestClientService.connectRequestTimeout", "-1")).intValue();

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final int connectTimeout = Integer.valueOf(System.getProperty("org.eclipse.ecf.remoteservice.rest.RestClientService.connectTimeout", "-1")).intValue();

    protected static final int DEFAULT_RESPONSE_BUFFER_SIZE = 1024;

    //$NON-NLS-1$
    protected static final String DEFAULT_HTTP_CONTENT_CHARSET = "UTF-8";

    protected HttpClient httpClient;

    protected int responseBufferSize = DEFAULT_RESPONSE_BUFFER_SIZE;

    public  RestClientService(RestClientContainer container, RemoteServiceClientRegistration registration) {
        super(container, registration);
        this.httpClient = createHttpClient();
    }

    protected HttpClient createHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private boolean isResponseOk(HttpResponse response) {
        int isOkCode = response.getStatusLine().getStatusCode() - 200;
        return (isOkCode >= 0 && isOkCode < 100);
    }

    protected HttpGet createGetMethod(String uri) {
        return new HttpGet(uri);
    }

    protected HttpPost createPostMethod(String uri) {
        return new HttpPost(uri);
    }

    protected HttpPut createPutMethod(String uri) {
        return new HttpPut(uri);
    }

    protected HttpDelete createDeleteMethod(String uri) {
        return new HttpDelete(uri);
    }

    /**
	 * @since 2.6
	 */
    protected HttpPatch createPatchMethod(String uri) {
        return new HttpPatch(uri);
    }

    protected HttpRequestBase createAndPrepareHttpMethod(UriRequest request) {
        HttpRequestBase httpMethod = null;
        String uri = request.getUri();
        final IRemoteCallable callable = request.getRemoteCallable();
        IRemoteCallableRequestType requestType = (callable == null) ? new HttpGetRequestType() : callable.getRequestType();
        if (requestType instanceof HttpGetRequestType)
            httpMethod = createGetMethod(uri);
        else if (requestType instanceof HttpPatchRequestType)
            httpMethod = createPatchMethod(uri);
        else if (requestType instanceof HttpPostRequestType)
            httpMethod = createPostMethod(uri);
        else if (requestType instanceof HttpPutRequestType)
            httpMethod = createPutMethod(uri);
        else if (requestType instanceof HttpDeleteRequestType)
            httpMethod = createDeleteMethod(uri);
        // all prepare HttpMethod
        prepareHttpMethod(httpMethod, request.getRemoteCall(), callable);
        return httpMethod;
    }

    /**
	 * Calls the Rest service with given URL of IRestCall. The returned value is
	 * the response body as an InputStream.
	 * 
	 * @param call
	 *            The remote call to make.  Must not be <code>null</code>.
	 * @param callable
	 *            The callable with default parameters to use to make the call.
	 * @return The InputStream of the response body or <code>null</code> if an
	 *         error occurs.
	 */
    protected Object invokeRemoteCall(final IRemoteCall call, final IRemoteCallable callable) throws ECFException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        trace("invokeRemoteCall", "call=" + call + ";callable=" + callable);
        String endpointUri = prepareEndpointAddress(call, callable);
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("invokeRemoteCall", "prepared endpoint=" + endpointUri);
        UriRequest urirequest = createUriRequest(endpointUri, call, callable);
        // If the request
        HttpRequestBase httpMethod = (urirequest == null) ? createAndPrepareHttpMethod(endpointUri, call, callable) : createAndPrepareHttpMethod(urirequest);
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("invokeRemoteCall", "executing httpMethod" + httpMethod);
        // execute method
        byte[] responseBody = null;
        int responseCode = 500;
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpMethod);
            //$NON-NLS-1$ //$NON-NLS-2$
            trace("invokeRemoteCall", "httpMethod executed. response=" + response);
            responseCode = response.getStatusLine().getStatusCode();
            if (isResponseOk(response)) {
                // Get responseBody as String
                responseBody = getResponseAsBytes(response);
            } else {
                // If this method returns true, we should retrieve the response body
                if (retrieveErrorResponseBody(response)) {
                    responseBody = getResponseAsBytes(response);
                }
                // Now pass to the exception handler
                //$NON-NLS-1$ //$NON-NLS-2$
                handleException("Http response not OK.  httpMethod=" + httpMethod + " responseCode=" + new Integer(responseCode), null, responseCode, responseBody);
            }
        } catch (IOException e) {
            handleException("RestClientService transport IOException", e, responseCode);
        }
        Object result = null;
        try {
            Map responseHeaders = convertResponseHeaders(response.getAllHeaders());
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            trace("processResponse", "httpMethod=" + httpMethod + ";call=" + call + ";callable=" + callable + ";responseHeaders=" + responseHeaders + ";responseBody=" + responseBody);
            result = processResponse(endpointUri, call, callable, responseHeaders, responseBody);
        } catch (NotSerializableException e) {
            handleException("Exception deserializing response.  httpMethod=" + httpMethod + " responseCode=" + new Integer(responseCode), e, responseCode);
        }
        return result;
    }

    protected boolean retrieveErrorResponseBody(HttpResponse response) {
        // XXX this needs to be defined differently for 
        return false;
    }

    protected byte[] getResponseAsBytes(HttpResponse response) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        response.getEntity().writeTo(os);
        return os.toByteArray();
    }

    /*
	 * @deprecated
	 */
    protected void setupTimeouts(HttpClient httpClient, IRemoteCall call, IRemoteCallable callable) {
    // No longer used
    }

    private Map convertResponseHeaders(Header[] headers) {
        Map result = new HashMap();
        if (headers == null)
            return result;
        for (int i = 0; i < headers.length; i++) {
            String name = headers[i].getName();
            String value = headers[i].getValue();
            result.put(name, value);
        }
        return result;
    }

    protected void addRequestHeaders(AbstractHttpMessage httpMethod, IRemoteCall call, IRemoteCallable callable) {
        // Add request headers from the callable
        Map requestHeaders = (callable.getRequestType() instanceof AbstractRequestType) ? ((AbstractRequestType) callable.getRequestType()).getDefaultRequestHeaders() : new HashMap();
        if (requestHeaders == null)
            requestHeaders = new HashMap();
        if (call instanceof IRestCall) {
            Map callHeaders = ((IRestCall) call).getRequestHeaders();
            if (callHeaders != null)
                requestHeaders.putAll(requestHeaders);
        }
        Set keySet = requestHeaders.keySet();
        Object[] headers = keySet.toArray();
        for (int i = 0; i < headers.length; i++) {
            String key = (String) headers[i];
            String value = (String) requestHeaders.get(key);
            httpMethod.addHeader(key, value);
        }
    }

    protected HttpRequestBase createAndPrepareHttpMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws RestException {
        HttpRequestBase httpMethod = null;
        IRemoteCallableRequestType requestType = callable.getRequestType();
        if (requestType == null)
            //$NON-NLS-1$
            throw new RestException("Request type for call cannot be null");
        try {
            if (requestType instanceof HttpGetRequestType) {
                httpMethod = prepareGetMethod(uri, call, callable);
            } else if (requestType instanceof HttpPatchRequestType) {
                httpMethod = preparePatchMethod(uri, call, callable);
            } else if (requestType instanceof HttpPostRequestType) {
                httpMethod = preparePostMethod(uri, call, callable);
            } else if (requestType instanceof HttpPutRequestType) {
                httpMethod = preparePutMethod(uri, call, callable);
            } else if (requestType instanceof HttpDeleteRequestType) {
                httpMethod = prepareDeleteMethod(uri, call, callable);
            } else {
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new RestException("HTTP method " + requestType + " not supported");
            }
        } catch (NotSerializableException e) {
            String message = "Could not serialize parameters for uri=" + uri + " call=" + call + " callable=" + callable;
            logException(message, e);
            throw new RestException(message);
        } catch (UnsupportedEncodingException e) {
            String message = "Could not serialize parameters for uri=" + uri + " call=" + call + " callable=" + callable;
            logException(message, e);
            throw new RestException(message);
        }
        prepareHttpMethod(httpMethod, call, callable);
        return httpMethod;
    }

    protected void prepareHttpMethod(HttpRequestBase httpMethod, IRemoteCall call, IRemoteCallable callable) {
        // add additional request headers
        addRequestHeaders(httpMethod, call, callable);
        // handle authentication
        setupAuthenticaton(httpClient, httpMethod);
        // setup http method config (redirects, timeouts, etc)
        setupHttpMethod(httpMethod, call, callable);
    }

    protected void setupHttpMethod(HttpRequestBase httpMethod, IRemoteCall call, IRemoteCallable callable) {
        RequestConfig defaultRequestConfig = httpMethod.getConfig();
        RequestConfig.Builder updatedRequestConfigBuilder = (defaultRequestConfig == null) ? RequestConfig.custom() : RequestConfig.copy(defaultRequestConfig);
        // setup to allow regular and circular redirects
        updatedRequestConfigBuilder.setCircularRedirectsAllowed(true);
        updatedRequestConfigBuilder.setRedirectsEnabled(true);
        int sTimeout = socketTimeout;
        int scTimeout = connectTimeout;
        int scrTimeout = connectRequestTimeout;
        long callTimeout = call.getTimeout();
        if (callTimeout == IRemoteCall.DEFAULT_TIMEOUT)
            callTimeout = callable.getDefaultTimeout();
        if (callTimeout != IRemoteCall.DEFAULT_TIMEOUT) {
            sTimeout = scTimeout = scrTimeout = new Long(callTimeout).intValue();
        }
        updatedRequestConfigBuilder.setSocketTimeout(sTimeout);
        updatedRequestConfigBuilder.setConnectTimeout(scTimeout);
        updatedRequestConfigBuilder.setConnectionRequestTimeout(scrTimeout);
        httpMethod.setConfig(updatedRequestConfigBuilder.build());
    }

    /**
	 * @throws RestException  
	 */
    protected HttpRequestBase prepareDeleteMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws RestException {
        return new HttpDelete(uri);
    }

    protected HttpRequestBase preparePutMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException, UnsupportedEncodingException {
        HttpPut result = new HttpPut(uri);
        HttpPutRequestType putRequestType = (HttpPutRequestType) callable.getRequestType();
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (putRequestType.useRequestEntity()) {
            if (defaultParameters != null && defaultParameters.length > 0 && parameters != null && parameters.length > 0) {
                HttpEntity requestEntity = putRequestType.generateRequestEntity(uri, call, callable, defaultParameters[0], parameters[0]);
                result.setEntity(requestEntity);
            }
        } else {
            NameValuePair[] params = toNameValuePairs(uri, call, callable);
            if (params != null) {
                result.setEntity(getUrlEncodedFormEntity(Arrays.asList(params), putRequestType));
            }
        }
        return result;
    }

    /**
	 * @throws UnsupportedEncodingException 
	 * @throws ECFException  
	 * @since 2.6
	 */
    protected HttpRequestBase preparePatchMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException, UnsupportedEncodingException {
        HttpPatch result = new HttpPatch(uri);
        HttpPostRequestType postRequestType = (HttpPostRequestType) callable.getRequestType();
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (postRequestType.useRequestEntity()) {
            if (defaultParameters != null && defaultParameters.length > 0 && parameters != null && parameters.length > 0) {
                HttpEntity requestEntity = postRequestType.generateRequestEntity(uri, call, callable, defaultParameters[0], parameters[0]);
                result.setEntity(requestEntity);
            }
        } else {
            NameValuePair[] params = toNameValuePairs(uri, call, callable);
            if (params != null) {
                result.setEntity(getUrlEncodedFormEntity(Arrays.asList(params), postRequestType));
            }
        }
        return result;
    }

    /**
	 * @throws UnsupportedEncodingException 
	 * @throws ECFException  
	 */
    protected HttpRequestBase preparePostMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException, UnsupportedEncodingException {
        HttpPost result = new HttpPost(uri);
        HttpPostRequestType postRequestType = (HttpPostRequestType) callable.getRequestType();
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (postRequestType.useRequestEntity()) {
            if (defaultParameters != null && defaultParameters.length > 0 && parameters != null && parameters.length > 0) {
                HttpEntity requestEntity = postRequestType.generateRequestEntity(uri, call, callable, defaultParameters[0], parameters[0]);
                result.setEntity(requestEntity);
            }
        } else {
            NameValuePair[] params = toNameValuePairs(uri, call, callable);
            if (params != null) {
                result.setEntity(getUrlEncodedFormEntity(Arrays.asList(params), postRequestType));
            }
        }
        return result;
    }

    /**
	 * @throws NotSerializableException 
	 * @throws ECFException  
	 */
    protected HttpRequestBase prepareGetMethod(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        NameValuePair[] params = toNameValuePairs(uri, call, callable);
        URI httpURI = null;
        try {
            URIBuilder builder = new URIBuilder(uri);
            if (params != null)
                for (int i = 0; i < params.length; i++) builder.addParameter(params[i].getName(), params[i].getValue());
            httpURI = builder.build();
        } catch (URISyntaxException e1) {
            throw new NotSerializableException("uri=" + uri + " does not have proper URI syntax");
        }
        return new HttpGet(httpURI);
    }

    protected UrlEncodedFormEntity getUrlEncodedFormEntity(List list, AbstractEntityRequestType postRequestType) throws UnsupportedEncodingException {
        if (postRequestType.defaultCharset != null) {
            return new UrlEncodedFormEntity(list, postRequestType.defaultCharset);
        }
        return new UrlEncodedFormEntity(list);
    }

    protected NameValuePair[] toNameValuePairs(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        IRemoteCallParameter[] restParameters = prepareParameters(uri, call, callable);
        List nameValueList = new ArrayList();
        if (restParameters != null) {
            for (int i = 0; i < restParameters.length; i++) {
                String parameterValue = null;
                Object o = restParameters[i].getValue();
                if (o instanceof String) {
                    parameterValue = (String) o;
                } else if (o != null) {
                    parameterValue = o.toString();
                }
                if (parameterValue != null) {
                    nameValueList.add(new BasicNameValuePair(restParameters[i].getName(), parameterValue));
                }
            }
        }
        return (NameValuePair[]) nameValueList.toArray(new NameValuePair[nameValueList.size()]);
    }

    protected void setupAuthenticaton(HttpClient httpClient, HttpRequestBase method) {
        IConnectContext connectContext = container.getConnectContextForAuthentication();
        if (connectContext != null) {
            //$NON-NLS-1$
            NameCallback nameCallback = new NameCallback("");
            ObjectCallback passwordCallback = new ObjectCallback();
            Callback[] callbacks = new Callback[] { nameCallback, passwordCallback };
            CallbackHandler callbackHandler = connectContext.getCallbackHandler();
            if (callbackHandler == null)
                return;
            try {
                callbackHandler.handle(callbacks);
                String username = nameCallback.getName();
                String password = (String) passwordCallback.getObject();
                Credentials credentials = new UsernamePasswordCredentials(username, password);
                method.addHeader(new BasicScheme().authenticate(credentials, method, new BasicHttpContext()));
            } catch (IOException e) {
                logException("IOException setting credentials for rest httpclient", e);
            } catch (UnsupportedCallbackException e) {
                logException("UnsupportedCallbackException setting credentials for rest httpclient", e);
            } catch (AuthenticationException e) {
                logException("AuthenticationException setting credentials for rest httpclient", e);
            }
        }
    }
}
