/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.client;

import java.io.*;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.entity.*;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.IRemoteCallParameter;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;

public abstract class AbstractEntityRequestType extends AbstractRequestType {

    public static final int NO_REQUEST_ENTITY = -1;

    public static final int INPUT_STREAM_REQUEST_ENTITY = 0;

    public static final int STRING_REQUEST_ENTITY = 1;

    public static final int BYTEARRAY_REQUEST_ENTITY = 2;

    public static final int FILE_REQUEST_ENTITY = 3;

    public static final long CONTENT_LENGTH_AUTO = -2;

    //$NON-NLS-1$
    public static final String CHARSET_PARAM_NAME = "charset";

    //$NON-NLS-1$
    public static final String CONTENT_TYPE_PARAM_NAME = "contentType";

    //$NON-NLS-1$
    public static final String CONTENT_LENGTH_PARAM_NAME = "contentLength";

    protected int requestEntityType = NO_REQUEST_ENTITY;

    protected long defaultContentLength = AbstractEntityRequestType.CONTENT_LENGTH_AUTO;

    protected String defaultContentType = null;

    protected String defaultCharset = null;

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, String defaultCharset, Map defaultRequestHeaders) {
        super(defaultRequestHeaders);
        this.requestEntityType = requestEntityType;
        this.defaultContentType = defaultContentType;
        this.defaultContentLength = defaultContentLength;
        this.defaultCharset = defaultCharset;
    }

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, String defaultCharset) {
        this(requestEntityType, defaultContentType, defaultContentLength, defaultCharset, null);
    }

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, Map defaultRequestHeaders) {
        this(requestEntityType, defaultContentType, defaultContentLength, null, defaultRequestHeaders);
    }

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType, long defaultContentLength) {
        this(requestEntityType, defaultContentType, defaultContentLength, (String) null);
    }

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType, Map defaultRequestHeaders) {
        this(requestEntityType, defaultContentType, AbstractEntityRequestType.CONTENT_LENGTH_AUTO, null, defaultRequestHeaders);
    }

    public  AbstractEntityRequestType(int requestEntityType, String defaultContentType) {
        this(requestEntityType, defaultContentType, null);
    }

    public  AbstractEntityRequestType(int requestEntityType, Map defaultRequestHeaders) {
        this(requestEntityType, null, defaultRequestHeaders);
    }

    public  AbstractEntityRequestType(Map defaultRequestHeaders) {
        this(NO_REQUEST_ENTITY, defaultRequestHeaders);
    }

    public  AbstractEntityRequestType() {
        this(null);
    }

    public boolean useRequestEntity() {
        return requestEntityType > -1;
    }

    public HttpEntity generateRequestEntity(String uri, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault, Object paramToSerialize) throws NotSerializableException {
        if (paramToSerialize instanceof HttpEntity)
            return (HttpEntity) paramToSerialize;
        switch(requestEntityType) {
            case INPUT_STREAM_REQUEST_ENTITY:
                if (paramToSerialize instanceof InputStream) {
                    return new InputStreamEntity((InputStream) paramToSerialize, getContentLength(call, callable, paramDefault));
                }
                throw new //$NON-NLS-1$
                NotSerializableException(//$NON-NLS-1$
                "Cannot generate request entity.  Expecting InputStream and got class=" + paramToSerialize.getClass().getName());
            case STRING_REQUEST_ENTITY:
                if (paramToSerialize instanceof String) {
                    try {
                        StringEntity entity = new StringEntity((String) paramToSerialize, getCharset(call, callable, paramDefault));
                        entity.setContentType(getContentType(call, callable, paramDefault));
                        return entity;
                    } catch (UnsupportedCharsetException e) {
                        throw new NotSerializableException("Could not create request entity from call parameters: " + e.getMessage());
                    }
                }
                throw new //$NON-NLS-1$
                NotSerializableException(//$NON-NLS-1$
                "Cannot generate request entity.  Expecting String and got class=" + paramToSerialize.getClass().getName());
            case BYTEARRAY_REQUEST_ENTITY:
                if (paramToSerialize instanceof byte[]) {
                    return new ByteArrayEntity((byte[]) paramToSerialize);
                }
                throw new //$NON-NLS-1$
                NotSerializableException(//$NON-NLS-1$
                "Cannot generate request entity.  Expecting byte[] and got class=" + paramToSerialize.getClass().getName());
            case FILE_REQUEST_ENTITY:
                if (paramToSerialize instanceof File) {
                    return new FileEntity((File) paramToSerialize, ContentType.create(getContentType(call, callable, paramDefault)));
                }
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new NotSerializableException("Remote call parameter with name=" + paramDefault.getName() + " is incorrect type for creating request entity.");
            default:
                throw new //$NON-NLS-1$
                NotSerializableException(//$NON-NLS-1$
                "Request entity generation not supported for this request type");
        }
    }

    protected String getCharset(IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault) {
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (defaultParameters != null) {
            for (int i = 0; i < defaultParameters.length; i++) {
                if (CHARSET_PARAM_NAME.equals(defaultParameters[i].getName())) {
                    Object o = (parameters != null && parameters.length > i) ? parameters[i] : defaultParameters[i].getValue();
                    if (o instanceof String) {
                        return (String) o;
                    }
                }
            }
        }
        return defaultCharset;
    }

    protected long getContentLength(IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault) {
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (defaultParameters != null) {
            for (int i = 0; i < defaultParameters.length; i++) {
                if (CONTENT_LENGTH_PARAM_NAME.equals(defaultParameters[i].getName())) {
                    Object o = (parameters != null && parameters.length > i) ? parameters[i] : defaultParameters[i].getValue();
                    if (o instanceof Number) {
                        return ((Number) o).longValue();
                    } else if (o instanceof String) {
                        try {
                            return Integer.parseInt((String) o);
                        } catch (NumberFormatException e) {
                            return AbstractEntityRequestType.CONTENT_LENGTH_AUTO;
                        }
                    }
                }
            }
        }
        return defaultContentLength;
    }

    protected String getContentType(IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault) {
        IRemoteCallParameter[] defaultParameters = callable.getDefaultParameters();
        Object[] parameters = call.getParameters();
        if (defaultParameters != null) {
            for (int i = 0; i < defaultParameters.length; i++) {
                if (CONTENT_TYPE_PARAM_NAME.equals(defaultParameters[i].getName())) {
                    Object o = (parameters != null && parameters.length > i) ? parameters[i] : defaultParameters[i].getValue();
                    if (o instanceof String) {
                        return (String) o;
                    }
                }
            }
        }
        return defaultContentType;
    }
}
