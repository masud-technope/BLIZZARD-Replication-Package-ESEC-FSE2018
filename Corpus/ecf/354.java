package org.eclipse.ecf.presence.roster;

/**
 * @since 2.1
 */
public interface IMultiResourceRosterEntry {

    /**
	 * Get all the resources for this multi resource roster entry
	 * @return IRosterResource[] of resources.  Will not return <code>null</code>.
	 */
    public IRosterResource[] getResources();
}
