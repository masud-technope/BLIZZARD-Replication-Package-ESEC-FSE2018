package org.eclipse.ecf.remoteservice.rest.util;

public interface IDSPresent {

    /**
	 * Checks whether declarative service is present. This service is configured
	 * by declarative services, so if it's registered to the OSGI runtime, DS is
	 * present
	 * 
	 * @return true, if is present
	 */
    public boolean isPresent();
}
