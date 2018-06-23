/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.presence.ui.dialogs;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.presence.ui.Messages;
import org.eclipse.ecf.presence.chatroom.*;
import org.eclipse.ecf.presence.ui.MultiRosterAccount;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.viewers.deferred.DeferredContentProvider;
import org.eclipse.jface.viewers.deferred.SetModel;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ChatRoomSelectionDialog extends TitleAreaDialog {

    private static final int X_INITIAL_SIZE = 640;

    private static final int Y_INITIAL_SIZE = 400;

    private Room loadingRoom = new Room();

    MultiRosterAccount[] accounts = null;

    private Room selectedRoom = null;

    private Job roomRetrieveJob = null;

    public class Room {

        IChatRoomInfo info;

        MultiRosterAccount account;

        public  Room() {
            this.info = new IChatRoomInfo() {

                /**
				 * @throws ContainerCreateException  
				 */
                public IChatRoomContainer createChatRoomContainer() throws ContainerCreateException {
                    return null;
                }

                public ID getConnectedID() {
                    return null;
                }

                public String getDescription() {
                    return null;
                }

                public String getName() {
                    return "Retrieving chat room list from servers...";
                }

                public int getParticipantsCount() {
                    return 0;
                }

                public ID getRoomID() {
                    return null;
                }

                public String getSubject() {
                    return null;
                }

                public boolean isModerated() {
                    return false;
                }

                public boolean isPersistent() {
                    return false;
                }

                public boolean requiresPassword() {
                    return false;
                }

                public Object getAdapter(Class adapter) {
                    return null;
                }
            };
        }

        public  Room(IChatRoomInfo info, MultiRosterAccount man) {
            this.info = info;
            this.account = man;
        }

        public IChatRoomInfo getRoomInfo() {
            return info;
        }

        public MultiRosterAccount getAccount() {
            return account;
        }
    }

    private SetModel rooms = new SetModel();

    public  ChatRoomSelectionDialog(Shell parentShell, MultiRosterAccount[] accounts) {
        super(parentShell);
        this.accounts = accounts;
        setTitleImage(SharedImages.getImage(SharedImages.IMG_CHAT_WIZARD));
    }

    protected boolean isResizable() {
        return true;
    }

    protected Point getInitialSize() {
        return getShell().computeSize(X_INITIAL_SIZE, Y_INITIAL_SIZE, true);
    }

    protected Control createDialogArea(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout());
        main.setLayoutData(new GridData(GridData.FILL_BOTH));
        TableViewer viewer = new TableViewer(main, SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        TableColumn tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_ROOM_NAME_COLUMN);
        //		tc.pack();
        //		int width = tc.getWidth();
        tc.setWidth(X_INITIAL_SIZE / 3);
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_SUBJECT_COLUMN);
        tc.pack();
        int width;
        width = tc.getWidth();
        tc.setWidth(width + (width / 4));
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_DESCRIPTION_COLUMN);
        tc.pack();
        width = tc.getWidth();
        tc.setWidth(width + (width / 4));
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_MEMBERS_COLUMN);
        tc.pack();
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_MODERATED_COLUMN);
        tc.pack();
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_PERSISTENT_COLUMN);
        tc.pack();
        tc = new TableColumn(table, SWT.NONE);
        tc.setText(Messages.ChatRoomSelectionDialog_ACCOUNT_COLUMN);
        tc.pack();
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                IStructuredSelection ss = (IStructuredSelection) ((selection instanceof IStructuredSelection) ? selection : null);
                Object firstElement = (ss == null) ? null : ss.getFirstElement();
                if (!event.getSelection().isEmpty() && !loadingRoom.equals(firstElement)) {
                    ChatRoomSelectionDialog.this.getButton(Window.OK).setEnabled(true);
                }
            }
        });
        viewer.setContentProvider(new DeferredContentProvider(new Comparator() {

            public int compare(Object r1, Object r2) {
                Room room1 = (Room) r1;
                Room room2 = (Room) r2;
                return room1.getRoomInfo().getName().compareTo(room2.getRoomInfo().getName());
            }
        }));
        viewer.setLabelProvider(new ChatRoomLabelProvider());
        rooms.addAll(Arrays.asList(new Room[] { loadingRoom }));
        viewer.setInput(rooms);
        this.setTitle(Messages.ChatRoomSelectionDialog_TITLE);
        this.setMessage(Messages.ChatRoomSelectionDialog_MESSAGE);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent e) {
                IStructuredSelection s = (IStructuredSelection) e.getSelection();
                Object o = s.getFirstElement();
                if (o instanceof Room) {
                    selectedRoom = (Room) o;
                }
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                if (selectedRoom != null) {
                    ChatRoomSelectionDialog.this.okPressed();
                }
            }
        });
        startRetrieveJob();
        applyDialogFont(parent);
        return parent;
    }

    private void startRetrieveJob() {
        roomRetrieveJob = new //$NON-NLS-1$
        Job(//$NON-NLS-1$
        "Chat Room Retrieve") {

            protected IStatus run(IProgressMonitor monitor) {
                ArrayList add = new ArrayList();
                for (int i = 0; i < accounts.length; i++) {
                    IChatRoomManager chatRoomManager = accounts[i].getPresenceContainerAdapter().getChatRoomManager();
                    if (chatRoomManager != null) {
                        try {
                            IChatRoomInfo[] infos = chatRoomManager.getChatRoomInfos();
                            if (infos != null) {
                                for (int j = 0; j < infos.length; j++) {
                                    if (infos[j] != null && accounts[i] != null) {
                                        add.add(new Room(infos[j], accounts[i]));
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                if (monitor.isCanceled())
                    return Status.CANCEL_STATUS;
                rooms.removeAll(new Room[] { loadingRoom });
                rooms.addAll(add);
                return Status.OK_STATUS;
            }
        };
        roomRetrieveJob.schedule();
    }

    private class ChatRoomLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            Room room = (Room) element;
            IChatRoomInfo info = room.getRoomInfo();
            MultiRosterAccount account = room.getAccount();
            switch(columnIndex) {
                case 0:
                    return info.getName();
                case 1:
                    return info.getSubject();
                case 2:
                    return info.getDescription();
                case 3:
                    return (account == null) ? null : String.valueOf(info.getParticipantsCount());
                case 4:
                    return (account == null) ? null : String.valueOf(info.isModerated());
                case 5:
                    return (account == null) ? null : String.valueOf(info.isPersistent());
                case 6:
                    return (account == null) ? null : account.getContainer().getConnectedID().getName();
                default:
                    //$NON-NLS-1$
                    return "";
            }
        }

        public void addListener(ILabelProviderListener listener) {
        // do nothing
        }

        public void dispose() {
        // do nothing
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        // do nothing
        }
    }

    protected Control createButtonBar(Composite parent) {
        Control bar = super.createButtonBar(parent);
        this.getButton(Window.OK).setText(Messages.ChatRoomSelectionDialog_ENTER_CHAT_BUTTON_TEXT);
        this.getButton(Window.OK).setEnabled(false);
        return bar;
    }

    public Room getSelectedRoom() {
        return selectedRoom;
    }

    public boolean close() {
        if (roomRetrieveJob != null) {
            roomRetrieveJob.cancel();
            roomRetrieveJob = null;
            rooms.clear();
        }
        return super.close();
    }
}
