package org.eclipse.ecf.remoteservice;

import java.util.Map;
import org.eclipse.ecf.core.ContainerConnectException;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @since 8.9
 */
public interface IRSAConsumerContainerAdapter {

    public IRemoteServiceReference[] importEndpoint(Map<String, Object> endpointDescriptionProperties) throws ContainerConnectException, InvalidSyntaxException;
}
