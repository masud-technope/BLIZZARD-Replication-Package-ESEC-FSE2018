/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.xmpp.search;

import java.util.Iterator;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.search.ICriteria;
import org.eclipse.ecf.presence.search.ICriterion;
import org.eclipse.ecf.presence.search.IRestriction;
import org.eclipse.ecf.presence.search.IResult;
import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.ecf.presence.search.ISearch;
import org.eclipse.ecf.presence.search.IUserSearchCompleteEvent;
import org.eclipse.ecf.presence.search.IUserSearchEvent;
import org.eclipse.ecf.presence.search.IUserSearchListener;
import org.eclipse.ecf.presence.search.IUserSearchManager;
import org.eclipse.ecf.presence.search.UserSearchException;
import org.eclipse.ecf.tests.presence.AbstractSearchTest;
import org.eclipse.ecf.tests.provider.xmpp.XMPP;

/**
 * 
 * @since 3.0
 */
public class XMPPSearchTest extends AbstractSearchTest {

    private static final String USERNAME = "Username";

    private static final String EMAIL = "Email";

    private static final String NAME = "Name";

    IContainer client;

    IUserSearchManager searchManager;

    ISearch searchResult;

    protected String getClientContainerName() {
        return XMPP.CONTAINER_NAME;
    }

    public void testRetrieveBuddiesAsync() throws Exception {
        assertNotNull(searchManager);
        IRestriction selection = searchManager.createRestriction();
        assertNotNull(selection);
        // fields to consider on XMPP server side search
        // search field for XMPP, criterion to match the search
        ICriterion name = selection.eq(NAME, "Marcelo*");
        ICriterion email = selection.eq(EMAIL, "zx*");
        ICriterion username = selection.eq(USERNAME, "sl*");
        // create a specific criteria
        final ICriteria criteria = searchManager.createCriteria();
        assertNotNull(criteria);
        criteria.add(name);
        criteria.add(email);
        criteria.add(username);
        IUserSearchListener listenerCompleted = new IUserSearchListener() {

            public void handleUserSearchEvent(IUserSearchEvent event) {
                if (event instanceof IUserSearchCompleteEvent) {
                    searchResult = ((IUserSearchCompleteEvent) event).getSearch();
                }
            }
        };
        // call the non-block search
        searchManager.search(criteria, listenerCompleted);
        assertNull(searchResult);
        Thread.sleep(5000);
        // put the completion result on the search handle
        if (searchResult == null)
            return;
        assertNotNull(searchResult);
        // check if there is at least one result
        assertTrue(0 != searchResult.getResultList().getResults().size());
    }

    /**
	 * Try to locate buddies on the XMPP server in a call block way
	 * 
	 * @throws ContainerConnectException
	 */
    public void testRetrieveBuddiesAllFieldsSync() throws ContainerConnectException {
        try {
            assertNotNull(searchManager);
            IRestriction selection = searchManager.createRestriction();
            assertNotNull(selection);
            // fields to consider on XMPP server side search
            // search field for XMPP, criterion to match the search
            ICriterion name = selection.eq(NAME, "Marcelo*");
            ICriterion email = selection.eq(EMAIL, "zx*");
            ICriterion username = selection.eq(USERNAME, "sl*");
            // create a specific criteria
            ICriteria criteria = searchManager.createCriteria();
            assertNotNull(criteria);
            criteria.add(name);
            criteria.add(email);
            criteria.add(username);
            // call the block search
            ISearch search = searchManager.search(criteria);
            // the collection of IResult
            IResultList resultList = search.getResultList();
            // check if there is at least one result
            assertTrue(0 != resultList.getResults().size());
            Iterator it = resultList.getResults().iterator();
            while (it.hasNext()) {
                IResult type = (IResult) it.next();
                System.out.println(type.getUser().getName() + " : " + type.getUser().getID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * Try to locate buddies on the XMPP server in a call block way
	 * 
	 * @throws ContainerConnectException
	 */
    public void testRetrieveBuddiesEmailFieldSync() throws Exception {
        assertNotNull(searchManager);
        IRestriction selection = searchManager.createRestriction();
        assertNotNull(selection);
        // search field for XMPP, criterion to match the search
        // fields to consider on XMPP server side search
        ICriterion email = selection.eq(EMAIL, "mayworm*");
        // create a specific criteria
        ICriteria criteria = searchManager.createCriteria();
        assertNotNull(criteria);
        criteria.add(email);
        // call the block search
        try {
            ISearch search = searchManager.search(criteria);
            // the collection of IResult
            IResultList resultList = search.getResultList();
            // check if there is at least one result
            int resultListSize = resultList.getResults().size();
            if (resultListSize == 0)
                System.out.println("XMPPSearchTest.testRetrieveBuddiesEmailFieldSync...no email field retrieved");
            else
                assertTrue(1 == resultListSize);
        } catch (UserSearchException e) {
            e.printStackTrace();
        }
    }

    /**
	 * Try to locate buddies on the XMPP server in a call block way
	 * 
	 * @throws ContainerConnectException
	 */
    public void testRetrieveBuddiesNameFieldSync() throws Exception {
        assertNotNull(searchManager);
        IRestriction selection = searchManager.createRestriction();
        assertNotNull(selection);
        // fields to consider on XMPP server side search
        // search field for XMPP, criterion to match the search
        ICriterion name = selection.eq(NAME, "marcelo*");
        // create a specific criteria
        try {
            ICriteria criteria = searchManager.createCriteria();
            assertNotNull(criteria);
            // criteria.add(searchCriterion);
            criteria.add(name);
            // call the block search
            ISearch search = searchManager.search(criteria);
            // the collection of IResult
            IResultList resultList = search.getResultList();
            int resultListSize = resultList.getResults().size();
            if (resultListSize == 0)
                System.out.println("XMPPSearchTest.testRetrieveBuddiesNameFieldSync...no email field retrieved");
            else
                assertTrue(1 == resultListSize);
        } catch (UserSearchException e) {
            e.printStackTrace();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        final int clientIndex = 0;
        client = getClient(clientIndex);
        assertNull(client.getConnectedID());
        final ID serverConnectID = getServerConnectID(clientIndex);
        assertNotNull(serverConnectID);
        connectClient(client, serverConnectID, getConnectContext(clientIndex));
        assertEquals(serverConnectID, client.getConnectedID());
        searchManager = getPresenceAdapter(clientIndex).getUserSearchManager();
    }

    protected void tearDown() throws Exception {
        // This is a possible workaround for what appears to be Smack bug:  https://bugs.eclipse.org/bugs/show_bug.cgi?id=321032
        Thread.sleep(2000);
        super.tearDown();
        client.disconnect();
    }

    public void testUserProperties() throws ECFException {
        assertNotNull(searchManager);
        try {
            String userProperties[] = searchManager.getUserPropertiesFields();
            // check if there is at least one result
            assertTrue(0 != userProperties.length);
        } catch (ECFException e) {
            e.printStackTrace();
        }
    }
}
