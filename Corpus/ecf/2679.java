/*******************************************************************************
 * Copyright (c) 2004, 2008 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Rob Elves - creator of the original TaskListNotificationPopup class
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - Adapted for ECF/Mylyn bridge use
 *     Red Hat - update to Mylyn 3.0 API
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import org.eclipse.ecf.internal.mylyn.ui.CompoundContextActivationContributionItem.ActivateTaskAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TaskListHyperlink;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

class IncomingSharedTaskNotificationPopup extends AbstractNotificationPopup {

    private static final DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

    private ITask task;

     IncomingSharedTaskNotificationPopup(Widget widget) {
        super(widget.getDisplay());
    }

    protected String getPopupShellTitle() {
        return "Incoming Task Context";
    }

    void setTask(ITask task) {
        this.task = task;
    }

    protected void createContentArea(Composite parent) {
        Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
        notificationComposite.setLayout(new GridLayout(2, false));
        notificationComposite.setBackground(parent.getBackground());
        final Label notificationLabelIcon = new Label(notificationComposite, SWT.LEAD);
        notificationLabelIcon.setBackground(parent.getBackground());
        notificationLabelIcon.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW));
        final TaskListHyperlink itemLink = new TaskListHyperlink(notificationComposite, SWT.BEGINNING | SWT.WRAP | SWT.NO_FOCUS);
        itemLink.setText(task.getTaskId());
        itemLink.setImage(labelProvider.getImage(task));
        itemLink.setBackground(parent.getBackground());
        itemLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                ActivateTaskAction action = new CompoundContextActivationContributionItem.ActivateTaskAction();
                action.setShell(getParentShell());
                action.setTask(task);
                action.run();
                close();
            }
        });
        String descriptionText = task.getSummary();
        Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
        descriptionLabel.setText(descriptionText);
        descriptionLabel.setBackground(parent.getBackground());
        GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);
    }
}
