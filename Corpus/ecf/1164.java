package org.eclipse.ecf.internal.remoteservices.ui;

import java.io.InvalidObjectException;
import org.eclipse.core.runtime.IConfigurationElement;

public class ServicesViewExtension {

    private final String viewId;

    private final boolean local;

    private int priority = 0;

    public  ServicesViewExtension(IConfigurationElement ce) throws InvalidObjectException {
        //$NON-NLS-1$
        this.viewId = ce.getAttribute("viewid");
        if (this.viewId == null)
            //$NON-NLS-1$
            throw new InvalidObjectException("viewId must be set for services view extension");
        //$NON-NLS-1$
        this.local = Boolean.parseBoolean(ce.getAttribute("local"));
        //$NON-NLS-1$
        String priorityStr = ce.getAttribute("priority");
        try {
            this.priority = Integer.parseInt(priorityStr);
        } catch (NumberFormatException e) {
        }
    }

    public String getViewId() {
        return this.viewId;
    }

    public boolean isLocal() {
        return this.local;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return "ServicesViewExtension [viewId=" + viewId + ", local=" + local + ", priority=" + priority + "]";
    }
}
