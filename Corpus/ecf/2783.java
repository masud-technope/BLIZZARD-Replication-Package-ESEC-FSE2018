package org.eclipse.ecf.tests.provider.xmpp;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.tests.presence.AbstractPresenceTestCase;

/**
 *
 */
public class RosterTest extends AbstractPresenceTestCase {

    private int resourceId = 1;

    protected String getClientContainerName() {
        return XMPP.CONTAINER_NAME;
    }

    protected String getUsername(int client) {
        // in resource IDs.
        return super.getUsername(client) + "/ECF." + resourceId;
    }

    public void testDisconnectedResourcesAreRemovedFromRoster() throws Exception {
    // Tests that when a user connects with a resource ID, then
    // disconnects, then reconnects with a different resource ID, the user
    // still appears only once in the roster.
    // Determined from the XMPPID with getUsernameAtHost to make sure it
    // doesn't contain a resource ID.
    // This test is removed because in our automated test environment the accounts to not have each other 
    // on their rosters...and so this test fails because it assumes that the two accounts are on each
    // others rosters
    //		String client = ((XMPPID) getServerConnectID(1)).getUsernameAtHost();
    //		
    //		connectClient(0);
    //		Thread.sleep(500);
    //		IRoster roster = getPresenceAdapter(0).getRosterManager().getRoster();
    //		assertEquals(1, countMatchingEntries(roster, client));
    //		
    //		connectClient(1);
    //		Thread.sleep(500);
    //		assertEquals(1, countMatchingEntries(roster, client));
    //		
    //		clients[1].disconnect();
    //		Thread.sleep(500);
    //		assertEquals(1, countMatchingEntries(roster, client));
    //		
    //		resourceId++;
    //		connectClient(1);
    //		Thread.sleep(500);
    //		assertEquals(1, countMatchingEntries(roster, client));
    //		
    //		clients[1].disconnect();
    //		clients[0].disconnect();
    }

    public void testClientConnectsTwiceWithOneUsername() throws Exception {
    // This test is removed because in our automated test environment the accounts to not have each other 
    // on their rosters...and so this test fails because it assumes that the two accounts are on each
    // others rosters
    //		String client = ((XMPPID) getServerConnectID(1)).getUsernameAtHost();
    //		
    //		connectClient(0);
    //		Thread.sleep(3000);
    //		IRoster roster = getPresenceAdapter(0).getRosterManager().getRoster();
    //		assertEquals(1, countMatchingEntries(roster, client));
    //		
    //		IContainer c0 = ContainerFactory.getDefault().createContainer(getClientContainerName());
    //		IContainer c1 = ContainerFactory.getDefault().createContainer(getClientContainerName());
    //		ID connectID0 = getServerConnectID(1);
    //		IConnectContext connectContext0 = getConnectContext(1);
    //		resourceId++;
    //		ID connectID1 = getServerConnectID(1);
    //		IConnectContext connectContext1 = getConnectContext(1);
    //		
    //		connectClient(c0, connectID0, connectContext0);
    //		connectClient(c1, connectID1, connectContext1);
    //		Thread.sleep(3000);
    //		// Two clients are connected with the same username, so the user should
    //		// be found twice in the roster.
    //		assertEquals(2, countMatchingEntries(roster, client));
    //		
    //		c0.disconnect();
    //		Thread.sleep(500);
    //		assertEquals(1, countMatchingEntries(roster, client));
    //
    //		c1.disconnect();
    //		Thread.sleep(500);
    //		assertEquals(1, countMatchingEntries(roster, client));
    }

    /**
	 * Counts the entries that match the username in the roster.
	 */
    protected int countMatchingEntries(IRoster roster, String username) {
        return countMatchingItems(roster.getItems(), username);
    }

    protected int countMatchingItems(Collection items, String username) {
        int sum = 0;
        for (Iterator i = items.iterator(); i.hasNext(); ) {
            IRosterItem item = (IRosterItem) i.next();
            if (item instanceof IRosterGroup) {
                sum += countMatchingItems(((IRosterGroup) item).getEntries(), username);
            } else if (item instanceof IRosterEntry) {
                ID id = ((IRosterEntry) item).getUser().getID();
                String itemName = ((XMPPID) id).getUsernameAtHost();
                if (itemName.equals(username)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    protected void tearDown() throws Exception {
        // This is a possible workaround for what appears to be Smack bug:  https://bugs.eclipse.org/bugs/show_bug.cgi?id=321032
        Thread.sleep(2000);
        super.tearDown();
    }
}
