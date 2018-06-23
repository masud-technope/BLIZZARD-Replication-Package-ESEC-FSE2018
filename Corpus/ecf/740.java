/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

import java.util.Collection;
import java.util.List;

/**
 * Models a poll. Some Bulletin Board implementations only allow a single poll
 * to be associated with a thread. In that case, the implementation may use the
 * same IBBObject property values for the thread and the poll.
 * 
 * Implementation should consider implementing IPoll, IPollOption and IPollVote
 * optional.
 * 
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will work or
 * that it will remain the same. Please do not use this API without consulting
 * with the ECF team.
 * </p>
 * 
 * @author Erkki
 */
public interface IPoll extends IBBObject {

    /**
	 * This polls allows voters to be tracked.
	 */
    public static final int VOTERS_VISIBLE = 1;

    /**
	 * This poll allows multiple options to be selected when voting.
	 */
    public static final int SELECT_MULTIPLE = 2;

    /**
	 * The type of this poll. VOTERS_VISIBLE and/or SELECT_MULTIPPLE.
	 * 
	 * @return type of this poll
	 */
    public int getType();

    /**
	 * Returns the thread that this poll belongs to, or null if this poll
	 * doesn't belong to any thread.
	 * 
	 * @return the owner thread
	 */
    public IThread getThread();

    /**
	 * Returns the question for this poll.
	 * 
	 * @return the question
	 */
    public String getQuestion();

    /**
	 * Returns the options for this poll.
	 * 
	 * @return the options
	 */
    public List getOptions();

    /**
	 * Returns a particular option for this poll, identified by the option
	 * number paremeter.
	 * 
	 * @param optionNumber
	 *            number of the option to return
	 * @return an option
	 */
    public IPollOption getOption(int optionNumber);

    /**
	 * Returns the summary of votes for this poll. One vote will be returned for
	 * each option and the vote will contain the number of people that voted for
	 * the option.
	 * 
	 * @return the vote summary
	 * @throws BBException
	 *             if fetching the votes failed.
	 */
    public List getSummaryVotes() throws BBException;

    /**
	 * Returns all the votes for this poll. One vote will be returned for all
	 * votes made.
	 * 
	 * @return the votes
	 * @throws BBException
	 *             if fetching the votes failed.
	 */
    public List getDetailVotes() throws BBException;

    /**
	 * Creates a IPollVote implementation that can be used to vote on this poll.
	 * 
	 * @return a newly created IPollVote
	 * @throws IllegalWriteException
	 *             if this poll is read-only
	 */
    public IPollVote createVote() throws IllegalWriteException;

    /**
	 * Votes on this poll, using the vote passed in.
	 * 
	 * @param vote
	 *            the vote to be made
	 * @return true if voting was successful, false otherwise
	 * @throws IllegalWriteException
	 *             if the poll is read-only
	 * @throws BBException
	 *             if voting failed
	 */
    public boolean vote(IPollVote vote) throws IllegalWriteException, BBException;

    /**
	 * Votes on this poll, using the votes passed in.
	 * 
	 * @param votes
	 *            the votes to be made
	 * @return true if voting was successful, false otherwise
	 * @throws IllegalWriteException
	 *             if the poll is read-only
	 * @throws BBException
	 *             if voting failed
	 */
    public boolean vote(Collection votes) throws IllegalWriteException, BBException;
}
