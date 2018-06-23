package org.eclipse.ecf.internal.examples.webinar.util.rosterentry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class SendMessageRosterEntryContribution extends AbstractRosterEntryContributionItem {

    private static final String CHANNELID = "mychannel";

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.ui.roster.AbstractPresenceContributionItem#makeActions()
	 */
    protected IAction[] makeActions() {
        IAction action = null;
        final IRosterEntry rosterEntry = getSelectedRosterEntry();
        if (rosterEntry != null) {
            if (initializeChannelFor(getContainerForRosterEntry(rosterEntry)) != null) {
                action = new Action() {

                    public void run() {
                        sendDataToChannel(rosterEntry);
                    }
                };
                action.setText("Send Message and URL");
                action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
                return new IAction[] { action };
            }
        }
        return null;
    }

    private void sendDataToChannel(IRosterEntry rosterEntry) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(new String[] { "Hi " + rosterEntry.getName(), "http://www.eclipse.org/ecf" });
            IChannel channel = initializeChannelFor(getContainerForRosterEntry(rosterEntry));
            channel.sendMessage(rosterEntry.getUser().getID(), bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveDataFromChannel(byte[] data) {
        try {
            ObjectInputStream oos = new ObjectInputStream(new ByteArrayInputStream(data));
            String[] received = (String[]) oos.readObject();
            showMessageAndURL(received[0], received[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map channels = new HashMap();

    private IChannel initializeChannelFor(IContainer container) {
        if (container == null)
            return null;
        ID containerID = container.getID();
        IChannel chan = (IChannel) channels.get(containerID);
        if (chan == null) {
            IChannelContainerAdapter adapter = (IChannelContainerAdapter) container.getAdapter(IChannelContainerAdapter.class);
            if (adapter != null) {
                chan = createChannel(adapter);
                channels.put(containerID, chan);
            }
        }
        return chan;
    }

    public void dispose() {
        super.dispose();
        for (Iterator i = channels.keySet().iterator(); i.hasNext(); ) {
            IChannel chan = (IChannel) channels.get(i.next());
            chan.dispose();
        }
        channels.clear();
    }

    private IChannel createChannel(IChannelContainerAdapter adapter) {
        try {
            return adapter.createChannel(IDFactory.getDefault().createStringID(CHANNELID), new IChannelListener() {

                public void handleChannelEvent(IChannelEvent event) {
                    if (event instanceof IChannelMessageEvent) {
                        receiveDataFromChannel(((IChannelMessageEvent) event).getData());
                    }
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showMessageAndURL(final String string, final String url) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (MessageDialog.openConfirm(null, "Received message", string + ".  Show URL?"))
                    showURL(url);
            }
        });
    }

    private void showURL(String string) {
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        IWebBrowser browser;
        try {
            browser = support.createBrowser(null);
            browser.openURL(new URL(string));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
