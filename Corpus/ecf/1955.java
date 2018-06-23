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
package org.eclipse.team.internal.ecf.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.team.internal.ecf.core.TeamSynchronization;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.team.internal.ecf.ui.subscriber.RemoteSubscriberParticipant;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;

public class RemotePeerSynchronizeWizard extends Wizard {

    private RemotePeerSynchronizeWizardPage page;

    public  RemotePeerSynchronizeWizard() {
        setWindowTitle(Messages.RemotePeerSynchronizeWizard_WindowTitle);
    }

    public void addPages() {
        page = new RemotePeerSynchronizeWizardPage();
        addPage(page);
    }

    public boolean performFinish() {
        ID containerId = page.getContainerId();
        RemoteShare share = TeamSynchronization.getShare(containerId);
        IRosterEntry entry = page.getRosterEntry();
        IUser remoteUser = entry.getUser();
        ID ownId = entry.getRoster().getUser().getID();
        IResource[] resources = page.getSelectedResources();
        RemoteSubscriberParticipant participant = getSubscriberParticipant(share, ownId, remoteUser.getID());
        participant.setResources(resources);
        TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] { participant });
        if (resources.length == 1) {
            participant.refresh(resources, NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationTaskName, remoteUser.getNickname()), NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationResourceDescription, resources[0].getName(), remoteUser.getNickname()), null);
        } else {
            participant.refresh(resources, NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationTaskName, remoteUser.getNickname()), NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationResourcesDescription, remoteUser.getNickname()), null);
        }
        return true;
    }

    public static RemoteSubscriberParticipant getSubscriberParticipant(RemoteShare share, ID ownId, ID remoteId) {
        return new RemoteSubscriberParticipant(share, ownId, remoteId);
    // FIXME: we should try to reuse participants, but reusing causes the
    // 'Synchronize' view to currently not update for some reasons, thus,
    // you can effectively only synchronize once per Eclipse session
    }
}
