package org.eclipse.ecf.presence.roster;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.presence.PresencePlugin;
import org.eclipse.ecf.presence.IPresence;

/**
 * @since 2.1
 */
public class RosterResource implements IRosterResource {

    private String name;

    private IRosterEntry parent;

    private IPresence presence;

    public  RosterResource(IRosterEntry parent, String name, IPresence presence) {
        Assert.isNotNull(parent);
        this.parent = parent;
        Assert.isNotNull(name);
        this.name = name;
        this.presence = presence;
    }

    public String getName() {
        return name;
    }

    public IRosterItem getParent() {
        return parent;
    }

    public IRoster getRoster() {
        return parent.getRoster();
    }

    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    public IPresence getPresence() {
        return presence;
    }

    public void setPresence(IPresence presence) {
        this.presence = presence;
    }
}
