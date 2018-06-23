/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui.subscriber;

import org.eclipse.core.resources.IResource;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.team.internal.ecf.core.variants.RemoteResourceVariantTreeSubscriber;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.team.internal.ecf.ui.actions.OverrideWithRemoteAction;
import org.eclipse.team.ui.synchronize.*;

public class RemoteSubscriberParticipant extends SubscriberParticipant {

    public  RemoteSubscriberParticipant(RemoteShare share, ID ownId, ID remoteId) {
        setSubscriber(new RemoteResourceVariantTreeSubscriber(share, ownId, remoteId));
    }

    public void setResources(IResource[] resources) {
        RemoteResourceVariantTreeSubscriber subscriber = (RemoteResourceVariantTreeSubscriber) getSubscriber();
        subscriber.setResources(resources);
    }

    protected void initializeConfiguration(final ISynchronizePageConfiguration configuration) {
        super.initializeConfiguration(configuration);
        configuration.setProperty(ISynchronizePageConfiguration.P_PAGE_DESCRIPTION, Messages.RemoteSubscriberParticipant_PageDescription);
        configuration.addActionContribution(new SynchronizePageActionGroup() {

            public void initialize(ISynchronizePageConfiguration pageConfiguration) {
                super.initialize(pageConfiguration);
                appendToGroup(ISynchronizePageConfiguration.P_CONTEXT_MENU, ISynchronizePageConfiguration.SYNCHRONIZE_GROUP, new OverrideWithRemoteAction(pageConfiguration));
            }
        });
        configuration.setSupportedModes(ISynchronizePageConfiguration.ALL_MODES);
        configuration.setMode(ISynchronizePageConfiguration.BOTH_MODE);
    }

    public String getName() {
        return Messages.RemoteSubscriberParticipant_PageDescription;
    }

    public String getId() {
        //$NON-NLS-1$
        return "org.eclipse.ecf.sync.team.participant";
    }

    public String getSecondaryId() {
        //$NON-NLS-1$
        return "secondaryId";
    }
}
