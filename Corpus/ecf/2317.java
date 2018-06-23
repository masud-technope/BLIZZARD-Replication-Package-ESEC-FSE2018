/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.internal.remoteservices.ui.Activator;
import org.eclipse.ecf.internal.remoteservices.ui.DiscoveryComponent;
import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @since 3.3
 */
public class AbstractRSANode implements IAdaptable {

    public static final String CLOSED = Messages.AbstractRSANode_NodeClosed;

    private AbstractRSANode parent;

    private final List<AbstractRSANode> children = new ArrayList<AbstractRSANode>();

    public  AbstractRSANode() {
    }

    protected RemoteServiceAdmin getRSA() {
        return DiscoveryComponent.getDefault().getRSA();
    }

    public AbstractRSANode getParent() {
        return this.parent;
    }

    protected void setParent(AbstractRSANode p) {
        this.parent = p;
    }

    public void addChild(AbstractRSANode child) {
        children.add(child);
        child.setParent(this);
    }

    public void addChildAtIndex(int index, AbstractRSANode child) {
        children.add(index, child);
        child.setParent(this);
    }

    public void removeChild(AbstractRSANode child) {
        children.remove(child);
        child.setParent(null);
    }

    public AbstractRSANode[] getChildren() {
        return (AbstractRSANode[]) children.toArray(new AbstractRSANode[children.size()]);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public void clearChildren() {
        children.clear();
    }

    protected ServiceReference<org.osgi.service.remoteserviceadmin.RemoteServiceAdmin> getRSARef() {
        Activator a = Activator.getDefault();
        if (a != null) {
            Collection<ServiceReference<org.osgi.service.remoteserviceadmin.RemoteServiceAdmin>> rsaRefs = null;
            try {
                rsaRefs = a.getBundle().getBundleContext().getServiceReferences(org.osgi.service.remoteserviceadmin.RemoteServiceAdmin.class, null);
            } catch (InvalidSyntaxException e) {
            }
            return (rsaRefs != null && rsaRefs.size() > 0) ? rsaRefs.iterator().next() : null;
        } else
            return null;
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }
}
