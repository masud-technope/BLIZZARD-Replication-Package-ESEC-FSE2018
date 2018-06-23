package org.eclipse.ecf.remoteservice.rest.client;

import java.util.Map;

/**
 * @since 2.6
 */
public class HttpPatchRequestType extends HttpPostRequestType {

    public  HttpPatchRequestType() {
        super();
    }

    public  HttpPatchRequestType(int requestEntityType, Map defaultRequestHeaders) {
        super(requestEntityType, defaultRequestHeaders);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, Map defaultRequestHeaders) {
        super(requestEntityType, defaultContentType, defaultContentLength, defaultRequestHeaders);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, String defaultCharset, Map defaultRequestHeaders) {
        super(requestEntityType, defaultContentType, defaultContentLength, defaultCharset, defaultRequestHeaders);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType, long defaultContentLength, String defaultCharset) {
        super(requestEntityType, defaultContentType, defaultContentLength, defaultCharset);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType, long defaultContentLength) {
        super(requestEntityType, defaultContentType, defaultContentLength);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType, Map defaultRequestHeaders) {
        super(requestEntityType, defaultContentType, defaultRequestHeaders);
    }

    public  HttpPatchRequestType(int requestEntityType, String defaultContentType) {
        super(requestEntityType, defaultContentType);
    }

    public  HttpPatchRequestType(int requestEntityType) {
        super(requestEntityType);
    }

    public  HttpPatchRequestType(Map defaultRequestHeaders) {
        super(defaultRequestHeaders);
    }
}
