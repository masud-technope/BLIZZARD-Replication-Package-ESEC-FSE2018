package org.eclipse.ecf.presence.roster;

import org.eclipse.ecf.presence.IPresence;

/**
 * @since 2.1
 */
public interface IRosterResource extends IRosterItem {

    // No new methods
    public IPresence getPresence();
}
