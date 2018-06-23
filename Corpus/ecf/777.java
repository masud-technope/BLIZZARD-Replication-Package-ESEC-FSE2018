package org.eclipse.ecf.remoteservice.rest.util;

/**
 * The Class DSPresent.
 */
public class DSPresent implements IDSPresent {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.remoteservice.rest.util.IDSPresent#isPresent()
	 */
    public boolean isPresent() {
        // if registered, it is present!
        return true;
    }
}
