/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.example.collab.share.io.FileSenderUI;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.example.collab.share.HelloMessageSharedObject;
import org.eclipse.ecf.example.collab.share.url.ShowURLSharedObject;
import org.eclipse.ecf.example.collab.share.url.StartProgramSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;

public class LineChatClientView implements FileSenderUI {

    //$NON-NLS-1$
    public static final String DEFAULT_UNIX_BROWSER = "mozilla";

    //$NON-NLS-1$
    public static final String ENTER_STRING = "ARRIVED";

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String EXECPROGARGTYPES[] = { ID.class.getName(), "[Ljava.lang.String;", "[Ljava.lang.String;", Boolean.class.getName(), Boolean.class.getName() };

    public static final String EXECPROGCLASSNAME = StartProgramSharedObject.class.getName();

    //$NON-NLS-1$
    public static final String LEFT_STRING = "LEFT";

    public static final String MESSAGECLASSNAME = HelloMessageSharedObject.class.getName();

    public static final String REMOTEFILEPATH = null;

    //$NON-NLS-1$
    public static final String SHOWURLARGTYPES[] = { ID.class.getName(), "java.lang.String" };

    public static final String SHOWURLCLASSNAME = ShowURLSharedObject.class.getName();

    private boolean showTimestamp = ClientPlugin.getDefault().getPreferenceStore().getBoolean(ClientPlugin.PREF_DISPLAY_TIMESTAMP);

    //$NON-NLS-1$
    private final SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm a");

    String downloaddir;

    EclipseCollabSharedObject lch;

    Hashtable myNames = new Hashtable();

    String name;

    private final TeamChat teamChat;

    IUser userdata;

    LineChatView view;

    private final List users;

    public  LineChatClientView(EclipseCollabSharedObject lch, LineChatView view, String name, String initText, String downloaddir) {
        super();
        this.lch = lch;
        this.view = view;
        this.name = name;
        this.teamChat = new TeamChat(this, view.tabFolder, SWT.NULL, initText);
        this.userdata = lch.getUser();
        this.downloaddir = downloaddir;
        users = new ArrayList();
        teamChat.getTableViewer().setInput(users);
        if (userdata != null)
            addUser(userdata);
        ClientPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(ClientPlugin.PREF_DISPLAY_TIMESTAMP)) {
                    showTimestamp = ((Boolean) event.getNewValue()).booleanValue();
                }
            }
        });
        JFaceResources.getColorRegistry().put(ViewerToolTip.HEADER_BG_COLOR, new RGB(255, 255, 255));
        JFaceResources.getFontRegistry().put(ViewerToolTip.HEADER_FONT, JFaceResources.getFontRegistry().getBold(JFaceResources.getDefaultFont().getFontData()[0].getName()).getFontData());
        final ToolTip toolTip = new ViewerToolTip(teamChat.getTableViewer().getControl());
        toolTip.setHideOnMouseDown(false);
        toolTip.setPopupDelay(200);
    }

    public ViewPart getView() {
        return view;
    }

    public Control getTextControl() {
        return teamChat.getTextControl();
    }

    public Control getTreeControl() {
        return teamChat.getTreeControl();
    }

    public boolean addUser(IUser ud) {
        if (ud == null)
            return false;
        final ID userID = ud.getID();
        final String username = ud.getNickname();
        if (myNames.containsKey(userID)) {
            final String existingName = (String) myNames.get(userID);
            if (!existingName.equals(username)) {
                myNames.put(userID, username);
                showLine(new ChatLine(NLS.bind(Messages.LineChatClientView_CHANGED_NAME_TO, existingName, username)));
            }
            return false;
        } else {
            myNames.put(userID, username);
            addUserToTree(ud);
            //$NON-NLS-1$
            showLine(new ChatLine(username + " " + ENTER_STRING));
            return true;
        }
    }

    protected void addUserToTree(final IUser user) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                users.add(user);
                if (!teamChat.isDisposed())
                    teamChat.getTableViewer().add(user);
            }
        });
    }

    protected void appendAndScrollToBottom(final ChatLine str) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (!teamChat.isDisposed())
                    teamChat.appendText(str);
            }
        });
    }

    public boolean changeUser(IUser user) {
        return changeUserInTree(user);
    }

    protected boolean changeUserInTree(final IUser userdata) {
        for (int i = 0; i < users.size(); i++) {
            final IUser user = (IUser) users.get(i);
            if (user.getID().equals(userdata.getID())) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (!teamChat.isDisposed()) {
                            final TableViewer view = teamChat.getTableViewer();
                            view.remove(user);
                            users.remove(user);
                            view.add(userdata);
                            users.add(userdata);
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }

    protected void closeClient() {
        if (lch != null) {
            lch.chatGUIDestroy();
        }
    }

    protected String getCurrentDateTime() {
        //$NON-NLS-1$
        final StringBuffer sb = new StringBuffer("[");
        sb.append(df.format(new Date())).append(']');
        return sb.toString();
    }

    public void disposeClient() {
        myNames.clear();
        users.clear();
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                if (!teamChat.isDisposed() && teamChat.chatWindow != null)
                    teamChat.chatWindow.close();
            }
        });
        view.disposeClient(this);
    }

    protected TeamChat getTeamChat() {
        return teamChat;
    }

    protected String getUserData(ID id) {
        return (String) myNames.get(id);
    }

    public IUser getUser(ID id) {
        if (id == null) {
            return null;
        } else {
            for (int i = 0; i < users.size(); i++) {
                final IUser user = (IUser) users.get(i);
                if (id.equals(user.getID())) {
                    return user;
                }
            }
            return null;
        }
    }

    protected void handleTextInput(String text) {
        final ChatLine line = new ChatLine(text, getCurrentDateTime());
        if (lch != null) {
            line.setOriginator(userdata);
        }
        appendAndScrollToBottom(line);
        teamChat.clearInput();
        if (lch != null)
            lch.inputText(text);
    }

    protected void createObject(ID target, String className, String[] args) {
        createObject(target, className, null, args);
    }

    protected void createObject(ID target, final String className, String[] argTypes, Object[] args) {
        if (lch != null) {
            final HashMap map = new HashMap();
            //$NON-NLS-1$
            map.put("args", args);
            //$NON-NLS-1$
            map.put("types", argTypes);
            try {
                lch.createObject(target, className, map);
            } catch (final Exception e) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openInformation(null, Messages.LineChatClientView_EXCEPTION_MSGBOX_TITLE, NLS.bind(Messages.LineChatClientView_EXCEPTION_MSGBOX_TEXT, className, e.getLocalizedMessage()));
                    }
                });
                e.printStackTrace();
                lch.chatException(e, "createObject(" + className + ")");
            }
        }
    }

    protected void refreshTreeView() {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (!teamChat.isDisposed())
                    teamChat.getTableViewer().refresh();
            }
        });
    }

    public void removeUser(ID id) {
        final String name = getUserData(id);
        if (name != null) {
            //$NON-NLS-1$
            showLine(new ChatLine(name + " " + LEFT_STRING));
        }
        myNames.remove(id);
        removeUserFromTree(id);
    }

    protected void removeUserFromTree(ID id) {
        if (id == null) {
            return;
        } else {
            for (int i = 0; i < users.size(); i++) {
                final IUser user = (IUser) users.get(i);
                if (user.getID().equals(id)) {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (!teamChat.isDisposed())
                                teamChat.getTableViewer().remove(user);
                        }
                    });
                    users.remove(i);
                    break;
                }
            }
        }
    }

    protected void runProgram(ID receiver, String program, String[] env) {
        final String[] cmds = { program };
        final Object[] args = { receiver, cmds, env, Boolean.valueOf(receiver == null), Boolean.FALSE };
        // Do it
        createObject(null, EXECPROGCLASSNAME, EXECPROGARGTYPES, args);
    }

    public void sendData(File aFile, long dataLength) {
    }

    public void sendDone(File aFile, Exception e) {
        if (e != null) {
            showLine(new ChatLine(NLS.bind(Messages.LineChatClientView_EXCEPTION_SENDING_FILE, e.getLocalizedMessage(), aFile.getName())));
        } else {
            showLine(new ChatLine(NLS.bind(Messages.LineChatClientView_SEND_COMPLETED, aFile.getName())));
            if (lch != null)
                lch.refreshProject();
        }
    }

    public void sendStart(File aFile, long length, float rate) {
        // present user with notification that file is being transferred
        showLine(new ChatLine(NLS.bind(Messages.LineChatClientView_SENDING_FILE, aFile.getName())));
    }

    public void setTitle(String title) {
    // NOTHING HAPPENS
    }

    public void showLine(ChatLine line) {
        if (showTimestamp) {
            line.setDate(getCurrentDateTime());
        }
        appendAndScrollToBottom(line);
    }

    public void startedTyping(final IUser user) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (!teamChat.isDisposed())
                    teamChat.setStatus(NLS.bind(Messages.LineChatClientView_TYPING, user.getNickname()));
            }
        });
    }

    public void toFront() {
        view.setActiveTab(name);
    }

    private class ViewerToolTip extends ToolTip {

        //$NON-NLS-1$
        public static final String HEADER_BG_COLOR = ClientPlugin.PLUGIN_ID + ".TOOLTIP_HEAD_BG_COLOR";

        //$NON-NLS-1$
        public static final String HEADER_FONT = ClientPlugin.PLUGIN_ID + ".TOOLTIP_HEAD_FONT";

        public  ViewerToolTip(Control control) {
            super(control);
        }

        protected Composite createToolTipContentArea(Event event, Composite parent) {
            final Widget item = teamChat.getTableViewer().getTable().getItem(new Point(event.x, event.y));
            final IUser user = (IUser) item.getData();
            GridLayout gl = new GridLayout();
            gl.marginBottom = 0;
            gl.marginTop = 0;
            gl.marginHeight = 0;
            gl.marginWidth = 0;
            gl.marginLeft = 0;
            gl.marginRight = 0;
            gl.verticalSpacing = 1;
            parent.setLayout(gl);
            final Composite topArea = new Composite(parent, SWT.NONE);
            final GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            data.widthHint = 200;
            topArea.setLayoutData(data);
            topArea.setBackground(JFaceResources.getColorRegistry().get(HEADER_BG_COLOR));
            gl = new GridLayout();
            gl.marginBottom = 2;
            gl.marginTop = 2;
            gl.marginHeight = 0;
            gl.marginWidth = 0;
            gl.marginLeft = 5;
            gl.marginRight = 2;
            topArea.setLayout(gl);
            final Label l = new Label(topArea, SWT.NONE);
            l.setText(user.getNickname());
            l.setBackground(JFaceResources.getColorRegistry().get(HEADER_BG_COLOR));
            l.setFont(JFaceResources.getFontRegistry().get(HEADER_FONT));
            l.setLayoutData(data);
            createContentArea(parent, user.getProperties()).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            return parent;
        }

        protected Control createContentArea(Composite parent, Map properties) {
            final Text label = new Text(parent, SWT.READ_ONLY | SWT.MULTI);
            label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            final StringBuffer buffer = new StringBuffer();
            synchronized (buffer) {
                for (Iterator it = properties.entrySet().iterator(); it.hasNext(); ) {
                    Entry entry = (Entry) it.next();
                    //$NON-NLS-1$
                    buffer.append(entry.getKey()).append(": ").append(//$NON-NLS-1$
                    entry.getValue());
                    buffer.append(Text.DELIMITER);
                }
            }
            label.setText(buffer.toString().trim());
            label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            return label;
        }

        protected boolean shouldCreateToolTip(Event e) {
            if (super.shouldCreateToolTip(e)) {
                final Widget item = teamChat.getTableViewer().getTable().getItem(new Point(e.x, e.y));
                if (item != null) {
                    final IUser user = (IUser) item.getData();
                    final Map properties = user.getProperties();
                    return properties != null && !properties.isEmpty();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
