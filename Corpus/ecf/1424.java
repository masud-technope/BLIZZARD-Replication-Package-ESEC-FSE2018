package org.eclipse.ecf.remoteservice.events;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.equinox.concurrent.future.IFuture;

/**
 * @since 3.0
 */
public interface IRemoteServiceReferenceRetrievedEvent extends IRemoteServiceEvent {

    public ID[] getIDFilter();

    public String getFilter();

    public IFuture getAsyncResult();
}
