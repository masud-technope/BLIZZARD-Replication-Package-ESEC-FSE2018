package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.remoteservice.util.RemoteFilterImpl;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @since 8.3
 */
public class RemoteServiceFilterImpl extends RemoteFilterImpl {

    /**
	 * @param createFilter filter as string
	 * @throws InvalidSyntaxException thrown if given String filter cannot be converted
	 * into a valid filter
	 */
    public  RemoteServiceFilterImpl(String createFilter) throws InvalidSyntaxException {
        super(createFilter);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IRemoteFilter#match(org.eclipse.ecf.remoteservice.IRemoteServiceReference)
	 */
    public boolean match(IRemoteServiceReference reference) {
        if (reference == null)
            return false;
        if (reference instanceof RemoteServiceReferenceImpl) {
            RemoteServiceReferenceImpl impl = (RemoteServiceReferenceImpl) reference;
            return match(impl.getRegistration().properties);
        }
        return false;
    }

    public boolean match(ServiceReference reference) {
        return false;
    }
}
