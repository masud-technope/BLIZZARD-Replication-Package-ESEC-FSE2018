//Copyright 2003-2005 Arthur van Hoff, Rick Blair
package javax.jmdns;

import java.util.EventListener;

/**
 * Listener for service types.
 *
 * @version %I%, %G%
 * @author	Arthur van Hoff, Werner Randelshofer
 */
public interface ServiceTypeListener extends EventListener {

    /**
     * A new service type was discovered.
     *
     * @param event The service event providing the fully qualified type of
     *              the service.
     */
    void serviceTypeAdded(ServiceEvent event);
}
