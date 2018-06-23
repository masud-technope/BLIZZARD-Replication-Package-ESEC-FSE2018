/*******************************************************************************
 * Copyright (c) 2004, 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bug 192762, 197329, 190851
 *    Abner Ballardo <modlost@modlost.net> - bug 192756, 199336, 200630
 *    Jakub Jurkiewicz <jakub.jurkiewicz@pl.ibm.com> - bug 197332
 *    Hiroyuki Inaba <Hiroyuki <hiroyuki.inaba@gmail.com> - bug 222253
 ******************************************************************************/
package org.eclipse.ecf.presence.ui.chatroom;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.presence.ui.*;
import org.eclipse.ecf.internal.presence.ui.preferences.PreferenceConstants;
import org.eclipse.ecf.internal.ui.actions.SelectProviderAction;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.chatroom.*;
import org.eclipse.ecf.presence.im.IChatID;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class ChatRoomManagerView extends ViewPart implements IChatRoomInvitationListener {

    public  ChatRoomManagerView() {
    }

    //$NON-NLS-1$
    private static final String ATSIGN = "@";

    //$NON-NLS-1$
    public static final String VIEW_ID = "org.eclipse.ecf.presence.ui.chatroom.ChatRoomManagerView";

    //$NON-NLS-1$ 
    public static final String PARTICIPANTS_MENU_ID = "org.eclipse.ecf.presence.ui.chatroom.participantsView";

    private static final int RATIO_READ_WRITE_PANE = 85;

    private static final int RATIO_PRESENCE_PANE = 15;

    protected static final int DEFAULT_INPUT_HEIGHT = 25;

    protected static final int DEFAULT_INPUT_SEPARATOR = 5;

    private CTabFolder rootTabFolder = null;

    private ChatRoomTab rootChannelTab = null;

    private IChatRoomViewCloseListener rootCloseListener = null;

    private IChatRoomMessageSender rootMessageSender = null;

    /**
	 * UI independent renderer, that is aware of displaying any special fragments
	 * of message, like formatting, graphical attachments, emotional content, etc.
	 */
    private IMessageRenderer messageRenderer = null;

    Action outputClear = null;

    Action outputCopy = null;

    Action outputPaste = null;

    Action outputSelectAll = null;

    boolean rootDisposed = false;

    private boolean rootEnabled = false;

    private Hashtable chatRooms = new Hashtable();

    private IChatRoomCommandListener commandListener = null;

    private IChatRoomContainer container = null;

    private String localUserName = Messages.ChatRoomManagerView_DEFAULT_USER;

    private String hostName = Messages.ChatRoomManagerView_DEFAULT_HOST;

    private CTabItem infoTab;

    private boolean fClearInfoTab = true;

    class ChatRoomTab {

        private SashForm fullChat;

        private CTabItem tabItem;

        private Composite rightComposite;

        private StyledText subjectText;

        private StyledText outputText;

        private Text inputText;

        private Label participantsNumberLabel;

        private TableViewer participantsTable;

        private Action tabSelectAll;

        private Action tabCopy;

        private Action tabClear;

        private Action tabPaste;

        private boolean withParticipants;

         ChatRoomTab(CTabFolder parent, String name) {
            this(true, parent, name, null);
        }

         ChatRoomTab(boolean withParticipantsList, CTabFolder parent, String name, KeyListener keyListener) {
            withParticipants = withParticipantsList;
            tabItem = new CTabItem(parent, SWT.NULL);
            tabItem.setText(name);
            if (withParticipants) {
                fullChat = new SashForm(parent, SWT.HORIZONTAL);
                fullChat.setLayout(new FillLayout());
                Composite memberComp = new Composite(fullChat, SWT.NONE);
                GridLayout layout = new GridLayout(1, true);
                layout.marginWidth = 0;
                layout.marginHeight = 0;
                memberComp.setLayout(layout);
                participantsNumberLabel = new Label(memberComp, SWT.BORDER | SWT.READ_ONLY);
                participantsNumberLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
                participantsNumberLabel.setAlignment(SWT.CENTER);
                participantsTable = new TableViewer(memberComp, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
                participantsTable.setSorter(new ViewerSorter());
                participantsTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                participantsTable.addOpenListener(new IOpenListener() {

                    public void open(OpenEvent event) {
                        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                        String user = ((ChatRoomParticipant) selection.getFirstElement()).getName();
                        if (!ChatRoomManagerView.this.localUserName.equals(user)) {
                            try {
                                MessagesView messagesView = getMessagesView();
                                messagesView.selectTab(container.getPrivateMessageSender(), null, createStringID(localUserName), createStringID(user), user);
                                getSite().getPage().activate(messagesView);
                            } catch (PartInitException e) {
                                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, NLS.bind(Messages.ChatRoomManagerView_EXCEPTION_MESSAGE_VIEW_INITIALIZATION, user), e));
                            }
                        }
                    }
                });
                rightComposite = new Composite(fullChat, SWT.NONE);
                layout = new GridLayout(1, true);
                layout.marginHeight = 0;
                layout.marginWidth = 0;
                rightComposite.setLayout(layout);
                subjectText = createStyledTextWidget(rightComposite, SWT.SINGLE | SWT.BORDER);
                subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                /*
				 * The sendSubjectChange method in Smack 2.2.1 does not seem to be working correctly, so this whole block
				 * can be temporily removed.  See https://bugs.eclipse.org/bugs/show_bug.cgi?id=223560
				subjectText.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent evt) {
						if (evt.character == SWT.CR || evt.character == SWT.KEYPAD_CR) {
							ChatRoom chatroom = (ChatRoom) chatRooms.get(tabItem.getText());
							if (chatroom != null) {
								IChatRoomAdminSender chatRoomAdminSender = chatroom.chatRoomContainer.getChatRoomAdminSender();
								try {
									if (chatRoomAdminSender != null) {
										chatRoomAdminSender.sendSubjectChange(subjectText.getText());
									}
								} catch (ECFException e) {
									Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "sendSubjectChange", e));disconnected(); //$NON-NLS-1$
								}
							}
						}
					}
				});
				*/
                subjectText.setEditable(false);
                subjectText.setEnabled(false);
            } else {
                rightComposite = new Composite(parent, SWT.NONE);
                GridLayout layout = new GridLayout(1, true);
                layout.marginHeight = 0;
                layout.marginWidth = 0;
                rightComposite.setLayout(layout);
            }
            outputText = createStyledTextWidget(rightComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
            outputText.setEditable(false);
            outputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            inputText = new Text(rightComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
            GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            GC gc = new GC(inputText);
            gc.setFont(inputText.getFont());
            FontMetrics fontMetrics = gc.getFontMetrics();
            gc.dispose();
            gd.heightHint = fontMetrics.getHeight() * 2;
            inputText.setLayoutData(gd);
            if (keyListener != null)
                inputText.addKeyListener(keyListener);
            if (withParticipants) {
                fullChat.setWeights(new int[] { RATIO_PRESENCE_PANE, RATIO_READ_WRITE_PANE });
                tabItem.setControl(fullChat);
            } else
                tabItem.setControl(rightComposite);
            parent.setSelection(tabItem);
            makeActions();
            hookContextMenu();
            if (withParticipants) {
                hookParticipantsContextMenu();
            }
            StyledText st = getOutputText();
            if (st != null) {
                ScrollBar vsb = st.getVerticalBar();
                if (vsb != null) {
                    vsb.addSelectionListener(scrollSelectionListener);
                    vsb.addDisposeListener(new DisposeListener() {

                        public void widgetDisposed(DisposeEvent e) {
                            StyledText ot = getOutputText();
                            if (ot != null) {
                                ScrollBar sb = ot.getVerticalBar();
                                if (sb != null)
                                    sb.removeSelectionListener(scrollSelectionListener);
                            }
                        }
                    });
                }
            }
        }

        private SelectionListener scrollSelectionListener = new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            // do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                if (!isLastOutputInvisible(getOutputText())) {
                    makeTabItemNormal();
                }
            }
        };

        protected void makeTabItemBold() {
            changeTabItem(true);
        }

        protected void makeTabItemNormal() {
            changeTabItem(false);
        }

        protected void changeTabItem(boolean bold) {
            CTabItem item = tabItem;
            Font oldFont = item.getFont();
            FontData[] fd = oldFont.getFontData();
            item.setFont(new Font(oldFont.getDevice(), fd[0].getName(), fd[0].getHeight(), (bold) ? SWT.BOLD : SWT.NORMAL));
        }

        private StyledText createStyledTextWidget(Composite parent, int styles) {
            SourceViewer result = null;
            try {
                result = new SourceViewer(parent, null, null, true, styles);
            } catch (NoClassDefFoundError e) {
                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, Messages.ChatRoomManagerView_WARNING_HYPERLINKING_NOT_AVAILABLE, e));
                return new StyledText(parent, styles);
            }
            result.configure(new ChatRoomViewerConfiguration(EditorsUI.getPreferenceStore(), container, ChatRoomManagerView.this));
            result.setDocument(new Document());
            return result.getTextWidget();
        }

        protected void outputClear() {
            if (MessageDialog.openConfirm(null, Messages.ChatRoomManagerView_CONFIRM_CLEAR_TEXT_OUTPUT_TITLE, Messages.ChatRoomManagerView_CONFIRM_CLEAR_TEXT_OUTPUT_MESSAGE)) {
                //$NON-NLS-1$
                outputText.setText(//$NON-NLS-1$
                "");
            }
        }

        protected void outputCopy() {
            String t = outputText.getSelectionText();
            if (t == null || t.length() == 0) {
                outputText.selectAll();
            }
            outputText.copy();
            outputText.setSelection(outputText.getText().length());
        }

        private void fillContextMenu(IMenuManager manager) {
            manager.add(tabCopy);
            manager.add(tabPaste);
            manager.add(tabClear);
            manager.add(new Separator());
            manager.add(tabSelectAll);
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        }

        private void hookContextMenu() {
            MenuManager menuMgr = new MenuManager();
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    fillContextMenu(manager);
                }
            });
            Menu menu = menuMgr.createContextMenu(outputText);
            outputText.setMenu(menu);
            ISelectionProvider selectionProvider = new ISelectionProvider() {

                public void addSelectionChangedListener(ISelectionChangedListener listener) {
                // do nothing
                }

                public ISelection getSelection() {
                    ISelection selection = new TextSelection(outputText.getSelectionRange().x, outputText.getSelectionRange().y);
                    return selection;
                }

                public void removeSelectionChangedListener(ISelectionChangedListener listener) {
                // do nothing
                }

                public void setSelection(ISelection selection) {
                    if (selection instanceof ITextSelection) {
                        ITextSelection textSelection = (ITextSelection) selection;
                        outputText.setSelection(textSelection.getOffset(), textSelection.getOffset() + textSelection.getLength());
                    }
                }
            };
            getSite().registerContextMenu(menuMgr, selectionProvider);
        }

        private void hookParticipantsContextMenu() {
            MenuManager menuMgr = new MenuManager();
            menuMgr.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                }
            });
            menuMgr.setRemoveAllWhenShown(true);
            Control control = participantsTable.getControl();
            Menu menu = menuMgr.createContextMenu(control);
            control.setMenu(menu);
            getSite().registerContextMenu(PARTICIPANTS_MENU_ID, menuMgr, participantsTable);
        }

        private void makeActions() {
            tabSelectAll = new Action() {

                public void run() {
                    outputText.selectAll();
                }
            };
            tabSelectAll.setText(Messages.ChatRoomManagerView_SELECT_ALL_TEXT);
            tabSelectAll.setToolTipText(Messages.ChatRoomManagerView_SELECT_ALL_TOOLTIP);
            tabSelectAll.setAccelerator(SWT.CTRL | 'A');
            tabCopy = new Action() {

                public void run() {
                    outputCopy();
                }
            };
            tabCopy.setText(Messages.ChatRoomManagerView_COPY_TEXT);
            tabCopy.setToolTipText(Messages.ChatRoomManagerView_COPY_TOOLTIP);
            tabCopy.setAccelerator(SWT.CTRL | 'C');
            tabCopy.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            tabClear = new Action() {

                public void run() {
                    outputClear();
                }
            };
            tabClear.setText(Messages.ChatRoomManagerView_CLEAR_TEXT);
            tabClear.setToolTipText(Messages.ChatRoomManagerView_CLEAR_TOOLTIP);
            tabPaste = new Action() {

                public void run() {
                    getInputText().paste();
                }
            };
            tabPaste.setText(Messages.ChatRoomManagerView_PASTE_TEXT);
            tabPaste.setToolTipText(Messages.ChatRoomManagerView_PASTE_TOOLTIP);
            tabPaste.setAccelerator(SWT.CTRL | 'V');
            tabPaste.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        }

        protected Text getInputText() {
            return inputText;
        }

        protected void setKeyListener(KeyListener listener) {
            if (listener != null)
                inputText.addKeyListener(listener);
        }

        protected Label getParticipantsLabel() {
            return participantsNumberLabel;
        }

        protected TableViewer getParticipantsViewer() {
            return participantsTable;
        }

        /**
		 * @return the <tt>StyledText</tt> widget that is displaying the output of the chatroom
		 */
        public StyledText getOutputText() {
            return outputText;
        }

        public void setSubject(String subject) {
            subjectText.setText(subject);
        }
    }

    public void createPartControl(Composite parent) {
        Composite rootComposite = new Composite(parent, SWT.NONE);
        rootComposite.setLayout(new FillLayout());
        boolean useTraditionalTabFolder = PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS);
        rootTabFolder = new CTabFolder(rootComposite, SWT.NORMAL | SWT.CLOSE);
        rootTabFolder.setUnselectedCloseVisible(false);
        rootTabFolder.setSimple(useTraditionalTabFolder);
        //$NON-NLS-1$
        populateInfoTab(getInfoTabControl(SWT.NONE, "Info", true));
        PlatformUI.getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS) && !rootTabFolder.isDisposed()) {
                    rootTabFolder.setSimple(((Boolean) event.getNewValue()).booleanValue());
                    rootTabFolder.redraw();
                }
            }
        });
        rootTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {

            public void close(CTabFolderEvent event) {
                event.doit = closeTabItem((CTabItem) event.item);
            }
        });
    }

    /**
	 * This enables IM providers to provide additional information about the provider by asking the creation of a separate information tab. This information is displayed in a separate info tab.
	 * 
	 * @param style with additional information about the provider
	 * @param title
	 * @param clearInfoTab set to true if the tab must be cleared when population of the view begins.
	 * @return the Composite to draw on
	 * @since 2.2
	 */
    public Composite getInfoTabControl(int style, String title, boolean clearInfoTab) {
        // dispose old tab
        if (infoTab != null)
            infoTab.dispose();
        CTabItem tab = new CTabItem(rootTabFolder, style);
        tab.setText(title);
        // assign new tab
        this.infoTab = tab;
        this.fClearInfoTab = clearInfoTab;
        Composite parent = new Composite(rootTabFolder, SWT.NONE);
        parent.setLayout(new FillLayout());
        tab.setControl(parent);
        rootTabFolder.setSelection(tab);
        return parent;
    }

    /**
	 * Creates a tab with view information. This view does nothing when it is opened manually. This tab is used to provide that information.
	 */
    private void populateInfoTab(Composite parent) {
        Link link = new Link(parent, SWT.NONE);
        //$NON-NLS-1$
        link.setText("\n   This view is not intended to be opened as a standalone view. Please select one of the <a>IM Providers</a> to open a populated view.");
        link.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                SelectProviderAction action = new SelectProviderAction();
                action.init(ChatRoomManagerView.this.getSite().getWorkbenchWindow());
                action.run(null);
            }
        });
    }

    private boolean closeTabItem(CTabItem tabItem) {
        ChatRoom chatRoom = findChatRoomForTabItem(tabItem);
        if (chatRoom == null) {
            return true;
        }
        if (MessageDialog.openQuestion(getSite().getShell(), Messages.ChatRoomManagerView_CLOSE_CHAT_ROOM_TITLE, NLS.bind(Messages.ChatRoomManagerView_CLOSE_CHAT_ROOM_MESSAGE, tabItem.getText()))) {
            chatRoom.chatRoomDisconnect();
            return true;
        }
        return false;
    }

    public IChatRoomContainer getRootChatRoomContainer() {
        return container;
    }

    /**
	 * @return a list of IChatRoomContainer for each open channel 
	 * or an empty array if there are no channels open. 
	 * @since 2.1
	 */
    public IChatRoomContainer[] getChatRoomContainers() {
        List containers = new ArrayList(chatRooms.size());
        for (Iterator i = chatRooms.values().iterator(); i.hasNext(); ) {
            ChatRoom cr = (ChatRoom) i.next();
            if (cr.chatRoomContainer != null) {
                containers.add(cr.chatRoomContainer);
            }
        }
        return (IChatRoomContainer[]) containers.toArray(new IChatRoomContainer[containers.size()]);
    }

    /**
	 * @return chat room container of currently selected tab or null if none found.
	 */
    public IChatRoomContainer getActiveChatRoomContainer() {
        CTabItem selection = rootTabFolder.getSelection();
        if (selection != null) {
            ChatRoom chatRoom = findChatRoomForTabItem(selection);
            if (chatRoom != null) {
                return chatRoom.chatRoomContainer;
            }
        }
        return null;
    }

    private ChatRoom findChatRoomForTabItem(CTabItem tabItem) {
        for (Iterator i = chatRooms.values().iterator(); i.hasNext(); ) {
            ChatRoom cr = (ChatRoom) i.next();
            if (tabItem == cr.chatRoomTab.tabItem)
                return cr;
        }
        return null;
    }

    private Text getRootTextInput() {
        if (rootChannelTab == null)
            return null;
        return rootChannelTab.getInputText();
    }

    private StyledText getRootTextOutput() {
        if (rootChannelTab == null)
            return null;
        return rootChannelTab.getOutputText();
    }

    public void initializeWithoutManager(String username, String hostname, final IChatRoomCommandListener commandListener1, final IChatRoomViewCloseListener closeListener) {
        initializeWithManager(username, hostname, null, commandListener1, closeListener);
    }

    public void initializeWithManager(String localUserName1, String hostName1, final IChatRoomContainer rootChatRoomContainer, final IChatRoomCommandListener commandListener1, final IChatRoomViewCloseListener closeListener) {
        // We get populated, remove the info tab if requested
        if (infoTab != null && fClearInfoTab && !(infoTab.isDisposed())) {
            infoTab.getControl().dispose();
            infoTab.dispose();
            infoTab = null;
        }
        ChatRoomManagerView.this.localUserName = (localUserName1 == null) ? Messages.ChatRoomManagerView_DEFAULT_USER : localUserName1;
        ChatRoomManagerView.this.hostName = (hostName1 == null) ? Messages.ChatRoomManagerView_DEFAULT_HOST : hostName1;
        ChatRoomManagerView.this.rootCloseListener = closeListener;
        ChatRoomManagerView.this.commandListener = commandListener1;
        String viewTitle = localUserName1 + ATSIGN + hostName1;
        ChatRoomManagerView.this.setPartName(NLS.bind(Messages.ChatRoomManagerView_VIEW_TITLE, viewTitle));
        ChatRoomManagerView.this.setTitleToolTip(Messages.ChatRoomManagerView_VIEW_TITLE_HOST_PREFIX + ChatRoomManagerView.this.hostName);
        if (rootChatRoomContainer != null) {
            ChatRoomManagerView.this.container = rootChatRoomContainer;
            ChatRoomManagerView.this.rootMessageSender = rootChatRoomContainer.getChatRoomMessageSender();
            rootChannelTab = new ChatRoomTab(false, rootTabFolder, ChatRoomManagerView.this.hostName, new KeyListener() {

                public void keyPressed(KeyEvent evt) {
                    handleKeyPressed(evt);
                }

                public void keyReleased(KeyEvent evt) {
                // do nothing
                }
            });
            makeActions();
            hookContextMenu();
            if (rootChatRoomContainer.getConnectedID() == null) {
                StyledText outputText = getRootTextOutput();
                if (!outputText.isDisposed())
                    outputText.setText(new SimpleDateFormat(Messages.ChatRoomManagerView_CONNECT_DATE_TIME_FORMAT).format(new Date()) + NLS.bind(Messages.ChatRoomManagerView_CONNECT_MESSAGE, viewTitle));
            }
        }
        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        this.rootEnabled = enabled;
        Text inputText = getRootTextInput();
        if (inputText != null && !inputText.isDisposed())
            inputText.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return rootEnabled;
    }

    protected void clearInput() {
        Text textInput = getRootTextInput();
        if (textInput != null)
            //$NON-NLS-1$
            textInput.setText("");
    }

    public void sendMessageLine(String line) {
        try {
            if (rootMessageSender != null)
                rootMessageSender.sendMessage(line);
        } catch (ECFException e) {
            removeLocalUser();
        }
    }

    public void disconnected() {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (rootDisposed)
                    return;
                setEnabled(false);
                setPartName(NLS.bind(Messages.ChatRoomManagerView_VIEW_DISABLED_NAME, getPartName()));
            }
        });
    }

    protected CTabItem getTabItem(String targetName) {
        CTabItem[] items = rootTabFolder.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getText().equals(targetName)) {
                return items[i];
            }
        }
        return null;
    }

    protected void doJoinRoom(final IChatRoomInfo roomInfo, final String password) {
        final ID targetRoomID = roomInfo.getRoomID();
        final String targetRoomName = targetRoomID.getName();
        // first, check to see if we already have it open. If so just activate
        ChatRoom room = (ChatRoom) chatRooms.get(targetRoomName);
        if (room != null && room.isConnected()) {
            room.setSelected();
            return;
        }
        // Then we create a new chatRoomContainer from the roomInfo
        try {
            final IChatRoomContainer chatRoomContainer = roomInfo.createChatRoomContainer();
            // Setup new user interface (new tab)
            final ChatRoom chatroom = new ChatRoom(chatRoomContainer, new ChatRoomTab(rootTabFolder, targetRoomName));
            // setup message listener
            chatRoomContainer.addMessageListener(new IIMMessageListener() {

                public void handleMessageEvent(IIMMessageEvent messageEvent) {
                    if (messageEvent instanceof IChatRoomMessageEvent) {
                        IChatRoomMessage m = ((IChatRoomMessageEvent) messageEvent).getChatRoomMessage();
                        chatroom.handleMessage(m.getFromID(), m.getMessage());
                    }
                }
            });
            // setup participant listener
            chatRoomContainer.addChatRoomParticipantListener(new IChatRoomParticipantListener() {

                public void handlePresenceUpdated(ID fromID, IPresence presence) {
                    chatroom.handlePresence(fromID, presence);
                }

                public void handleArrived(IUser participant) {
                // do nothing
                }

                public void handleUpdated(IUser updatedParticipant) {
                // do nothing
                }

                public void handleDeparted(IUser participant) {
                // do nothing
                }
            });
            chatRoomContainer.addListener(new IContainerListener() {

                public void handleEvent(IContainerEvent evt) {
                    if (evt instanceof IContainerDisconnectedEvent || evt instanceof IContainerEjectedEvent) {
                        chatroom.disconnected();
                    }
                }
            });
            // Now connect/join
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    try {
                        chatRoomContainer.connect(targetRoomID, ConnectContextFactory.createPasswordConnectContext(password));
                        chatRooms.put(targetRoomName, chatroom);
                    } catch (Exception e) {
                        MessageDialog.openError(getSite().getShell(), "Connect Error", NLS.bind("Could not connect to {0}.\n\nError is {1}.", targetRoomName, e.getLocalizedMessage()));
                    }
                }
            });
        } catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "Container Create Error", NLS.bind("Could not create chatRoomContainer for {0}.\n\nError is {1}.", targetRoomName, e.getLocalizedMessage()));
        }
    }

    class ChatRoom implements IChatRoomInvitationListener, KeyListener {

        private IChatRoomContainer chatRoomContainer;

        private ChatRoomTab chatRoomTab;

        private IChatRoomMessageSender chatRoomMessageSender;

        private IUser localUser;

        private Label chatRoomParticipantsLabel;

        private TableViewer chatRoomParticipantViewer = null;

        /**
		 * A list of available nicknames for nickname completion via the 'tab'
		 * key.
		 */
        private ArrayList options;

        /**
		 * Denotes the number of options that should be available for the user
		 * to cycle through when pressing the 'tab' key to perform nickname
		 * completion. The default value is set to 5.
		 */
        private int maximumCyclingOptions = 5;

        /**
		 * The length of a nickname's prefix that has already been typed in by
		 * the user. This is used to remove the beginning part of the available
		 * nickname choices.
		 */
        private int prefixLength;

        /**
		 * The index of the next nickname to select from {@link #options}.
		 */
        private int choice = 0;

        /**
		 * The length of the user's nickname that remains resulting from
		 * subtracting the nickname's length from the prefix that the user has
		 * keyed in already.
		 */
        private int nickRemainder;

        /**
		 * The caret position when the user first started
		 * cycling through nickname completion options.
		 */
        private int caret;

        /**
		 * The character to enter after the user's nickname has been
		 * autocompleted. The default value is a colon (':').
		 */
        private char nickCompletionSuffix = ':';

        /**
		 * Indicates whether the user is currently cycling over the list of
		 * nicknames for nickname completion.
		 */
        private boolean isCycling = false;

        /**
		 * Check to see whether the user is currently starting the line of text
		 * with a nickname at the beginning of the message. This determines
		 * whether {@link #nickCompletionSuffix} should be inserted when
		 * performing autocompletion. If the user is not at the beginning of the
		 * message, it is likely that the user is typing another user's name to
		 * reference that person and not to direct the message to said person,
		 * as such, the <code>nickCompletionSuffix</code> does not need to be
		 * appended.
		 */
        private boolean isAtStart = false;

        private CTabItem itemSelected = null;

        private Text getInputText() {
            return chatRoomTab.getInputText();
        }

        private StyledText getOutputText() {
            return chatRoomTab.getOutputText();
        }

         ChatRoom(IChatRoomContainer container, ChatRoomTab tabItem) {
            Assert.isNotNull(container);
            Assert.isNotNull(tabItem);
            this.chatRoomContainer = container;
            this.chatRoomMessageSender = container.getChatRoomMessageSender();
            this.chatRoomTab = tabItem;
            options = new ArrayList();
            this.chatRoomTab.setKeyListener(this);
            this.chatRoomParticipantsLabel = tabItem.getParticipantsLabel();
            this.chatRoomParticipantViewer = tabItem.getParticipantsViewer();
            chatRoomContainer.addChatRoomAdminListener(new IChatRoomAdminListener() {

                public void handleSubjectChange(ID from, final String newSubject) {
                    if (!chatRoomTab.getInputText().isDisposed()) {
                        chatRoomTab.getInputText().getDisplay().asyncExec(new Runnable() {

                            public void run() {
                                chatRoomTab.setSubject(newSubject);
                            }
                        });
                    }
                }
            });
            rootTabFolder.setUnselectedCloseVisible(true);
            rootTabFolder.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
                }

                public void widgetSelected(SelectionEvent e) {
                    itemSelected = (CTabItem) e.item;
                    if (itemSelected == chatRoomTab.tabItem)
                        makeTabItemNormal();
                    if (rootChannelTab != null && itemSelected == rootChannelTab.tabItem)
                        rootChannelTab.makeTabItemNormal();
                }
            });
            StyledText st = getOutputText();
            if (st != null) {
                ScrollBar vsb = st.getVerticalBar();
                if (vsb != null) {
                    vsb.addSelectionListener(scrollSelectionListener);
                    vsb.addDisposeListener(new DisposeListener() {

                        public void widgetDisposed(DisposeEvent e) {
                            StyledText ot = getOutputText();
                            if (ot != null) {
                                ScrollBar vb = ot.getVerticalBar();
                                if (vb != null)
                                    vb.removeSelectionListener(scrollSelectionListener);
                            }
                        }
                    });
                }
            }
        }

        private SelectionListener scrollSelectionListener = new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            // do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                if (!isLastOutputInvisible(getOutputText())) {
                    makeTabItemNormal();
                }
            }
        };

        protected void makeTabItemBold() {
            changeTabItem(true);
        }

        protected void makeTabItemNormal() {
            changeTabItem(false);
        }

        protected void changeTabItem(boolean bold) {
            CTabItem item = chatRoomTab.tabItem;
            Font oldFont = item.getFont();
            FontData[] fd = oldFont.getFontData();
            item.setFont(new Font(oldFont.getDevice(), fd[0].getName(), fd[0].getHeight(), (bold) ? SWT.BOLD : SWT.NORMAL));
        }

        public void handleMessage(final ID fromID, final String messageBody) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    if (rootDisposed)
                        return;
                    appendText(chatRoomTab, getOutputText(), new ChatLine(messageBody, new ChatRoomParticipant(fromID)));
                }
            });
        }

        public void handleInvitationReceived(ID roomID, ID from, String subject, String body) {
        // XXX TODO show UI for invitation
        }

        public void keyPressed(KeyEvent e) {
            handleKeyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            handleKeyReleased(e);
        }

        protected void handleKeyPressed(KeyEvent evt) {
            Text inputText = getInputText();
            if (evt.character == SWT.CR) {
                if (inputText.getText().trim().length() > 0)
                    handleTextInput(inputText.getText());
                clearInput();
                makeTabItemNormal();
                if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.PREFERENCES_SCROLLONINPUT))
                    scrollToEnd(getOutputText());
                evt.doit = false;
                isCycling = false;
            } else if (evt.character == SWT.TAB) {
                // don't propogate the event upwards and insert a tab character
                evt.doit = false;
                int pos = inputText.getCaretPosition();
                // if the user is at the beginning of the line, do nothing
                if (pos == 0)
                    return;
                String text = inputText.getText();
                // available nicknames
                if (isCycling) {
                    // if everything's been cycled over, start over at zero
                    if (choice == options.size()) {
                        choice = 0;
                    }
                    //$NON-NLS-1$ //$NON-NLS-2$
                    String append = ((String) options.get(choice++)) + (isAtStart ? nickCompletionSuffix + " " : " ");
                    // remove the previous completion proposal and insert the new one
                    inputText.setText(text.substring(0, caret - prefixLength) + append + text.substring(caret + nickRemainder));
                    // subtract the prefix so we remember where we were originally
                    nickRemainder = append.length() - prefixLength;
                    // set the caret position to be the place where the nickname
                    // completion ended
                    inputText.setSelection(caret + nickRemainder);
                } else {
                    // the user is not cycling, so we need to identify what the
                    // user has typed based on the current caret position
                    int count = pos - 1;
                    // the beginning of the message has been reached
                    while (count > -1 && !Character.isWhitespace(text.charAt(count))) {
                        count--;
                    }
                    count++;
                    // remove all previous options
                    options.clear();
                    // get the prefix that the user typed as a lowercase string
                    String prefix = text.substring(count, pos).toLowerCase();
                    isAtStart = count == 0;
                    // if what's found was actually whitespace, do nothing
                    if (//$NON-NLS-1$
                    prefix.trim().equals(//$NON-NLS-1$
                    "")) {
                        return;
                    }
                    // get all of the users in this room and store them if they
                    // start with the prefix that the user has typed
                    TableItem[] participants = chatRoomParticipantViewer.getTable().getItems();
                    for (int i = 0; i < participants.length; i++) {
                        String name = participants[i].getText();
                        // do a lowercase comparison because we should display options that differ in casing
                        if (name.toLowerCase().startsWith(prefix)) {
                            options.add(name);
                        }
                    }
                    // simply return if no matches have been found
                    if (options.isEmpty())
                        return;
                    prefixLength = prefix.length();
                    if (options.size() == 1) {
                        String nickname = (String) options.get(0);
                        // since only one nickname is available, we'll select
                        // the prefix that has been entered and then replace it
                        // with the nickname option
                        inputText.setSelection(pos - prefixLength, pos);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        inputText.insert(nickname + (isAtStart ? nickCompletionSuffix + " " : " "));
                    } else if (options.size() <= maximumCyclingOptions) {
                        // note that the user is currently cycling through
                        // options and also store the current caret position
                        isCycling = true;
                        caret = pos;
                        choice = 0;
                        // insert the nickname after removing the prefix
                        //$NON-NLS-1$ //$NON-NLS-2$
                        String nickname = options.get(choice++) + (isAtStart ? nickCompletionSuffix + " " : " ");
                        // select the prefix of the proposal
                        inputText.setSelection(pos - prefixLength, pos);
                        // and then replace it with a proposal
                        inputText.insert(nickname);
                        // store the length of this truncated nickname so that
                        // it can be removed when the user is cycling
                        nickRemainder = nickname.length() - prefixLength;
                    } else {
                        // as there are too many choices for the user to pick
                        // from, simply display all of the available ones on the
                        // chat window so that the user can get a visual
                        // indicator of what's available and narrow down the
                        // choices by typing a few more additional characters
                        StringBuffer choices = new StringBuffer();
                        synchronized (choices) {
                            for (int i = 0; i < options.size(); i++) {
                                choices.append(options.get(i)).append(' ');
                            }
                            choices.delete(choices.length() - 1, choices.length());
                        }
                        appendText(chatRoomTab, getOutputText(), new ChatLine(choices.toString()));
                    }
                }
            } else {
                // remove the cycling marking for any other key pressed
                isCycling = false;
            }
        }

        protected void handleKeyReleased(KeyEvent evt) {
            if (evt.character == SWT.TAB) {
                // don't move to the next widget or try to add tabs
                evt.doit = false;
            }
        }

        protected void handleTextInput(String text) {
            if (chatRoomMessageSender == null) {
                MessageDialog.openError(getViewSite().getShell(), Messages.ChatRoomManagerView_NOT_CONNECTED_TITLE, Messages.ChatRoomManagerView_NOT_CONNECTED_MESSAGE);
                return;
            }
            String output = processForCommand(chatRoomContainer, text);
            if (output != null)
                sendMessageLine(output);
        }

        protected void chatRoomDisconnect() {
            if (chatRoomContainer != null)
                chatRoomContainer.disconnect();
        }

        protected void clearInput() {
            //$NON-NLS-1$
            getInputText().setText("");
        }

        protected void sendMessageLine(String line) {
            try {
                chatRoomMessageSender.sendMessage(line);
            } catch (ECFException e) {
                disconnected();
            }
        }

        public void handlePresence(final ID fromID, final IPresence presence) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    if (rootDisposed)
                        return;
                    boolean isAdd = presence.getType().equals(IPresence.Type.AVAILABLE);
                    ChatRoomParticipant p = new ChatRoomParticipant(fromID);
                    if (isAdd) {
                        if (localUser == null)
                            localUser = p;
                        addParticipant(p);
                    } else
                        removeParticipant(p);
                }
            });
        }

        public void disconnected() {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    if (rootDisposed)
                        return;
                    Text inputText = getInputText();
                    if (!inputText.isDisposed())
                        inputText.setEnabled(false);
                }
            });
        }

        protected boolean isConnected() {
            Text inputText = getInputText();
            return !inputText.isDisposed() && inputText.isEnabled();
        }

        protected void setSelected() {
            rootTabFolder.setSelection(chatRoomTab.tabItem);
        }

        protected void addParticipant(IUser p) {
            if (p != null) {
                ID id = p.getID();
                if (id != null) {
                    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
                    if (store.getBoolean(PreferenceConstants.CHATROOM_SHOW_USER_PRESENCE))
                        appendText(chatRoomTab, getOutputText(), new ChatLine(NLS.bind(Messages.ChatRoomManagerView_ENTERED_MESSAGE, getUsernameFromID(id)), null));
                    chatRoomParticipantViewer.add(p);
                    chatRoomParticipantsLabel.setText(NLS.bind(Messages.ChatRoomManagerView_USERS_IN_CHAT_ROOM, String.valueOf(chatRoomContainer.getChatRoomParticipants().length)));
                }
            }
        }

        protected boolean isLocalUser(ID id) {
            if (localUser == null)
                return false;
            else if (localUser.getID().equals(id))
                return true;
            else
                return false;
        }

        protected void removeLocalUser() {
            // It's us that's gone away... so we're outta here
            String title = getPartName();
            setPartName(NLS.bind(Messages.ChatRoomManagerView_VIEW_DISABLED_NAME, title));
            removeAllParticipants();
            disconnect();
            setEnabled(false);
        }

        protected void removeParticipant(IUser p) {
            if (p != null) {
                ID id = p.getID();
                if (id != null) {
                    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
                    if (store.getBoolean(PreferenceConstants.CHATROOM_SHOW_USER_PRESENCE))
                        appendText(chatRoomTab, getOutputText(), new ChatLine(NLS.bind(Messages.ChatRoomManagerView_LEFT_MESSAGE, getUsernameFromID(id)), null));
                    chatRoomParticipantViewer.remove(p);
                    chatRoomParticipantsLabel.setText(NLS.bind(Messages.ChatRoomManagerView_USERS_IN_CHAT_ROOM, String.valueOf(chatRoomContainer.getChatRoomParticipants().length)));
                }
            }
        }

        protected void removeAllParticipants() {
            Table t = chatRoomParticipantViewer.getTable();
            for (int i = 0; i < t.getItemCount(); i++) {
                Object o = chatRoomParticipantViewer.getElementAt(i);
                if (o != null)
                    chatRoomParticipantViewer.remove(o);
            }
            chatRoomParticipantsLabel.setText(NLS.bind(Messages.ChatRoomManagerView_USERS_IN_CHAT_ROOM, String.valueOf(chatRoomContainer.getChatRoomParticipants().length)));
        }
    }

    protected void handleTextInput(String text) {
        if (rootMessageSender == null) {
            MessageDialog.openError(getViewSite().getShell(), Messages.ChatRoomManagerView_NOT_CONNECTED_TITLE, Messages.ChatRoomManagerView_NOT_CONNECTED_MESSAGE);
        } else {
            String output = processForCommand(null, text);
            if (output != null)
                sendMessageLine(output);
        }
    }

    protected String processForCommand(IChatRoomContainer chatRoomContainer, String text) {
        IChatRoomCommandListener l = commandListener;
        if (l != null)
            return l.handleCommand(chatRoomContainer, text);
        return text;
    }

    protected void handleEnter() {
        Text inputText = getRootTextInput();
        if (inputText.getText().trim().length() > 0)
            handleTextInput(inputText.getText());
        clearInput();
        scrollToEnd(getRootTextOutput());
        if (rootChannelTab != null)
            rootChannelTab.makeTabItemNormal();
    }

    protected void handleKeyPressed(KeyEvent evt) {
        if (evt.character == SWT.CR) {
            handleEnter();
            evt.doit = false;
        }
    }

    public void setFocus() {
        Text text = getRootTextInput();
        if (text != null)
            text.setFocus();
    }

    public void joinRoom(final IChatRoomInfo info, final String password) {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                if (rootDisposed)
                    return;
                doJoinRoom(info, password);
            }
        });
    }

    public void dispose() {
        disconnect();
        rootDisposed = true;
        super.dispose();
    }

    protected String getMessageString(ID fromID, String text) {
        return NLS.bind(Messages.ChatRoomManagerView_MESSAGE, fromID.getName(), text);
    }

    private ID createStringID(String str) {
        try {
            return IDFactory.getDefault().createStringID(str);
        } catch (IDCreateException e) {
        }
        return null;
    }

    private MessagesView getMessagesView() throws PartInitException {
        IWorkbenchPage page = getSite().getPage();
        MessagesView messageView = (MessagesView) page.findView(MessagesView.VIEW_ID);
        if (messageView == null) {
            messageView = (MessagesView) page.showView(MessagesView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
        }
        return messageView;
    }

    /**
	 * A delegate method to handle chat messages.
	 * @param message the chat message that has been received
	 * @since 1.1
	 */
    public void handleChatMessage(final IChatMessage message) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    ID targetID = createStringID(localUserName);
                    MessagesView messageView = getMessagesView();
                    IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) messageView.getSite().getAdapter(IWorkbenchSiteProgressService.class);
                    if (container.getPrivateMessageSender() != null) {
                        messageView.openTab(container.getPrivateMessageSender(), null, targetID, message.getFromID(), new ChatRoomParticipant(targetID).getNickname());
                        messageView.showMessage(message);
                        service.warnOfContentChange();
                    }
                } catch (Exception e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.ChatRoomManagerView_EXCEPTION_MESSAGE_VIEW_INITIALIZATION + message.getFromID(), e));
                }
            }
        });
    }

    public void handleMessage(final ID fromID, final String messageBody) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (rootDisposed)
                    return;
                if (rootChannelTab != null) {
                    appendText(rootChannelTab, getRootTextOutput(), new ChatLine(messageBody, new ChatRoomParticipant(fromID)));
                    rootChannelTab.makeTabItemBold();
                }
            }
        });
    }

    /**
	 * @return String username for given <code>targetID</code>
	 */
    public static String getUsernameFromID(ID targetID) {
        IChatID chatID = (IChatID) targetID.getAdapter(IChatID.class);
        if (chatID != null)
            return chatID.getUsername();
        try {
            URI uri = new URI(targetID.getName());
            String user = uri.getUserInfo();
            return user == null ? targetID.getName() : user;
        } catch (URISyntaxException e) {
            String userAtHost = targetID.getName();
            int atIndex = userAtHost.lastIndexOf(ATSIGN);
            if (atIndex != -1)
                userAtHost = userAtHost.substring(0, atIndex);
            return userAtHost;
        }
    }

    /**
	 * @return String hostname for given <code>targetID</code>
	 */
    public static String getHostnameFromID(ID targetID) {
        IChatID chatID = (IChatID) targetID.getAdapter(IChatID.class);
        if (chatID != null)
            return chatID.getHostname();
        try {
            URI uri = new URI(targetID.getName());
            String host = uri.getHost();
            return host == null ? targetID.getName() : host;
        } catch (URISyntaxException e) {
            String userAtHost = targetID.getName();
            int atIndex = userAtHost.lastIndexOf(ATSIGN);
            if (atIndex != -1)
                userAtHost = userAtHost.substring(atIndex + 1);
            return userAtHost;
        }
    }

    class ChatRoomParticipant implements IUser, IActionFilter {

        private static final long serialVersionUID = 2008114088656711572L;

        ID id;

        public  ChatRoomParticipant(ID id) {
            this.id = id;
        }

        public ID getID() {
            return id;
        }

        public String getName() {
            return toString();
        }

        public boolean equals(Object other) {
            if (!(other instanceof ChatRoomParticipant))
                return false;
            ChatRoomParticipant o = (ChatRoomParticipant) other;
            if (id.equals(o.id))
                return true;
            return false;
        }

        public int hashCode() {
            return id.hashCode();
        }

        public String toString() {
            return getUsernameFromID(id);
        }

        public Map getProperties() {
            return null;
        }

        public Object getAdapter(Class adapter) {
            return null;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ecf.core.user.IUser#getNickname()
		 */
        public String getNickname() {
            return getName();
        }

        public boolean testAttribute(Object target, String name, String value) {
            if (//$NON-NLS-1$
            name.equals("scheme")) {
                IChatRoomContainer c = ChatRoomManagerView.this.container;
                String scheme = c.getConnectedID().getNamespace().getScheme();
                return scheme.equalsIgnoreCase(value);
            }
            return false;
        }
    }

    public void disconnect() {
        if (rootCloseListener != null) {
            rootCloseListener.chatRoomViewClosing();
        }
        // disconnect from each chat room container
        for (Iterator i = chatRooms.values().iterator(); i.hasNext(); ) {
            ChatRoom chatRoom = (ChatRoom) i.next();
            IChatRoomContainer c = chatRoom.chatRoomContainer;
            if (c != null)
                c.disconnect();
        }
        rootMessageSender = null;
        rootCloseListener = null;
        chatRooms.clear();
    }

    protected void removeLocalUser() {
        // It's us that's gone away... so we're outta here
        String title = getPartName();
        setPartName(NLS.bind(Messages.ChatRoomManagerView_VIEW_DISABLED_NAME, title));
        disconnect();
        setEnabled(false);
    }

    public void handleInvitationReceived(ID roomID, ID from, String subject, String body) {
    // XXX TODO
    }

    private boolean isLastOutputInvisible(StyledText chatText) {
        Point locAtEnd = chatText.getLocationAtOffset(chatText.getText().length());
        Rectangle bounds = chatText.getBounds();
        return (locAtEnd.y > bounds.height + 5);
    }

    private void scrollToEnd(StyledText chatText) {
        chatText.setSelection(chatText.getText().length());
    }

    protected void appendText(ChatRoomTab chatRoomTab, StyledText st, ChatLine text) {
        if (st == null || text == null) {
            return;
        }
        boolean isAtEndBeforeAppend = !isLastOutputInvisible(st);
        String originator = null;
        if (text.getOriginator() != null) {
            originator = text.getOriginator().getNickname();
        }
        if (messageRenderer == null)
            messageRenderer = new MessageRenderer();
        String output = messageRenderer.render(text.getText(), originator, localUserName);
        StyleRange[] ranges = messageRenderer.getStyleRanges();
        if (output == null) {
            return;
        }
        int startRange = st.getText().length();
        if (!text.isNoCRLF()) {
            //$NON-NLS-1$
            output += "\n";
        }
        st.append(output);
        if (ranges != null) {
            // set all ranges to start as message line starts
            for (int i = 0; i < ranges.length; i++) {
                ranges[i].start += startRange;
            }
            st.replaceStyleRanges(startRange, output.length(), ranges);
        }
        if (isAtEndBeforeAppend)
            scrollToEnd(st);
        if (isCurrentlyActive(chatRoomTab))
            chatRoomTab.makeTabItemNormal();
        else
            chatRoomTab.makeTabItemBold();
    }

    protected void outputClear() {
        if (MessageDialog.openConfirm(null, Messages.ChatRoomManagerView_CLEAR_CONFIRM_TITLE, Messages.ChatRoomManagerView_CLEAR_CONFIRM_MESSAGE)) {
            //$NON-NLS-1$
            getRootTextOutput().setText("");
        }
    }

    protected void outputCopy() {
        StyledText outputText = getRootTextOutput();
        String t = outputText.getSelectionText();
        if (t == null || t.length() == 0) {
            outputText.selectAll();
        }
        outputText.copy();
        outputText.setSelection(outputText.getText().length());
    }

    protected void outputSelectAll() {
        getRootTextOutput().selectAll();
    }

    protected void makeActions() {
        outputSelectAll = new Action() {

            public void run() {
                outputSelectAll();
            }
        };
        outputSelectAll.setText(Messages.ChatRoomManagerView_SELECT_ALL_TEXT);
        outputSelectAll.setToolTipText(Messages.ChatRoomManagerView_SELECT_ALL_TOOLTIP);
        outputSelectAll.setAccelerator(SWT.CTRL | 'A');
        outputCopy = new Action() {

            public void run() {
                outputCopy();
            }
        };
        outputCopy.setText(Messages.ChatRoomManagerView_COPY_TEXT);
        outputCopy.setToolTipText(Messages.ChatRoomManagerView_COPY_TOOLTIP);
        outputCopy.setAccelerator(SWT.CTRL | 'C');
        outputCopy.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        outputClear = new Action() {

            public void run() {
                outputClear();
            }
        };
        outputClear.setText(Messages.ChatRoomManagerView_CLEAR_TEXT);
        outputClear.setToolTipText(Messages.ChatRoomManagerView_CLEAR_TOOLTIP);
        outputPaste = new Action() {

            public void run() {
                getRootTextInput().paste();
            }
        };
        outputPaste.setText(Messages.ChatRoomManagerView_PASTE_TEXT);
        outputPaste.setToolTipText(Messages.ChatRoomManagerView_PASTE_TOOLTIP);
        outputPaste.setAccelerator(SWT.CTRL | 'V');
        outputPaste.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(outputCopy);
        manager.add(outputPaste);
        manager.add(outputClear);
        manager.add(new Separator());
        manager.add(outputSelectAll);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        StyledText outputText = getRootTextOutput();
        Menu menu = menuMgr.createContextMenu(outputText);
        outputText.setMenu(menu);
        ISelectionProvider selectionProvider = new ISelectionProvider() {

            public void addSelectionChangedListener(ISelectionChangedListener listener) {
            // do nothing
            }

            public ISelection getSelection() {
                StyledText ot = getRootTextOutput();
                ISelection selection = new TextSelection(ot.getSelectionRange().x, ot.getSelectionRange().y);
                return selection;
            }

            public void removeSelectionChangedListener(ISelectionChangedListener listener) {
            // do nothing
            }

            public void setSelection(ISelection selection) {
                if (selection instanceof ITextSelection) {
                    ITextSelection textSelection = (ITextSelection) selection;
                    StyledText ot = getRootTextOutput();
                    ot.setSelection(textSelection.getOffset(), textSelection.getOffset() + textSelection.getLength());
                }
            }
        };
        getSite().registerContextMenu(menuMgr, selectionProvider);
    }

    public void setMessageRenderer(IMessageRenderer defaultMessageRenderer) {
        this.messageRenderer = defaultMessageRenderer;
    }

    private boolean isCurrentlyActive(ChatRoomTab chatRoomTab) {
        int selected = rootTabFolder.getSelectionIndex();
        if (selected != -1) {
            CTabItem item = rootTabFolder.getItem(selected);
            if (item == chatRoomTab.tabItem)
                return true;
        }
        return false;
    }
}
