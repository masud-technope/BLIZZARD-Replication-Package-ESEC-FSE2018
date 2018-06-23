package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

/**
 * @since 3.3
 */
public class EndpointNameValueNode extends EndpointPropertyNode {

    private final String propertyValue;

    public  EndpointNameValueNode(String propertyName, String propertyValue) {
        super(propertyName);
        setPropertyAlias(propertyName);
        this.propertyValue = propertyValue;
    }

    @Override
    public Object getPropertyValue() {
        return this.propertyValue;
    }
}
